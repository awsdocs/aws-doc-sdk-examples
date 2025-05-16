# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

from typing import Any, Dict, List, Optional
from dataclasses import dataclass, field

from aws_doc_sdk_examples_tools import metadata_errors
from .metadata_errors import (
    MetadataErrors,
    MetadataParseError,
    check_mapping,
)


@dataclass
class SdkVersionExpanded:
    long: str
    short: str


@dataclass
class SdkApiRef:
    uid: str
    name: str
    link_template: Optional[str]

    @classmethod
    def from_yaml(
        cls, yaml: Dict[str, str] | None, errors: MetadataErrors
    ) -> Optional[SdkApiRef]:
        if yaml is None:
            return None
        uid = yaml.get("uid")
        name = check_mapping(yaml.get("name"), "api_ref.name")
        link_template = yaml.get("link_template")

        if uid is None:
            errors.append(metadata_errors.MissingField(field="api_ref.uid"))
            uid = ""
        if isinstance(name, MetadataParseError):
            errors.append(name)
            name = ""

        return cls(uid, name, link_template)


@dataclass
class SdkTitleOverride:
    title: str
    title_abbrev: str


@dataclass
class SdkVersion:
    version: int
    long: str
    short: str
    expanded: Optional[SdkVersionExpanded] = field(default=None)
    suppress_version_heading: bool = field(default=False)
    guide: Optional[str] = field(default=None)
    api_ref: Optional[SdkApiRef] = field(default=None)
    caveat: Optional[str] = field(default=None)
    bookmark: Optional[str] = field(default=None)
    title_override: Optional[SdkTitleOverride] = field(default=None)

    @classmethod
    def from_yaml(
        cls, version: int, yaml: Dict[str, Any], strict: bool
    ) -> tuple[SdkVersion, MetadataErrors]:
        errors = MetadataErrors()
        long = check_mapping(yaml.get("long"), "long", strict)
        short = check_mapping(yaml.get("short"), "short", strict)
        suppress_version_heading = yaml.get("suppress_version_heading", False)
        guide = yaml.get("guide")
        caveat = yaml.get("caveat")
        bookmark = yaml.get("bookmark")

        expanded = yaml.get("expanded")
        if expanded is not None:
            long_expanded = expanded.get("long")
            short_expanded = expanded.get("short")
            if not long_expanded:
                errors.append(metadata_errors.MissingField(field="expanded.long"))
            if not short_expanded:
                errors.append(metadata_errors.MissingField(field="expanded.short"))
            expanded = SdkVersionExpanded(long=long_expanded, short=short_expanded)

        title_override = yaml.get("title_override")
        if title_override is not None:
            title = title_override.get("title")
            title_abbrev = title_override.get("title_abbrev")
            if not title:
                errors.append(
                    metadata_errors.MissingField(field="title_override.title")
                )
            if not title_abbrev:
                errors.append(
                    metadata_errors.MissingField(field="title_override.title_abbrev")
                )
            title_override = SdkTitleOverride(title=title, title_abbrev=title_abbrev)

        if isinstance(long, MetadataParseError):
            errors.append(long)
            long = ""
        if isinstance(short, MetadataParseError):
            errors.append(short)
            short = ""
        api_ref = SdkApiRef.from_yaml(yaml.get("api_ref"), errors)

        return (
            cls(
                version=version,
                long=long,
                short=short,
                expanded=expanded,
                suppress_version_heading=suppress_version_heading,
                guide=guide,
                api_ref=api_ref,
                caveat=caveat,
                bookmark=bookmark,
                title_override=title_override,
            ),
            errors,
        )


@dataclass
class SdkWithNoVersionsError(metadata_errors.MetadataError):
    def message(self):
        return "SDK has no versions"


@dataclass
class Sdk:
    name: str
    display: str
    versions: List[SdkVersion]
    guide: str
    property: str
    is_pseudo_sdk: bool

    def validate(self, errors: MetadataErrors):
        if len(self.versions) == 0:
            errors.append(SdkWithNoVersionsError(id=self.name))

    @classmethod
    def from_yaml(
        cls, name: str, yaml: Dict[str, Any], strict: bool
    ) -> tuple[Sdk, MetadataErrors]:
        errors = MetadataErrors()
        display = yaml.get("display", name)
        property = yaml.get("property", "")
        guide = check_mapping(yaml.get("guide"), "guide")
        is_pseudo_sdk = yaml.get("is_pseudo_sdk", False)
        if isinstance(guide, MetadataParseError):
            errors.append(guide)
            guide = ""

        versions: List[SdkVersion] = []
        sdk_versions: None | Dict[str, Any] = yaml.get("sdk")
        sdk_versions = sdk_versions or {}
        for version in sdk_versions:
            (sdk_version, errs) = SdkVersion.from_yaml(
                int(version), sdk_versions[version], strict
            )
            versions.append(sdk_version)
            errors.extend(errs)

        return (
            cls(
                name=name,
                display=display,
                versions=versions,
                guide=guide,
                property=property,
                is_pseudo_sdk=is_pseudo_sdk,
            ),
            errors,
        )


def parse(
    file: Path, yaml: Dict[str, Any], strict: bool = True
) -> tuple[Dict[str, Sdk], MetadataErrors]:
    sdks: Dict[str, Sdk] = {}
    errors = MetadataErrors()

    for name in yaml:
        sdk, errs = Sdk.from_yaml(name, yaml[name], strict)
        sdks[name] = sdk
        for error in errs:
            error.file = file
            error.id = name
        errors.extend(errs)

    return sdks, errors


if __name__ == "__main__":
    import yaml
    from pathlib import Path

    path = Path(__file__).parent.parent.parent / ".doc_gen" / "metadata" / "sdks.yaml"
    with open(path) as file:
        meta = yaml.safe_load(file)
    examples, errors = parse(path, meta)
    print(f"{examples}")
