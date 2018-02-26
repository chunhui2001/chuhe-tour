#!/usr/bin/env bash

ng build --prod

cp -rf ./dist/assets/* ../src/main/resources/static/assets/

cp dist/fontawesome-webfont.*.eot ../src/main/resources/static/assets/fonts/fontawesome-webfont.eot
cp dist/fontawesome-webfont.*.svg ../src/main/resources/static/assets/fonts/fontawesome-webfont.svg
cp dist/fontawesome-webfont.*.woff2 ../src/main/resources/static/assets/fonts/fontawesome-webfont.woff2
cp dist/fontawesome-webfont.*.ttf ../src/main/resources/static/assets/fonts/fontawesome-webfont.ttf
cp dist/fontawesome-webfont.*.woff ../src/main/resources/static/assets/fonts/fontawesome-webfont.woff

cp ./dist/styles.*.bundle.css ../src/main/resources/static/angular/styles.bundle.css
cp ./dist/inline.*.bundle.js ../src/main/resources/static/angular/inline.bundle.js
cp ./dist/polyfills.*.bundle.js ../src/main/resources/static/angular/polyfills.bundle.js
cp ./dist/main.*.bundle.js ../src/main/resources/static/angular/main.bundle.js

sed -i -e "s/fontawesome-webfont.[0-9a-z]*.eot?/\/assets\/fonts\/fontawesome-webfont.eot?/" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.[0-9a-z]*.svg?/\/assets\/fonts\/fontawesome-webfont.svg?/" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.[0-9a-z]*.woff2?/\/assets\/fonts\/fontawesome-webfont.woff2?/" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.[0-9a-z]*.ttf?/\/assets\/fonts\/fontawesome-webfont.ttf?/" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.[0-9a-z]*.woff?/\/assets\/fonts\/fontawesome-webfont.woff?/" ../src/main/resources/static/angular/styles.bundle.css

rm -rf ../src/main/resources/static/angular/styles.bundle.css-e
