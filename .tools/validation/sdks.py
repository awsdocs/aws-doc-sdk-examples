from typing import Self, Optional
import metadata_errors
from metadata_errors import MetadataErrors, MetadataParseError
from dataclasses import dataclass


@dataclass
class SdkVersionExpanded:
    long: str
    short: str


@dataclass
class SdkApiRef:
    uid: str
    name: str
    link_template: str

    def from_yaml(yaml: dict[str, str] | None, errors: MetadataErrors) -> Self | None:
        if yaml is None:
            return None
        uid = yaml.get("uid")
        name = check_mapping(yaml.get("name"), "api_ref.name")
        link_template = yaml.get("link_template")

        e = len(errors)

        if not uid:
            errors.append(metadata_errors.MissingField(field="api_ref.uid"))
        if not link_template:
            errors.append(metadata_errors.MissingField(field="api_ref.link_template"))
        if isinstance(name, MetadataParseError):
            errors.append(name)

        if e == len(errors):
            return SdkApiRef(uid, name, link_template)
        return None


@dataclass
class SdkVersion:
    version: int
    long: str
    short: str
    expanded: Optional[SdkVersionExpanded]
    guide: Optional[str]
    api_ref: Optional[SdkApiRef]

    @staticmethod
    def from_yaml(version: int, yaml: dict[str, any]) -> Self | MetadataErrors:
        errors = MetadataErrors()
        long = check_mapping(yaml.get("long"), "long")
        short = check_mapping(yaml.get("short"), "short")
        guide = yaml.get("guide", "")

        expanded = yaml.get("expanded", {})
        if expanded is not None:
            long_expanded = expanded.get("long")
            short_expanded = expanded.get("short")
            if not long_expanded:
                errors.append(metadata_errors.MissingField(field="expanded.long"))
            if not short_expanded:
                errors.append(metadata_errors.MissingField(field="expanded.short"))

        if isinstance(long, MetadataParseError):
            errors.append(long)
        if isinstance(short, MetadataParseError):
            errors.append(short)
        api_ref = SdkApiRef.from_yaml(yaml.get("api_ref"), errors)

        if len(errors) > 0:
            return errors

        return SdkVersion(
            version,
            long,
            short,
            SdkVersionExpanded(long_expanded, short_expanded),
            guide,
            api_ref,
        )


@dataclass
class Sdk:
    name: str
    sdk: dict[int, SdkVersion]
    guide: str

    @staticmethod
    def from_yaml(name: str, yaml: dict[str, any]) -> Self | MetadataErrors:
        errors = MetadataErrors()
        guide = check_mapping(yaml.get("guide"), "guide")
        if isinstance(guide, MetadataParseError):
            errors.append(guide)

        sdk = {}
        sdk_versions = yaml.get("sdk", {})
        for version in sdk_versions:
            sdk_version = SdkVersion.from_yaml(version, sdk_versions[version])
            if isinstance(sdk_version, MetadataErrors):
                errors.extend(sdk_version)
            else:
                sdk[version] = sdk_version

        if len(errors) > 0:
            return errors

        return Sdk(name, sdk, guide)


def check_mapping(mapping: str | None, field: str) -> str | MetadataParseError:
    if not mapping:
        return metadata_errors.MissingField(field=field)
    if not (mapping.startswith("&") and mapping.endswith(";")):
        return metadata_errors.MappingMustBeEntity(field=field, value=mapping)

    return mapping


def parse(file: str, yaml: dict[str, any]):
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

    with open(
        Path(__file__).parent.parent.parent / ".doc_gen" / "metadata" / "sdks.yaml"
    ) as file:
        meta = yaml.safe_load(file)
    examples = parse(file.name, meta)
    print(f"{examples!r}")
