# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.  
# SPDX-License-Identifier: Apache-2.0

import pytest
import subprocess
import sys

files_under_test = [
    "flows/playlist_flow.py"
]

@pytest.mark.integ
@pytest.mark.parametrize("file", files_under_test)
def test_playlist_flow(file):
    # Simulate user input - each string represents one input() call
    # Creates a playlist of 3 pop songs.
    test_input = "pop\n3\ny"  
    
    result = subprocess.run(
        [sys.executable, file],
        input=test_input,
        capture_output=True,
        text=True,
    )
    
    print(f"STDOUT: {result.stdout}")  # For debugging
    print(f"STDERR: {result.stderr}")  # For debugging
    
    assert result.stdout != ""
    assert result.returncode == 0

