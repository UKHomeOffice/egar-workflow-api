#!/bin/sh
# Build and deploy the Maven code to S3
 
./scripts/bash/install-egar-parent.sh 8064aaac1efeaefc9cac19fb88adfb5baade5d20
./scripts/bash/install-egar-common.sh e035a55a26a0e9d4b078571b0b75ae2a70338cae
 
mvn -B compile
mvn -B test
mvn -B package -DskipTests
