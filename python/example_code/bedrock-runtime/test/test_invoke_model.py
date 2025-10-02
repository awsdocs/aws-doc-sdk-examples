# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import subprocess
import sys
import time

files_under_test = [
    # Text models
    "models/amazon_nova/amazon_nova_canvas/invoke_model.py",
    "models/anthropic_claude/invoke_model.py",
    "models/cohere_command/command_r_invoke_model.py",
    "models/meta_llama/llama3_invoke_model.py",
    "models/mistral_ai/invoke_model.py",
    # Embeddings models
    "models/amazon_titan_text_embeddings/invoke_model.py",
    # Image models
    "models/stability_ai/invoke_model.py",
    "models/amazon_titan_image_generator/invoke_model.py",
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
    time.sleep(1)  # Wait a second to prevent throttling
