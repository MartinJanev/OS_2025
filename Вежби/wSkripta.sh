#!/bin/bash

file="$4"

data="temp.txt"
touch "$data"
ip="$3"
timeAbove="$2"
#echo "$ip"

moreThanTime(){
    user=$1
    timeS=$(awk -F"[()]" '{split($2, time, ":"); sum = time[1]*60+time[2]; print sum}' <<< "$user")
    echo "$timeS"
}

datas=$(awk '{if($3 ~ /^'$ip'\./) { gsub(/[[:space:]]+/, " "); print }}' ListaKorisnici.txt | grep -v 'still' | grep "$1")

>$data

while read -r user; do
    time=$(moreThanTime "$user")
    if [ "$time" -gt "$timeAbove" ]; then
        echo "$user" >> "$data"
    fi
done <<< "$datas"