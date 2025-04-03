#!/bin/bash

#Проверка за тоа дали се праќа аргумент при повикување на скриптата
# $0 претставува име на скриптата

# Проверка дали е внесен точно еден аргумент при повикување на скриптата
if [ $# -ne 1 ]; then
    echo "Usage: $0 <pathToCSVFile>"
    echo "Example: $0 /path/to/file_name.csv"
    echo ""
    echo "Explanation:"
    echo "  - This script analyzes a CSV file containing student exam scores."
    echo "  - You need to provide the path to the CSV file as the only argument when running the script."
    echo "  - The CSV file should have the following format:"
    echo "      id,name,math,science,english,history"
    echo "      1,John Doe,85,90,78,88"
    echo "      2,Jane Smith,92,87,85,91"
    echo "      ..."
    echo "  - Each row represents a student, with their scores in Math, Science, English, and History."
    echo "  - The script will calculate averages, find the highest and lowest scores, and display the results."
    echo ""
    echo "Please provide a valid CSV file path and try again."
    exit 1
fi

FILE="$1"

if [ ! -f "$FILE" ]; then
    echo "Error: You have provided a file '$FILE', which does not exist. In the future, you should provide a valid file."
    exit 1
fi

#Вкупен број на студенти
total_students=0

#Вкупен број на поени од предмет
math_sum=0
science_sum=0
english_sum=0
history_sum=0

#Број на студенти по предмет
math_count=0
science_count=0
english_count=0
history_count=0

# Екстреми по предмет
# Највисоки поени од 0 па нагоре би одело идеално
# Најниски поени од 150, за веднаш да се знае дека ќе се обнови
highest_in_math=0
lowest_in_math=150

highest_in_science=0
lowest_in_science=150

highest_in_english=0
lowest_in_english=150

highest_in_history=0
lowest_in_history=150

#Име на студент со екстремни поени
highest_math_name=""
highest_science_name=""
highest_english_name=""
highest_history_name=""
lowest_math_name=""
lowest_science_name=""
lowest_english_name=""
lowest_history_name=""

# Овој дел служи за читање на CSV датотеката ред по ред
# Првиот ред е header - треба да го игнорираме

# Променлива за проверка дали сме на првиот ред
flag_r1=true

while IFS=, read -r id name math science english history; do
    # Проверка дали сме на првиот ред
    if $flag_r1; then
        flag_r1=false
        continue
    fi

    # Зголемување на бројот на студенти
    total_students=$((total_students + 1))

    # Зголемување на вкупниот број на поени од предмет
    math_sum=$((math_sum + math))
    science_sum=$((science_sum + science))
    english_sum=$((english_sum + english))
    history_sum=$((history_sum + history))

    # Зголемување на бројот на студенти по предмет
    math_count=$((math_count + 1))
    science_count=$((science_count + 1))
    english_count=$((english_count + 1))
    history_count=$((history_count + 1))

    # Проверка за највисоки и најниски поени и обновување на имињата
    if [ $math -gt $highest_in_math ]; then
        highest_in_math=$math
        highest_math_name=$name
    fi

    if [ $math -lt $lowest_in_math ]; then
        lowest_in_math=$math
        lowest_math_name=$name
    fi

    if [ $english -gt $highest_in_english ]; then
        highest_in_english=$english
        highest_english_name=$name
    fi

    if [ $english -lt $lowest_in_english ]; then
        lowest_in_english=$english
        lowest_english_name=$name
    fi

    if [ $history -gt $highest_in_history ]; then
        highest_in_history=$history
        highest_history_name=$name
    fi

    if [ $history -lt $lowest_in_history ]; then
        lowest_in_history=$history
        lowest_history_name=$name
    fi

    if [ $science -gt $highest_in_science ]; then
        highest_in_science=$science
        highest_science_name=$name
    fi

    if [ $science -lt $lowest_in_science ]; then
        lowest_in_science=$science
        lowest_science_name=$name
    fi

done < "$FILE"
# Пренасочување на содржината на датотеката специфицирана со променливата FILE 
# како влез во јамката. Ова значи читање на секоја линија од датотеката, ќе се обработува.

echo "Exam Scores Analysis"
echo "-------------------"
echo "Total Number of Students: $total_students"
echo ""
echo "Subject Averages:"


echo "  Math:     $(awk "BEGIN {printf \"%.2f\", $math_sum / $math_count}")"
echo "  Science:  $(awk "BEGIN {printf \"%.2f\", $science_sum / $science_count}")"
echo "  English:  $(awk "BEGIN {printf \"%.2f\", $english_sum / $english_count}")"
echo "  History:  $(awk "BEGIN {printf \"%.2f\", $history_sum / $history_count}")"
echo ""
echo "Subject Extreme Performers:"
echo " Math - Highest: $highest_math_name (Score: $highest_in_math), Lowest: $lowest_math_name (Score: $lowest_in_math)"
echo " Science - Highest: $highest_science_name (Score: $highest_in_science), Lowest: $lowest_science_name (Score: $lowest_in_science)"
echo " English - Highest: $highest_english_name (Score: $highest_in_english), Lowest: $lowest_english_name (Score: $lowest_in_english)"
echo " History - Highest: $highest_history_name (Score: $highest_in_history), Lowest: $lowest_history_name (Score: $lowest_in_history)"
