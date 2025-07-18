#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Simple wrapper script to run the improved writeme.py

# Get the directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Check if Python is available
if ! command -v python &> /dev/null; then
    echo "Error: Python is not installed or not in PATH"
    exit 1
fi

# Check if the improved_writeme.py file exists
if [ ! -f "$SCRIPT_DIR/improved_writeme.py" ]; then
    echo "Error: improved_writeme.py not found in $SCRIPT_DIR"
    exit 1
fi

# Run the improved writeme.py script
echo "Running improved WRITEME..."
python "$SCRIPT_DIR/improved_writeme.py" "$@"
exit_code=$?

# Return the exit code from the Python script
if [ $exit_code -ne 0 ]; then
    echo "Error: improved_writeme.py exited with code $exit_code"
fi

exit $exit_code