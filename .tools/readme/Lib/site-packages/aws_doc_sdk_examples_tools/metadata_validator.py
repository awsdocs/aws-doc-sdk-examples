#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Validator for mapping and example metadata used to generate code example documentation.
This validator uses Yamale (https://github.com/23andMe/Yamale) to compare a schema
against YAML files stored in the metadata folder and runs as a GitHub action.
"""

import argparse
import datetime
import os
import re
import xml.etree.ElementTree as xml_tree
import yaml
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Dict, Iterable, List, Optional, Set

import yamale  # type: ignore
from yamale import YamaleError  # type: ignore
from yamale.validators import DefaultValidators, Validator, String  # type: ignore

from .metadata_errors import (
    MetadataErrors,
    MetadataParseError,
)


class SdkVersion(Validator):
    """Validate that sdk version appears in sdks.yaml."""

    tag = "sdk_version"
    sdks: Dict[str, Any] = {}

    def _is_valid(self, value: str):
        return value in self.sdks


class ServiceName(Validator):
    """Validate that service names appear in services.yaml."""

    tag = "service_name"
    services: Set[str] = set()

    def get_name(self):
        return "service name found in services.yaml"

    def _is_valid(self, value: str):
        return value in self.services


class ServiceVersion(Validator):
    tag = "service_version"

    def get_name(self):
        return "valid service version"

    def _is_valid(self, value: str):
        try:
            hyphen_index = len(value)
            for _ in range(3):
                hyphen_index = value.rfind("-", 0, hyphen_index)
            datetime.datetime.strptime(value[hyphen_index + 1 :], "%Y-%m-%d")
            return True
        except ValueError:
            return False


class ExampleId(Validator):
    """
    Validate an example ID starts with a service ID and has underscore-separated
    operation and specializations (like sns_Subscribe_Email).
    """

    tag = "example_id"
    services: Set[str] = set()

    def get_name(self):
        return "valid example ID"

    def _is_valid(self, value: str):
        return re.fullmatch("^[\\da-z-]+(_[\\da-zA-Z-]+)+$", value)


class BlockContent(Validator):
    """Validate that block content refers to an existing file."""

    tag = "block_content"
    block_names: List[str] = []

    def get_name(self):
        return "file found in the cross-content folder"

    def _is_valid(self, value: str):
        return value in self.block_names


class StringExtension(String):
    """Validate that strings don't contain non-entity AWS usage."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.check_aws = bool(kwargs.pop("check_aws", True))
        self.upper_start = bool(kwargs.pop("upper_start", False))
        self.lower_start = bool(kwargs.pop("lower_start", False))
        self.end_punc = bool(kwargs.pop("end_punc", False))
        self.no_end_punc = bool(kwargs.pop("no_end_punc", False))
        self.end_punc_or_colon = bool(kwargs.pop("end_punc_or_colon", False))
        self.end_punc_or_semicolon = bool(kwargs.pop("end_punc_or_semicolon", False))
        self.last_err = "valid string"

    def get_name(self):
        return self.last_err

    def _is_valid(self, value: str):
        if value == "":
            return True
        valid = True
        if self.check_aws:
            valid = self._validate_aws_entity_usage(value)
            if not valid:
                self.last_err = 'valid string: it contains a non-entity usage of "AWS"'
        if valid and self.upper_start:
            valid = str.isupper(value[0])
            if not valid:
                self.last_err = "valid string: it must start with an uppercase letter"
        if valid and self.lower_start:
            valid = str.islower(value[0])
            if not valid:
                self.last_err = "valid string: it must start with a lowercase letter"
        if valid and self.end_punc:
            valid = value.rstrip()[-1] in "!.?"
            if not valid:
                self.last_err = "valid sentence or phrase: it must end with punctuation"
        if valid and self.no_end_punc:
            valid = value.rstrip()[-1] not in "!.?"
            if not valid:
                self.last_err = "valid string: it must not end with punctuation"
        if valid and self.end_punc_or_colon:
            valid = value.rstrip()[-1] in "!.?:"
            if not valid:
                self.last_err = (
                    "valid sentence or phrase: it must end with punctuation or a colon"
                )
        if valid and self.end_punc_or_semicolon:
            valid = value.rstrip()[-1] in "!.?;"
            if not valid:
                self.last_err = "valid sentence or phrase: it must end with punctuation or a semicolon"
        if valid:
            valid = super()._is_valid(value)
        return valid

    @staticmethod
    def _validate_aws_entity_usage(value: str) -> bool:
        """
        All occurrences of AWS must be entities or within a word or within a programlisting or code or noloc block.

        Count all bare AWS occurrences within accepted XML tags.
        Count all bare AWS occurrences overall.
        If these counts differ, there's an invalid usage.
        """
        xval = value.replace("&", "&amp;")
        xtree = xml_tree.fromstring(f"<fake><para>{xval}</para></fake>")
        blocks = (
            xtree.findall(".//programlisting")
            + xtree.findall(".//code")
            + xtree.findall(".//noloc")
        )
        aws_in_blocks = 0
        for element in blocks:
            aws_in_blocks += len(
                re.findall("(?<![&0-9a-zA-Z])AWS(?![;0-9a-zA-Z])", str(element.text))
            )
        aws_everywhere = len(re.findall("(?<![&0-9a-zA-Z])AWS(?![;0-9a-zA-Z])", value))
        return aws_everywhere == aws_in_blocks


@dataclass
class ValidateYamaleError(MetadataParseError):
    yamale_error: Optional[YamaleError] = field(default=None)

    def message(self):
        return f"Yamale Error: {self.yamale_error.message}"


def validate_files(
    schema_name: Path,
    meta_names: Iterable[Path],
    validators: Dict[str, Validator],
    strict: bool,
    errors: MetadataErrors,
):
    """Iterate a list of files and validate each one against a schema."""

    schema = yamale.make_schema(schema_name, validators=validators)
    for meta_name in meta_names:
        try:
            data = yamale.make_data(meta_name)
            yamale.validate(schema, data, strict=strict)
            print(f"{meta_name.resolve()} validation success! 👍")
        except YamaleError as e:
            errors.append(ValidateYamaleError(file=meta_name, yamale_error=e))
    return errors


def validate_metadata(
    doc_gen_root: Path, strict: bool, errors: MetadataErrors
) -> MetadataErrors:
    config = Path(__file__).parent / "config"
    with open(config / "sdks.yaml") as sdks_file:
        sdks_yaml: Dict[str, Any] = yaml.safe_load(sdks_file)

    with open(config / "services.yaml") as services_file:
        services_yaml = yaml.safe_load(services_file)

    SdkVersion.sdks = sdks_yaml
    ServiceName.services = services_yaml
    ExampleId.services = services_yaml
    cross_content = doc_gen_root / ".doc_gen" / "cross-content"
    has_cross_content = cross_content.exists()
    BlockContent.block_names = os.listdir(cross_content) if has_cross_content else []

    validators = DefaultValidators.copy()
    validators[ServiceName.tag] = ServiceName
    validators[ServiceVersion.tag] = ServiceVersion
    validators[ExampleId.tag] = ExampleId
    validators[BlockContent.tag] = BlockContent
    validators[String.tag] = StringExtension

    config_root = Path(__file__).parent / "config"
    if strict:
        example_schema = "example_strict_schema.yaml"
    else:
        example_schema = "example_schema.yaml"

    to_validate = [
        # (schema, metadata_glob)
        (config_root / "sdks_schema.yaml", config_root, "sdks.yaml"),
        (config_root / "services_schema.yaml", config_root, "services.yaml"),
        (
            config_root / example_schema,
            doc_gen_root / ".doc_gen" / "metadata",
            "*_metadata.yaml",
        ),
    ]
    for schema, meta_root, metadata in to_validate:
        validate_files(
            schema,
            meta_root.glob(metadata),
            validators,
            strict,
            errors,
        )

    return errors


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--doc-gen",
        default=f"{Path(__file__).parent / '..' / '..' / '.doc_gen'}",
        help="The folder that contains schema and metadata files.",
        required=False,
    )
    parser.add_argument(
        "--strict", default=True, help="Use strict schema.", required=False
    )
    args = parser.parse_args()

    errors = validate_metadata(Path(args.doc_gen), args.strict, MetadataErrors())

    if len(errors) == 0:
        print("Validation succeeded! 👍👍👍")
    else:
        print("\n********************************************")
        print("* Validation failed, please check the log! *")
        print("********************************************")
        exit(1)


if __name__ == "__main__":
    main()
