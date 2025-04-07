#!/bin/bash

# Проверка дали има влезни аргументи
if [ $# -eq 0 ]; then
    echo "Insert name of file!"
    exit 1
elif [ $# -gt 1 ]; then
    echo "Too many input arguments!"
    exit 1
fi

output_file=$1

# Чистење на излезниот фајл ако веќе постои
> "$output_file"

# Прошетка низ сите .txt фајлови во тековниот директориум
for file in *.csv; do
#for file in *.txt; do
    # Проверка дали фајлот постои и дали корисникот има само дозвола за читање
    if [ -r "$file" ] && [ ! -w "$file" ] && [ ! -x "$file" ]; then
        cat "$file" >> "$output_file"
        echo "" >> "$output_file"  # Додавање празен ред меѓу содржините
    fi
done

echo "Content saved in $output_file"

