#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 <STATUS> <MONTH>"
    exit 1
fi

STATUS="$1"
MONTH="$2"
FILE="system_logs.tsv"


if [ ! -f "$FILE" ]
then
    echo "Log file $FILE not found!"
    exit 1
fi

if [[ ! "$MONTH" =~ ^[0-9]{4}-[0-9]{2}$ ]]
then
    echo "Invalid format of month. Please use YYYY-MM"
    exit 1
fi

COUNT=$(awk -F'\t' -v s="$STATUS" -v m="$MONTH" 'nr>1 && $3==s && $1 ~ M {c++} END{print c+0}' "$FILE")

COMMON_IP=$(awk -F'\t' -v s="$STATUS" 'NR>1 && $3==s {a[$4]++} END{max=0; for (ip in a) if (a[ip]>max) {max=a[ip]; best=ip} print best}' "$FILE")

echo "Number of logs with status '$STATUS' in month $MONTH: $COUNT"
echo "Most common IP  adress for logs with status '$STATUS': $COMMON_IP"
echo ""
echo "Count per status for month $MONTH:"
awk -F'\t' -v m="$MONTH" '
NR>1 && $1 ~ m {count[$3]++}
END {
    status["DEBUG"]; status["INFO"]; status["WARN"]; status["ERROR"];
    for (s in status){
        c = (s in count) ? count[s] : 0;
        printf "    %-6s: %d\n", s, c
    }
}' "$FILE"