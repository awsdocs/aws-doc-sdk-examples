#!/bin/bash

# Set valid arguments
valid_args=("@unit" "@integration")

# Iterate over all arguments
for arg in "$@"; do
  # Check if the current argument is a valid argument
  if [[ " ${valid_args[@]} " =~ " ${arg} " ]]; then
    # If the argument is "@unit", run `lerna run test`
    if [ "$arg" == "@unit" ]; then
      lerna run test
    # If the argument is "@integration", run `lerna run integration-test`
    elif [ "$arg" == "@integration" ]; then
      lerna run integration-test
    fi
  else
    # If the argument is invalid, print an error message
    echo "Invalid argument: $arg"
  fi
done

# If no valid arguments are provided, run both `lerna run test` and `lerna run integration-test`
if [ "$#" -eq 0 ]; then
  lerna run test
  lerna run integration-test
fi
