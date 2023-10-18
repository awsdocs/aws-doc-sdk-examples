# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import config
import logging
import os
import re
import yaml

from snippets import Snippet, scan_for_snippets

logger = logging.getLogger(__name__)


class Scanner:
    def __init__(self, meta_folder):
        self.meta_folder = meta_folder
        self.lang_name = None
        self.svc_name = None
        self.sdk_meta = None
        self.svc_meta = None
        self.example_meta = None
        self.cross_meta = None
        self.snippets = None

    def _load_meta(self, file_name, field):
        if field is not None:
            return field
        sdk_file_name = f"{self.meta_folder}/{file_name}"
        with open(sdk_file_name) as sdk_file:
            meta = yaml.safe_load(sdk_file)
        return meta

    def _load_sdks(self):
        self.sdk_meta = self._load_meta("sdks.yaml", self.sdk_meta)

    def _load_services(self):
        self.svc_meta = self._load_meta("services.yaml", self.svc_meta)

    def _load_cross(self):
        self.cross_meta = self._load_meta("cross_metadata.yaml", self.cross_meta)

    def _load_examples(self):
        self.example_meta = self._load_meta(
            f"{self.svc_name}_metadata.yaml", self.example_meta
        )

    def set_example(self, language, service):
        self.lang_name = language
        self.svc_name = service
        self.example_meta = None

    def sdk(self):
        self._load_sdks()
        return self.sdk_meta[self.lang_name]

    def sdks(self):
        self._load_sdks()
        return self.sdk_meta

    def service(self):
        self._load_services()
        return self.svc_meta[self.svc_name]

    def services(self):
        self._load_services()
        return self.svc_meta

    def expand_entity(self, entity):
        self._load_services()
        for _, svc in self.svc_meta.items():
            if svc["long"] == entity:
                return svc.get("expanded", {}).get("long")
            elif svc["short"] == entity:
                return svc.get("expanded", {}).get("short")
            elif entity in config.entities:
                return config.entities[entity]

    def hello(self):
        self._load_examples()
        hello = {}
        for example_name, example in self.example_meta.items():
            if (
                example.get("category", "") == config.categories["hello"]
                and self.lang_name in example["languages"]
            ):
                hello[example_name] = example
        return hello

    def actions(self):
        self._load_examples()
        actions = {}
        for example_name, example in self.example_meta.items():
            if not example.get("category") and self.lang_name in example["languages"]:
                actions[example_name] = example
        return actions

    def scenarios(self):
        self._load_examples()
        scenarios = {}
        for example_name, example in self.example_meta.items():
            if (
                example.get("category", "") == config.categories["scenarios"]
                and self.lang_name in example["languages"]
            ):
                scenarios[example_name] = example
        return scenarios

    def crosses(self):
        self._load_cross()
        crosses = {}
        scenarios = {}
        for example_name, example in self.cross_meta.items():
            if (
                self.lang_name in example["languages"]
                and self.svc_name in example["services"]
            ):
                if example.get("category", "") == config.categories["scenarios"]:
                    scenarios[example_name] = example
                else:
                    crosses[example_name] = example
        return crosses, scenarios

    def snippet(self, example, sdk_ver, readme_folder, api_name):
        if self.snippets is None:
            self.snippets = scan_for_snippets(".")

        github = None
        tag = None
        tag_path = None
        for ex_ver in example["languages"][self.lang_name]["versions"]:
            if ex_ver["sdk_version"] == sdk_ver:
                github = ex_ver.get("github")
                if github is not None:
                    if "excerpts" in ex_ver:
                        excerpt = ex_ver["excerpts"][0]
                        if "snippet_tags" in excerpt:
                            tags = excerpt.get("snippet_tags", [])
                            for t in tags:
                                if api_name in t:
                                    tag = t
                            if tag is None:
                                tag = next(iter(tags), None)
                        elif "snippet_files" in excerpt:
                            snippet_files = excerpt["snippet_files"]
                            # TODO: Find the best (or all?) snippet files, not the first.
                            tag_path = snippet_files[0]
                    elif "block_content" in ex_ver:
                        tag_path = github
        if github is not None and tag_path is None:
            snippet = self.snippets.get(tag, None)
            if snippet is not None:
                tag_path = os.path.relpath(snippet.path, readme_folder).replace(
                    "\\", "/"
                )
                if api_name != "":
                    tag_path += f"#L{snippet.line}"
        return tag_path
