# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from collections import defaultdict
import datetime
import logging
import os
import re
from jinja2 import Environment, FileSystemLoader, select_autoescape
from operator import itemgetter
from pathlib import Path

import config

logger = logging.getLogger(__name__)


class MissingMetadataError(Exception):
    pass


class Renderer:
    def __init__(self, scanner, sdk_ver, safe, svc_folder=None):
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
        self.scanner = scanner
        self.sdk_ver = int(sdk_ver)
        self.lang_config = config.language.get(self.scanner.lang_name, {}).get(
            self.sdk_ver, None
        )
        if self.lang_config is None:
            return
        self.lang_config = self.lang_config.copy()
        service_info = {
            "name": self.scanner.svc_name,
            "sort": self.scanner.service()["sort"].replace(" ", ""),
        }
        if svc_folder is not None:
            self.lang_config["service_folder"] = svc_folder
        else:
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
                    "Service folder not found. You must either specify a service_folder template in config.py or\n"
                    "as a command line --svc_folder argument."
                )
        sdk_api_ref_tmpl = env.from_string(self.lang_config.get("sdk_api_ref", ""))
        self.lang_config["sdk_api_ref"] = sdk_api_ref_tmpl.render(service=service_info)
        self.safe = safe

    @staticmethod
    def _doc_link(url_like: str) -> str:
        """Ensures `url_like` is a complete URL; either itself a http(s) URL, or prefixed with `doc_base_url`."""
        if url_like.startswith("http"):
            return url_like
        return f"{config.doc_base_url}/{url_like}"

    def _transform_sdk(self):
        pre_sdk = self.scanner.sdk()["sdk"][self.sdk_ver]
        if "expanded" not in pre_sdk or "guide" not in pre_sdk:
            logger.error(
                "%s %s entry in sdks.yaml does not have required entity expansions or guide URL defined.",
                self.scanner.lang_name,
                self.sdk_ver,
            )
            raise MissingMetadataError
        else:
            post_sdk = {
                "long": pre_sdk["expanded"]["long"],
                "short": pre_sdk["expanded"]["short"],
                "guide": pre_sdk["guide"],
            }
        return post_sdk

    def _transform_service(self):
        pre_svc = self.scanner.service()
        if (
            "expanded" not in pre_svc
            or "blurb" not in pre_svc
            or "guide" not in pre_svc
        ):
            logger.error(
                "%s entry in services.yaml does not have required entity expansions, blurb, or guide "
                "info defined.",
                self.scanner.svc_name,
            )
            raise MissingMetadataError
        else:
            post_svc = {
                "long": pre_svc["expanded"]["long"],
                "short": pre_svc["expanded"]["short"],
                "blurb": pre_svc["blurb"],
                "guide": pre_svc["guide"],
                "api_ref": self._doc_link(pre_svc["api_ref"]),
            }
            post_svc["guide"]["url"] = self._doc_link(post_svc["guide"]["url"])
        return post_svc

    def _transform_hello(self, pre_hello):
        post_hello = []
        for _, pre in pre_hello.items():
            try:
                api = next(iter(pre["services"][self.scanner.svc_name]))
            except:
                api = ""
            action = {
                "title_abbrev": pre["title_abbrev"],
                "synopsis": pre["synopsis"],
                "file": self.scanner.snippet(
                    pre, self.sdk_ver, self.lang_config["service_folder"], api
                ),
                "run_file": self.scanner.snippet(
                    pre, self.sdk_ver, self.lang_config["service_folder"], ""
                ),
                "api": api,
            }
            post_hello.append(action)
        return sorted(post_hello, key=itemgetter("title_abbrev"))

    def _transform_actions(self, pre_actions):
        post_actions = []
        for pre_id, pre in pre_actions.items():
            try:
                api = next(iter(pre["services"][self.scanner.svc_name]))
            except:
                raise MissingMetadataError(
                    f"Action not found for example {pre_id} and service {self.scanner.svc_name}."
                )
            action = {
                "title_abbrev": api,
                "file": self.scanner.snippet(
                    pre, self.sdk_ver, self.lang_config["service_folder"], api
                ),
            }
            post_actions.append(action)
        return sorted(post_actions, key=itemgetter("title_abbrev"))

    def _transform_scenarios(self):
        pre_scenarios = self.scanner.scenarios()
        _, cross_scenarios = self.scanner.crosses()
        pre_scenarios.update(cross_scenarios)
        post_scenarios = []
        for pre_id, pre in pre_scenarios.items():
            scenario = {
                "id": pre_id,
                "title_abbrev": pre["title_abbrev"],
                "synopsis": pre.get("synopsis"),
                "synopsis_list": pre.get("synopsis_list", []),
                "file": self.scanner.snippet(
                    pre, self.sdk_ver, self.lang_config["service_folder"], ""
                ),
            }
            if scenario["file"] is None:
                logger.warning(
                    "Couldn't find file for scenario: %s.", scenario["title_abbrev"]
                )
            else:
                post_scenarios.append(scenario)
        return sorted(post_scenarios, key=itemgetter("title_abbrev"))

    def _transform_custom_categories(self):
        pre_cats = self.scanner.custom_categories()
        post_cats = defaultdict(list)
        for pre_id, pre in pre_cats.items():
            api = ""
            if len(pre["services"][self.scanner.svc_name]) == 1:
                try:
                    api = next(iter(pre["services"][self.scanner.svc_name]))
                except:
                    api = ""
            cat = {
                "id": pre_id,
                "title_abbrev": pre["title_abbrev"],
                "synopsis": pre.get("synopsis"),
                "synopsis_list": pre.get("synopsis_list", []),
                "file": self.scanner.snippet(
                    pre, self.sdk_ver, self.lang_config["service_folder"], api
                ),
            }
            if cat["file"] is None:
                logger.warning(
                    "Couldn't find file for scenario: %s.", cat["title_abbrev"]
                )
            else:
                post_cats[pre.get("category")].append(cat)
        sorted_cats = {}
        for key in sorted(post_cats.keys()):
            sorted_cats[key] = sorted(post_cats[key], key=itemgetter("title_abbrev"))
        return sorted_cats

    def _transform_crosses(self):
        pre_crosses, _ = self.scanner.crosses()
        post_crosses = []
        for _, pre in pre_crosses.items():
            github = None
            for ver in pre["languages"][self.scanner.lang_name]["versions"]:
                if ver["sdk_version"] == self.sdk_ver:
                    github = ver.get("github")
                    break
            if github is None:
                logger.info(
                    "GitHub path not specified for cross-service example: %s %s.",
                    self.scanner.lang_name,
                    pre["title_abbrev"],
                )
            else:
                base_folder = f"{config.language[self.scanner.lang_name][self.sdk_ver]['base_folder']}/"
                if base_folder in github:
                    github = (
                        self._lang_level_double_dots() + github.split(base_folder, 1)[1]
                    )
                cross = {
                    "title_abbrev": pre["title_abbrev"],
                    "file": github,
                }
                post_crosses.append(cross)
        return sorted(post_crosses, key=itemgetter("title_abbrev"))

    def _expand_entities(self, readme_text):
        entities = set(re.findall(r"&[\dA-Za-z-_]+;", readme_text))
        for entity in entities:
            expanded = self.scanner.expand_entity(entity)
            if expanded is not None:
                readme_text = readme_text.replace(entity, expanded)
            else:
                logger.warning("Entity found with no expansion defined: %s", entity)
        return readme_text

    def _lang_level_double_dots(self):
        return "../" * self.lang_config["service_folder"].count("/")

    def _scrape_customs(self, readme_filename, sdk_short):
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

    def render(self):
        if self.lang_config is None:
            return None, False  # Return False to indicate no update
        sdk = self._transform_sdk()
        svc = self._transform_service()
        hello = self._transform_hello(self.scanner.hello())
        actions = self._transform_actions(self.scanner.actions())
        scenarios = self._transform_scenarios()
        custom_cats = self._transform_custom_categories()
        crosses = self._transform_crosses()
        self.lang_config["name"] = self.scanner.lang_name
        self.lang_config["sdk_ver"] = self.sdk_ver
        self.lang_config["readme"] = f"{self._lang_level_double_dots()}README.md"
        unsupported = self.lang_config.get("unsupported", False)

        self.readme_filename = f'{self.lang_config["service_folder"]}/{config.readme}'
        readme_exists = os.path.exists(self.readme_filename)
        customs = (
            self._scrape_customs(self.readme_filename, sdk["short"])
            if readme_exists
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
            scenarios=scenarios,
            custom_cats=custom_cats,
            crosses=crosses,
            customs=customs,
            unsupported=unsupported,
        )
        self.readme_text = self._expand_entities(self.readme_text)

        # Check if the rendered text is different from the existing file
        readme_updated = not self.check()

        # Assign the boolean value to the Renderer instance
        self.readme_updated = readme_updated

        return self, readme_updated

    def write(self):
        if self.safe and Path(self.readme_filename).exists():
            os.rename(
                self.readme_filename,
                f'{self.lang_config["service_folder"]}/{config.saved_readme}',
            )
        # Do this so that new files are always updated to the correct case (README.md).
        Path(self.readme_filename).unlink(missing_ok=True)
        with open(self.readme_filename, "w", encoding="utf-8") as f:
            f.write(self.readme_text)
        if self.readme_updated:
            print(f"Updated {self.readme_filename}.")
        else:
            print(f"No updates required for {self.readme_filename}.")

    def check(self):
        with open(self.readme_filename, "r", encoding="utf-8") as f:
            readme_current = f.read()
            return readme_current == self.readme_text
