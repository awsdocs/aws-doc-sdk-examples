#!/bin/bash

MIN_VERSION=11

# Check that the minor version of the default python3 command is at least 11.
PYTHON3_MINOR=$(python3 --version | cut -d '.' -f 2)
if [ "$PYTHON3_MINOR" -lt "$MIN_VERSION" ] ; then
  echo "python3 default minor version less than $MIN_VERSION"
  exit 1
fi

python3 -m pip install --upgrade pip
python3 -m pip install -r base_requirements.txt