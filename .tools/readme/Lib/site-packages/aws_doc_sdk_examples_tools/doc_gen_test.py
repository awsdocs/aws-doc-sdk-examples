# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Test for that parts of DocGen that aren't file I/O.
"""

import pytest
from pathlib import Path
import json

from .categories import Category, TitleInfo
from .doc_gen import DocGen, DocGenEncoder
from .metadata import Example
from .metadata_errors import MetadataErrors, MetadataError, UnknownLanguage
from .sdks import Sdk, SdkVersion
from .services import Service, ServiceExpanded
from .snippets import Snippet


@pytest.mark.parametrize(
    ["a", "b", "d"],
    [
        (
            DocGen(
                root=Path("/a"),
                errors=MetadataErrors(),
                sdks={
                    "a": Sdk(
                        name="a",
                        display="aa",
                        guide="guide_a",
                        property="a_prop",
                        versions=[],
                        is_pseudo_sdk=False,
                    ),
                },
                services={
                    "x": Service(
                        long="AWS X", short="X", sort="aws x", version=1, sdk_id="AWSx"
                    )
                },
            ),
            DocGen(
                root=Path("/b"),
                errors=MetadataErrors(),
                sdks={
                    "b": Sdk(
                        name="b",
                        display="bb",
                        guide="guide_b",
                        property="b_prop",
                        versions=[],
                        is_pseudo_sdk=False,
                    ),
                },
                services={
                    "y": Service(
                        long="AWS Y", short="Y", sort="aws y", version=1, sdk_id="AWSy"
                    )
                },
            ),
            DocGen(
                root=Path("/a"),
                errors=MetadataErrors(),
                sdks={
                    "a": Sdk(
                        name="a",
                        display="aa",
                        guide="guide_a",
                        property="a_prop",
                        versions=[],
                        is_pseudo_sdk=False,
                    ),
                    "b": Sdk(
                        name="b",
                        display="bb",
                        guide="guide_b",
                        property="b_prop",
                        versions=[],
                        is_pseudo_sdk=False,
                    ),
                },
                services={
                    "x": Service(
                        long="AWS X", short="X", sort="aws x", version=1, sdk_id="AWSx"
                    ),
                    "y": Service(
                        long="AWS Y", short="Y", sort="aws y", version=1, sdk_id="AWSy"
                    ),
                },
            ),
        )
    ],
)
def test_merge(a: DocGen, b: DocGen, d: DocGen):
    a.merge(b)
    assert a == d


def test_incremental():
    errors = MetadataErrors()
    doc_gen = DocGen(Path(), errors).for_root(
        Path(__file__).parent / "test_resources", incremental=False
    )
    assert len(doc_gen.examples) == 0
    doc_gen.process_metadata(doc_gen.root / "awsentity_metadata.yaml")
    assert len(doc_gen.examples) == 5
    doc_gen.process_metadata(doc_gen.root / "valid_metadata.yaml")
    assert len(doc_gen.examples) == 6


@pytest.fixture
def sample_doc_gen() -> DocGen:
    metadata_errors = MetadataErrors()
    metadata_errors._errors = [MetadataError(file=Path("filea.txt"), id="Error a")]
    return DocGen(
        root=Path("/test/root"),
        errors=metadata_errors,
        prefix="test_prefix",
        entities={
            "&S3long;": "Amazon Simple Storage Service",
            "&S3;": "Amazon S3",
            "&PYLong;": "Python SDK v1",
            "&PYShort;": "Python V1",
        },
        sdks={
            "python": Sdk(
                name="python",
                display="Python",
                versions=[SdkVersion(version=1, long="&PYLong;", short="&PYShort;")],
                guide="Python Guide",
                property="python",
                is_pseudo_sdk=False,
            )
        },
        services={
            "s3": Service(
                long="&S3long;",
                short="&S3;",
                expanded=ServiceExpanded(
                    long="Amazon Simple Storage Service", short="Amazon S3"
                ),
                sort="Amazon S3",
                version="2006-03-01",
                sdk_id="S3",
            )
        },
        snippets={
            "test_snippet": Snippet(
                id="test_snippet",
                file="test.py",
                line_start=1,
                line_end=5,
                code="print('Hello, World!')",
            )
        },
        snippet_files={"test.py"},
        examples={
            "s3_PutObject": Example(
                "s3_PutObject",
                file=Path("filea.txt"),
                languages={},
                service_sdk_id="S3",
                services={"s3": set(["PutObject"])},
            )
        },
        categories={
            "Actions": Category(
                "Actions",
                "Actions",
                defaults=TitleInfo(
                    title="<code>{{.Action}}</code>",
                    synopsis="{{.ServiceEntity.Short}} {{.Action}}",
                ),
                overrides=TitleInfo(title_abbrev="ExcerptPartsUsage"),
            )
        },
        cross_blocks={"test_block"},
    )


def test_expand_entities(sample_doc_gen: DocGen):
    expanded, errors = sample_doc_gen.expand_entities("Hello &S3;")
    assert expanded == "Hello Amazon S3"
    assert not errors


def test_expand_entity_fields(sample_doc_gen: DocGen):
    error_count = len(sample_doc_gen.errors)
    sample_doc_gen.expand_entity_fields(sample_doc_gen)
    assert sample_doc_gen.services["s3"].long == "Amazon Simple Storage Service"
    assert sample_doc_gen.sdks["python"].versions[0].long == "Python SDK v1"
    # The fixture has an error, so make sure we don't have _more_ errors.
    assert error_count == len(sample_doc_gen.errors)


def test_doc_gen_encoder(sample_doc_gen: DocGen):
    encoded = json.dumps(sample_doc_gen, cls=DocGenEncoder)
    decoded = json.loads(encoded)

    # Verify that the root path is not included in the encoded output
    assert "/test/root" not in decoded
    assert decoded["root"] == "root"

    # Verify SDK information
    assert "sdks" in decoded
    assert "python" in decoded["sdks"]
    assert decoded["sdks"]["python"]["name"] == "python"
    assert decoded["sdks"]["python"]["display"] == "Python"
    assert decoded["sdks"]["python"]["guide"] == "Python Guide"
    assert decoded["sdks"]["python"]["is_pseudo_sdk"] == False
    assert decoded["sdks"]["python"]["versions"][0]["version"] == 1
    assert decoded["sdks"]["python"]["versions"][0]["long"] == "&PYLong;"

    # Verify service information
    assert "services" in decoded
    assert "s3" in decoded["services"]
    assert decoded["services"]["s3"]["long"] == "&S3long;"
    assert decoded["services"]["s3"]["short"] == "&S3;"

    # Verify snippet information
    assert "snippets" in decoded
    assert "test_snippet" in decoded["snippets"]
    assert decoded["snippets"]["test_snippet"]["id"] == "test_snippet"
    assert decoded["snippets"]["test_snippet"]["file"] == "test.py"
    assert decoded["snippets"]["test_snippet"]["code"] == "print('Hello, World!')"

    # Verify snippet files
    assert "snippet_files" in decoded
    assert decoded["snippet_files"] == {"__set__": ["test.py"]}

    # Verify cross blocks
    assert "cross_blocks" in decoded
    assert decoded["cross_blocks"] == {"__set__": ["test_block"]}

    # Verify that errors are properly encoded
    assert "errors" in decoded
    assert decoded["errors"] == {
        "__metadata_errors__": [{"file": "filea.txt", "id": "Error a"}]
    }

    # Verify prefix
    assert decoded["prefix"] == "test_prefix"

    # Verify examples (empty in this case)
    assert "examples" in decoded
    assert decoded["examples"] == {
        "s3_PutObject": {
            "category": None,
            "doc_filenames": None,
            "file": "filea.txt",
            "guide_topic": None,
            "id": "s3_PutObject",
            "languages": {},
            "service_main": None,
            "service_sdk_id": "S3",
            "services": {
                "s3": {
                    "__set__": [
                        "PutObject",
                    ],
                },
            },
            "source_key": None,
            "synopsis": "",
            "synopsis_list": [],
            "title": "",
            "title_abbrev": "",
        },
    }


def test_doc_gen_load_snippets():
    errors = MetadataErrors()
    doc_gen = DocGen(Path(), errors).for_root(
        Path(__file__).parent / "test_resources", incremental=False
    )
    doc_gen.process_metadata(doc_gen.root / "valid_metadata.yaml")
    doc_gen.collect_snippets()
    assert doc_gen.snippet_files == set(["snippet_file.txt"])
    assert doc_gen.snippets["snippet_file.txt"].code == "Line A\nLine C\n"


def test_fill_fields(sample_doc_gen: DocGen):
    sample_doc_gen.fill_missing_fields()
    example = sample_doc_gen.examples["s3_PutObject"]
    assert example.title == "<code>PutObject</code>"
    assert example.title_abbrev == "ExcerptPartsUsage"
    assert example.synopsis == "&S3; PutObject"


def test_language_not_in_sdks():
    errors = MetadataErrors()
    doc_gen = DocGen(Path(), errors).for_root(
        Path(__file__).parent / "test_resources", incremental=False
    )
    doc_gen.process_metadata(doc_gen.root / "bad_language_example.yaml")
    assert isinstance(doc_gen.errors[0], UnknownLanguage)
