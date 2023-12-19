#!/bin/bash

# Set start timestamp three days in the past
THREE_DAYS_IN_MS=$((1000*60*60*24*3))
START=$(($(date +%s) * 1000 - THREE_DAYS_IN_MS))

# Set number of entries per file and total entries
ENTRIES_PER_FILE=10000
TOTAL_ENTRIES=50000
TIMESTAMP=$((START))
TIMESTAMP_INCREMENT=$((THREE_DAYS_IN_MS / TOTAL_ENTRIES))
NUM_FILES=$((TOTAL_ENTRIES / ENTRIES_PER_FILE))
ENTRY_COUNT=0

# Generate the json files
for i in $(seq 1 $NUM_FILES); do

  # Set filename 
  FILE="file$i.json"

  # Write opening bracket
  echo '[' > $FILE

  # Generate entries
  for j in $(seq 1 $ENTRIES_PER_FILE); do


    # Simple message 
    MSG="Entry $ENTRY_COUNT"
    ((ENTRY_COUNT++))

    # Write entry
    echo -e "\t{\"timestamp\": $TIMESTAMP, \"message\": \"$MSG\"}," >> $FILE

    # Increment timestamp
    TIMESTAMP=$((TIMESTAMP + TIMESTAMP_INCREMENT))

  done

  # Remove trailing comma and close bracket
  sed -i '' -e '$ s/.$//' $FILE
  echo ']' >> $FILE

done