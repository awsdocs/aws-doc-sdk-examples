# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for:
- models/amazon/titan_embeddings_text/g2_invoke_model_quickstart.py
- models/amazon/titan_embeddings_text/v2_invoke_model_quickstart.py
"""

import pytest
import subprocess
import sys


@pytest.mark.integ
def test_titan_embeddings_text_g1_quickstart():
    result = subprocess.run(
        [
            sys.executable,
            "models/amazon/titan_embeddings_text/g1_invoke_model_quickstart.py",
        ],
        capture_output=True,
        text=True,
    )
    assert result.stdout != ""
    assert result.returncode == 0


@pytest.mark.integ
def test_titan_embeddings_text_v2_quickstart():
    result = subprocess.run(
        [
            sys.executable,
            "models/amazon/titan_embeddings_text/v2_invoke_model_quickstart.py",
        ],
        capture_output=True,
        text=True,
    )
    assert result.stdout != ""
    assert result.returncode == 0
