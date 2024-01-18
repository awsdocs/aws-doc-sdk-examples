#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from dataclasses import dataclass, field
from typing import Any, Optional, Self
from os.path import splitext
import metadata_errors

from metadata_errors import MetadataErrors, MetadataParseError, DuplicateItemException
from metadata_validator import StringExtension
from services import Service
from sdks import Sdk


@dataclass
class Url:
    title: str
    url: Optional[str]

    @classmethod
    def from_yaml(
        cls, yaml: None | dict[str, str | None]
    ) -> None | Self | MetadataParseError:
        if yaml is None:
            return None
        title = yaml.get("title", "")
        url = yaml.get("url", "")

        if title is None:
            return metadata_errors.URLMissingTitle(url=str(url))

        return cls(title, url)


@dataclass
class Excerpt:
    description: Optional[str]
    # Tags embedded in source files to extract as snippets.
    snippet_tags: list[str]
    # A path within the repo to extract the entire file as a snippet.
    snippet_files: list[str] = field(default_factory=list)

    @classmethod
    def from_yaml(cls, yaml: Any) -> Self:
        description = yaml.get("description")
        snippet_files = [str(file) for file in yaml.get("snippet_files", [])]
        snippet_tags = [str(tag) for tag in yaml.get("snippet_tags", [])]
        return cls(description, snippet_tags, snippet_files)


@dataclass
class Version:
    sdk_version: int
    # Additional ZonBook XML to include in the tab for this sample.
    block_content: Optional[str] = field(default=None)
    # The specific code samples to include in the example.
    excerpts: list[Excerpt] = field(default_factory=list)
    # Link to the source code for this example. TODO rename.
    github: Optional[str] = field(default=None)
    add_services: dict[str, list[str]] = field(default_factory=dict)
    # Deprecated. Replace with guide_topic list.
    sdkguide: Optional[str] = field(default=None)
    # Link to additional topic places. TODO: Overwritten by aws-doc-sdk-example when merging.
    more_info: list[Url] = field(default_factory=list)

    @classmethod
    def from_yaml(
        cls,
        yaml: dict[str, Any],
        services: dict[str, Service],
        cross_content_blocks: set[str],
        is_action: bool,
    ) -> tuple[Self, MetadataErrors]:
        errors = MetadataErrors()

        sdk_version = int(yaml.get("sdk_version", 0))
        if sdk_version == 0:
            errors.append(metadata_errors.MissingField(field="sdk_version"))

        block_content = yaml.get("block_content")
        github = yaml.get("github")
        sdkguide = yaml.get("sdkguide")

        if sdkguide is not None:
            if sdkguide.startswith("https://docs.aws.amazon.com"):
                errors.append(metadata_errors.InvalidSdkGuideStart(guide=sdkguide))

        if github is not None:
            _, ext = splitext(github)
            if ext != "":
                errors.append(
                    metadata_errors.InvalidGithubLink(
                        link=github, sdk_version=sdk_version
                    )
                )

        excerpts = [Excerpt.from_yaml(excerpt) for excerpt in yaml.get("excerpts", [])]

        if len(excerpts) == 0 and block_content is None:
            errors.append(metadata_errors.MissingBlockContentAndExcerpt())
            excerpts = []
        if len(excerpts) > 0 and block_content is not None:
            errors.append(metadata_errors.BlockContentAndExcerptConflict())

        more_info: list[Url] = []
        for url in yaml.get("more_info", []):
            url = Url.from_yaml(url)
            if isinstance(url, Url):
                more_info.append(url)
            elif url is not None:
                errors.append(url)

        add_services = parse_services(yaml.get("add_services", {}), errors, services)
        if add_services and is_action:
            errors.append(metadata_errors.APIExampleCannotAddService())

        if block_content is not None and block_content not in cross_content_blocks:
            errors.append(metadata_errors.MissingCrossContent(block=block_content))

        return (
            cls(
                sdk_version,
                block_content,
                excerpts,
                github,
                add_services,
                sdkguide,
                more_info,
            ),
            errors,
        )


@dataclass
class Language:
    name: str
    versions: list[Version]

    @classmethod
    def from_yaml(
        cls,
        name: str,
        yaml: Any,
        sdks: dict[str, Sdk],
        services: dict[str, Service],
        blocks: set[str],
        is_action: bool,
    ) -> tuple[Self, MetadataErrors]:
        errors = MetadataErrors()
        if name not in sdks:
            errors.append(metadata_errors.UnknownLanguage(language=name))

        yaml_versions: list[dict[str, Any]] | None = yaml.get("versions")
        if yaml_versions is None or len(yaml_versions) == 0:
            errors.append(metadata_errors.MissingField(field="versions"))
            yaml_versions = []

        versions: list[Version] = []
        for version in yaml_versions:
            version, version_errors = Version.from_yaml(
                version, services, blocks, is_action
            )
            errors.extend(version_errors)
            versions.append(version)

        for error in errors:
            error.language = name

        return cls(name, versions), errors


@dataclass
class Example:
    id: str
    file: str
    # Human readable title. TODO: Defaults to slug-to-title of the ID if not provided.
    title: str
    # Used in the TOC. TODO: Defaults to slug-to-title of the ID if not provided.
    title_abbrev: str
    synopsis: str
    languages: dict[str, Language]
    # String label categories. Categories inferred by cross-service with multiple services, and can be whatever else it wants. Controls where in the TOC it appears.
    category: Optional[str] = field(default=None)
    # Link to additional topic places.
    guide_topic: Optional[Url] = field(default=None)  # TODO: Url|list[Url]
    # TODO how to add a language here and require it in services_schema.
    # TODO document service_main and services. Not to be used by tributaries. Part of Cross Service.
    # List of services used by the examples. Lines up with those in services.yaml.
    service_main: Optional[str] = field(default=None)
    services: dict[str, list[str]] = field(default_factory=dict)
    synopsis_list: list[str] = field(default_factory=list)
    source_key: Optional[str] = field(default=None)

    @classmethod
    def from_yaml(
        cls,
        yaml: Any,
        sdks: dict[str, Sdk],
        services: dict[str, Service],
        blocks: set[str],
    ) -> tuple[Self, MetadataErrors]:
        errors = MetadataErrors()

        title = get_with_valid_entities("title", yaml, errors)
        title_abbrev = get_with_valid_entities("title_abbrev", yaml, errors)
        synopsis = get_with_valid_entities("synopsis", yaml, errors, opt=True)

        category = yaml.get("category", "")
        source_key = yaml.get("source_key")
        parsed_services = parse_services(yaml.get("services", {}), errors, services)
        synopsis_list = [str(syn) for syn in yaml.get("synopsis_list", [])]
        guide_topic = Url.from_yaml(yaml.get("guide_topic"))
        if isinstance(guide_topic, MetadataParseError):
            errors.append(guide_topic)
            guide_topic = None

        service_main = yaml.get("service_main", None)
        if service_main is not None and service_main not in services:
            try:
                errors.append(metadata_errors.UnknownService(service=service_main))
            except DuplicateItemException:
                pass

        if category == "":
            category = "Api" if len(parsed_services) == 1 else "Cross"

        is_action = category == "Api"

        yaml_languages = yaml.get("languages")
        languages: dict[str, Language] = {}
        if yaml_languages is None:
            errors.append(metadata_errors.MissingField(field="languages"))
        else:
            for name in yaml_languages:
                language, errs = Language.from_yaml(
                    name, yaml_languages[name], sdks, services, blocks, is_action
                )
                languages[language.name] = language
                errors.extend(errs)

        return (
            cls(
                id="",
                file="",
                title=title,
                title_abbrev=title_abbrev,
                category=category,
                guide_topic=guide_topic,
                languages=languages,
                service_main=service_main,
                services=parsed_services,
                synopsis=synopsis,
                synopsis_list=synopsis_list,
                source_key=source_key,
            ),
            errors,
        )


def parse_services(
    yaml: Any, errors: MetadataErrors, known_services: dict[str, Service]
) -> dict[str, list[str]]:
    if yaml is None:
        return {}
    services: dict[str, list[str]] = {}
    for name in yaml:
        if name not in known_services:
            errors.append(metadata_errors.UnknownService(service=name))
        else:
            service: dict[str, None] | None = yaml.get(name)
            # While .get replaces missing with {}, `sqs: ` in yaml parses a literal `None`
            if service is None:
                service = {}
            # Make a copy of the dict
            services[name] = [*service.keys()]
    return services


ALLOWED = ["&AWS;", "&AWS-Region;", "&AWS-Regions;" "AWSJavaScriptSDK"]


def get_with_valid_entities(
    name: str, d: dict[str, str], errors: MetadataErrors, opt: bool = False
) -> str:
    field = d.get(name)
    if field is None:
        if not opt:
            errors.append(metadata_errors.MissingField(field=name))
        return ""

    checker = StringExtension()
    if not checker.is_valid(field):
        errors.append(
            metadata_errors.AwsNotEntity(
                field=name, value=field, check_err=checker.get_name()
            )
        )
        return ""
    return field


def idFormat(id: str, services: dict[str, Service]) -> bool:
    [service, *rest] = id.split("_")
    if len(rest) == 0:
        return False
    return service in services or service == "cross"


def parse(
    file: str,
    yaml: dict[str, Any],
    sdks: dict[str, Sdk],
    services: dict[str, Service],
    blocks: set[str],
) -> tuple[list[Example], MetadataErrors]:
    examples: list[Example] = []
    errors = MetadataErrors()
    for id in yaml:
        if not idFormat(id, services):
            errors.append(metadata_errors.NameFormat(file=file, id=id))
        example, example_errors = Example.from_yaml(yaml[id], sdks, services, blocks)
        for error in example_errors:
            error.file = file
            error.id = id
        errors.extend(example_errors)
        example.file = file
        example.id = id
        examples.append(example)

    return examples, errors


def main():
    import yaml
    from pathlib import Path

    path = (
        Path(__file__).parent.parent.parent
        / ".doc_gen"
        / "metadata"
        / "s3_metadata.yaml"
    )
    with open(path) as file:
        meta = yaml.safe_load(file)
    (examples, errors) = parse(path.name, meta, {}, {}, set())
    if len(errors) > 0:
        print(f"{errors}")
    else:
        print(f"{examples!r}")


if __name__ == "__main__":
    main()
