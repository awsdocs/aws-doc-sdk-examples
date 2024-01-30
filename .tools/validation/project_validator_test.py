# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains tests that verify the project_validator script works as expected.
"""

import pytest

import project_validator
from metadata_errors import MetadataErrors
from pathlib import Path


@pytest.mark.parametrize(
    "contents,expected_parts",
    [
        (".Contents word WORD file:", ["contents", "word", "word", "file"]),
        ("https://example.com/foo", ["https", "", "example.com", "foo"]),
    ],
)
def test(contents: str, expected_parts: list[str]):
    """Test that the word part stemmer finds the right pieces"""
    actual_parts = [part for _, part in project_validator.word_parts(contents)]
    assert expected_parts == actual_parts


@pytest.mark.parametrize(
    "file_contents,expected_error_count",
    [
        (
            "Test file contents.\n"
            "This URL is not allowed: http://alpha-docs-aws" + ".amazon.com/test\n"
            "And neither is this one:\n"
            "https://integ-docs-aws" + ".amazon.com/something-else\n"
            "But that's it for denied words.",
            2,
        ),
        ("This string has no denied words.\n" "And neither does this one.", 0),
    ],
)
def test_verify_no_deny_list_words(file_contents: str, expected_error_count: int):
    """Test that file contents that contain disallowed words are counted as errors."""
    errors = MetadataErrors()
    project_validator.verify_no_deny_list_words(file_contents, Path("location"), errors)
    error_count = len(errors)
    assert error_count == expected_error_count


@pytest.mark.parametrize(
    "file_contents,expected_error_count",
    [
        ("This sentence has a hidAKAAIOS" + "FODNN7EXAMPLEden secret key.", 1),
        ("This sentence has a hidAKIAIOSFO" + "DNN7EXAMPLEden example key.", 0),
        ("This sentence has nothing interesting about it at all.", 0),
        (
            "This could be a secret key, I guess: aws/monitoring/mo"
            "del/DeleteAlarmsRequbbb\n"
            "And so could this: TargetTrackingScalingP" + "olicy1234567891234\n"
            "Not this: wJalrXUtnFEMI/K7MDENG/bPxR" + "fiCYEXAMPLEKEY is allowed!",
            2,
        ),
        ("Normal_file_name.py", 0),
        ("Something AppStreamUsageReportsCFNGl" + "ueAthenaAccess.cs", 0),
        ("Something AppStreamUsageReportsCFNGlue" + "AtNotAllowed.py", 1),
    ],
)
def test_verify_no_secret_keys(file_contents: str, expected_error_count: int):
    """Test that file contents that contain 20- or 40-character strings and are
    not in the allowed list are counted as errors."""
    errors = MetadataErrors()
    project_validator.verify_no_secret_keys(file_contents, Path("location"), errors)
    error_count = len(errors)
    assert error_count == expected_error_count


if __name__ == "__main__":
    pytest.main([__file__])
