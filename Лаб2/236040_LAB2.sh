#!/bin/bash


max(){
	subject=$1
	column=$2
	awk -F',' -v col="$column" 'NR>1 {if ($col > max) {max=$col; name=$2}} END {print name, max}' "$file"
}

min(){
	subject=$1
	column=$2
	awk -F',' -v col="$column" 'NR>1 {if ($col < min || min == "") {min=$col; name=$2}} END {print name, min}' "$file"
}

sum(){
	subject=$1
	column=$2
	awk -F',' -v col="$column" 'NR>1 {sum+=$col; count++} END {printf "%.2f", sum/count}' "$file"
}

if [ $# -ne 1 ]; then
    echo "Usage: $0 <pathToCSVFile>"
    echo "Example: $0 /path/to/file_name.csv"
    exit 1
fi

file="$1"

if [ ! -f "$file" ]
then
	echo "Error: Non-existent file!"
	echo "Please provide a valid file."
	exit 1
fi

if [[ "$file" != *.csv ]]
then
	echo "Error: The file sent as an argument is not a .csv file!"
	echo "Please provide a .csv file."
	exit 1
fi

totalStudents=$(awk 'NR>1 {count++} END {print count}' "$file")

# Функциски повик (функција, предмет за испитување, број на колона)

math=$(sum "Math" 3)
science=$(sum "Science" 4)
english=$(sum "English" 5)
history=$(sum "History" 6)


# Math best and worst
mathHigh=$(max "Math" 3)
mathLow=$(min "Math" 3)

# Science best and worst
scienceHigh=$(max "Science" 4)
scienceLow=$(min "Science" 4)

# English best and worst
englishHigh=$(max "English" 5)
englishLow=$(min "English" 5)

# History best and worst
historyHigh=$(max "History" 6)
historyLow=$(min "History" 6)

echo "Exam Scores Analysis"
echo "-------------------"
echo "Total Number of Students: $totalStudents"
echo ""
echo "Subject Averages:"
echo "  Math:     $math"
echo "  Science:  $science"
echo "  English:  $english"
echo "  History:  $history"
echo ""
echo "Subject Extreme Performers:"
echo "  Math - Highest: $(echo "$mathHigh" | awk '{print $1,$2}') (Score: $(echo "$mathHigh" |awk '{print $3}')), Lowest: $(echo "$mathLow" | awk '{print $1,$2}') (Score: $(echo "$mathLow" |awk '{print $3}'))"
echo "  Science - Highest: $(echo "$scienceHigh" | awk '{print $1,$2}') (Score: $(echo "$scienceHigh" |awk '{print $3}')), Lowest: $(echo "$scienceLow" | awk '{print $1,$2}') (Score: $(echo "$scienceLow" |awk '{print $3}'))"
echo "  English - Highest: $(echo "$englishHigh" | awk '{print $1,$2}') (Score: $(echo "$englishHigh" |awk '{print $3}')), Lowest: $(echo "$englishLow" | awk '{print $1,$2}') (Score: $(echo "$englishLow" |awk '{print $3}'))"
echo "  History - Highest: $(echo "$historyHigh" | awk '{print $1,$2}') (Score: $(echo "$historyHigh" |awk '{print $3}')), Lowest: $(echo "$historyLow" | awk '{print $1,$2}') (Score: $(echo "$historyLow" |awk '{print $3}'))"