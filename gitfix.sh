#!/bin/bash
for D in `ls .git/objects/`
do
    len=${#D}
    if [ $len -eq 2 ]
    then
    	for D2 in `ls '.git/objects/'$D`
    	do    		
    		FILESIZE=$(stat -c%s '.git/objects/'$D'/'$D2)    		
    		if [ $FILESIZE -eq 0 ]
    		then
    			echo '.git/objects/'$D'/'$D2
    			echo $FILESIZE
    			rm -rf '.git/objects/'$D'/'$D2
    		fi
    	done
    fi
done
