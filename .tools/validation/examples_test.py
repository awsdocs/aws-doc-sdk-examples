# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains tests that verify the examples loader finds appropriate errors
"""

import pytest
import yaml
from pathlib import Path

from examples import parse, Example
import example_errors


def load(path) -> list[Example] | list[example_errors.ExampleParseError]:
    with open(Path(__file__).parent / "test_resources" / path) as file:
        meta = yaml.safe_load(file)
    return parse(path, meta)


def test_verify_load_successful():
    examples = load("valid_metadata.yaml")
    assert len(examples) == 1
    assert isinstance(examples[0], Example)


@pytest.mark.parametrize(
    "filename,expected_errors",
    [
        (
            "empty_metadata.yaml",
            [
                example_errors.MissingField(
                    field="title",
                    file="empty_metadata.yaml",
                    id="sns_EmptyExample",
                ),
                example_errors.MissingField(
                    field="title_abbrev",
                    file="empty_metadata.yaml",
                    id="sns_EmptyExample",
                ),
                example_errors.MissingField(
                    field="languages",
                    file="empty_metadata.yaml",
                    id="sns_EmptyExample",
                ),
            ],
        ),
        (
            "errors_metadata.yaml",
            [
                example_errors.UnknownLanguage(
                    language="Perl",
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                ),
                example_errors.APIExampleCannotAddService(
                    language="Perl",
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                ),
                # ExampleParseError(
                #     file="errors_metadata.yaml",
                #     id="sqs_WrongServiceSlug",
                #     language="Perl",
                #     message="metaVersionSdkGuideStartsWithDocsDomain https://docs.aws.amazon.com/absolute/link-to-my-guide",
                # ),
                example_errors.MissingField(
                    field="sdk_version",
                    file="errors_metadata.yaml",
                    id="sqs_TestExample",
                    language="Java",
                ),
                example_errors.MissingBlockContentAndExcerpt(
                    file="errors_metadata.yaml",
                    id="sqs_TestExample",
                    language="Java",
                    sdk_version=2,
                ),
                example_errors.InvalidGithubLink(
                    file="errors_metadata.yaml",
                    id="sqs_TestExample",
                    language="Java",
                    sdk_version=2,
                ),
                example_errors.MissingSnippetTag(
                    file="errors_metadata.yaml",
                    id="sqs_TestExample",
                    language="Java",
                    sdk_version=2,
                    tag="this.snippet.does.not.exist",
                ),
                example_errors.NameFormat(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                ),
                example_errors.BlockContentAndExcerptConflict(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                    language="Java",
                    sdk_version=2,
                ),
                example_errors.MissingBlockContent(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                    language="Java",
                    sdk_version=2,
                ),
            ],
        ),
        (
            "formaterror_metadata.yaml",
            [
                example_errors.NameFormat(
                    file="formaterror_metadata.yaml",
                    id="WrongNameFormat",
                ),
                example_errors.UnknownService(
                    file="formaterror_metadata.yaml",
                    id="cross_TestExample",
                    language="Java",
                    service="garbage",
                ),
            ],
        ),
    ],
)
def test_common_errors(filename, expected_errors):
    actual = load(filename)
    # assert len(actual) == len(
    #     expected_errors
    # ), f"Mismatch for errors between {filename} and expected"
    for actual, expected in zip(actual, expected_errors):
        assert isinstance(actual, example_errors.ExampleParseError)
        assert actual == expected


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
