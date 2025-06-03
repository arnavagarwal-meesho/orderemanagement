#!/bin/bash

# Build the application
./mvnw clean package -DskipTests

# Start three instances on different ports
SERVER_PORT=8080 java -jar target/orderemanagement-0.0.1-SNAPSHOT.jar &
echo "Started server on port 8080"

SERVER_PORT=8081 java -jar target/orderemanagement-0.0.1-SNAPSHOT.jar &
echo "Started server on port 8081"

SERVER_PORT=8082 java -jar target/orderemanagement-0.0.1-SNAPSHOT.jar &
echo "Started server on port 8082"

# Wait for all background processes
wait 