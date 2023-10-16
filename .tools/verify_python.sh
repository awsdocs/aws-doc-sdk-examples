#!/bin/bash

HAS_3_11=$(python3.11 --version > /dev/null && echo "yes")
HAS_3_12=$(python3.12 --version > /dev/null && echo "yes")

if [ "yes" ~= "${HAS_3_11}${HAS_3_12}"] ; then
else
  echo "No python3.11 or python3.12"
  exit 1
fi

PYTHON3_MINOR=$(python3 --version | cut -d '.' -f 2)
if [ "$PYTHON3_MINOR" -lt 11 ] ; then
  echo "python3 default minor version less than 11"
  exit 1
fi

python3 -m pip install --upgrade pip
python3 -m pip install -r base_requirements.txt