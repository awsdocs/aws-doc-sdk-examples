from typing import Self, Optional
from dataclasses import dataclass, field
import metadata_errors
from metadata_errors import MetadataErrors, check_mapping


@dataclass
class ServiceGuide:
    subtitle: str
    url: str


@dataclass
class Service:
    long: str
    short: str
    sort: str
    version: str
    api_ref: Optional[str] = field(default=None)
    blurb: Optional[str] = field(default=None)
    bundle: Optional[str] = field(default=None)
    caveat: Optional[str] = field(default=None)
    guide: Optional[ServiceGuide] = field(default=None)
    tags: dict[str, set[str]] = field(default_factory=dict)

    @staticmethod
    def from_yaml(name: str, yaml: dict[str, any]) -> Self | MetadataErrors:
        errors = MetadataErrors()

        long = check_mapping(yaml.get("long"), "long")
        short = check_mapping(yaml.get("short"), "short")
        sort = yaml.get("sort")
        version = yaml.get("version")

        if isinstance(long, metadata_errors.MetadataParseError):
            errors.append(long)
        if isinstance(short, metadata_errors.MetadataParseError):
            errors.append(short)
        if sort is None:
            errors.append(metadata_errors.MissingField(field="sort"))
        if version is None:
            errors.append(metadata_errors.MissingField(field="version"))

        api_ref = yaml.get("api_ref")
        blurb = yaml.get("blurb")
        caveat = yaml.get("caveat")
        bundle = yaml.get("bundle")

        guide = yaml.get("guide")
        if guide is not None:
            subtitle = guide.get("subtitle")
            url = guide.get("url")
            if subtitle is None:
                errors.append(metadata_errors.MissingField(field="guide.subtitle"))
            if url is None:
                errors.append(metadata_errors.MissingField(field="guide.url"))
            guide = ServiceGuide(subtitle=subtitle, url=url)

        tags = yaml.get("tags", {})
        for tag in tags:
            tags[tag] = set(tags[tag].keys())

        if len(errors) > 0:
            for error in errors:
                error.id = name
            return errors

        return Service(
            long=long,
            short=short,
            sort=sort,
            api_ref=api_ref,
            blurb=blurb,
            bundle=bundle,
            caveat=caveat,
            guide=guide,
            tags=tags,
            version=version,
        )


def parse(filename: str, yaml: dict[str, any]) -> dict[str, Service] | MetadataErrors:
    errors = metadata_errors.MetadataErrors()
    services = {}
    for name in yaml:
        meta = yaml[name]
        if meta is None:
            errors.append(metadata_errors.MissingServiceBody(file=filename, id=name))
        else:
            service = Service.from_yaml(name, meta)
            if isinstance(service, MetadataErrors):
                for error in service:
                    error.file = filename
                errors.extend(service)
            else:
                services[name] = service

    return services if len(errors) == 0 else errors


if __name__ == "__main__":
    import yaml
    from pathlib import Path

    path = (
        Path(__file__).parent.parent.parent / ".doc_gen" / "metadata" / "services.yaml"
    )
    with open(path) as file:
        meta = yaml.safe_load(file)
    examples = parse(path.name, meta)
    print(f"{examples}")
