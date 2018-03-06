#!/usr/bin/env bash

# https://segmentfault.com/a/1190000010981919

ng build --prod --aot --vc --cc --dop --build-optimizer --stats-json --vendor-chunk true --output-hashing none
#ng build --prod --aot --vc --cc --dop --build-optimizer --stats-json --vendor-chunk false --output-hashing none

# npm run bundle-report

cp -rf ./dist/assets/* ../src/main/resources/static/assets/

cp -rf dist/fontawesome-webfont.* ../src/main/resources/static/angular/
cp -rf dist/roboto-v15-latin-regular.* ../src/main/resources/static/angular/
cp -rf dist/*.js ../src/main/resources/static/angular/
cp -rf dist/*.css ../src/main/resources/static/angular/

