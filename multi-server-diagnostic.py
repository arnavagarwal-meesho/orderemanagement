#!/usr/bin/env python3
"""
Multi-Server Order Management System Diagnostic Tool
This script performs comprehensive testing of the order management system
across multiple server instances to identify potential issues.
"""

import requests
import json
import time
import threading
import concurrent.futures
from collections import defaultdict, Counter
import statistics

class MultiServerDiagnostic:
    def __init__(self):
        self.servers = [
            "http://localhost:8080",
            "http://localhost:8081", 
            "http://localhost:8082"
        ]
        self.results = {
            'server_health': {},
            'load_distribution': defaultdict(int),
            'concurrency_test': {},
            'data_consistency': {},
            'performance_metrics': {},
            'error_analysis': defaultdict(list)
        }
        
    def test_server_health(self):
        """Test if all servers are responding correctly"""
        print("🔍 Testing Server Health...")
        
        for server in self.servers:
            try:
                start_time = time.time()
                response = requests.get(f"{server}/api/customers/products", timeout=5)
                response_time = time.time() - start_time
                
                if response.status_code == 200:
                    products = response.json()
                    self.results['server_health'][server] = {
                        'status': 'HEALTHY',
                        'response_time': response_time,
                        'product_count': len(products),
                        'products': products
                    }
                    print(f"  ✅ {server} - OK ({response_time:.3f}s, {len(products)} products)")
                else:
                    self.results['server_health'][server] = {
                        'status': 'ERROR',
                        'error': f"HTTP {response.status_code}"
                    }
                    print(f"  ❌ {server} - HTTP {response.status_code}")
                    
            except Exception as e:
                self.results['server_health'][server] = {
                    'status': 'UNREACHABLE',
                    'error': str(e)
                }
                print(f"  ❌ {server} - UNREACHABLE: {e}")
    
    def test_data_consistency(self):
        """Test if all servers return consistent data"""
        print("\n🔍 Testing Data Consistency...")
        
        product_data = {}
        
        for server in self.servers:
            if self.results['server_health'][server]['status'] == 'HEALTHY':
                products = self.results['server_health'][server]['products']
                product_data[server] = {p['id']: p for p in products}
        
        if len(product_data) < 2:
            print("  ⚠️  Not enough healthy servers to test consistency")
            return
            
        # Compare product data across servers
        server_list = list(product_data.keys())
        reference_server = server_list[0]
        reference_products = product_data[reference_server]
        
        consistency_issues = []
        
        for server in server_list[1:]:
            server_products = product_data[server]
            
            for product_id, ref_product in reference_products.items():
                if product_id not in server_products:
                    consistency_issues.append(f"Product {product_id} missing from {server}")
                else:
                    server_product = server_products[product_id]
                    for field in ['name', 'price', 'stockQuantity']:
                        if ref_product[field] != server_product[field]:
                            consistency_issues.append(
                                f"Product {product_id} {field} mismatch: "
                                f"{reference_server}={ref_product[field]} vs "
                                f"{server}={server_product[field]}"
                            )
        
        self.results['data_consistency'] = {
            'issues': consistency_issues,
            'status': 'CONSISTENT' if not consistency_issues else 'INCONSISTENT'
        }
        
        if not consistency_issues:
            print("  ✅ All servers return consistent data")
        else:
            print(f"  ❌ Found {len(consistency_issues)} consistency issues:")
            for issue in consistency_issues:
                print(f"     - {issue}")
    
    def simulate_concurrent_purchases(self, num_requests=30, num_threads=10):
        """Simulate concurrent purchase requests across multiple servers"""
        print(f"\n🔍 Testing Concurrent Purchases ({num_requests} requests, {num_threads} threads)...")
        
        # First, get a customer ID and product info
        healthy_server = None
        for server, health in self.results['server_health'].items():
            if health['status'] == 'HEALTHY':
                healthy_server = server
                break
                
        if not healthy_server:
            print("  ❌ No healthy servers available for testing")
            return
            
        # Try to login with test customer
        try:
            login_data = {
                "email": "test2@example.com",
                "password": "password123"
            }
            login_response = requests.post(f"{healthy_server}/api/customers/login", 
                                         json=login_data, timeout=5)
            
            if login_response.status_code != 200:
                print(f"  ❌ Cannot login test customer: {login_response.status_code}")
                return
                
            customer_data = login_response.json()
            customer_id = customer_data['id']
            
            # Get product with stock
            products = self.results['server_health'][healthy_server]['products']
            test_product = None
            for product in products:
                if product['stockQuantity'] > 0:
                    test_product = product
                    break
                    
            if not test_product:
                print("  ❌ No products with stock available for testing")
                return
                
            print(f"  📦 Testing with Product: {test_product['name']} (Stock: {test_product['stockQuantity']})")
            
        except Exception as e:
            print(f"  ❌ Error setting up test: {e}")
            return
        
        # Prepare purchase requests
        purchase_data = {
            "customerId": customer_id,
            "productId": test_product['id'],
            "quantity": 1
        }
        
        results = {
            'successful_purchases': 0,
            'failed_purchases': 0,
            'errors': Counter(),
            'response_times': [],
            'server_distribution': Counter()
        }
        
        def make_purchase_request(request_id):
            server = self.servers[request_id % len(self.servers)]
            
            try:
                start_time = time.time()
                response = requests.post(f"{server}/api/customers/products/buy",
                                       json=purchase_data, timeout=10)
                response_time = time.time() - start_time
                
                results['server_distribution'][server] += 1
                results['response_times'].append(response_time)
                
                if response.status_code == 200:
                    results['successful_purchases'] += 1
                    return {'status': 'success', 'server': server, 'time': response_time}
                else:
                    results['failed_purchases'] += 1
                    error_msg = response.text if response.text else f"HTTP {response.status_code}"
                    results['errors'][error_msg] += 1
                    return {'status': 'error', 'server': server, 'error': error_msg}
                    
            except Exception as e:
                results['failed_purchases'] += 1
                results['errors'][str(e)] += 1
                return {'status': 'exception', 'server': server, 'error': str(e)}
        
        # Execute concurrent requests
        with concurrent.futures.ThreadPoolExecutor(max_workers=num_threads) as executor:
            futures = [executor.submit(make_purchase_request, i) for i in range(num_requests)]
            concurrent_results = [future.result() for future in concurrent.futures.as_completed(futures)]
        
        # Analyze results
        self.results['concurrency_test'] = {
            'total_requests': num_requests,
            'successful_purchases': results['successful_purchases'],
            'failed_purchases': results['failed_purchases'],
            'success_rate': results['successful_purchases'] / num_requests * 100,
            'errors': dict(results['errors']),
            'server_distribution': dict(results['server_distribution']),
            'avg_response_time': statistics.mean(results['response_times']) if results['response_times'] else 0,
            'max_response_time': max(results['response_times']) if results['response_times'] else 0
        }
        
        print(f"  📊 Results:")
        print(f"     ✅ Successful: {results['successful_purchases']}/{num_requests} ({results['successful_purchases']/num_requests*100:.1f}%)")
        print(f"     ❌ Failed: {results['failed_purchases']}/{num_requests}")
        print(f"     ⏱️  Avg Response Time: {statistics.mean(results['response_times']):.3f}s" if results['response_times'] else "     ⏱️  No timing data")
        print(f"     🔄 Server Distribution: {dict(results['server_distribution'])}")
        
        if results['errors']:
            print(f"     ⚠️  Error Summary:")
            for error, count in results['errors'].items():
                print(f"        - {error}: {count} times")
    
    def check_inventory_integrity(self):
        """Check if inventory is properly managed after concurrent operations"""
        print("\n🔍 Checking Inventory Integrity...")
        
        healthy_servers = [server for server, health in self.results['server_health'].items() 
                          if health['status'] == 'HEALTHY']
        
        if not healthy_servers:
            print("  ❌ No healthy servers to check inventory")
            return
            
        # Get current inventory from all servers
        inventory_data = {}
        for server in healthy_servers:
            try:
                response = requests.get(f"{server}/api/customers/products", timeout=5)
                if response.status_code == 200:
                    products = response.json()
                    inventory_data[server] = {p['id']: p['stockQuantity'] for p in products}
            except Exception as e:
                print(f"  ⚠️  Error getting inventory from {server}: {e}")
        
        # Check consistency
        if len(inventory_data) > 1:
            servers = list(inventory_data.keys())
            reference = inventory_data[servers[0]]
            
            for server in servers[1:]:
                for product_id, ref_stock in reference.items():
                    if product_id in inventory_data[server]:
                        server_stock = inventory_data[server][product_id]
                        if ref_stock != server_stock:
                            print(f"  ❌ Inventory mismatch for product {product_id}: {servers[0]}={ref_stock}, {server}={server_stock}")
                            return
            
            print("  ✅ Inventory is consistent across all servers")
            
            # Show final inventory
            for product_id, stock in reference.items():
                print(f"     Product {product_id}: {stock} remaining")
        else:
            print("  ⚠️  Only one server available for inventory check")
    
    def generate_report(self):
        """Generate a comprehensive diagnostic report"""
        print("\n" + "="*60)
        print("📋 MULTI-SERVER DIAGNOSTIC REPORT")
        print("="*60)
        
        # Server Health Summary
        healthy_count = sum(1 for health in self.results['server_health'].values() 
                           if health['status'] == 'HEALTHY')
        total_servers = len(self.servers)
        
        print(f"\n🏥 SERVER HEALTH: {healthy_count}/{total_servers} servers healthy")
        
        if healthy_count == total_servers:
            print("   ✅ All servers are operational")
        elif healthy_count > 0:
            print("   ⚠️  Some servers are down - system partially operational")
        else:
            print("   ❌ All servers are down - system unavailable")
        
        # Data Consistency
        consistency_status = self.results['data_consistency'].get('status', 'UNKNOWN')
        print(f"\n📊 DATA CONSISTENCY: {consistency_status}")
        
        if consistency_status == 'CONSISTENT':
            print("   ✅ All servers return identical data")
        else:
            issues = self.results['data_consistency'].get('issues', [])
            print(f"   ❌ Found {len(issues)} consistency issues")
        
        # Concurrency Performance
        if 'concurrency_test' in self.results:
            concurrency = self.results['concurrency_test']
            success_rate = concurrency['success_rate']
            
            print(f"\n🔄 CONCURRENCY HANDLING: {success_rate:.1f}% success rate")
            
            if success_rate >= 95:
                print("   ✅ Excellent concurrency handling")
            elif success_rate >= 80:
                print("   ⚠️  Good concurrency handling with some issues")
            else:
                print("   ❌ Poor concurrency handling - significant issues detected")
                
            print(f"   📈 Average Response Time: {concurrency['avg_response_time']:.3f}s")
            print(f"   📈 Max Response Time: {concurrency['max_response_time']:.3f}s")
        
        # Overall Assessment
        print(f"\n🎯 OVERALL ASSESSMENT:")
        
        if healthy_count == total_servers and consistency_status == 'CONSISTENT':
            if 'concurrency_test' in self.results and self.results['concurrency_test']['success_rate'] >= 95:
                print("   🟢 EXCELLENT - System is ready for production multi-server deployment")
            else:
                print("   🟡 GOOD - System works but has some concurrency issues")
        elif healthy_count > 0:
            print("   🟡 PARTIAL - System works but has availability or consistency issues")
        else:
            print("   🔴 CRITICAL - System has major issues and needs attention")
        
        # Recommendations
        print(f"\n💡 RECOMMENDATIONS:")
        
        if healthy_count < total_servers:
            print("   - Fix server startup issues for unhealthy instances")
            
        if consistency_status != 'CONSISTENT':
            print("   - Investigate data synchronization issues")
            
        if 'concurrency_test' in self.results:
            concurrency = self.results['concurrency_test']
            if concurrency['success_rate'] < 95:
                print("   - Review distributed locking mechanism")
                print("   - Check database connection pool settings")
                print("   - Monitor Redis lock timeouts")
        
        print("   - Implement health check endpoints")
        print("   - Add load balancer for better request distribution")
        print("   - Set up monitoring and alerting")
        print("   - Consider database read replicas for better performance")

def main():
    diagnostic = MultiServerDiagnostic()
    
    print("🚀 Starting Multi-Server Diagnostic for Order Management System")
    print("=" * 60)
    
    # Run all diagnostic tests
    diagnostic.test_server_health()
    diagnostic.test_data_consistency()
    diagnostic.simulate_concurrent_purchases()
    diagnostic.check_inventory_integrity()
    
    # Generate comprehensive report
    diagnostic.generate_report()
    
    print(f"\n🏁 Diagnostic complete!")

if __name__ == "__main__":
    main() 