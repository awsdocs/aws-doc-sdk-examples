# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Test for validate.
"""

from pathlib import Path

from .validate import validate


def test_validate():
    root_path = Path(__file__).parent / "test_resources" / "doc_gen_tributary_test"
    error_count = validate(root_path, root_path / ".doc_gen/config", False, False)
    assert error_count == 0
