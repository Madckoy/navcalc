#!/bin/bash
echo "Building navcalc..."
mvn clean package

echo "Running navcalc..."
java -jar target/navcalc-jar-with-dependencies.jar