# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from typing import Any, Self, Optional
import metadata_errors
from metadata_errors import MetadataErrors, MetadataParseError, check_mapping
from dataclasses import dataclass, field


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
        cls, yaml: dict[str, str] | None, errors: MetadataErrors
    ) -> None | Self:
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
    guide: Optional[str] = field(default=None)
    api_ref: Optional[SdkApiRef] = field(default=None)
    caveat: Optional[str] = field(default=None)
    bookmark: Optional[str] = field(default=None)
    title_override: Optional[SdkTitleOverride] = field(default=None)

    @classmethod
    def from_yaml(
        cls, version: int, yaml: dict[str, Any]
    ) -> tuple[Self, MetadataErrors]:
        errors = MetadataErrors()
        long = check_mapping(yaml.get("long"), "long")
        short = check_mapping(yaml.get("short"), "short")
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
                guide=guide,
                api_ref=api_ref,
                caveat=caveat,
                bookmark=bookmark,
                title_override=title_override,
            ),
            errors,
        )


@dataclass
class Sdk:
    name: str
    versions: list[SdkVersion]
    guide: str
    property: str

    @classmethod
    def from_yaml(cls, name: str, yaml: dict[str, Any]) -> tuple[Self, MetadataErrors]:
        errors = MetadataErrors()
        property = yaml.get("property", "")
        guide = check_mapping(yaml.get("guide"), "guide")
        if isinstance(guide, MetadataParseError):
            errors.append(guide)
            guide = ""

        versions: list[SdkVersion] = []
        sdk_versions: None | dict[str, Any] = yaml.get("sdk")
        if sdk_versions is None:
            sdk_versions = {}
        for version in sdk_versions:
            (sdk_version, errs) = SdkVersion.from_yaml(
                int(version), sdk_versions[version]
            )
            versions.append(sdk_version)
            errors.extend(errs)

        return cls(name=name, versions=versions, guide=guide, property=property), errors


def parse(file: str, yaml: dict[str, Any]) -> tuple[dict[str, Sdk], MetadataErrors]:
    sdks: dict[str, Sdk] = {}
    errors = MetadataErrors()

    for name in yaml:
        sdk, errs = Sdk.from_yaml(name, yaml[name])
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
    examples, errors = parse(path.name, meta)
    print(f"{examples}")
