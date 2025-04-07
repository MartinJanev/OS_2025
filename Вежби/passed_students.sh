#!/bin/bash

if [ "$#" -lt 1 ];then
	echo " Error: No arguments after "$0""
	echo ""
	echo " Usage: ./passed_studnets.sh <file.csv> (file is the name of your file)"
	exit 1
fi

if [ "$#" -gt 1 ];then
	echo " Error: More than one argument after "$0""
	echo ""
	echo " Usage: ./passed_studnets.sh <file.csv> (file is the name of your file)"
	exit 1
fi

file=$1

if [ "$file" == "*.csv" ];then

	echo " Error: File type not supported"
	exit 1
fi

output="passed_${file}"


data=$(awk -F"," 'NR > 1 && $4 > 5 {print $0}' "$file" | sort -t',' -k4,4 -nr)
avg=$(echo "$data" | awk -F"," '{sum+=$4; count++} END {if (count > 0) print "Passed Student average points: " sum/count; else print 0}')
{
    echo "$data"
    echo "$avg"
} > "$output"

echo "Output has been written to $output"
