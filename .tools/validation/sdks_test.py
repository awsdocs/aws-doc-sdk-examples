# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains tests that verify the examples loader finds appropriate errors
"""

import pytest
import yaml
from pathlib import Path

import metadata_errors
from sdks import parse, Sdk, SdkVersion, SdkApiRef, SdkTitleOverride


def load(path: str) -> tuple[dict[str, Sdk], metadata_errors.MetadataErrors]:
    root = Path(__file__).parent
    filename = root / "test_resources" / path
    with open(filename) as file:
        meta = yaml.safe_load(file)
    return parse(filename.name, meta)


def test_empty_sdks():
    _, errors = load("empty_sdks.yaml")
    assert [*errors] == [
        metadata_errors.MissingField(
            file="empty_sdks.yaml",
            id="C++",
            language=None,
            sdk_version=None,
            field="guide",
        )
    ]


def test_entityusage():
    _, actual = load("entityusage_sdks.yaml")
    expected = [
        metadata_errors.MappingMustBeEntity(
            file="entityusage_sdks.yaml",
            id="C++",
            field="guide",
            value="guide-cpp-dev",
        ),
        metadata_errors.MappingMustBeEntity(
            file="entityusage_sdks.yaml",
            id="C++",
            field="long",
            value="CPPlong",
        ),
        metadata_errors.MappingMustBeEntity(
            file="entityusage_sdks.yaml",
            id="C++",
            field="short",
            value="CPP",
        ),
    ]
    assert [*actual] == expected


def test_sdks():
    actual, _ = load("sdks.yaml")
    expected = {
        "C++": Sdk(
            name="C++",
            property="cpp",
            guide="&guide-cpp-dev;",
            versions=[
                SdkVersion(
                    version=1, long="&CPPlong;", short="&CPP;", bookmark="code-examples"
                )
            ],
        ),
        "Go": Sdk(
            name="Go",
            property="go",
            guide="&guide-go-dev;",
            versions=[
                SdkVersion(version=1, long="&Golong; V1", short="&Go; V1"),
                SdkVersion(version=2, long="&Golong; V2", short="&Go; V2"),
            ],
        ),
        "Java": Sdk(
            name="Java",
            property="java",
            guide="&guide-javav2-dev;",
            versions=[
                SdkVersion(version=1, long="&Javalong;", short="&Java;"),
                SdkVersion(version=2, long="&JavaV2long;", short="&Java;"),
            ],
        ),
        "JavaScript": Sdk(
            name="JavaScript",
            property="javascript",
            guide="&guide-jsb-dev;",
            versions=[
                SdkVersion(version=2, long="&JSBlong; V2", short="&JSB; V2"),
                SdkVersion(
                    version=3,
                    long="&JSBlong; V3",
                    short="&JSB; V3",
                    api_ref=SdkApiRef(
                        uid="AWSJavaScriptSDK",
                        name="&guide-jsb-api;",
                        link_template="AWSJavaScriptSDK/v3/latest/clients/client-{{.Service}}/classes/{{.OperationLower}}command.html",
                    ),
                ),
            ],
        ),
        "Kotlin": Sdk(
            name="Kotlin",
            property="kotlin",
            guide="&NO_GUIDE;",
            versions=[
                SdkVersion(
                    version=1,
                    long="&AWS; SDK for Kotlin",
                    short="&AWS; SDK for Kotlin",
                    caveat="This is prerelease documentation for a feature in preview release. It is subject to change.",
                ),
            ],
        ),
        ".NET": Sdk(
            name=".NET",
            property="csharp",
            guide="&guide-net-dev;",
            versions=[
                SdkVersion(
                    version=3,
                    long="&NETlong;",
                    short="&NET;",
                    title_override=SdkTitleOverride(
                        title="Additional &NET; code examples",
                        title_abbrev="Additional code examples",
                    ),
                ),
            ],
        ),
        "PHP": Sdk(
            name="PHP",
            property="php",
            guide="&guide-php-dev;",
            versions=[SdkVersion(version=3, long="&PHPlong;", short="&PHP;")],
        ),
        "Python": Sdk(
            name="Python",
            property="python",
            guide="&guide-python3-gsg;",
            versions=[SdkVersion(version=3, long="&Python3long;", short="&Python3;")],
        ),
        "Ruby": Sdk(
            name="Ruby",
            property="ruby",
            guide="&guide-ruby-dev;",
            versions=[SdkVersion(version=3, long="&Rubylong;", short="&Ruby;")],
        ),
    }
    assert actual == expected


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
