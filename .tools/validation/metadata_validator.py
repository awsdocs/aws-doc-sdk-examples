#!/usr/bin/env python3
"""
Validator for mapping and example metadata used to generate code example documentation.
This validator uses Yamale (https://github.com/23andMe/Yamale) to compare a schema
against YAML files stored in the metadata folder and runs as a GitHub action.
"""

import argparse
import datetime
import os
import re
import yaml
import yamale
from pathlib import Path
from typing import Iterable
from yamale import YamaleError
from yamale.validators import DefaultValidators, Validator, String


class ServiceName(Validator):
    """Validate that service names appear in services.yaml."""

    tag = "service_name"
    services = {}

    def get_name(self):
        return "service name found in services.yaml"

    def _is_valid(self, value):
        return value in self.services


class ServiceVersion(Validator):
    tag = "service_version"

    def get_name(self):
        return "valid service version"

    def _is_valid(self, value):
        try:
            hyphen_index = len(value)
            for _ in range(3):
                hyphen_index = value.rfind("-", 0, hyphen_index)
            time = datetime.datetime.strptime(value[hyphen_index + 1 :], "%Y-%m-%d")
            isdate = isinstance(time, datetime.date)
        except ValueError:
            isdate = False
        return isdate


class SourceKey(Validator):
    """Validate that curated source keys appear in curated/sources.yaml."""

    tag = "source_key"
    curated_sources: dict[str, any] = {}

    def get_name(self):
        return "source key found in curated/sources.yaml"

    def _is_valid(self, value):
        return value in self.curated_sources


class ExampleId(Validator):
    """
    Validate an example ID starts with a service ID and has underscore-separated
    operation and specializations (like sns_Subscribe_Email).
    """

    tag = "example_id"
    services: dict[str, any] = {}

    def get_name(self):
        return "valid example ID"

    def _is_valid(self, value):
        if not re.fullmatch("^[\\da-z-]+(_[\\da-zA-Z]+)+$", value):
            return False
        else:
            svc = value.split("_")[0]
            return svc == "cross" or svc in self.services


class BlockContent(Validator):
    """Validate that block content refers to an existing file."""

    tag = "block_content"
    block_names: list[str] = []

    def get_name(self):
        return "file found in the cross-content folder"

    def _is_valid(self, value):
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

    def _is_valid(self, value):
        if value == "":
            return True
        valid = True
        if self.check_aws:
            # All occurrences of AWS must be entities or within a word.
            valid = len(re.findall("(?<![&0-9a-zA-Z])AWS(?![;0-9a-zA-Z])", value)) == 0
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
            valid = value[-1] in "!.?"
            if not valid:
                self.last_err = "valid sentence or phrase: it must end with punctuation"
        if valid and self.no_end_punc:
            valid = value[-1] not in "!.?"
            if not valid:
                self.last_err = "valid string: it must not end with punctuation"
        if valid and self.end_punc_or_colon:
            valid = value[-1] in "!.?:"
            if not valid:
                self.last_err = (
                    "valid sentence or phrase: it must end with punctuation or a colon"
                )
        if valid and self.end_punc_or_semicolon:
            valid = value[-1] in "!.?;"
            if not valid:
                self.last_err = "valid sentence or phrase: it must end with punctuation or a semicolon"
        if valid:
            valid = super()._is_valid(value)
        return valid


def validate_files(schema_name: Path, meta_names: Iterable[Path], validators):
    """Iterate a list of files and validate each one against a schema."""
    errors = 0

    schema = yamale.make_schema(schema_name, validators=validators)
    for meta_name in meta_names:
        try:
            data = yamale.make_data(meta_name)
            yamale.validate(schema, data)
            print(f"{meta_name.resolve()} validation success! 👍")
        except YamaleError as e:
            print(e.message)
            errors += 1
    return errors


def validate_metadata(doc_gen: Path):
    # with open(doc_gen / "metadata" / "sdks.yaml") as sdks_file:
    #     sdks_yaml: dict[str, any] = yaml.safe_load(sdks_file)

    with open(doc_gen / "metadata" / "services.yaml") as services_file:
        services_yaml = yaml.safe_load(services_file)

    with open(
        doc_gen / "metadata" / "curated" / "sources.yaml"
    ) as curated_sources_file:
        curated_sources_yaml = yaml.safe_load(curated_sources_file)

    ServiceName.services = services_yaml
    SourceKey.curated_sources = curated_sources_yaml
    ExampleId.services = services_yaml
    BlockContent.block_names = os.listdir(doc_gen / "cross-content")

    validators = DefaultValidators.copy()
    validators[ServiceName.tag] = ServiceName
    validators[ServiceVersion.tag] = ServiceVersion
    validators[SourceKey.tag] = SourceKey
    validators[ExampleId.tag] = ExampleId
    validators[BlockContent.tag] = BlockContent
    validators[String.tag] = StringExtension

    schema_root = Path(__file__).parent / "schema"

    to_validate = [
        # (schema, metadata_glob)
        ("sdks_schema.yaml", "sdks.yaml"),
        ("services_schema.yaml", "services.yaml"),
        # TODO: Switch between strict schema for aws-doc-sdk-examples and loose schema for tributaries
        ("example_strict_schema.yaml", "*_metadata.yaml"),
        ("curated_sources_schema.yaml", "curated/sources.yaml"),
        ("curated_example_schema.yaml", "curated/*_metadata.yaml"),
    ]
    errors = 0
    for schema, metadata in to_validate:
        errors += validate_files(
            schema_root / schema, (doc_gen / "metadata").glob(metadata), validators
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
    args = parser.parse_args()

    errors = validate_metadata(Path(args.doc_gen))

    if errors == 0:
        print("Validation succeeded! 👍👍👍")
    else:
        print("\n********************************************")
        print("* Validation failed, please check the log! *")
        print("********************************************")
        exit(1)


if __name__ == "__main__":
    main()
