#!/bin/bash
# Build script for Render.com

echo "Starting build process..."

# Clean and package the application
./mvnw clean package -DskipTests -Pproduction

echo "Build completed successfully!"