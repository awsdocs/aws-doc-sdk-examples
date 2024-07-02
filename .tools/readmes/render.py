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
    """Custom exception to be raised when required metadata is missing."""
    pass


class Renderer:
    """
    A class for rendering service READMEs using Jinja2 templates.

    Attributes:
        scanner (object): An instance of a Scanner class used to scan code examples.
        sdk_ver (int): The version of the SDK being used.
        safe (bool): A flag indicating whether to keep the existing README file.
        svc_folder (str, optional): The service folder path.
        template (jinja2.environment.Template): The Jinja2 template for rendering the README.
        lang_config (dict): The language configuration for the specified SDK version.
        readme_filename (str): The path to the README file.
        readme_text (str): The rendered README text.
        readme_updated (bool): A flag indicating whether the README file was updated.
    """

    def __init__(self, scanner, sdk_ver, safe, svc_folder=None):
        """
        Initialize the Renderer class.

        Args:
            scanner (object): An instance of a Scanner class used to scan code examples.
            sdk_ver (int): The version of the SDK being used.
            safe (bool): A flag indicating whether to keep the existing README file.
            svc_folder (str, optional): The service folder path.
        """
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
    def _doc_link(url):
        """
        Constructs a documentation link from the given URL.

        Args:
            url (str): The URL to be converted into a documentation link.

        Returns:
            str: The constructed documentation link.
        """
        return url if url.startswith("http") else f"{config.doc_base_url}/{url}"

    def _transform_sdk(self):
        """
        Transforms the SDK information from the SDK metadata.

        Returns:
            dict: A dictionary containing the transformed SDK information.
        """
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
        """
        Transforms the service information from the service metadata.

        Returns:
            dict: A dictionary containing the transformed service information.
        """
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
        """
        Transforms the "hello" examples from the metadata.

        Args:
            pre_hello (dict): The pre-transformed "hello" examples from the metadata.

        Returns:
            list: A list of transformed "hello" examples.
        """
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
                    pre, self.sdk_ver, self.lang_config["service_folder"], ''
                ),
                "api": api,
            }
            post_hello.append(action)
        return sorted(post_hello, key=itemgetter("title_abbrev"))

    def _transform_actions(self, pre_actions):
        """
        Transforms the action examples from the metadata.

        Args:
            pre_actions (dict): The pre-transformed action examples from the metadata.

        Returns:
            list: A list of transformed action examples.
        """
        post_actions = []
        for pre_id, pre in pre_actions.items():
            try:
                api = next(iter(pre["services"][self.scanner.svc_name]))
            except:
                raise MissingMetadataError(
                    f"Action not found for example {pre_id} and service {self.scanner.svc_name}."
                )
            action = {
                "title