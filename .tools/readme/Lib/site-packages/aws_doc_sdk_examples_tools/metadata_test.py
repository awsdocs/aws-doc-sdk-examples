# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains tests that verify the examples loader finds appropriate errors
"""

import pytest
import yaml
from pathlib import Path
from typing import List, Set, Tuple

from . import metadata_errors
from .metadata_errors import MetadataErrors, ExampleMergeConflict
from .metadata import (
    DocFilenames,
    SDKPageVersion,
    Example,
    Url,
    Language,
    Version,
    Excerpt,
)
from .doc_gen import DocGen, parse_examples, check_id_format
from .project_validator import ValidationConfig
from .sdks import Sdk
from .services import Service, ServiceExpanded


def load(
    path: Path, doc_gen: DocGen, blocks: Set[str] = set()
) -> Tuple[List[Example], metadata_errors.MetadataErrors]:
    with path.open() as file:
        meta = yaml.safe_load(file)
    return parse_examples(
        path,
        meta,
        doc_gen.sdks,
        doc_gen.services,
        doc_gen.standard_categories,
        blocks,
        doc_gen.validation,
    )


SERVICES = {
    "api-gateway": Service(
        long="&ABPlong;",
        short="&ABP;",
        expanded=ServiceExpanded(
            long="Amazon API Gateway (API Gateway)", short="API Gateway"
        ),
        sort="api-gateway",
        version=1,
        sdk_id="apigateway",
    ),
    "medical-imaging": Service(
        long="&AHIlong;",
        short="&AHI;",
        expanded=ServiceExpanded(
            long="AWS HealthImaging (HealthImaging)", short="HealthImaging"
        ),
        sort="HealthImaging",
        version=1,
        sdk_id="Medical Imaging",
    ),
    "sqs": Service(
        long="&SQSlong;",
        short="&SQS;",
        expanded=ServiceExpanded(
            long="Amazon Simple Queue Service (Amazon SQS)", short="Amazon SQS"
        ),
        sort="sqs",
        version=1,
        sdk_id="SQS",
    ),
    "s3": Service(
        long="&S3long;",
        short="&S3;",
        expanded=ServiceExpanded(
            long="Amazon Simple Storage Service (Amazon S3)", short="Amazon S3"
        ),
        sort="s3",
        version=1,
        sdk_id="S3",
    ),
    "autogluon": Service(
        long="AutoGluon Test",
        short="AG Test",
        expanded=ServiceExpanded(long="AutoGluon Test", short="AutoGluon Test"),
        sort="autogluon",
        version=1,
        sdk_id="",
    ),
}
SDKS = {
    "C++": Sdk(
        name="C++",
        display="C++",
        versions=[],
        guide="",
        property="cpp",
        is_pseudo_sdk=False,
    ),
    "Java": Sdk(
        name="Java",
        display="Java",
        versions=[],
        guide="",
        property="java",
        is_pseudo_sdk=False,
    ),
    "JavaScript": Sdk(
        name="JavaScript",
        display="JavaScript",
        versions=[],
        guide="",
        property="javascript",
        is_pseudo_sdk=False,
    ),
    "PHP": Sdk(
        name="PHP",
        display="PHP",
        versions=[],
        guide="",
        property="php",
        is_pseudo_sdk=False,
    ),
}
STANDARD_CATS = ["Api"]
DOC_GEN = DocGen(
    root=Path(),
    errors=metadata_errors.MetadataErrors(),
    validation=ValidationConfig(),
    services=SERVICES,
    sdks=SDKS,
    standard_categories=STANDARD_CATS,
)

GOOD_SINGLE_CPP = """
medical-imaging_CreateDatastore:
   languages:
     C++:
       versions:
         - sdk_version: 1
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     medical-imaging:
       ? Operation1
       ? Operation2
     api-gateway: { Operation1, Operation2 }
     sqs:
"""


def test_parse():
    meta = yaml.safe_load(GOOD_SINGLE_CPP)
    parsed, errors = parse_examples(
        Path("test_cpp.yaml"),
        meta,
        SDKS,
        SERVICES,
        STANDARD_CATS,
        set(),
        DOC_GEN.validation,
    )
    assert len(errors) == 0
    assert len(parsed) == 1
    language = Language(
        name="C++",
        property="cpp",
        versions=[
            Version(
                sdk_version=1,
                excerpts=[
                    Excerpt(
                        description="test excerpt description",
                        snippet_tags=["test.excerpt"],
                    )
                ],
            )
        ],
    )
    example = Example(
        file=Path("test_cpp.yaml"),
        id="medical-imaging_CreateDatastore",
        category="Scenarios",
        services={
            "medical-imaging": set(["Operation1", "Operation2"]),
            "api-gateway": set(["Operation1", "Operation2"]),
            "sqs": set(),
        },
        doc_filenames=DocFilenames(
            service_pages={
                "medical-imaging": make_doc_link(
                    stub="medical-imaging_example_medical-imaging_CreateDatastore_section"
                ),
                "sqs": make_doc_link(
                    stub="sqs_example_medical-imaging_CreateDatastore_section"
                ),
                "api-gateway": make_doc_link(
                    stub="api-gateway_example_medical-imaging_CreateDatastore_section"
                ),
            },
            sdk_pages={
                "cpp": {
                    1: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="cpp_1_medical-imaging_code_examples",
                                anchor="scenarios",
                            ),
                            "sqs": make_doc_link(
                                stub="cpp_1_sqs_code_examples", anchor="scenarios"
                            ),
                            "api-gateway": make_doc_link(
                                stub="cpp_1_api-gateway_code_examples",
                                anchor="scenarios",
                            ),
                        }
                    )
                }
            },
        ),
        languages={"C++": language},
    )
    assert parsed[0] == example


STRICT_TITLE_META = """
medical-imaging_GoodOne:
   languages:
     C++:
       versions:
         - sdk_version: 1
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     medical-imaging: {GoodOne}
medical-imaging_GoodScenario:
   title: Scenario title
   title_abbrev: Scenario title abbrev
   synopsis: scenario synopsis.
   category: Scenarios
   languages:
     C++:
       versions:
         - sdk_version: 1
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     medical-imaging: {GoodOne}
"""


def test_parse_strict_titles():
    meta = yaml.safe_load(STRICT_TITLE_META)
    parsed, errors = parse_examples(
        Path("test_cpp.yaml"),
        meta,
        SDKS,
        SERVICES,
        STANDARD_CATS,
        set(),
        ValidationConfig(strict_titles=True),
    )
    assert len(errors) == 0
    assert len(parsed) == 2
    language = Language(
        name="C++",
        property="cpp",
        versions=[
            Version(
                sdk_version=1,
                excerpts=[
                    Excerpt(
                        description="test excerpt description",
                        snippet_tags=["test.excerpt"],
                    )
                ],
            )
        ],
    )
    example_action = Example(
        file=Path("test_cpp.yaml"),
        id="medical-imaging_GoodOne",
        category="Api",
        services={
            "medical-imaging": {"GoodOne"},
        },
        doc_filenames=DocFilenames(
            service_pages={
                "medical-imaging": make_doc_link(
                    stub="medical-imaging_example_medical-imaging_GoodOne_section"
                ),
            },
            sdk_pages={
                "cpp": {
                    1: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="cpp_1_medical-imaging_code_examples",
                                anchor="actions",
                            ),
                        }
                    )
                }
            },
        ),
        languages={"C++": language},
    )
    example_scenario = Example(
        file=Path("test_cpp.yaml"),
        id="medical-imaging_GoodScenario",
        title="Scenario title",
        title_abbrev="Scenario title abbrev",
        synopsis="scenario synopsis.",
        category="Scenarios",
        doc_filenames=DocFilenames(
            service_pages={
                "medical-imaging": make_doc_link(
                    stub="medical-imaging_example_medical-imaging_GoodScenario_section"
                ),
            },
            sdk_pages={
                "cpp": {
                    1: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="cpp_1_medical-imaging_code_examples",
                                anchor="scenarios",
                            ),
                        }
                    )
                }
            },
        ),
        services={
            "medical-imaging": {"GoodOne"},
        },
        languages={"C++": language},
    )
    assert parsed[0] == example_action
    assert parsed[1] == example_scenario


STRICT_TITLE_ERRORS = """
medical-imaging_BadOne:
   title: Disallowed title
   title_abbrev: Disallowed title abbrev
   synopsis: disallowed synopsis. 
   languages:
     C++:
       versions:
         - sdk_version: 1
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     medical-imaging: {Different}
medical-imaging_BadScenario:
   category: Scenarios
   languages:
     C++:
       versions:
         - sdk_version: 1
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     medical-imaging: {BadOne}
medical-imaging_BadBasics:
   category: Basics
   languages:
     C++:
       versions:
         - sdk_version: 1
           github: cpp/example_code/medical-imaging
           sdkguide: sdkguide/link
           excerpts:
             - description: test excerpt description
               snippet_tags:
                 - test.excerpt
   services:
     medical-imaging: {BadOne}
"""


def test_parse_strict_title_errors():
    meta = yaml.safe_load(STRICT_TITLE_ERRORS)
    _, errors = parse_examples(
        Path("test_cpp.yaml"),
        meta,
        SDKS,
        SERVICES,
        STANDARD_CATS,
        set(),
        ValidationConfig(strict_titles=True),
    )
    expected = [
        metadata_errors.APICannotHaveTitleFields(
            file=Path("test_cpp.yaml"),
            id="medical-imaging_BadOne",
        ),
        metadata_errors.ActionNameFormat(
            file=Path("test_cpp.yaml"),
            id="medical-imaging_BadOne",
        ),
        metadata_errors.NonAPIMustHaveTitleFields(
            file=Path("test_cpp.yaml"),
            id="medical-imaging_BadScenario",
        ),
        metadata_errors.BasicsMustHaveSynopsisField(
            file=Path("test_cpp.yaml"),
            id="medical-imaging_BadBasics",
        ),
    ]
    assert expected == [*errors]


CROSS_META = """
cross_DeleteTopic:
  title: Delete Topic
  title_abbrev: delete topic
  category: Cross-service examples
  languages:
     Java:
       versions:
         - sdk_version: 3
           block_content: cross_DeleteTopic_block.xml
  services:
     medical-imaging:
     api-gateway:
"""


def test_parse_cross():
    meta = yaml.safe_load(CROSS_META)
    actual, errors = parse_examples(
        Path("cross.yaml"),
        meta,
        SDKS,
        SERVICES,
        STANDARD_CATS,
        set(["cross_DeleteTopic_block.xml"]),
        DOC_GEN.validation,
    )
    assert len(errors) == 0
    assert len(actual) == 1
    language = Language(
        name="Java",
        property="java",
        versions=[Version(sdk_version=3, block_content="cross_DeleteTopic_block.xml")],
    )
    example = Example(
        file=Path("cross.yaml"),
        id="cross_DeleteTopic",
        category="Cross-service examples",
        title="Delete Topic",
        title_abbrev="delete topic",
        synopsis="",
        services={"api-gateway": set(), "medical-imaging": set()},
        doc_filenames=DocFilenames(
            service_pages={
                "medical-imaging": make_doc_link(
                    stub="medical-imaging_example_cross_DeleteTopic_section"
                ),
                "api-gateway": make_doc_link(
                    stub="api-gateway_example_cross_DeleteTopic_section"
                ),
            },
            sdk_pages={
                "java": {
                    3: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="java_3_medical-imaging_code_examples",
                                anchor="scenarios",
                            ),
                            "api-gateway": make_doc_link(
                                stub="java_3_api-gateway_code_examples",
                                anchor="scenarios",
                            ),
                        }
                    )
                }
            },
        ),
        languages={"Java": language},
    )
    assert actual[0] == example


def test_verify_load_successful():
    actual, errors = load(
        TEST_RESOURCES_PATH / "valid_metadata.yaml",
        DOC_GEN,
        set(["test block"]),
    )
    assert len(errors) == 0
    assert len(actual) == 1
    java = Language(
        name="Java",
        property="java",
        versions=[
            Version(
                sdk_version=2,
                github="test_path",
                block_content="test block",
                excerpts=[],
                sdkguide=None,
                more_info=[],
            ),
        ],
    )

    javascript = Language(
        name="JavaScript",
        property="javascript",
        versions=[
            Version(
                sdk_version=3,
                block_content=None,
                excerpts=[
                    Excerpt(
                        description="Descriptive",
                        snippet_files=[],
                        snippet_tags=[
                            "medical-imaging.JavaScript.datastore.createDatastoreV3"
                        ],
                        genai="some",
                    )
                ],
                sdkguide=None,
                more_info=[],
            ),
        ],
    )

    php = Language(
        name="PHP",
        property="php",
        versions=[
            Version(
                sdk_version=3,
                sdkguide="php/sdkguide/link",
                block_content=None,
                excerpts=[
                    Excerpt(
                        description="Optional description.",
                        snippet_tags=[
                            "php.snippet.tag.1",
                            "php.snippet.tag.2",
                        ],
                        snippet_files=["snippet_file.txt"],
                    )
                ],
                more_info=[],
            )
        ],
    )

    languages = {
        "Java": java,
        "JavaScript": javascript,
        "PHP": php,
    }

    example = Example(
        file=Path(__file__).parent / "test_resources/valid_metadata.yaml",
        id="medical-imaging_TestExample",
        title="Check whether a phone number is opted out using an &AWS; SDK",
        title_abbrev="Check whether a phone number is opted out",
        synopsis="check whether a phone number is opted out using some of the &AWS; SDKs that are available.",
        synopsis_list=["Check the one thing.", "Do some other thing."],
        guide_topic=Url(title="Test guide topic title", url="test-guide/url"),
        category="Usage",
        service_main=None,
        languages=languages,
        doc_filenames=DocFilenames(
            service_pages={
                "medical-imaging": make_doc_link(
                    stub="medical-imaging_example_medical-imaging_TestExample_section"
                ),
            },
            sdk_pages={
                "java": {
                    2: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="java_2_medical-imaging_code_examples",
                                anchor="scenarios",
                            ),
                        }
                    )
                },
                "php": {
                    3: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="php_3_medical-imaging_code_examples",
                                anchor="scenarios",
                            ),
                        }
                    )
                },
                "javascript": {
                    3: SDKPageVersion(
                        actions_scenarios={
                            "medical-imaging": make_doc_link(
                                stub="javascript_3_medical-imaging_code_examples",
                                anchor="scenarios",
                            ),
                        }
                    )
                },
            },
        ),
        services={"medical-imaging": set()},
    )
    assert actual[0] == example


TEST_RESOURCES_PATH = Path(__file__).parent / "test_resources"
EMPTY_METADATA_PATH = TEST_RESOURCES_PATH / "empty_metadata.yaml"
ERRORS_METADATA_PATH = TEST_RESOURCES_PATH / "errors_metadata.yaml"
FORMATTER_METADATA_PATH = TEST_RESOURCES_PATH / "formaterror_metadata.yaml"


@pytest.mark.parametrize(
    "filename,expected_errors,validation_errors",
    [
        (
            "empty_metadata.yaml",
            [
                metadata_errors.MissingField(
                    field="languages",
                    file=EMPTY_METADATA_PATH,
                    id="medical-imaging_EmptyExample",
                ),
            ],
            [],
        ),
        (
            "errors_metadata.yaml",
            [
                metadata_errors.APIMustHaveOneServiceOneAction(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                    svc_actions="",
                ),
                metadata_errors.UnknownLanguage(
                    language="Perl",
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                ),
                metadata_errors.InvalidSdkGuideStart(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    guide="https://docs.aws.amazon.com/absolute/link-to-my-guide",
                    sdk_version=1,
                ),
                metadata_errors.MissingBlockContentAndExcerpt(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    sdk_version=1,
                ),
                metadata_errors.AddServicesHasBeenDeprecated(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    sdk_version=1,
                    add_services={"sqs": set()},
                ),
                metadata_errors.ServiceNameFormat(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                    svc="sqs",
                    svcs=["medical-imaging"],
                ),
                metadata_errors.MissingField(
                    field="versions",
                    file=ERRORS_METADATA_PATH,
                    id="sqs_TestExample",
                    language="Java",
                ),
                metadata_errors.MissingBlockContentAndExcerpt(
                    file=ERRORS_METADATA_PATH,
                    id="medical-imaging_TestExample",
                    language="Java",
                    sdk_version=2,
                ),
                # example_errors.MissingSnippetTag(
                #     file="errors_metadata.yaml",
                #     id="sqs_TestExample",
                #     language="Java",
                #     sdk_version=2,
                #     tag="this.snippet.does.not.exist",
                # ),
                metadata_errors.FieldError(
                    file=ERRORS_METADATA_PATH,
                    id="medical-imaging_TestExample2",
                    field="genai",
                    value="so much",
                    language="Java",
                    sdk_version=2,
                ),
                metadata_errors.BlockContentAndExcerptConflict(
                    file=ERRORS_METADATA_PATH,
                    id="cross_TestExample_Versions",
                    language="Java",
                    sdk_version=2,
                ),
                metadata_errors.AddServicesHasBeenDeprecated(
                    file=ERRORS_METADATA_PATH,
                    id="cross_TestExample_Versions",
                    language="Java",
                    sdk_version=2,
                    add_services={"sqs": set()},
                ),
                metadata_errors.MissingCrossContent(
                    file=ERRORS_METADATA_PATH,
                    id="cross_TestExample_Missing",
                    language="Java",
                    sdk_version=2,
                    block="missing_block_content.xml",
                ),
                metadata_errors.MissingBlockContentAndExcerpt(
                    file=ERRORS_METADATA_PATH,
                    id="medical-imagingBadFormat",
                    language="Java",
                    sdk_version=2,
                ),
                metadata_errors.NameFormat(
                    file=ERRORS_METADATA_PATH,
                    id="medical-imagingBadFormat",
                ),
                metadata_errors.PersonMissingField(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_InvalidOwner",
                    language="Java",
                    sdk_version=2,
                    name="None",
                    alias="author@example.com",
                ),
                metadata_errors.InvalidFieldType(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_InvalidOwner",
                    language="Java",
                    sdk_version=2,
                    reason="must be string",
                ),
            ],
            [
                metadata_errors.MissingGithubLink(
                    file=ERRORS_METADATA_PATH,
                    id="sqs_WrongServiceSlug",
                    language="Perl",
                    sdk_version=1,
                    link="perl/example_code/medical-imaging",
                    root=TEST_RESOURCES_PATH,
                ),
                metadata_errors.UnknownService(
                    file=ERRORS_METADATA_PATH,
                    id="medical-imaging_TestExample2",
                    service="garbled",
                ),
                metadata_errors.InvalidGithubLink(
                    file=ERRORS_METADATA_PATH,
                    id="medical-imaging_TestExample2",
                    language="Java",
                    sdk_version=2,
                    link="github/link/to/README.md",
                ),
            ],
        ),
        (
            "formaterror_metadata.yaml",
            [
                metadata_errors.NameFormat(
                    file=FORMATTER_METADATA_PATH,
                    id="WrongNameFormat",
                ),
                metadata_errors.AddServicesHasBeenDeprecated(
                    file=FORMATTER_METADATA_PATH,
                    id="cross_TestExample",
                    language="Java",
                    sdk_version=2,
                    add_services={"garbage": set()},
                ),
            ],
            [],
        ),
    ],
)
def test_common_errors(
    filename: str,
    expected_errors: List[metadata_errors.MetadataError],
    validation_errors: List[metadata_errors.MetadataError],
):
    root = TEST_RESOURCES_PATH / filename
    examples, actual = load(root, DOC_GEN, set(["test/block", "cross_block.xml"]))
    assert expected_errors == [*actual]
    validations = MetadataErrors()
    for example in examples:
        example.validate(validations, DOC_GEN.services, root.parent)
    assert validation_errors == [*validations]


TEST_SERVICES = {"test": {"Test", "Test2", "Test3", "1"}}


@pytest.mark.parametrize(
    "name,check_action,error_count",
    [
        ("serverless_Snippet", False, 0),
        ("test_Test", False, 0),
        ("test_Test", True, 0),
        ("test_Test_More", True, 1),
        ("test_NotThere", True, 1),
        ("cross_Cross", False, 0),
        ("other_Other", False, 1),
        ("test", False, 1),
    ],
)
def test_check_id_format(name, check_action, error_count):
    errors = MetadataErrors()
    check_id_format(name, TEST_SERVICES, check_action, errors)
    assert len(errors) == error_count


@pytest.mark.parametrize(
    ["a", "b", "d"],
    [
        (
            Example(
                id="ex_a",
                file=Path("file_a"),
                languages={
                    "a": Language(
                        name="a",
                        property="a",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("a_v1", ["a1_snippet"])],
                            )
                        ],
                    )
                },
                services={"a_svc": {"ActionA"}},
            ),
            Example(
                id="ex_a",
                file=Path("file_b"),
                languages={
                    "a": Language(
                        name="a",
                        property="a",
                        versions=[
                            Version(
                                sdk_version=2,
                                excerpts=[Excerpt("a_v2", ["a2_snippet"])],
                            )
                        ],
                    ),
                    "b": Language(
                        name="b",
                        property="b",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("b_v1", ["b1_snippet"])],
                            )
                        ],
                    ),
                },
                services={"b_svc": {"ActionB"}},
            ),
            Example(
                id="ex_a",
                file=Path("file_a"),
                languages={
                    "a": Language(
                        name="a",
                        property="a",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("a_v1", ["a1_snippet"])],
                            ),
                            Version(
                                sdk_version=2,
                                excerpts=[Excerpt("a_v2", ["a2_snippet"])],
                            ),
                        ],
                    ),
                    "b": Language(
                        name="b",
                        property="b",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("b_v1", ["b1_snippet"])],
                            )
                        ],
                    ),
                },
                services={"a_svc": {"ActionA"}, "b_svc": {"ActionB"}},
            ),
        )
    ],
)
def test_merge(a: Example, b: Example, d: Example):
    a.merge(b, MetadataErrors())
    assert a == d


@pytest.mark.parametrize(
    ["a", "b", "d"],
    [
        (
            Example(
                id="ex_a",
                file=Path("file_a"),
                languages={
                    "a": Language(
                        name="a",
                        property="a",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("a_v1", ["a1_snippet"])],
                            )
                        ],
                    )
                },
            ),
            Example(
                id="ex_a",
                file=Path("file_b"),
                languages={
                    "a": Language(
                        name="a",
                        property="a",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("a2_v1", ["a2_snippet"])],
                            )
                        ],
                    )
                },
            ),
            Example(
                id="ex_a",
                file=Path("file_a"),
                languages={
                    "a": Language(
                        name="a",
                        property="a",
                        versions=[
                            Version(
                                sdk_version=1,
                                excerpts=[Excerpt("a_v1", ["a1_snippet"])],
                            )
                        ],
                    ),
                },
            ),
        )
    ],
)
def test_merge_conflict(a: Example, b: Example, d: Example):
    errors = MetadataErrors()
    a.merge(b, errors)
    assert a == d
    assert errors[0] == ExampleMergeConflict(
        id=a.id, file=a.file, language="a", sdk_version=1, other_file=Path("file_b")
    )


def make_doc_link(stub: str, anchor: str = ""):
    base_url = "https://docs.aws.amazon.com/code-library/latest/ug"
    file_ext = "html"
    anchor = f"#{anchor}" if anchor else ""
    return f"{base_url}/{stub}.{file_ext}{anchor}"


def test_no_duplicate_title_abbrev():
    errors = MetadataErrors()
    doc_gen = DocGen(
        Path(__file__).parent / "test_no_duplicate_title_abbrev",
        errors=errors,
        examples={
            "a": Example(
                id="a",
                file=Path("a"),
                title_abbrev="abbr",
                category="cat",
                languages={
                    "java": Language(
                        name="java", property="java", versions=[Version(sdk_version=1)]
                    )
                },
                services={"svc": set()},
            ),
            "b": Example(
                id="b",
                file=Path("b"),
                title_abbrev="abbr",
                category="cat",
                languages={
                    "java": Language(
                        name="java", property="java", versions=[Version(sdk_version=1)]
                    )
                },
                services={"svc": set(), "cvs": set()},
            ),
        },
        services={
            "svc": Service(
                long="Service", short="svc", version="1", sort="svc", sdk_id="SVC"
            ),
            "cvs": Service(
                long="CVS", short="cvs", version="2", sort="cvs", sdk_id="CVS"
            ),
        },
    )
    doc_gen.validate()

    expected = [
        metadata_errors.DuplicateTitleAbbrev(
            id="a, b", title_abbrev="abbr", language="svc:cat"
        )
    ]

    assert expected == [*errors]


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
