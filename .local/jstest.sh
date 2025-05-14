#!/bin/bash

# Define the pre-commit file path
PRE_COMMIT_FILE="./javascriptv3/.husky/pre-commit"

# Check if an argument was provided
if [ $# -ne 1 ]; then
    echo "Error: Please provide exactly one argument (on or off)"
    echo "Usage: $0 [on|off]"
    exit 1
fi

# Check if the argument is valid
if [ "$1" != "on" ] && [ "$1" != "off" ]; then
    echo "Error: Argument must be either 'on' or 'off'"
    echo "Usage: $0 [on|off]"
    exit 1
fi

# Check if the pre-commit file exists
if [ ! -f "$PRE_COMMIT_FILE" ]; then
    echo "Error: Pre-commit file not found at $PRE_COMMIT_FILE"
    exit 1
fi

# Process the argument
if [ "$1" = "on" ]; then
    echo "Turning ON npm commands in pre-commit hook"
    # Remove comment symbols from lines starting with "# npm"
    sed -i 's/^# npm/npm/g' "$PRE_COMMIT_FILE"
    echo "npm commands are now enabled in the pre-commit hook"
elif [ "$1" = "off" ]; then
    echo "Turning OFF npm commands in pre-commit hook"
    # Add comment symbols to lines starting with "npm"
    sed -i 's/^npm/# npm/g' "$PRE_COMMIT_FILE"
    echo "npm commands are now disabled in the pre-commit hook"
fi

exit 0
