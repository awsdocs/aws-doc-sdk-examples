# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import subprocess
import sys

import pytest

files_under_test = [
    "models/amazon_nova/amazon_nova_text/document_understanding.py",
    "models/anthropic_claude/document_understanding.py",
    "models/cohere_command/document_understanding.py",
    "models/deepseek/document_understanding.py",
    "models/meta_llama/document_understanding.py",
    "models/mistral_ai/document_understanding.py",
]


@pytest.mark.integ
@pytest.mark.parametrize("file", files_under_test)
def test_invoke_model(file):
    result = subprocess.run(
        [sys.executable, file],
        capture_output=True,
        text=True,
    )
    assert result.stdout != ""
    assert result.returncode == 0
