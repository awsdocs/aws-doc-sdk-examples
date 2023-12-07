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
import sys
from dataclasses import dataclass, field
from pathlib import Path

from file_utils import get_files
from metadata_errors import (
    MetadataErrors,
    MetadataError,
    MetadataParseError,
    DuplicateItemException,
)
from spdx import verify_spdx
import validator_config

logger = logging.getLogger(__name__)


def check_files(root: Path, errors: MetadataErrors, do_check_spdx: bool):
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

        with open(file_path, encoding="utf-8-sig") as f:
            file_contents = f.read()

        verify_no_deny_list_words(file_contents, file_path, errors)
        verify_no_secret_keys(file_contents, file_path, errors)
        verify_no_secret_keys(file_contents, file_path, errors)
        verify_snippet_start_end(file_contents, file_path, errors)
        if do_check_spdx:
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
                errors.append(DenyListWord(file=str(file_location), word=word))
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


def verify_sample_files(root_path: Path, errors: MetadataErrors) -> None:
    """Verify sample files meet the requirements and have not moved."""
    sample_files_folder = root_path / "resources/sample_files"
    media_folder = ".sample_media"
    file_list: list[str] = []
    for path in get_files(sample_files_folder):
        file_list.append(path.name)
        ext = path.suffix
        if path.name not in validator_config.EXPECTED_SAMPLE_FILES:
            errors.append(UnknownSampleFile(file=str(path)))
        if ext.lower() in validator_config.MEDIA_FILE_TYPES:
            if media_folder not in path:
                errors.append(InvalidSampleDirectory(file=str(path), dir=media_folder))
        size_in_mb = os.path.getsize(path) / ONE_MB_AS_BYTES
        if size_in_mb > MAX_FILE_SIZE_MB:
            errors.append(SampleFileTooLarge(file=str(path), size_in_mb=size_in_mb))

    for sample_file in validator_config.EXPECTED_SAMPLE_FILES:
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
        return f"{len(self.word)} character string '{self.word}' might be a secret access key. If not, add it to ALLOW_LIST in validator_config.py"


TWENTY_LONG_KEY_REGEX = "(?<=[^A-Z0-9])[A][ACGIKNPRS][A-Z]{2}[A-Z0-9]{16}(?=[^A-Z0-9])"
FORTY_LONG_KEY_REGEX = "(?<=[^a-zA-Z0-9/+=])[a-zA-Z0-9/+=]{40}(?=[^a-zA-Z0-9/+=])"


def verify_no_secret_keys(
    file_contents: str, file_location: Path, errors: MetadataErrors
):
    """Verify the file does not contain 20- or 40- length character strings,
    which might be secret keys. Allow strings in the allowlist in
    https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/.tools/validation/validator_config.py.
    """
    keys = set(
        re.findall(TWENTY_LONG_KEY_REGEX, file_contents)
        + re.findall(FORTY_LONG_KEY_REGEX, file_contents)
    )
    keys -= validator_config.ALLOW_LIST
    for word in keys:
        errors.append(PossibleSecretKey(file=str(file_location), word=word))


def main():
    root_path = Path(__file__).parent.parent.parent
    print("----------\n\nRun Tests\n")
    errors = MetadataErrors()
    check_files(root_path, errors, True)
    verify_sample_files(root_path, errors)
    error_count = len(errors)
    if error_count > 0:
        print(errors)
        print(f"{error_count} errors found, please fix them.")
    else:
        print("All checks passed, you are cleared to check in.")
    # Travis CI reports an error if the script exits with a non-zero code.
    sys.exit(error_count)


if __name__ == "__main__":
    main()
