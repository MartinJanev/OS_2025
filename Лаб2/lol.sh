#!/bin/bash

if [ "$#" -lt 3 ]
then
    echo "ERROR: Not enough arguments given, need 3"
    exit 1
fi
if [ "$#" -gt 3 ]
then
    echo "ERROR: Too many arguments given, need 3"
    exit 1
fi

min="$1"
max="$2"
dir="$3"

getfiles(){
    min="$1" max="$2" directory="$3"
    
    ls -l | tail -n +2 | while read -r line
    do    
        size=$(echo "$line" | awk '{print $5}')
        date=$(echo "$line" | awk '{print $7}') 
        name=$(echo "$line" | awk '{print $9}')
        
        if [[ "$date" -ge "$min" ]] && [[ "$date" -le "$max" ]]
        then
            if [[ "$size" -gt 150 ]] && [[ "$name" == *.txt ]]
            then
            
                wordCount=$(grep -0 -i echo $name | wc -l)
                
                test="236072_${wordCount}.txt"
                
                other="$dir$test"
                
                echo "$other"
                
                if [ -e "$other" ]
                then
                    other="236072_$wordCount"
                    count=$(ls -l "$dir" | awk '{ if($9 -eq "$other[0-9][0-9].txt") {print}}' | wc -l )
                    count=$(("$count" - 1))
                    filename="236072_${wordCount}${count}.txt"
                    touch "$filename"
                else
                    filename="236072_${wordCount}.txt"
                touch "$filename"
                fi
                
                cat "$name" >> "$filename"
                
                cp "$filename" "$dir"
                
                rm "$filename"
                
            fi
        fi
        
    done
}

getfiles "$min" "$max" "$dir"