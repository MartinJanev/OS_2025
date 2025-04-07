#!/bin/bash

kola=`ls -r -l $1 | awk '{print $10}'`

rm siteKola.txt

touch siteKola.txt

rm teams.txt

touch teams.txt

for kolo in $kola
do
        rez=`ls -l $1$kolo | awk '{print $10}'`
        path=`echo "$1$kolo/$rez" | tr -d '\n'`
        cat $path >> siteKola.txt

done

kola=`cat siteKola.txt | grep -v ^l`
teams=`cat siteKola.txt | awk -F, '{print $2}' | sort | uniq`


IFS=$'\n'

for team in $teams
do
        count=0
        for kolo in $kola
        do
                t=`echo $kolo | awk -F, '{print $2}'`
                if [ $t == $team ]
                then
                        count=`expr $count + 1`
                fi
        done
        if [ $count -gt 0 ]
        then
                echo $count $team
        fi
done
