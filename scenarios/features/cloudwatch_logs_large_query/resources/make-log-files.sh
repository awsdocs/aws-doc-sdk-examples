#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

FIVE_MINUTES_IN_MS=$((1000*60*5))
START=$(($(date +%s) * 1000))

# Set number of entries per file and total entries
ENTRIES_PER_FILE=10000
TOTAL_ENTRIES=50000
TIMESTAMP=$((START))
TIMESTAMP_INCREMENT=$((FIVE_MINUTES_IN_MS / TOTAL_ENTRIES))
NUM_FILES=$((TOTAL_ENTRIES / ENTRIES_PER_FILE))
ENTRY_COUNT=0

# Generate the json files
for i in $(seq 1 "$NUM_FILES"); do

  # Set filename 
  FILE="file$i.json"

  # Write opening bracket
  echo '[' > "$FILE"

  # Generate entries
  for j in $(seq 1 "$ENTRIES_PER_FILE"); do

    # Output the timestamp of the first entry in the first file.
    if [ "$i" -eq 1 ] && [ "$j" -eq 1 ]; then
      echo "export QUERY_START_DATE=$TIMESTAMP";
    fi

    # Output the timestamp of the last entry in the last file.
    if [ "$i" -eq "$NUM_FILES" ] && [ "$j" -eq "$ENTRIES_PER_FILE" ]; then
      echo "export QUERY_END_DATE=$TIMESTAMP";
    fi
    

    # Simple message 
    MSG="Entry $ENTRY_COUNT"
    ((ENTRY_COUNT++))

    ENTRY="\t{\"timestamp\": $TIMESTAMP, \"message\": \"$MSG\"}"

    # Write entry
    if [ "$j" -eq "$ENTRIES_PER_FILE" ]; then
      echo -e "$ENTRY" >> "$FILE"
    else
      echo -e "$ENTRY," >> "$FILE"
    fi

    # Increment timestamp
    TIMESTAMP=$((TIMESTAMP + TIMESTAMP_INCREMENT))

  done

  # Close bracket
  echo ']' >> "$FILE"

done