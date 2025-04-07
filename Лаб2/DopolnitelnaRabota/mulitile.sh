#!/bin/bash

max_perform(){
    awk -F',' 'NR>1 {sum = $3+$4+$5+$6; if (sum>max) {max=sum;name=$2}} END {print name, max}' "$1"
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
    }' "$2"
}

sum(){
    awk -F',' -v col="$2" 'NR>1 {sum+=$col;count++} END {printf "%.2f", sum/count}' "$1"
}

min(){
    awk -F',' -v col="$2" 'NR>1 {if ($col<min || min=="") {min=$col;name=$2}} END {print name, min}' "$1"
}

max(){
    awk -F',' -v col="$2" 'NR>1 {if ($col>max) {max=$col;name=$2}} END {print name, max}' "$1"
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
    NR > 1 {print "\t"$2, "("$col")" | "sort -k2 -n" }
    ' "$2"
}

if [ $# -lt 1 ]
then
    echo ""
    echo "Usage: $0 <pathToFile> <pathToFile2> ..."
    echo "Example: $0 csvFile.csv csvFile2.csv"
    echo ""
    exit 1
fi

all_files=("$@")
output="output2.txt"

for file in "${all_files[@]}"
do
    if [ ! -f "$file" ]
    then
        echo "Error: Non-existent file"
        echo "Please provide an existing .csv file or create it"
        exit 1
    fi

    if [[ "$file" != *.csv ]]
    then
        echo "Error: The file provided is not a .csv file"
        echo "Please provide a .csv file"
        exit 1
    fi
done

total_students=0
math_total=0
science_total=0
english_total=0
history_total=0
math_count=0
science_count=0
english_count=0
history_count=0

for file in "${all_files[@]}"
do
    count=$(awk 'NR>1 {count++} END {print count}' "$file")
    total_students=$((total_students + count))

    math_total=$(awk -v total="$math_total" -F',' 'NR>1 {total+=$3} END {print total}' "$file")
    science_total=$(awk -v total="$science_total" -F',' 'NR>1 {total+=$4} END {print total}' "$file")
    english_total=$(awk -v total="$english_total" -F',' 'NR>1 {total+=$5} END {print total}' "$file")
    history_total=$(awk -v total="$history_total" -F',' 'NR>1 {total+=$6} END {print total}' "$file")

    math_count=$(awk -v count="$math_count" -F',' 'NR>1 {count++} END {print count}' "$file")
    science_count=$(awk -v count="$science_count" -F',' 'NR>1 {count++} END {print count}' "$file")
    english_count=$(awk -v count="$english_count" -F',' 'NR>1 {count++} END {print count}' "$file")
    history_count=$(awk -v count="$history_count" -F',' 'NR>1 {count++} END {print count}' "$file")
done

math_avg=$(awk -v total="$math_total" -v count="$math_count" 'BEGIN {printf "%.2f", total/count}')
science_avg=$(awk -v total="$science_total" -v count="$science_count" 'BEGIN {printf "%.2f", total/count}')
english_avg=$(awk -v total="$english_total" -v count="$english_count" 'BEGIN {printf "%.2f", total/count}')
history_avg=$(awk -v total="$history_total" -v count="$history_count" 'BEGIN {printf "%.2f", total/count}')

maxperformer=$(max_perform "${all_files[@]}")

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
    grade_distribution "$subject" "${all_files[@]}"
    echo ""
    echo ""
    sort_subject "$subject" "${all_files[@]}"
    echo ""
    echo "----------------------------------------------------------------------"
} > "$output"

echo "Output written to $output"
