#!/usr/bin/env bash

ng build --prod

cp -rf ./src/assets/* ../src/main/resources/static/assets/

cp ./dist/styles.*.bundle.css ../src/main/resources/static/angular/styles.bundle.css
cp ./dist/inline.*.bundle.js ../src/main/resources/static/angular/inline.bundle.js
cp ./dist/polyfills.*.bundle.js ../src/main/resources/static/angular/polyfills.bundle.js
cp ./dist/main.*.bundle.js ../src/main/resources/static/angular/main.bundle.js


cp ./dist/fontawesome-webfont.*.eot ../src/main/resources/static/angular/fontawesome-webfont.eot
cp ./dist/fontawesome-webfont.*.svg ../src/main/resources/static/angular/fontawesome-webfont.svg
cp ./dist/fontawesome-webfont.*.woff2 ../src/main/resources/static/angular/fontawesome-webfont.woff2
cp ./dist/fontawesome-webfont.*.ttf ../src/main/resources/static/angular/fontawesome-webfont.ttf
cp ./dist/fontawesome-webfont.*.woff ../src/main/resources/static/angular/fontawesome-webfont.woff

sed -i -e "s/fontawesome-webfont.*.eot/fontawesome-webfont.eot/g" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.*.svg/fontawesome-webfont.svg/g" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.*.woff2/fontawesome-webfont.woff2/g" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.*.ttf/fontawesome-webfont.ttf/g" ../src/main/resources/static/angular/styles.bundle.css
sed -i -e "s/fontawesome-webfont.*.woff/fontawesome-webfont.woff/g" ../src/main/resources/static/angular/styles.bundle.css
