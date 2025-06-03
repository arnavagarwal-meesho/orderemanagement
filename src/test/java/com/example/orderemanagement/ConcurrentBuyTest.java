package com.example.orderemanagement;

import com.example.orderemanagement.dto.BuyProductRequestDto;
import com.example.orderemanagement.dto.CustomerLoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentBuyTest {
    private final String[] SERVER_URLS = {
        "http://localhost:8080",
        "http://localhost:8081",
        "http://localhost:8082"
    };
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long customerId;
    private Long productId;

    @BeforeEach
    public void setup() throws Exception {
        // Login to get customer ID
        CustomerLoginRequestDto loginRequest = new CustomerLoginRequestDto();
        loginRequest.setEmail("test2@example.com");
        loginRequest.setPassword("password123");

        String loginResponse = restTemplate.postForObject(
            SERVER_URLS[0] + "/api/customers/login",
            new HttpEntity<>(loginRequest),
            String.class
        );

        // Extract customer ID from response
        customerId = objectMapper.readTree(loginResponse).get("id").asLong();
        
        // Get product ID from the products list
        String productsResponse = restTemplate.getForObject(
            SERVER_URLS[0] + "/api/customers/products",
            String.class
        );
        
        productId = objectMapper.readTree(productsResponse)
            .get(0)  // Get first product
            .get("id")
            .asLong();
    }

    @Test
    public void testConcurrentProductPurchase() throws Exception {
        // Create a buy request
        BuyProductRequestDto requestDto = new BuyProductRequestDto();
        requestDto.setCustomerId(customerId);
        requestDto.setProductId(productId);
        requestDto.setQuantity(1);

        // Convert request to JSON string
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity
        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        // Send 10 concurrent requests distributed across ALL THREE SERVERS
        for (int i = 0; i < 10; i++) {
            final int index = i;
            futures.add(executor.submit(() -> {
                try {
                    // Round-robin between server instances
                    String serverUrl = SERVER_URLS[index % SERVER_URLS.length];
                    String url = serverUrl + "/api/customers/products/buy";
                    
                    System.out.println("Sending request " + (index + 1) + " to: " + url);
                    String response = restTemplate.postForObject(url, request, String.class);
                    System.out.println("Response from request " + (index + 1) + " (" + serverUrl + "): " + response);
                } catch (Exception e) {
                    System.out.println("Error in request " + (index + 1) + " (" + SERVER_URLS[index % SERVER_URLS.length] + "): " + e.getMessage());
                }
            }));
        }

        // Wait for all requests to complete
        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();
    }
} 