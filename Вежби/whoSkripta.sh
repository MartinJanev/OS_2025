#!/bin/bash


# Get the list of logged-in users, filter usernames with exactly 6 digits, remove duplicates, and count them

who | awk '{print $1}' | grep -E '^[0-9]{6}$' | sort | uniq | wc -l
