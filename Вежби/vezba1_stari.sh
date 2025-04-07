#!/bin/bash

echo "Hello"


i=0

output1="temp/x1.txt"
output2="temp/x2.txt"
output3="temp/x3.txt"

current1=$(find . -type f -mtime -1 > $output1)
#current2=$(find . | grep "/[a-z].*\.txt$" > $output2 | while read file; do mv "$file" new_dir done)
2   