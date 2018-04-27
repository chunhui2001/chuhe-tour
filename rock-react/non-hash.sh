#!/usr/bin/env bash

# cat build/asset-manifest.json | grep -o -E "\.[0-9a-z]{8}\." | awk '{print $0}'
cat build/asset-manifest.json | grep -E "\.[0-9a-z]{8}\."


### rename
# find ./build/static -type f | grep -E "\.[0-9a-z]{8}\." | rename "s/.[a-z0-9]{8}././g"                      # linux but mac not work
# brew install rename
find ./build/static -type f  | grep -E "\.[0-9a-z]{8}\." | awk '{system("rename s/.[a-z0-9]{8}././g "$0)}'     # compatible linux and mac 
   
mv ./build/service-worker.js ./build/static
mv ./build/index.html ./build/static

### replace hash contents
cat build/asset-manifest.json | grep -o -E "\.[0-9a-z]{8}\." | \
   awk '{system("find ./build/static -exec sed -i \"\" -E \"s/\\" $0 "/./g\" {} \\; 2>/dev/null")}'

mv ./build/static/service-worker.js ./build 
mv ./build/static/index.html ./build 

# docker ps -a | grep vigorous_allen | awk '{split($0,a," "); print a[1]}'