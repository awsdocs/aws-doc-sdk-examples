from typing import Self, Optional
import metadata_errors
from metadata_errors import MetadataErrors, MetadataParseError
from dataclasses import dataclass, field
import re


@dataclass
class SdkVersionExpanded:
    long: str
    short: str


@dataclass
class SdkApiRef:
    uid: str
    name: str
    link_template: Optional[str]

    def from_yaml(yaml: dict[str, str] | None, errors: MetadataErrors) -> Self | None:
        if yaml is None:
            return None
        uid = yaml.get("uid")
        name = check_mapping(yaml.get("name"), "api_ref.name")
        link_template = yaml.get("link_template")

        e = len(errors)

        if not uid:
            errors.append(metadata_errors.MissingField(field="api_ref.uid"))
        if isinstance(name, MetadataParseError):
            errors.append(name)

        if e == len(errors):
            return SdkApiRef(uid, name, link_template)
        return None


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

    @staticmethod
    def from_yaml(version: int, yaml: dict[str, any]) -> Self | MetadataErrors:
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
            expanded = (SdkVersionExpanded(long=long_expanded, short=short_expanded),)

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
        if isinstance(short, MetadataParseError):
            errors.append(short)
        api_ref = SdkApiRef.from_yaml(yaml.get("api_ref"), errors)

        if len(errors) > 0:
            return errors

        return SdkVersion(
            version=version,
            long=long,
            short=short,
            expanded=expanded,
            guide=guide,
            api_ref=api_ref,
            caveat=caveat,
            bookmark=bookmark,
            title_override=title_override,
        )


@dataclass
class Sdk:
    name: str
    versions: list[SdkVersion]
    guide: str
    property: str

    @staticmethod
    def from_yaml(name: str, yaml: dict[str, any]) -> Self | MetadataErrors:
        errors = MetadataErrors()
        property = yaml.get("property")
        guide = check_mapping(yaml.get("guide"), "guide")
        if isinstance(guide, MetadataParseError):
            errors.append(guide)

        sdk = []
        sdk_versions = yaml.get("sdk", {})
        if sdk_versions is None:
            sdk_versions = {}
        for version in sdk_versions:
            sdk_version = SdkVersion.from_yaml(version, sdk_versions[version])
            if isinstance(sdk_version, MetadataErrors):
                errors.extend(sdk_version)
            else:
                sdk.append(sdk_version)

        if len(errors) > 0:
            return errors

        return Sdk(name=name, versions=sdk, guide=guide, property=property)


def check_mapping(mapping: str | None, field: str) -> str | MetadataParseError:
    if not mapping:
        return metadata_errors.MissingField(field=field)
    if not re.match("&[-_a-zA-Z0-9]+;", mapping):
        return metadata_errors.MappingMustBeEntity(field=field, value=mapping)

    return mapping


def parse(file: str, yaml: dict[str, any]) -> list[Sdk]:
    sdks = []
    errors = MetadataErrors()

    for name in yaml:
        sdk = Sdk.from_yaml(name, yaml[name])
        if isinstance(sdk, Sdk):
            sdks.append(sdk)
        else:
            for error in sdk:
                error.file = file
                error.id = name
            errors.extend(sdk)

    if len(errors) > 0:
        return errors

    return sdks


if __name__ == "__main__":
    import yaml
    from pathlib import Path

    path = Path(__file__).parent.parent.parent / ".doc_gen" / "metadata" / "sdks.yaml"
    with open(path) as file:
        meta = yaml.safe_load(file)
    examples = parse(path.name, meta)
    print(f"{examples}")
