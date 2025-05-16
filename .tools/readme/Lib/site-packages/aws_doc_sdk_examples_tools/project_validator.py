#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
This script contains the checkin tests that are run whenever a pull request is
submitted or changed (using Travis CI, configured in .travis.yml).

The script scans code files and does the following:

    * Disallows a list of specific words.
    * Checks for known sample files.
    * Disallows any 20- or 40- character strings that fit a specific regex profile
      that indicates they might be secret access keys. Allows strings that fit the
      regex profile if they are in the allow list.
    * Disallows filenames that contain 20- or 40- character strings that fit the same
      regex profile, unless the filename is in the allow list.
    * Verifies that snippet-start and snippet-end tags are in matched pairs. You are
      not required to include these tags, but if you do they must be in pairs.
"""

import os
import re
import logging
from dataclasses import dataclass, field
from pathlib import Path
from typing import List, Set

from .file_utils import get_files
from .metadata_errors import (
    MetadataErrors,
    MetadataError,
    MetadataParseError,
    DuplicateItemException,
)
from .spdx import verify_spdx
from aws_doc_sdk_examples_tools import validator_config

logger = logging.getLogger(__name__)


@dataclass
class ValidationConfig:
    allow_list: Set[str] = field(default_factory=set)
    sample_files: Set[Path] = field(default_factory=set)
    strict_titles: bool = False

    def clone(self):
        return ValidationConfig(
            allow_list={*self.allow_list}, sample_files={*self.sample_files}
        )


def check_files(
    root: Path,
    validation: ValidationConfig,
    errors: MetadataErrors,
):
    """
    Walk a folder system, scanning all files with specified extensions.
    Errors are logged and counted and the count of errors is returned.

    :param root: The root folder to start the walk.
    :return: The number of errors found in the scanned files.
    """
    file_count = 0
    for file_path in get_files(root, validator_config.skip):
        file_count += 1
        logger.info("\nChecking File: %s", file_path)

        try:
            with open(file_path, encoding="utf-8-sig") as f:
                file_contents = f.read()
        except Exception as e:
            file_contents = ""
            print(f"Could not verify {file_path}: {e}")
            errors.append(MetadataError(file=file_path))

        verify_no_deny_list_words(file_contents, file_path, errors)
        verify_no_secret_keys(file_contents, file_path, validation, errors)
        verify_spdx(file_contents, file_path, errors)

    print(f"{file_count} files scanned in {root}.\n")


def word_parts(contents: str):
    for word in contents.split():
        # split on / for URLs, to find invalid Host names
        for part in word.lower().split("/"):
            part = re.sub(r"^[.:]", "", re.sub(r"[.:]$", "", part))
            yield word, part


@dataclass
class DenyListWord(MetadataError):
    word: str = field(default="")

    def message(self) -> str:
        return f"found deny list word {self.word}"


def verify_no_deny_list_words(
    file_contents: str, file_location: Path, errors: MetadataErrors
) -> None:
    """Verify no words in the file are in the list of denied words."""
    for word, part in word_parts(file_contents):
        if part in validator_config.DENY_LIST:
            try:
                errors.append(DenyListWord(file=file_location, word=word))
            except DuplicateItemException:
                pass


@dataclass
class UnknownSampleFile(MetadataError):
    def message(self):
        return f"File {self.file} was not found in the list of expected sample files. If this is a new sample file, add it to the EXPECTED_SAMPLE_FILES list in {__package__}.{__file__}."


@dataclass
class InvalidSampleDirectory(MetadataParseError):
    dir: str = field(default="")

    def message(self):
        return f"must be in the {self.dir} directory."


ONE_MB_AS_BYTES = 1000000
MAX_FILE_SIZE_MB = 10


@dataclass
class SampleFileTooLarge(MetadataParseError):
    size_in_mb: float = field(default=-1)

    def message(self):
        return f"maximum file size is {MAX_FILE_SIZE_MB}MB, file is {self.size_in_mb}"


@dataclass
class MissingSampleFile(MetadataError):
    samples_dir: str = field(default="")

    def message(self):
        return f"Expected sample file was not found in '{self.samples_dir}'. If this file was intentionally removed, remove it from the EXPECTED_SAMPLE_FILES list in {__package__}.{__file__}."


def verify_sample_files(
    root_path: Path, validation: ValidationConfig, errors: MetadataErrors
) -> None:
    """Verify sample files meet the requirements and have not moved."""
    sample_files_folder = root_path / "resources/sample_files"
    if not sample_files_folder.exists():
        # TODO allow projects to configure their specific expected sample files.
        return
    media_folder = ".sample_media"
    file_list: List[str] = []
    for path in get_files(sample_files_folder):
        file_list.append(path.name)
        ext = path.suffix
        if path.name not in validation.sample_files:
            errors.append(UnknownSampleFile(file=path))
        if ext.lower() in validator_config.MEDIA_FILE_TYPES:
            if media_folder not in str(path):
                errors.append(InvalidSampleDirectory(file=path, dir=media_folder))
        size_in_mb = os.path.getsize(path) / ONE_MB_AS_BYTES
        if size_in_mb > MAX_FILE_SIZE_MB:
            errors.append(SampleFileTooLarge(file=path, size_in_mb=size_in_mb))

    for sample_file in validation.sample_files:
        if sample_file not in file_list:
            errors.append(
                MissingSampleFile(
                    file=sample_file, samples_dir=str(sample_files_folder)
                )
            )


@dataclass
class PossibleSecretKey(MetadataError):
    word: str = field(default="")

    def message(self):
        return f"{len(self.word)} character string '{self.word}' might be a secret access key. If not, add it to allow_list in your project's .doc_gen/validation.yaml"


TWENTY_LONG_KEY_REGEX = "(?<=[^A-Z0-9])[A][ACGIKNPRS][A-Z]{2}[A-Z0-9]{16}(?=[^A-Z0-9])"
FORTY_LONG_KEY_REGEX = "(?<=[^a-zA-Z0-9/+=])[a-zA-Z0-9/+=]{40}(?=[^a-zA-Z0-9/+=])"


def verify_no_secret_keys(
    file_contents: str,
    file_location: Path,
    validation: ValidationConfig,
    errors: MetadataErrors,
):
    """Verify the file does not contain 20- or 40- length character strings,
    which might be secret keys. Allow strings in the allowlist in
    https://github.com/awsdocs/aws-doc-sdk-examples-tools/blob/main/.tools/validation/validator_config.py.
    """
    keys = set(
        re.findall(TWENTY_LONG_KEY_REGEX, file_contents)
        + re.findall(FORTY_LONG_KEY_REGEX, file_contents)
    )
    keys -= validator_config.ALLOW_LIST
    keys -= validation.allow_list
    for word in keys:
        errors.append(PossibleSecretKey(file=file_location, word=word))
