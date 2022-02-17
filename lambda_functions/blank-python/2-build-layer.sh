#!/bin/bash
set -eo pipefail
rm -rf package
cd function
pip install --target ../package/python -r requirements.txt
