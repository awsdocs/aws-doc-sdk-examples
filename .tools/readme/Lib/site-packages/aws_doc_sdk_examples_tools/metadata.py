#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

from collections import defaultdict
from dataclasses import dataclass, field
from typing import Container, Dict, Literal, List, Optional, Set, Iterable
from os.path import splitext
from pathlib import Path

from . import metadata_errors
from .categories import Category
from .metadata_errors import (
    MetadataErrors,
    MetadataParseError,
    ExampleMergeMismatchedId,
    ExampleMergeMismatchedLanguage,
    ExampleMergeConflict,
)


@dataclass
class Url:
    title: str
    url: Optional[str]


@dataclass
class Person:
    name: str
    alias: str


@dataclass
class Excerpt:
    description: Optional[str]
    # Tags embedded in source files to extract as snippets.
    snippet_tags: List[str]
    # A path within the repo to extract the entire file as a snippet.
    snippet_files: List[str] = field(default_factory=list)
    # The amount to which generative AI was used to create this content.
    #
    # none: No GenAI generated content is included in this excerpt, but it may have been consulted for reference.
    # some: A human wrote this content, though some portions may be copied or inserted from a GenAI tool.
    # most: The bulk of this content was written by a GenAI tool, though a human has edited and reviewed it for accuracy.
    # all: This content was entirely written by GenAI, and has not been reviewed by a human.
    genai: Literal["none", "some", "most", "all"] = "none"

    def validate(self, errors: MetadataErrors):
        pass


@dataclass
class Version:
    sdk_version: int
    # Additional ZonBook XML to include in the tab for this sample.
    block_content: Optional[str] = field(default=None)
    # The specific code samples to include in the example.
    excerpts: List[Excerpt] = field(default_factory=list)
    # Link to the source code for this example. TODO rename.
    github: Optional[str] = field(default=None)
    # Deprecated. Replace with guide_topic list.
    sdkguide: Optional[str] = field(default=None)
    # Link to additional topic places.
    more_info: List[Url] = field(default_factory=list)
    # List of people who have contributed to this example.
    authors: List[Person] = field(default_factory=list)
    # Feedback and maintenance owner. Primarily for internal use.
    owner: Optional[str] = field(default=None)
    # Link to the original tributary that contributed this version.
    source: Optional[Url] = field(default=None)

    def validate(self, errors: MetadataErrors, root: Path):
        github = self.github
        if github is not None:
            _, ext = splitext(github)
            if ext != "":
                errors.append(
                    metadata_errors.InvalidGithubLink(
                        link=github, sdk_version=self.sdk_version
                    )
                )
            elif github.startswith("http"):
                pass  # Tributaries specify full GitHub path. Consider passing in GitHub root from tributaries and doing a full check at some point.
            elif not (root / github).exists():
                errors.append(
                    metadata_errors.MissingGithubLink(
                        link=github, sdk_version=self.sdk_version, root=root
                    )
                )

        for excerpt in self.excerpts:
            excerpt.validate(errors)


@dataclass
class Language:
    name: str
    # A downcased, special-character-free version of the name. Matches a key of the same name in sdks.yaml. Used for syntax parser.
    property: str
    versions: List[Version]

    def merge(self, other: "Language", errors: MetadataErrors):
        """Add new versions from `other`"""
        if self.name != other.name:
            errors.append(
                ExampleMergeMismatchedLanguage(
                    language=self.name, other_lang=other.name
                )
            )
            return
        self_versions = {v.sdk_version for v in self.versions}
        for other_version in other.versions:
            if other_version.sdk_version in self_versions:
                errors.append(
                    ExampleMergeConflict(
                        language=self.name, sdk_version=other_version.sdk_version
                    )
                )
            else:
                self.versions.append(other_version)
            # Merge down to the SDK Version level, so later guides can add new
            # excerpts to existing examples, but don't try to merge the excerpts
            # within the language. If a tributary or writer feels they need to
            # modify an excerpt, they should go modify the excerpt directly.

    def validate(self, errors: MetadataErrors, root: Path):
        errs = MetadataErrors()
        for version in self.versions:
            version.validate(errs, root)
        for error in errs:
            if isinstance(error, MetadataParseError):
                error.language = self.name
        errors.extend(errs)


@dataclass
class Example:
    id: str
    file: Optional[Path]
    languages: Dict[str, Language]
    # Human readable title.
    title: Optional[str] = field(default="")
    # Used in the TOC.
    title_abbrev: Optional[str] = field(default="")
    synopsis: Optional[str] = field(default="")
    # String label categories. Categories inferred by cross-service with multiple services, and can be whatever else it wants. Controls where in the TOC it appears.
    category: Optional[str] = field(default=None)
    # Link to additional topic places.
    guide_topic: Optional[Url] = field(default=None)  # TODO: Url|List[Url]
    # TODO how to add a language here and require it in services_schema.
    # TODO document service_main and services. Not to be used by tributaries. Part of Cross Service.
    # List of services used by the examples. Lines up with those in services.yaml.
    service_main: Optional[str] = field(default=None)
    # Main service sdk_id. Matches Smithy model svc_id in services.yaml.
    service_sdk_id: Optional[str] = field(default="")
    services: Dict[str, Set[str]] = field(default_factory=dict)
    # HTML file names corresponding to the documentation pages in the Code Library
    doc_filenames: Optional[DocFilenames] = field(default=None)
    synopsis_list: List[str] = field(default_factory=list)
    source_key: Optional[str] = field(default=None)

    def fill_display_fields(self, categories: Dict[str, Category], service, action):
        category = self.choose_category(categories)
        if category:
            self.title = category.evaluate(
                self.title, lambda x: x.title, service, action
            )
            self.title_abbrev = category.evaluate(
                self.title_abbrev, lambda x: x.title_abbrev, service, action
            )
            self.synopsis = category.evaluate(
                self.synopsis, lambda x: x.synopsis, service, action
            )

    def choose_category(self, categories: Dict[str, Category]) -> Optional[Category]:
        """Find a category for an example. This logic is taken from directories and zexii.

        Original Zexii code at https://code.amazon.com/packages/GoAmzn-AWSDocsCodeExampleDocBuilder/blobs/1321fffadd8ff02e6acbae4a1f42b81006cdfa72/--/zexi/zonbook/category.go#L31-L50.
        """
        if self.category in categories:
            return categories[self.category]
        if len(self.services) == 1:
            return categories["Actions"]
        return categories["Scenarios"]

    def merge(self, other: Example, errors: MetadataErrors):
        """Combine `other` Example into self example.

        Merge down to the SDK Version level, so later guides can add new excerpts to existing examples, but don't try to merge the excerpts within the language.
        If a tributary or writer feels they need to modify an excerpt, they should go modify the excerpt directly.

        Keep title, title_abbrev, synopsis, guide_topic, category, service_main, synopsis_list, and source_key from source (typically awsdocs/aws-doc-sdk-examples).
        !NOTE: This means `merge` is NOT associative!

        Add error if IDs are not the same and return early.
        """
        if self.id != other.id:
            errors.append(
                ExampleMergeMismatchedId(
                    id=self.id, other_id=other.id, file=self.file, other_file=other.file
                )
            )
            return

        for service, actions in other.services.items():
            if service not in self.services:
                self.services[service] = actions

        for name, language in other.languages.items():
            if name not in self.languages:
                self.languages[name] = language
            else:
                merge_errs = MetadataErrors()
                self.languages[name].merge(language, merge_errs)
                for err in merge_errs:
                    err.id = self.id
                    err.file = self.file
                    if hasattr(err, "other_file"):
                        err.other_file = other.file  # type: ignore
                errors.extend(merge_errs)

    def validate(
        self, errors: MetadataErrors, known_services: Container[str], root: Path
    ):
        errs = MetadataErrors()
        for service in self.services.keys():
            if service not in known_services:
                errors.append(
                    metadata_errors.UnknownService(
                        id=self.id, file=self.file, service=service
                    )
                )
        for language in self.languages.values():
            language.validate(errs, root)
        for error in errs:
            error.file = self.file
            error.id = self.id
        errors.extend(errs)


ALLOWED = ["&AWS;", "&AWS-Region;", "&AWS-Regions;" "AWSJavaScriptSDK"]


@dataclass
class CrossServicePage:
    cross: str


@dataclass
class SDKPageVersion:
    """
    A mapping of example type to a dictionary of service id's
    and their documentation URL. Cross service example have a
    special service id named `cross`.
    """

    actions_scenarios: Optional[Dict[str, str]] = None
    cross_service: Optional[CrossServicePage] = None


SDKPageLanguage = Dict[int, SDKPageVersion]
SDKPages = Dict[str, SDKPageLanguage]

ServicePages = Dict[str, str]


@dataclass
class DocFilenames:
    """
    Names that match the one or more Code Library entries for a single example.

    Example structure:
    ```
    transcribe_app.doc_filenames == {
        "service_pages": {
            "cognito-identity": "https://docs.aws.amazon.com/code-library/latest/ug/cognito-identity_example_cross_TranscriptionApp_section.html",
            "transcribe": "https://docs.aws.amazon.com/code-library/latest/ug/transcribe_example_cross_TranscriptionApp_section.html",
        },
        "sdk_pages": {
            "JavaScript": {
                3: {
                    "cross_service": {
                        "cross": "https://docs.aws.amazon.com/code-library/latest/ug/cross_TranscriptionStreamingApp_javascript_3_topic.html"
                    }
                }
            }
        },
    }

    # A multi service scenario
    resilient_service.doc_file_names == {
        "service_pages": {
            "auto-scaling": "https://docs.aws.amazon.com/code-library/latest/ug/auto-scaling_example_cross_ResilientService_section.html",
            "ec2": "https://docs.aws.amazon.com/code-library/latest/ug/ec2_example_cross_ResilientService_section.html",
        },
        "sdk_pages": {
            "JavaScript": {
                3: {
                    "actions_scenarios": {
                        "auto-scaling": "https://docs.aws.amazon.com/code-library/latest/ug/javascript_3_auto-scaling_code_examples.html",
                        "ec2": "https://docs.aws.amazon.com/code-library/latest/ug/javascript_3_ec2_code_examples.html"
                    }
                }
            },
        },
    }
    ```
    """

    service_pages: Optional[ServicePages] = None
    sdk_pages: Optional[SDKPages] = None


def validate_no_duplicate_api_examples(
    examples: Iterable[Example], errors: MetadataErrors
):
    """Call this on a full set of examples to verify that there are no duplicate API examples."""
    svc_action_map: Dict[str, List[str]] = defaultdict(list)
    title_abbr_map: Dict[str, Dict[str, List[str]]] = defaultdict(
        lambda: defaultdict(list)
    )
    for example in examples:
        if example.category == "Api":
            for service, actions in example.services.items():
                for action in actions:
                    svc_action_map[f"{service}:{action}"].append(example.id)
        if example.title_abbrev:
            for service in example.services.keys():
                title_abbr_map[example.title_abbrev][
                    f"{service}:{example.category}"
                ].append(example.id)
    for svc_action, ex_items in svc_action_map.items():
        if len(ex_items) > 1:
            errors.append(
                metadata_errors.DuplicateAPIExample(
                    id=", ".join({ex_item for ex_item in ex_items}),
                    svc_action=svc_action,
                    duplicates=ex_items,
                )
            )
    for title_abbrev, languages in title_abbr_map.items():
        for lang, ids in languages.items():
            if len(ids) > 1:
                errors.append(
                    metadata_errors.DuplicateTitleAbbrev(
                        id=", ".join(ids),
                        title_abbrev=title_abbrev,
                        language=lang,
                    )
                )
