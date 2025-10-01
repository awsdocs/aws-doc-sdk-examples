# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import subprocess
import sys

files_under_test = [
    "models/amazon_nova/amazon_nova_text/converse.py",
    "models/anthropic_claude/converse.py",
    "models/cohere_command/converse.py",
    "models/meta_llama/converse.py",
    "models/mistral_ai/converse.py",
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
