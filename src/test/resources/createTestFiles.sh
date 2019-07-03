#!/bin/bash

 

for filename in {1..200}

do

        SIZE=$((0+RANDOM%3))      

	echo "filename = $filename.txt and size = $SIZE"

	truncate -s ${SIZE}G $filename.txt

done
