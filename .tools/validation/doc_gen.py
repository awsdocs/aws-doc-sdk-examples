import yaml

from typing import Self
from dataclasses import dataclass, field
from pathlib import Path

from metadata_errors import MetadataErrors
from sdks import Sdk, parse as parse_sdks
from services import Service, parse as parse_services
from snippets import Snippet


@dataclass
class DocGen:
    sdks: dict[str, Sdk] = field(default_factory=dict)
    services: dict[str, Service] = field(default_factory=dict)
    snippets: dict[str, Snippet] = field(default_factory=dict)

    @staticmethod
    def from_root(root: Path) -> Self | MetadataErrors:
        errors = MetadataErrors()

        with open(root / "sdks.yaml", encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            parsed = parse_sdks("sdks.yaml", meta)
            sdks = errors.maybe_extend(parsed)

        with open(root / "services.yaml", encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            parsed = parse_services("services.yaml", meta)
            services = errors.maybe_extend(parsed)

        snippets = {}

        if len(errors) > 0:
            return errors

        return DocGen(sdks=sdks, services=services, snippets=snippets)
