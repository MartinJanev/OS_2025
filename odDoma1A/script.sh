#!/bin/bash

if [ $# -lt 3 ]; then
	echo "At least 3 exe times needed."
	exit 1
fi


sum=$(( $1 + $2 + $3 ))
avg=$(( (sum/3) * 60 ))


echo "Average execution time: $avg"
echo "Count of executions: $#"

if [ $# -ge 5 ]; then
	echo "The testing is done"
else
	echo "More testing is needed"
fi
