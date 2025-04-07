#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "==============================="
    if [ "$#" -eq 1 ]; then
        echo "Error: Either JSON file or CSV is missing!"
        echo "Make sure you add all files correctly"
        echo ""
    fi
    echo "Usage: $0 input.json output.csv"
    echo "IMPORTANT: You need to create both files and fill input.json"
    exit 1
fi

input_file=$1
output_file=$2

if [ ! -f "$input_file" ]; then
    echo "Error: Input file '$input_file' does not exist"
    exit 1
fi

if [ ! -r "$input_file" ]; then
    echo "Error: Input file '$input_file' is not readable"
    echo "Option: do chmod +r '$input_file' and try again"
    exit 1
fi

durations=$(grep -o '"duration": *"[^"]*"' "$input_file" | sed 's/"duration": "//;s/"//')

total_duration=0
count=0

convert_to_seconds() {
    local t=$1
    local t_min=$(echo "$t" | cut -d":" -f1)
    local t_sec=$(echo "$t" | cut -d":" -f2)
    echo $((t_min * 60 + t_sec))
}

for d in $durations; do
    duration_in_seconds=$(convert_to_seconds "$d")
    total_duration=$((total_duration + duration_in_seconds))
    count=$((count + 1))
done

if [ "$count" -eq 0 ]; then
    echo "Error: No valid records."
    exit 1
fi

avg_dur=$((total_duration / count))

echo "id,filepath,filesize,is_longer" > "$output_file"

id=0
grep -o '{[^}]*}' "$input_file" | while read record; do
    filepath=$(echo "$record" | grep -o '"filepath": *"[^"]*"' | sed 's/"filepath": "//;s/"//')
    duration=$(echo "$record" | grep -o '"duration": *"[^"]*"' | sed 's/"duration": "//;s/"//')
    filesize=$(echo "$record" | grep -o '"filesize": *"[^"]*"' | sed 's/"filesize": "//;s/"//')

    duration_s=$(convert_to_seconds "$duration")
    is_longer=$((duration_s > avg_dur ? 1 : 0))

    id=$((id + 1))

    echo "$id,$filepath,$filesize,$is_longer" >> "$output_file"
done

echo "JSON data sent to $output_file"
