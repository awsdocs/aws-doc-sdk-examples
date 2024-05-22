# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import pytest
import subprocess
import sys

files_under_test = [
    "models/ai21_labs_jurassic2/invoke_model.py",
    "models/amazon_titan/titan_image_generator/invoke_model.py",
    "models/amazon_titan/titan_text/invoke_model.py",
    "models/amazon_titan/titan_text_embeddings/invoke_model.py",
    "models/anthropic_claude/invoke_model.py",
    "models/anthropic_claude/invoke_model_with_response_stream.py",
    "models/meta_llama/llama2/invoke_model.py",
    "models/meta_llama/llama3/invoke_model.py",
    "models/mistral_ai/invoke_model.py",
    "models/stability_ai/invoke_model.py",
    "models/amazon_titan/titan_text/invoke_model_with_response_stream.py",
    "models/meta_llama/llama2/invoke_model_with_response_stream.py",
    "models/meta_llama/llama3/invoke_model_with_response_stream.py",
    "models/mistral_ai/invoke_model_with_response_stream.py",
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
