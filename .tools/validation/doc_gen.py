# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import yaml

from typing import Self
from dataclasses import dataclass, field
from pathlib import Path

# from os import glob

from metadata import Example, parse as parse_examples
from metadata_errors import MetadataErrors
from metadata_validator import validate_metadata
from project_validator import check_files, verify_sample_files
from sdks import Sdk, parse as parse_sdks
from services import Service, parse as parse_services
from snippets import Snippet, collect_snippets, validate_snippets


@dataclass
class DocGen:
    root: Path
    errors: MetadataErrors
    sdks: dict[str, Sdk] = field(default_factory=dict)
    services: dict[str, Service] = field(default_factory=dict)
    snippets: dict[str, Snippet] = field(default_factory=dict)
    snippet_files: set[str] = field(default_factory=set)
    examples: list[Example] = field(default_factory=list)
    cross_blocks: set[str] = field(default_factory=set)

    def collect_snippets(self, snippets_root: Path | None):
        if snippets_root is None:
            snippets_root = self.root.parent.parent
        snippets, errs = collect_snippets(snippets_root)
        self.snippets = snippets
        self.errors.extend(errs)

    @classmethod
    def from_root(cls, root: Path) -> Self:
        errors = MetadataErrors()

        metadata = root / ".doc_gen/metadata"

        with open(metadata / "sdks.yaml", encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            sdks, errs = parse_sdks("sdks.yaml", meta)
            errors.extend(errs)

        with open(metadata / "services.yaml", encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            services, service_errors = parse_services("services.yaml", meta)
            errors.extend(service_errors)

        cross = set(
            [path.name for path in (metadata.parent / "cross-content").glob("*.xml")]
        )

        doc_gen = cls(
            root=root,
            sdks=sdks,
            services=services,
            errors=errors,
            cross_blocks=cross,
        )

        for path in metadata.glob("cross_metadata.yaml"):
            with open(path) as file:
                ex, errs = parse_examples(
                    path.name,
                    yaml.safe_load(file),
                    doc_gen.sdks,
                    doc_gen.services,
                    doc_gen.cross_blocks,
                )
                doc_gen.examples.extend(ex)
                errors.extend(errs)

        return doc_gen

    def validate(self, check_spdx: bool):
        check_files(self.root, self.errors, check_spdx)
        verify_sample_files(self.root, self.errors)
        validate_metadata(self.root, self.errors)
        validate_snippets(
            self.examples,
            self.snippets,
            self.snippet_files,
            self.errors,
            self.root,
        )
