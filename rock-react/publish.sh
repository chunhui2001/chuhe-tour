#!/usr/bin/env bash

yarn build
./non-hash.sh
mv build/assets build/static 


cp -rf ./build/* ../src/main/resources/static/react/
rsync -az ../src/main/resources/static/react/static/* ../src/main/resources/static/react/
rm -rf ../src/main/resources/static/react/static