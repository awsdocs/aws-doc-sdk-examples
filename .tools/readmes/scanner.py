# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import config
import logging
from collections import defaultdict
from os.path import relpath
from pathlib import Path
from typing import Dict, List

from aws_doc_sdk_examples_tools.doc_gen import DocGen
from aws_doc_sdk_examples_tools.metadata import Example
from aws_doc_sdk_examples_tools.sdks import Sdk
from aws_doc_sdk_examples_tools.services import Service

logger = logging.getLogger(__name__)


class Scanner:
    def __init__(self, doc_gen: DocGen):
        self.doc_gen = doc_gen
        self.lang_name: str = ""
        self.sdk_ver: int = -1
        self.svc_name: str = ""
        self.snippets = None
        self.entities: Dict[str, str] = {}
        self._prepare_entities()
        self.examples: Dict[str, List[Example]] = {}
        self.hellos: Dict[str, Example] = {}
        self.actions: Dict[str, Example] = {}
        self.scenarios: Dict[str, Example] = {}
        self.customs: Dict[str, Example] = {}
        self.crosses: Dict[str, Example] = {}
        self.cross_scenarios: Dict[str, Example] = {}

    def _prepare_entities(self):
        for svc in self.services().values():
            if svc.expanded:
                self.entities[svc.long] = svc.expanded.long
                self.entities[svc.short] = svc.expanded.short
        # config entities override
        for entity, expanded in config.entities.items():
            self.entities[entity] = expanded

    def load_crosses(self):
        self.doc_gen.process_metadata(
            self.doc_gen.root / ".doc_gen" / "metadata" / "cross_metadata.yaml"
        )

    def set_service(self, service):
        self.svc_name = service
        self.doc_gen.process_metadata(
            Path(__file__).parent.parent.parent
            / ".doc_gen"
            / "metadata"
            / f"{service}_metadata.yaml"
        )

        # Kinda sucks to rebuild this every round. Should building it get moved into DocGen?
        self.examples: Dict[str, List[Example]] = defaultdict(list)
        for example in self.doc_gen.examples.values():
            for lang_name, language in example.languages.items():
                for sdk_version in language.versions:
                    for svc_name in example.services:
                        self.examples[
                            f"{lang_name}:{sdk_version.sdk_version}:{svc_name}"
                        ].append(example)

    def _example_key(self):
        return f"{self.lang_name}:{self.sdk_ver}:{self.svc_name}"

    def set_example(self, language, sdk_ver):
        self.lang_name = language
        self.sdk_ver = sdk_ver

        self.hellos.clear()
        self.actions.clear()
        self.scenarios.clear()
        self.customs.clear()
        self.cross_scenarios.clear()
        self.crosses.clear()

        key = self._example_key()
        examples = self.examples[key]
        for example in examples:
            if example.id.startswith("cross_"):
                if example.category == config.categories["scenarios"]:
                    self.cross_scenarios[example.id] = example
                else:
                    self.crosses[example.id] = example
            elif example.category == config.categories["hello"]:
                self.hellos[example.id] = example
            elif example.category == config.categories["actions"]:
                self.actions[example.id] = example
            elif example.category == config.categories["scenarios"]:
                self.scenarios[example.id] = example
            elif example.category not in config.categories.values():
                self.customs[example.id] = example

    def sdk(self) -> Sdk:
        return self.doc_gen.sdks[self.lang_name]

    def sdks(self) -> Dict[str, Sdk]:
        return self.doc_gen.sdks

    def service(self) -> Service:
        return self.doc_gen.services[self.svc_name]

    def services(self) -> Dict[str, Service]:
        return self.doc_gen.services

    def expand_entity(self, entity):
        return self.entities[entity]

    def snippet(self, example: Example, sdk_ver, readme_folder, api_name: str):
        github = None
        tag = None
        tag_path = None
        if self.lang_name in example.languages:
            for ex_ver in example.languages[self.lang_name].versions:
                if ex_ver.sdk_version == sdk_ver:
                    github = ex_ver.github
                    if github is not None:
                        if ex_ver.excerpts:
                            excerpt = ex_ver.excerpts[0]
                            if excerpt.snippet_tags:
                                tags = excerpt.snippet_tags
                                for t in tags:
                                    if api_name in t:
                                        tag = t
                                if tag is None:
                                    tag = tags[0]
                            elif excerpt.snippet_files:
                                snippet_files = excerpt.snippet_files
                                # TODO: Find the best (or all?) snippet files, not the first.
                                full_path = snippet_files[0]
                                tag_path = "/".join(full_path.split("/")[3:])
                                if "cross-services" in full_path:
                                    tag_path = "../cross-services/" + tag_path
                        elif ex_ver.block_content:
                            tag_path = github
        if tag and github is not None and tag_path is None:
            snippet = self.doc_gen.snippets[tag]
            if snippet is not None:
                snippet_path = self.doc_gen.root / Path(snippet.file)
                readme_path = Path(__file__).parent.parent.parent / readme_folder
                # tag_path = snippet_path.relative_to(readme_path)  # Must be subpaths, no ..
                tag_path = relpath(snippet_path, readme_path)
                tag_path = str(tag_path).replace("\\", "/")
                if api_name != "":
                    tag_path += f"#L{snippet.line_start + 1}"
        return tag_path


def _contains_language_version(example: Example, lang_name: str, sdk_ver: int):
    return (
        lang_name in example.languages
        and next(
            v.sdk_version == sdk_ver for v in example.languages[lang_name].versions
        )
        is not None
    )
