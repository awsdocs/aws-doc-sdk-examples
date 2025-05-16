# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import yaml
import json

from collections import defaultdict
from dataclasses import dataclass, field, fields, is_dataclass, asdict
from functools import reduce
from pathlib import Path
from typing import Dict, Iterable, Optional, Set, Tuple, List, Any

# from os import glob

from .categories import Category, parse as parse_categories
from .metadata import (
    Example,
    DocFilenames,
    SDKPages,
    SDKPageVersion,
    CrossServicePage,
    validate_no_duplicate_api_examples,
)
from .entities import expand_all_entities, EntityErrors
from .metadata_errors import (
    MetadataErrors,
    MetadataError,
    NameFormat,
    ActionNameFormat,
    ServiceNameFormat,
)
from .metadata_validator import validate_metadata
from .project_validator import ValidationConfig
from .sdks import Sdk, parse as parse_sdks
from .services import Service, parse as parse_services
from .snippets import (
    Snippet,
    collect_snippets,
    collect_snippet_files,
    validate_snippets,
)
from .yaml_mapper import example_from_yaml


@dataclass
class DocGenMergeWarning(MetadataError):
    pass


@dataclass
class DocGen:
    root: Path
    errors: MetadataErrors
    entities: Dict[str, str] = field(default_factory=dict)
    prefix: Optional[str] = None
    validation: ValidationConfig = field(default_factory=ValidationConfig)
    sdks: Dict[str, Sdk] = field(default_factory=dict)
    services: Dict[str, Service] = field(default_factory=dict)
    standard_categories: List[str] = field(default_factory=list)
    categories: Dict[str, Category] = field(default_factory=dict)
    snippets: Dict[str, Snippet] = field(default_factory=dict)
    snippet_files: Set[str] = field(default_factory=set)
    examples: Dict[str, Example] = field(default_factory=dict)
    cross_blocks: Set[str] = field(default_factory=set)
    _loaded: Set[Path] = field(default_factory=set, init=False)

    def collect_snippets(
        self, snippets_root: Optional[Path] = None, prefix: Optional[str] = None
    ):
        prefix = prefix or ""
        snippets_root = snippets_root or self.root
        snippets, errs = collect_snippets(snippets_root)
        collect_snippet_files(
            self.examples.values(),
            prefix=prefix,
            snippets=snippets,
            errors=errs,
            root=self.root,
        )
        self.snippets = snippets
        self.errors.extend(errs)

    def languages(self) -> Set[str]:
        languages: Set[str] = set()
        for sdk_name, sdk in self.sdks.items():
            for version in sdk.versions:
                languages.add(f"{sdk_name}:{version.version}")
        return languages

    def expand_entities(self, text: str) -> Tuple[str, EntityErrors]:
        return expand_all_entities(text, self.entities)

    def expand_entity_fields(self, obj: object):
        if isinstance(obj, list):
            for o in obj:
                self.expand_entity_fields(o)
        if isinstance(obj, dict):
            for val in obj.values():
                self.expand_entity_fields(val)
        if is_dataclass(obj) and not isinstance(obj, type):
            for f in fields(obj):
                val = getattr(obj, f.name)
                if isinstance(val, str):
                    [expanded, errs] = self.expand_entities(val)
                    if errs:
                        self.errors.extend(errs)
                    else:
                        setattr(obj, f.name, expanded)
                self.expand_entity_fields(val)

    def merge(self, other: "DocGen") -> MetadataErrors:
        """Merge fields from other into self, prioritizing self fields."""
        warnings = MetadataErrors()
        for name, sdk in other.sdks.items():
            if name not in self.sdks:
                self.sdks[name] = sdk
            else:
                warnings.append(
                    DocGenMergeWarning(file=other.root, id=f"conflict in sdk {name}")
                )
        for name, service in other.services.items():
            if name not in self.services:
                self.services[name] = service
            else:
                warnings.append(
                    DocGenMergeWarning(
                        file=other.root, id=f"conflict in service {name}"
                    )
                )
        for name, snippet in other.snippets.items():
            if name not in self.snippets:
                self.snippets[name] = snippet
            else:
                warnings.append(
                    DocGenMergeWarning(
                        file=other.root, id=f"conflict in snippet {name}"
                    )
                )

        for entity, expanded in other.entities.items():
            if entity not in self.entities:
                self.entities[entity] = expanded
            else:
                warnings.append(
                    DocGenMergeWarning(
                        file=other.root, id=f"conflict in entity {entity}"
                    )
                )

        self.validation.allow_list.update(other.validation.allow_list)
        self.validation.sample_files.update(other.validation.sample_files)
        self.snippet_files.update(other.snippet_files)
        self.cross_blocks.update(other.cross_blocks)
        self.extend_examples(other.examples.values(), warnings)
        for name, category in other.categories.items():
            if name not in self.categories:
                self.categories[name] = category

        return warnings

    def extend_examples(self, examples: Iterable[Example], errors: MetadataErrors):
        for example in examples:
            id = example.id
            if id in self.examples:
                self.examples[id].merge(example, errors)
            else:
                self.examples[id] = example

    @classmethod
    def empty(cls, validation: ValidationConfig = ValidationConfig()) -> "DocGen":
        return DocGen(root=Path("/"), errors=MetadataErrors(), validation=validation)

    @classmethod
    def default(cls) -> "DocGen":
        return DocGen.empty().for_root(Path(__file__).parent, incremental=True)

    def clone(self) -> "DocGen":
        return DocGen(
            root=self.root,
            validation=self.validation.clone(),
            sdks={**self.sdks},
            entities={**self.entities},
            services={**self.services},
            errors=MetadataErrors(),
            snippets={},
            snippet_files=set(),
            cross_blocks=set(),
            examples={},
        )

    def for_root(
        self, root: Path, config: Optional[Path] = None, incremental=False
    ) -> "DocGen":
        self.root = root

        config = config or Path(__file__).parent / "config"

        doc_gen = DocGen.empty()
        parse_config(doc_gen, root, config, self.validation.strict_titles)
        self.merge(doc_gen)

        if not incremental:
            self.find_and_process_metadata(root / ".doc_gen/metadata")

        return self

    def find_and_process_metadata(self, metadata_path: Path):
        for path in metadata_path.glob("*_metadata.yaml"):
            self.process_metadata(path)

    def process_metadata(self, path: Path) -> "DocGen":
        if path in self._loaded:
            return self
        with open(path) as file:
            examples, errs = parse_examples(
                path,
                yaml.safe_load(file),
                self.sdks,
                self.services,
                self.standard_categories,
                self.cross_blocks,
                self.validation,
            )
            self.extend_examples(examples, self.errors)
            self.errors.extend(errs)
            for example in examples:
                for lang in example.languages:
                    language = example.languages[lang]
                    for version in language.versions:
                        for excerpt in version.excerpts:
                            self.snippet_files.update(excerpt.snippet_files)
        self._loaded.add(path)
        return self

    @classmethod
    def from_root(
        cls,
        root: Path,
        config: Optional[Path] = None,
        validation: ValidationConfig = ValidationConfig(),
        incremental: bool = False,
    ) -> "DocGen":
        return DocGen.empty(validation=validation).for_root(
            root, config, incremental=incremental
        )

    def validate(self):
        for sdk in self.sdks.values():
            sdk.validate(self.errors)
        for service in self.services.values():
            service.validate(self.errors)
        for category in self.categories.values():
            category.validate(self.errors)
        for example in self.examples.values():
            example.validate(self.errors, self.services, self.root)
        validate_metadata(self.root, self.validation.strict_titles, self.errors)
        validate_no_duplicate_api_examples(self.examples.values(), self.errors)
        validate_snippets(
            [*self.examples.values()],
            self.snippets,
            self.validation,
            self.errors,
            self.root,
        )

    def fill_missing_fields(self):
        def safe_split_id(ex_id: str) -> Tuple[str, str]:
            if "_" in example.id:
                svc, act = ex_id.split("_", 1)
            else:
                svc, act = "unknown_service", "unknown_action"
            return svc, act

        for example in self.examples.values():
            id_service, id_action = safe_split_id(example.id)
            service_id = example.service_main or next(
                (k for (k, _) in example.services.items()), None
            )
            if service_id is None:
                # TODO Log and find which tributaries this effects, as it was supposed to be caught by validations.
                service_id = id_service
            action = (
                next((k for k in example.services.get(service_id, [])), None)
                or safe_split_id(example.id)[1]
            )
            if action is None:
                # TODO Log and find which tributaries this effects, as it was supposed to be caught by validations.
                action = id_action
            if service_id in self.services:
                service_name = self.services[service_id].short
                example.service_sdk_id = self.services[service_id].sdk_id
            else:
                service_name = service_id
            example.fill_display_fields(self.categories, service_name, action)

    def stats(self):
        values = self.examples.values()
        initial = defaultdict(int)

        def count_genai(d: Dict[str, int], e: Example):
            for lang in e.languages.values():
                for version in lang.versions:
                    for excerpt in version.excerpts:
                        d[excerpt.genai] += 1
            return d

        genai = reduce(count_genai, values, initial)

        return {
            "sdks": len(self.sdks),
            "services": len(self.services),
            "examples": len(self.examples),
            "versions": sum(
                sum(len(lang.versions) for lang in e.languages.values())
                for e in self.examples.values()
            ),
            "snippets": len(self.snippets) + len(self.snippet_files),
            "genai": dict(genai),
        }


# Encode a DocGen instance as JSON. Originally
# it was planned to have a DocGenDecoder as well,
# but that required writing environment data like
# Path to the JSON, which was not very secure
# and arguably not useful either.
class DocGenEncoder(json.JSONEncoder):
    def default(self, obj):
        if is_dataclass(obj) and not isinstance(obj, type):
            return asdict(obj)

        if isinstance(obj, Path):
            # Strip out paths to prevent leaking environment data.
            return obj.name

        if isinstance(obj, MetadataErrors):
            return {"__metadata_errors__": [asdict(error) for error in obj]}

        if isinstance(obj, EntityErrors):
            return {
                "__entity_errors__": [{error.entity: error.message()} for error in obj]
            }

        if isinstance(obj, set):
            return {"__set__": list(obj)}

        return super().default(obj)


def parse_config(doc_gen: DocGen, root: Path, config: Path, strict: bool):
    try:
        with open(root / ".doc_gen" / "validation.yaml", encoding="utf-8") as file:
            validation = yaml.safe_load(file)
            validation = validation or {}
            doc_gen.validation.allow_list.update(validation.get("allow_list", []))
            doc_gen.validation.sample_files.update(validation.get("sample_files", []))
    except Exception:
        pass

    try:
        sdk_path = config / "sdks.yaml"
        with sdk_path.open(encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            sdks, errs = parse_sdks(sdk_path, meta, strict)
            doc_gen.sdks = sdks
            doc_gen.errors.extend(errs)
    except Exception:
        pass

    try:
        services_path = config / "services.yaml"
        with services_path.open(encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            services, service_errors = parse_services(services_path, meta)
            doc_gen.services = services
            for service in doc_gen.services.values():
                if service.expanded:
                    doc_gen.entities[service.long] = service.expanded.long
                    doc_gen.entities[service.short] = service.expanded.short
            doc_gen.errors.extend(service_errors)
    except Exception:
        pass

    try:
        categories_path = config / "categories.yaml"
        with categories_path.open(encoding="utf-8") as file:
            meta = yaml.safe_load(file)
            standard_categories, categories, errs = parse_categories(
                categories_path, meta
            )
            doc_gen.standard_categories = standard_categories
            doc_gen.categories = categories
            doc_gen.errors.extend(errs)
    except Exception:
        pass

    try:
        entities_config_path = config / "entities.yaml"
        with entities_config_path.open(encoding="utf-8") as file:
            entities_config = yaml.safe_load(file)
        for entity, expanded in entities_config["expanded_override"].items():
            doc_gen.entities[entity] = expanded
    except Exception:
        pass

    metadata = root / ".doc_gen/metadata"
    try:
        doc_gen.cross_blocks = set(
            [path.name for path in (metadata.parent / "cross-content").glob("*.xml")]
        )
    except Exception:
        pass


def parse_examples(
    file: Path,
    yaml: Dict[str, Any],
    sdks: Dict[str, Sdk],
    services: Dict[str, Service],
    standard_categories: List[str],
    blocks: Set[str],
    validation: Optional[ValidationConfig],
) -> Tuple[List[Example], MetadataErrors]:
    examples: List[Example] = []
    errors = MetadataErrors()
    validation = validation or ValidationConfig()
    for id in yaml:
        example, example_errors = example_from_yaml(
            yaml[id], sdks, services, blocks, validation
        )
        if example.category in standard_categories:
            check_id_format(
                id,
                example.services,
                validation.strict_titles and example.category == "Api",
                example_errors,
            )
        for error in example_errors:
            error.file = file
            error.id = id
        errors.extend(example_errors)
        example.file = file
        example.id = id
        example.doc_filenames = get_doc_filenames(id, example)
        examples.append(example)

    return examples, errors


def check_id_format(
    id: str,
    parsed_services: Dict[str, Set[str]],
    check_action: bool,
    errors: MetadataErrors,
):
    [service, *rest] = id.split("_")
    if len(rest) == 0:
        errors.append(NameFormat(id=id))
    elif service not in parsed_services and service not in ["cross", "serverless"]:
        errors.append(
            ServiceNameFormat(id=id, svc=service, svcs=[*parsed_services.keys()])
        )
    elif check_action and (
        len(rest) > 1 or rest[0] not in parsed_services.get(service, {})
    ):
        errors.append(ActionNameFormat(id=id))


def get_doc_filenames(example_id: str, example: Example) -> Optional[DocFilenames]:
    base_url = "https://docs.aws.amazon.com/code-library/latest/ug"
    service_pages = {
        service_id: f"{base_url}/{service_id}_example_{example_id}_section.html"
        for service_id in example.services
    }

    if example.file is not None:
        is_cross = example.file.match("cross_*")
    else:
        is_cross = False

    sdk_pages: SDKPages = {}

    for language in example.languages.values():
        sdk_pages[language.property] = {}
        for version in language.versions:
            if is_cross:
                sdk_pages[language.property][version.sdk_version] = SDKPageVersion(
                    cross_service=CrossServicePage(
                        cross=f"{base_url}/{example_id}_{language.property}_{version.sdk_version}_topic.html"
                    )
                )
            else:
                anchor = "actions" if example.category == "Api" else "scenarios"
                sdk_pages[language.property][version.sdk_version] = SDKPageVersion(
                    actions_scenarios={
                        service_id: f"{base_url}/{language.property}_{version.sdk_version}_{service_id}_code_examples.html#{anchor}"
                        for service_id in example.services
                    }
                )

    return DocFilenames(service_pages, sdk_pages)
