#!/bin/bash

IP_ADRESI=`cat last.txt | grep -v still | awk '{print $1, $3, $10}' | \
sed -e 's/(//g' -e 's/)//g' -e 's/:/ /g' | awk '$3>0 || $4>10 {print $2, $1}' | \
sort -n -r | uniq | sed 's/ /|/g' | tr '\n' '|' | awk -F\| '{ for(i=1;i<NF;i+=2) { for(j=i;j<NF;j+=2) { if($i == $j) { if($(i+1) != $(j+1)) {print  $i} }}}}'`
echo "IP adresite koi se pristapeni od poveke korisnici od OS serverot se:"
for line in $IP_ADRESI
do
        echo $line
done
