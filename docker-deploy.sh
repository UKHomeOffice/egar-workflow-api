#!/bin/sh
NAME="${1}"
version=$(./scripts/bash/get-site-version.sh)

docker tag $NAME:$version pipe.egarteam.co.uk/$NAME:$version
docker push pipe.egarteam.co.uk/$NAME:$version

docker tag $NAME:$version pipe.egarteam.co.uk/$NAME:latest
docker push pipe.egarteam.co.uk/$NAME:latest
