# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains tests that verify the examples loader finds appropriate errors
"""

import pytest
import yaml
from pathlib import Path

import metadata_errors
from metadata import parse, Example, Url, Language, Version, Excerpt
from doc_gen import DocGen
from sdks import Sdk
from services import Service


def load(
    path: Path, doc_gen: DocGen
) -> tuple[list[Example], metadata_errors.MetadataErrors]:
    root = Path(__file__).parent
    filename = root / "test_resources" / path
    with open(filename) as file:
        meta = yaml.safe_load(file)
    return parse(filename.name, meta, doc_gen.sdks, doc_gen.services)


SERVICES = {
    "ses": Service(long="&SESlong;", short="&SES;", sort="ses", version=1),
    "sns": Service(long="&SNSlong;", short="&SNS;", sort="sns", version=1),
    "sqs": Service(long="&SQSlong;", short="&SQS;", sort="sqs", version=1),
    "s3": Service(long="&S3long;", short="&S3;", sort="s3", version=1),
    "autogluon": Service(
        long="AutoGluon Test", short="AG Test", sort="autogluon", version=1
    ),
}
SDKS = {
    "C++": Sdk(name="C++", versions=[], guide="", property=""),
    "Java": Sdk(name="Java", versions=[], guide="", property=""),
    "JavaScript": Sdk(name="JavaScript", versions=[], guide="", property=""),
    "PHP": Sdk(name="PHP", versions=[], guide="", property=""),
}
DOC_GEN = DocGen(services=SERVICES, sdks=SDKS)

GOOD_SINGLE_CPP = """
sns_DeleteTopic:
   title: Deleting an &SNS; topic
   title_abbrev: Deleting a topic
   synopsis: |-
     Shows how to delete an &SNS; topic.
   languages:
     C++:
       versions:
         - sdk_version: 1
           github: cpp/example_code/sns
           sdkguide: sdkguide/link
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     sns:
       ? Operation1
       ? Operation2
     ses: { Operation1, Operation2 }
     sqs:
"""


def test_parse():
    meta = yaml.safe_load(GOOD_SINGLE_CPP)
    parsed, errors = parse("test_cpp.yaml", meta, SDKS, SERVICES)
    assert len(errors) == 0
    assert parsed == [
        Example(
            file="test_cpp.yaml",
            id="sns_DeleteTopic",
            title="Deleting an &SNS; topic",
            title_abbrev="Deleting a topic",
            synopsis="Shows how to delete an &SNS; topic.",
            services={
                "sns": ["Operation1", "Operation2"],
                "ses": ["Operation1", "Operation2"],
                "sqs": [],
            },
            languages={
                "C++": Language(
                    name="C++",
                    versions=[
                        Version(
                            sdk_version=1,
                            github="cpp/example_code/sns",
                            sdkguide="sdkguide/link",
                            excerpts=[
                                Excerpt(
                                    description="test excerpt description",
                                    snippet_tags=["test.excerpt"],
                                )
                            ],
                        )
                    ],
                )
            },
        )
    ]


CROSS_META = """
cross_DeleteTopic:
  title: Delete Topic
  title_abbrev: delete topic
  languages:
     Java:
       versions:
         - sdk_version: 3
           block_content: cross_DeleteTopic_block.xml
  services:
     sns:
"""


def test_parse_cross():
    meta = yaml.safe_load(CROSS_META)
    actual, errors = parse("cross.yaml", meta, SDKS, SERVICES)
    assert len(errors) == 0
    assert actual == [
        Example(
            file="cross.yaml",
            id="cross_DeleteTopic",
            title="Delete Topic",
            title_abbrev="delete topic",
            synopsis="",
            services={"sns": []},
            languages={
                "Java": Language(
                    name="Java",
                    versions=[
                        Version(
                            sdk_version=3, block_content="cross_DeleteTopic_block.xml"
                        )
                    ],
                )
            },
        )
    ]


CURATED = """
autogluon_tabular_with_sagemaker_pipelines:
  title: AutoGluon Tabular with SageMaker Pipelines
  title_abbrev: AutoGluon Tabular with SageMaker Pipelines
  synopsis: use AutoGluon with SageMaker Pipelines.
  source_key: amazon-sagemaker-examples
  languages:
     Java:
       versions:
         - sdk_version: 2
           block_content: block.xml
  services:
     s3:
"""


def test_parse_curated():
    meta = yaml.safe_load(CURATED)
    actual, errors = parse("curated.yaml", meta, SDKS, SERVICES)
    assert len(errors) == 0
    assert actual == [
        Example(
            id="autogluon_tabular_with_sagemaker_pipelines",
            file="curated.yaml",
            title="AutoGluon Tabular with SageMaker Pipelines",
            title_abbrev="AutoGluon Tabular with SageMaker Pipelines",
            source_key="amazon-sagemaker-examples",
            languages={
                "Java": Language(
                    name="Java",
                    versions=[Version(sdk_version=2, block_content="block.xml")],
                )
            },
            services={"s3": []},
            synopsis="use AutoGluon with SageMaker Pipelines.",
        )
    ]


def test_verify_load_successful():
    examples, errors = load(Path("valid_metadata.yaml"), DOC_GEN)
    assert len(errors) == 0
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
            languages={
                "Java": Language(
                    name="Java",
                    versions=[
                        Version(
                            sdk_version=2,
                            github="javav2/example_code/sns",
                            block_content="test block",
                            excerpts=[],
                            add_services={},
                            sdkguide=None,
                            more_info=[],
                        ),
                    ],
                ),
                "JavaScript": Language(
                    name="JavaScript",
                    versions=[
                        Version(
                            sdk_version=3,
                            github=None,
                            block_content=None,
                            add_services={"s3": []},
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
                "PHP": Language(
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
            },
            services={"sns": [], "sqs": []},
        )
    ]


@pytest.mark.parametrize(
    "filename,expected_errors",
    [
        (
            "empty_metadata.yaml",
            [
                metadata_errors.MissingField(
                    field="title",
                    file="empty_metadata.yaml",
                    id="sns_EmptyExample",
                ),
                metadata_errors.MissingField(
                    field="title_abbrev",
                    file="empty_metadata.yaml",
                    id="sns_EmptyExample",
                ),
                metadata_errors.MissingField(
                    field="languages",
                    file="empty_metadata.yaml",
                    id="sns_EmptyExample",
                ),
            ],
        ),
        (
            "errors_metadata.yaml",
            [
                metadata_errors.UnknownLanguage(
                    language="Perl",
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                ),
                metadata_errors.InvalidSdkGuideStart(
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    guide="https://docs.aws.amazon.com/absolute/link-to-my-guide",
                ),
                metadata_errors.MissingBlockContentAndExcerpt(
                    file="errors_metadata.yaml",
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    sdk_version=None,
                ),
                metadata_errors.MissingField(
                    field="versions",
                    file="errors_metadata.yaml",
                    id="sqs_TestExample",
                    language="Java",
                ),
                metadata_errors.MissingBlockContentAndExcerpt(
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
                metadata_errors.UnknownService(
                    file="errors_metadata.yaml",
                    id="sns_TestExample2",
                    service="garbled",
                ),
                metadata_errors.InvalidGithubLink(
                    file="errors_metadata.yaml",
                    id="sns_TestExample2",
                    language="Java",
                    sdk_version=2,
                    link="github/link/to/README.md",
                ),
                metadata_errors.BlockContentAndExcerptConflict(
                    file="errors_metadata.yaml",
                    id="cross_TestExample_Versions",
                    language="Java",
                    sdk_version=None,
                ),
                metadata_errors.APIExampleCannotAddService(
                    language="Java",
                    file="errors_metadata.yaml",
                    id="cross_TestExample_Versions",
                ),
                metadata_errors.NameFormat(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                ),
                metadata_errors.MissingBlockContentAndExcerpt(
                    file="errors_metadata.yaml",
                    id="snsBadFormat",
                    language="Java",
                ),
            ],
        ),
        (
            "formaterror_metadata.yaml",
            [
                metadata_errors.NameFormat(
                    file="formaterror_metadata.yaml",
                    id="WrongNameFormat",
                ),
                metadata_errors.UnknownService(
                    file="formaterror_metadata.yaml",
                    id="cross_TestExample",
                    language="Java",
                    service="garbage",
                ),
            ],
        ),
    ],
)
def test_common_errors(
    filename: str, expected_errors: list[metadata_errors.MetadataError]
):
    _, actual = load(Path(filename), DOC_GEN)
    assert expected_errors == [*actual]


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
