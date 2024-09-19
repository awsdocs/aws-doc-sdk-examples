# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import datetime
import logging
import os
import re
from collections import defaultdict
from dataclasses import asdict
from enum import Enum
from operator import itemgetter
from pathlib import Path
from typing import Dict, List, Tuple

import config
from aws_doc_sdk_examples_tools.metadata import Example
from aws_doc_sdk_examples_tools.sdks import Sdk
from aws_doc_sdk_examples_tools.services import Service
from jinja2 import Environment, FileSystemLoader, select_autoescape
from scanner import Scanner

logger = logging.getLogger(__name__)


class RenderStatus(Enum):
    UNCHANGED = 0
    NO_EXAMPLES = 1
    UNIMPLEMENTED = 2
    UPDATED = 3
    UNMANAGED = 4
    NO_FOLDER = 5


class MissingMetadataError(Exception):
    pass


class Renderer:
    def __init__(self, scanner: Scanner):
        self.scanner = scanner

    def set_example(self, service: str, language: str, version: int, safe: bool):
        self.scanner.set_example(service, language, version)
        self.safe = safe
        self.lang_config = config.language.get(self.scanner.lang_name, {}).get(
            self.scanner.sdk_ver, None
        )
        if self.lang_config is None:
            return

        env = Environment(
            autoescape=select_autoescape(
                disabled_extensions=("jinja2",), default_for_string=True
            ),
            loader=FileSystemLoader(os.path.dirname(__file__)),
            trim_blocks=True,
            lstrip_blocks=True,
        )
        self.template = env.get_template("service_readme.jinja2")
        self.template.globals["now"] = datetime.datetime.utcnow

        self.lang_config = self.lang_config.copy()
        service_info = {
            "name": self.scanner.svc_name,
            "sort": self.scanner.service().sort.replace(" ", ""),
        }

        self._extract_service_folder(self.scanner, env, service_info)
        sdk_api_ref_tmpl = env.from_string(self.lang_config.get("sdk_api_ref", ""))
        self.lang_config["sdk_api_ref"] = sdk_api_ref_tmpl.render(service=service_info)

        self.readme_filename = (
            Path(__file__).parent.parent.parent
            / self.lang_config["service_folder"]
            / config.readme
        )

    def _extract_service_folder(self, scanner, env, service_info):
        if (
            "service_folder_overrides" in self.lang_config
            and scanner.svc_name in self.lang_config["service_folder_overrides"]
        ):
            overrides = self.lang_config["service_folder_overrides"]
            self.lang_config["service_folder"] = overrides[scanner.svc_name]
        elif "service_folder" in self.lang_config:
            svc_folder_tmpl = env.from_string(self.lang_config["service_folder"])
            self.lang_config["service_folder"] = svc_folder_tmpl.render(
                service=service_info
            )
        else:
            raise MissingMetadataError(
                "Service folder not found. You must specify a service_folder template in config.py"
            )

    def _transform_examples(
        self, pre_examples: Dict[str, Example], sort_key="title_abbrev", github=False
    ) -> List[Dict[str, str]]:
        post_examples: List[Dict[str, str]] = []
        for pre in pre_examples.values():
            try:
                api = sorted(pre.services[self.scanner.svc_name])[0]
            except Exception:
                api = ""

            file, run_file = self._find_files(pre, api, github)

            if not file:
                if github:
                    logger.debug(
                        "GitHub path not specified for cross-service example: %s %s.",
                        self.scanner.lang_name,
                        pre.title_abbrev,
                    )
                else:
                    logger.warning(
                        "Couldn't find file for example: %s.", pre.title_abbrev
                    )
                continue

            action = {
                "id": pre.id,
                "title_abbrev": pre.title_abbrev,
                "synopsis": pre.synopsis,
                "synopsis_list": pre.synopsis_list,
                "file": file,
                "run_file": run_file,
                "api": api,
                "category": pre.category,
            }
            post_examples.append(action)
        return sorted(post_examples, key=itemgetter(sort_key))

    def _find_files(self, example: Example, api: str, github: bool = False):
        file = None
        run_file = None
        if github:
            file = next(
                ver.github
                for ver in example.languages[self.scanner.lang_name].versions
                if ver.sdk_version == self.scanner.sdk_ver
            )
            if file:
                base_folder = f"{config.language[self.scanner.lang_name][self.scanner.sdk_ver]['base_folder']}/"
                if base_folder in file:
                    file = (
                        self._lang_level_double_dots() + file.split(base_folder, 1)[1]
                    )
        else:
            lang_config = self.lang_config["service_folder"]
            file = self.scanner.snippet(example, lang_config, api)
            run_file = self.scanner.snippet(example, lang_config, "")

        return file, run_file

    def _transform_hellos(self) -> List[Dict[str, str]]:
        examples = self._transform_examples(self.scanner.hellos)
        return examples

    def _transform_actions(self) -> List[Dict[str, str]]:
        examples = self._transform_examples(self.scanner.actions, sort_key="api")
        for example in examples:
            example["title_abbrev"] = example["api"]
            del example["api"]
        return examples

    def _transform_basics(self) -> List[Dict[str, str]]:
        examples = self._transform_examples(self.scanner.basics)
        for example in examples:
            if not example["title_abbrev"]:
                example["title_abbrev"] = config.basics_title_abbrev
            example["file"] = example["run_file"]
            del example["run_file"]
            del example["api"]
        return examples

    def _transform_scenarios_and_crosses(
        self,
    ) -> Tuple[List[Dict[str, str]], List[Dict[str, str]]]:
        scenarios = self._transform_examples(
            {**self.scanner.scenarios, **self.scanner.cross_scenarios}
        )
        for scenario in scenarios:
            scenario["file"] = scenario["run_file"]
            del scenario["run_file"]
            del scenario["api"]
        crosses = self._transform_examples(self.scanner.crosses, github=True)
        return scenarios, crosses

    def _transform_custom_categories(self) -> Dict[str, List[Dict[str, str]]]:
        examples = self._transform_examples(self.scanner.customs)

        post_cats = defaultdict(list)
        for example in examples:
            del example["api"]
            post_cats[example["category"]].append(example)

        sorted_cats = {}
        for key in sorted(post_cats.keys()):
            if len(post_cats[key]) == 0:
                del sorted_cats[key]
            else:
                sorted_cats[key] = post_cats[key]
        return sorted_cats

    def _expand_entities(self, readme_text: str) -> str:
        entities = set(re.findall(r"&[\dA-Za-z-_]+;", readme_text))
        for entity in entities:
            expanded = self.scanner.expand_entity(entity)
            if expanded is not None:
                readme_text = readme_text.replace(entity, expanded)
            else:
                logger.warning("Entity found with no expansion defined: %s", entity)
        return readme_text

    def _lang_level_double_dots(self) -> str:
        return "../" * self.lang_config["service_folder"].count("/")

    def _scrape_customs(self, readme_filename: Path, sdk_short: str) -> Dict[str, str]:
        customs = {}
        section = None
        subsection = None
        with open(readme_filename, "r", encoding="utf-8") as readme:
            for line in readme.readlines():
                if line.lstrip().startswith("<!--custom") and line.rstrip().endswith(
                    "start-->"
                ):
                    tag_parts = line.split(".")
                    section = tag_parts[1]
                    if len(tag_parts) > 3:
                        subsection = tag_parts[2]
                        if section not in customs:
                            customs[section] = {subsection: ""}
                        else:
                            customs[section][subsection] = ""
                    else:
                        customs[section] = ""
                elif line.lstrip().startswith("<!--custom") and line.rstrip().endswith(
                    "end-->"
                ):
                    end_section = line.split(".")[1]
                    if end_section != section:
                        logger.warning(
                            "Start section '%s' with non-matching end section '%s'.",
                            section,
                            end_section,
                        )
                    section = None
                    subsection = None
                elif section is not None:
                    if subsection is None:
                        customs[section] += line
                    else:
                        customs[section][subsection] += line
                else:
                    link_re = r"^\s*[-*] \[([^\]]+)\]\(([^)]+)\)\s*$"
                    link_match = re.match(link_re, line)
                    if link_match:
                        link, href = link_match.groups()
                        if link.startswith(sdk_short):
                            self.lang_config["sdk_api_ref"] = href
        return customs

    def render(self) -> RenderStatus:
        if self.lang_config is None:
            return RenderStatus.UNIMPLEMENTED

        sdk = _transform_sdk(self.scanner.sdk(), self.scanner.sdk_ver)
        svc = _transform_service(self.scanner.service())

        hello = self._transform_hellos()
        actions = self._transform_actions()
        basics = self._transform_basics()
        scenarios, crosses = self._transform_scenarios_and_crosses()
        custom_cats = self._transform_custom_categories()

        if (
            len(hello)
            + len(actions)
            + len(basics)
            + len(scenarios)
            + len(custom_cats)
            + len(crosses)
            == 0
        ):
            return RenderStatus.NO_EXAMPLES

        if not self.readme_filename.parent.exists():
            return RenderStatus.NO_FOLDER

        self.lang_config["name"] = self.scanner.lang_name
        self.lang_config["sdk_ver"] = self.scanner.sdk_ver
        self.lang_config["readme"] = f"{self._lang_level_double_dots()}README.md"
        unsupported = self.lang_config.get("unsupported", False)

        customs = (
            self._scrape_customs(self.readme_filename, sdk["short"])
            if self.readme_filename.exists()
            else {}
        )
        if "examples" not in customs:
            customs["examples"] = ""

        self.readme_text = self.template.render(
            lang_config=self.lang_config,
            sdk=sdk,
            service=svc,
            hello=hello,
            actions=actions,
            basics=basics,
            scenarios=scenarios,
            custom_cats=custom_cats,
            crosses=crosses,
            customs=customs,
            unsupported=unsupported,
        )
        self.readme_text = self._expand_entities(self.readme_text)

        # Check if the rendered text is different from the existing file
        if self.read_current() == self.readme_text:
            return RenderStatus.UNCHANGED
        else:
            return RenderStatus.UPDATED

    def write(self):
        if self.readme_filename.exists():
            if self.safe:
                self.readme_filename.rename(
                    self.readme_filename.parent / config.saved_readme
                )
            logging.debug("Removing existing file %s", self.readme_filename)
            # Do this so that new files are always updated to the correct case (README.md).
            self.readme_filename.unlink(missing_ok=True)

        with self.readme_filename.open("w", encoding="utf-8") as f:
            logging.debug("Writing file %s", self.readme_filename)
            f.write(self.readme_text)

    def read_current(self):
        try:
            with self.readme_filename.open("r", encoding="utf-8") as f:
                return f.read()
        except FileNotFoundError:
            return ""


def _transform_sdk(sdk: Sdk, sdk_ver):
    pre_sdk = next(v for v in sdk.versions if v.version == sdk_ver)

    if not pre_sdk:
        raise ValueError(
            f"Failed to find {sdk_ver} in {[v.version for v in sdk.versions]}"
        )

    post_sdk = {
        "long": pre_sdk.expanded.long,
        "short": pre_sdk.expanded.short,
        "guide": pre_sdk.guide,
    }

    return post_sdk


def _transform_service(pre_svc: Service):
    post_svc = {
        "long": pre_svc.expanded.long,
        "short": pre_svc.expanded.short,
        "blurb": pre_svc.blurb,
        "guide": asdict(pre_svc.guide),
        "api_ref": _doc_link(pre_svc.api_ref),
    }
    post_svc["guide"]["url"] = _doc_link(post_svc["guide"]["url"])
    return post_svc


def _doc_link(url_like: str) -> str:
    """Ensures `url_like` is a complete URL; either itself a http(s) URL, or prefixed with `doc_base_url`."""
    if url_like.startswith("http"):
        return url_like
    return f"{config.doc_base_url}/{url_like}"
