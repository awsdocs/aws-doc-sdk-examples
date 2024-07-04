# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import config
import logging
import os
from pathlib import Path

from aws_doc_sdk_examples_tools.doc_gen import DocGen
from aws_doc_sdk_examples_tools.metadata import Example
from aws_doc_sdk_examples_tools.sdks import Sdk
from aws_doc_sdk_examples_tools.services import Service

logger = logging.getLogger(__name__)


class Scanner:
    def __init__(self, doc_gen: DocGen):
        self.doc_gen = doc_gen
        self.lang_name = None
        self.sdk_ver = None
        self.svc_name = None
        self.example_meta = None
        self.cross_meta = {}
        self.snippets = None

    def _contains_language_version(self, example):
        if self.lang_name in example["languages"]:
            for version in example["languages"][self.lang_name]["versions"]:
                if version["sdk_version"] == self.sdk_ver:
                    return True
        return False

    def set_example(self, language, sdk_ver, service):
        self.lang_name = language
        self.sdk_ver = sdk_ver
        self.svc_name = service
        self.example_meta = None

    def sdk(self) -> Sdk:
        return self.doc_gen.sdks[self.lang_name]

    def sdks(self) -> dict[str, Sdk]:
        return self.doc_gen.sdks

    def service(self) -> Service:
        return self.doc_gen.services[self.svc_name]

    def services(self) -> dict[str, Service]:
        return self.doc_gen.services

    def examples(self) -> list[Example]:
        return [
            example
            for example in self.doc_gen.examples.values()
            if self.svc_name in example.services
        ]

    def expand_entity(self, entity):
        for svc in self.services().values():
            if svc.long == entity:
                return svc.expanded.long
            elif svc.short == entity:
                return svc.expanded.short
            elif entity in config.entities:
                return config.entities[entity]

    def hello(self) -> dict[str, Example]:
        return {
            example.id: example
            for example in self.examples()
            if example.category == config.categories["hello"]
            and self.lang_name in example.languages
        }

    def actions(self) -> dict[str, Example]:
        return {
            example.id: example
            for example in self.examples()
            if example.category == config.categories["actions"]
            and self.lang_name in example.languages
        }

    def scenarios(self) -> dict[str, Example]:
        return {
            example.id: example
            for example in self.examples()
            if example.category == config.categories["scenarios"]
            and self.lang_name in example.languages
        }

    def custom_categories(self):
        return {
            example.id: example
            for example in self.examples()
            if example.category == config.categories["cross"]
            and self.lang_name in example.languages
        }

    def crosses(self):
        crosses = {}
        scenarios = {}
        for name, example in self.cross_meta.items():
            if (
                self._contains_language_version(example)
                and self.svc_name in example["services"]
            ):
                if example.get("category", "") == config.categories["scenarios"]:
                    scenarios[name] = example
                else:
                    crosses[name] = example
        return crosses, scenarios

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
        if github is not None and tag_path is None:
            snippet = self.doc_gen.snippets[tag]
            if snippet is not None:
                snippet_path = Path(snippet.file)
                readme_path = Path(__file__).parent.parent.parent / readme_folder
                tag_path = snippet_path.relative_to(readme_path)
                tag_path = str(tag_path).replace("\\", "/")
                if api_name != "":
                    tag_path += f"#L{snippet.line_start + 1}"
        return tag_path
