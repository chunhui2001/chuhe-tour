#!/usr/bin/env bash

yarn build
./non-hash.sh
mv build/assets build/static 