#!/bin/bash

if [ $# -ne 2 ]
then
    echo "Usage: $0 <STATUS> <MONTH in YYYY-MM>"
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
    echo "Invalid month format. Please use YYYY-MM"
    exit 1
fi

# Count logs with matching STATUS and MONTH
COUNT=$(awk -F'\t' -v s="$STATUS" -v m="$MONTH" 'NR>1 && $3==s && $1 ~ m {c++} END{print c+0}' "$FILE")

# Most common IP for STATUS
COMMON_IP=$(awk -F'\t' -v s="$STATUS" 'NR>1 && $3==s {ips[$4]++} END{max=0; for (ip in ips) if (ips[ip]>max) {max=ips[ip]; best=ip} print best}' "$FILE")

# Print formatted output
echo "Number of logs with status '$STATUS' in month $MONTH: $COUNT"
echo "Most common IP address for logs with status '$STATUS': $COMMON_IP"
echo ""

echo "Count per status for month $MONTH:"
awk -F'\t' -v m="$MONTH" '
NR>1 && $1 ~ m {count[$3]++}
END {
    statuses["DEBUG"]; statuses["INFO"]; statuses["WARN"]; statuses["ERROR"];
    for (s in statuses) {
        c = (s in count) ? count[s] : 0;
        printf "    %-6s: %d\n", s, c
    }
}' "$FILE"

