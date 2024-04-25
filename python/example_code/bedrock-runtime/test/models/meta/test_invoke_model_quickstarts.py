# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for:
- models/meta/llama2/invoke_model_quickstart.py
- models/meta/llama3/invoke_model_quickstart.py
"""

import pytest
import subprocess
import sys


@pytest.mark.integ
def test_llama2_quickstart():
    result = subprocess.run([
        sys.executable,
        'models/meta/llama2/invoke_model_quickstart.py'
    ], capture_output=True, text=True)
    assert result.stdout != ""
    assert result.returncode == 0


@pytest.mark.integ
def test_llama2_with_response_stream_quickstart():
    result = subprocess.run([
        sys.executable,
        'models/meta/llama2/invoke_model_with_response_stream_quickstart.py'
    ], capture_output=True, text=True)
    assert result.stdout != ""
    assert result.returncode == 0

@pytest.mark.integ
def test_llama3_quickstart():
    result = subprocess.run([
        sys.executable,
        'models/meta/llama2/invoke_model_quickstart.py'
    ], capture_output=True, text=True)
    assert result.stdout != ""
    assert result.returncode == 0


@pytest.mark.integ
def test_llama3_with_response_stream_quickstart():
    result = subprocess.run([
        sys.executable,
        'models/meta/llama3/invoke_model_with_response_stream_quickstart.py'
    ], capture_output=True, text=True)
    assert result.stdout != ""
    assert result.returncode == 0
