#!/bin/bash

max_perform(){
	awk -F',' 'NR>1 {sum = $3+$4+$5+$6; if (sum>max) {max=sum;name=$2}} END {print name, max}' "$file"
}

grade_distribution(){
	awk -F',' -v subject="$1" '
	BEGIN {
		subjects["Math"] = 3;
		subjects["Science"] = 4;
		subjects["English"] = 5;
		subjects["History"] = 6;
		if (!(subject in subjects)) {
			print "Invalid subject. Please enter one of: Math, Science, English, History.";
			exit 1;
		}
		col = subjects[subject];
	}
	NR > 1 {
		if ($col >= 90) {grade="A"}
		else if ($col >= 80) {grade="B"}
		else if ($col >= 70) {grade="C"}
		else if ($col >= 60) {grade="D"}
		else if ($col >= 50) {grade="E"}
		else {grade="F"}
		counter[grade]++;
	}
	END {
		print "Grade Distribution - " subject ":";
		for (grade in counter) {
			printf "  %s: %d\n", grade, counter[grade];
		}
	}' "$file"
}

sum(){
	awk -F',' -v col="$2" 'NR>1 {sum+=$col;count++} END {printf "%.2f", sum/count}' "$file"
}

min(){
	awk -F, -v col="$2" 'NR>1 {if ($col<min || min=="") {min=$col;name=$2}} END {print name, min}' "$file"
}

max(){
	awk -F, -v col="$2" 'NR>1 {if ($col>max) {max=$col;name=$2}} END {print name, max}' "$file"
}

extreme(){
	echo ""
	echo "Subjects Extreme Performers:"
	echo "  Math - Highest: $(echo "$max_math" | awk '{print $1,$2}') (Score: $(echo "$max_math" | awk '{print $3}')), Lowest: $(echo "$min_math" | awk '{print $1,$2}') (Score: $(echo "$min_math" | awk '{print $3}'))"
	echo "  Science - Highest: $(echo "$max_science" | awk '{print $1,$2}') (Score: $(echo "$max_science" | awk '{print $3}')), Lowest: $(echo "$min_science" | awk '{print $1,$2}') (Score: $(echo "$min_science" | awk '{print $3}'))"
	echo "  English - Highest: $(echo "$max_english" | awk '{print $1,$2}') (Score: $(echo "$max_english" | awk '{print $3}')), Lowest: $(echo "$min_english" | awk '{print $1,$2}') (Score: $(echo "$min_english" | awk '{print $3}'))"
	echo "  History - Highest: $(echo "$max_history" | awk '{print $1,$2}') (Score: $(echo "$max_history" | awk '{print $3}')), Lowest: $(echo "$min_history" | awk '{print $1,$2}') (Score: $(echo "$min_history" | awk '{print $3}'))"
	echo ""
}

sort_subject(){
	awk -F',' -v subject="$1" '
	BEGIN {
		subjects["Math"] = 3;
		subjects["Science"] = 4;
		subjects["English"] = 5;
		subjects["History"] = 6;
		if (!(subject in subjects)) {
			print "Invalid subject. Please enter one of: Math, Science, English, History.";
			exit 1;
		}
		col = subjects[subject];
		print "Sorted Students by " subject ":";
	}
	NR > 1 {print "\t"$2, "\t""("$col")" | "sort -k2 -n"}
	' "$file"
}

if [ $# -ne 1 ]
then
	echo ""
	echo "Usage: $0 <pathToFile>"
	echo "Example: $0 csvFile.csv"
	echo ""
	exit 1
fi

file=$1
output="output.txt"

if [ ! -f "$file" ]
then
	echo "Error: Non - existent file"
	echo "Please provide an existing .csv file or create it"
	exit 1
fi

if [[ "$file" != *.csv ]]
then
	echo "Error: The file provided is not a .csv file"
	echo "Please provied a .csv file"
	exit 1
fi


total_students=$(awk 'NR>1 {count++} END {print count}' "$file")

math_avg=$(sum "Math" 3)
science_avg=$(sum "Science" 4)
english_avg=$(sum "English" 5)
history_avg=$(sum "History" 6)


max_math=$(max "Math" 3)
max_science=$(max "Science" 4)
max_english=$(max "English" 5)
max_history=$(max "History" 6)


min_math=$(min "Math" 3)
min_science=$(min "Science" 4)
min_english=$(min "English" 5)
min_history=$(min "History" 6)

maxperformer=$(max_perform)

read -p "Enter subject: " subject
{
	echo "Exam Scores Analysis"
	echo "-------------------"
	echo "Total number of Students: $total_students"
	echo ""
	echo "  Math:    $math_avg"
	echo "  Science: $science_avg"
	echo "  English: $english_avg"
	echo "  History: $history_avg"

	extreme

	echo "Top Overall Performer: "
	echo "  $(echo "$maxperformer" | awk '{print $1,$2}') (Total Score: $(echo "$maxperformer" | awk '{print $3}'))"
	echo ""

	subject=$(echo "$subject" | xargs)
	grade_distribution "$subject"
	echo ""
	echo ""
	sort_subject "$subject"
	echo ""
	echo "----------------------------------------------------------------------"
	echo ""
} >> "$output"

echo "Output written to $output"