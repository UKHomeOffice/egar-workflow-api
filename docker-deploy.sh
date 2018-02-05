#!/bin/sh
NAME="${1}"
version=$(./scripts/bash/get-site-version.sh)

docker login -u="ukhomeofficedigital+egar_robot" -p=${DOCKER_PASSWORD} quay.io
docker tag $NAME:$version pipe.egarteam.co.uk/$NAME:$version
docker push pipe.egarteam.co.uk/$NAME:$version

docker tag $NAME:$version pipe.egarteam.co.uk/$NAME:latest
docker push pipe.egarteam.co.uk/$NAME:latest
