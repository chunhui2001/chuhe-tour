#!/usr/bin/env bash

ng build --prod

cp -rf ./src/assets/* ../src/main/resources/static/assets/

cp ./dist/styles.*.bundle.css ../src/main/resources/static/angular/styles.bundle.css
cp ./dist/inline.*.bundle.js ../src/main/resources/static/angular/inline.bundle.js
cp ./dist/polyfills.*.bundle.js ../src/main/resources/static/angular/polyfills.bundle.js
cp ./dist/main.*.bundle.js ../src/main/resources/static/angular/main.bundle.js
