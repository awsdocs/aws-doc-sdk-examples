# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains tests that verify the examples loader finds appropriate errors
"""

import pytest
import yaml
from pathlib import Path

from examples import parse, Example, Url, Language, Version, Excerpt
import example_errors


def load(path) -> list[Example] | list[example_errors.ExampleParseError]:
    with open(Path(__file__).parent / "test_resources" / path) as file:
        meta = yaml.safe_load(file)
    return parse(path, meta)


def test_verify_load_successful():
    examples = load("valid_metadata.yaml")
    assert examples == [
        Example(
            file="valid_metadata.yaml",
            id="sns_TestExample",
            title="Check whether a phone number is opted out using an &AWS; SDK",
            title_abbrev="Check whether a phone number is opted out",
            synopsis="check whether a phone number is opted out using some of the &AWS; SDKs that are available.",
            synopsis_list=["Check the one thing.", "Do some other thing."],
            guide_topic=Url(title="Test guide topic title", url="test-guide/url"),
            category="Usage",
            service_main=None,
            languages=[
                Language(
                    name="Java",
                    versions=[
                        Version(
                            sdk_version=2,
                            github="javav2/example_code/sns",
                            block_content="test block",
                            excerpts=None,
                            add_services={},
                            sdkguide=None,
                            more_info=[],
                        ),
                    ],
                ),
                Language(
                    name="JavaScript",
                    versions=[
                        Version(
                            sdk_version=3,
                            github=None,
                            block_content=None,
                            add_services={"s3": {}},
                            excerpts=[
                                Excerpt(
                                    description="Descriptive",
                                    snippet_files=[],
                                    snippet_tags=["javascript.snippet.tag"],
                                )
                            ],
                            sdkguide=None,
                            more_info=[],
                        ),
                    ],
                ),
                Language(
                    name="PHP",
                    versions=[
                        Version(
                            sdk_version=3,
                            github="php/example_code/sns",
                            sdkguide="php/sdkguide/link",
                            block_content=None,
                            excerpts=[
                                Excerpt(
                                    description="Optional description.",
                                    snippet_tags=[
                                        "php.snippet.tag.1",
                                        "php.snippet.tag.2",
                                    ],
                                    snippet_files=[],
                                )
                            ],
                            add_services={},
                            more_info=[],
                        )
                    ],
                ),
            ],
            services={"sns": {}, "sqs": {}},
        )
    ]


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
                example_errors.InvalidSdkGuideStart(
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    guide="https://docs.aws.amazon.com/absolute/link-to-my-guide",
                ),
                example_errors.MissingBlockContentAndExcerpt(
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    sdk_version=None,
                ),
                example_errors.MissingField(
                    field="versions",
                    file="errors_metadata.yaml",
                    id="sqs_TestExample",
                    language=None,
                ),
                example_errors.MissingBlockContentAndExcerpt(
                    file="errors_metadata.yaml",
                    id="sns_TestExample",
                    language="Java",
                    sdk_version=None,
                ),
                # example_errors.MissingSnippetTag(
                #     file="errors_metadata.yaml",
                #     id="sqs_TestExample",
                #     language="Java",
                #     sdk_version=2,
                #     tag="this.snippet.does.not.exist",
                # ),
                example_errors.UnknownService(
                    file="errors_metadata.yaml",
                    id="sns_TestExample2",
                    service="garbled",
                ),
                example_errors.InvalidGithubLink(
                    file="errors_metadata.yaml",
                    id="sns_TestExample2",
                    language="Java",
                    sdk_version="",
                ),
                example_errors.BlockContentAndExcerptConflict(
                    file="errors_metadata.yaml",
                    id="cross_TestExample_Versions",
                    language="Java",
                    sdk_version=None,
                ),
                example_errors.APIExampleCannotAddService(
                    language="Java",
                    file="errors_metadata.yaml",
                    id="cross_TestExample_Versions",
                ),
                example_errors.NameFormat(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                ),
                example_errors.MissingBlockContentAndExcerpt(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                    language="Java",
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
    assert expected_errors == actual._errors


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
