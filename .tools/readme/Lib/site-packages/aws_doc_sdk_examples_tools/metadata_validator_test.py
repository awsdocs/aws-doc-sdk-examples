#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from pathlib import Path

import pytest

from .metadata_errors import MetadataErrors
from .metadata_validator import validate_metadata


@pytest.mark.parametrize("strict", [True, False])
def test_aws_entity_usage(strict):
    errors = MetadataErrors()
    validate_metadata(
        Path(Path(__file__).parent / "test_resources/doc_gen_test"), strict, errors
    )

    e_str = str(errors)
    assert "Title has AWS" in e_str
    assert "Title Abbrev has AWS" in e_str
    assert "Synopsis has AWS" in e_str
    assert "Synopsis list has AWS" in e_str
    assert "Description has AWS" in e_str

    assert "Title has &AWS;" not in e_str
    assert "Title Abbrev has &AWS;" not in e_str
    assert "Synopsis programlisting has AWS" not in e_str
    assert "Synopsis list code has <code>AWS" not in e_str
    assert "Description programlisting has AWS" not in e_str
