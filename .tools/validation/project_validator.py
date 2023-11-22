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
import argparse
import logging
import sys
from dataclasses import dataclass, field
from pathlib import Path

from file_utils import get_files
from metadata_errors import MetadataErrors, MetadataParseError, DuplicateItemException
import validator_config

logger = logging.getLogger(__name__)


def check_files(root: Path, errors: MetadataErrors):
    """
    Walk a folder system, scanning all files with specified extensions.
    Errors are logged and counted and the count of errors is returned.

    :param root: The root folder to start the walk.
    :return: The number of errors found in the scanned files.
    """
    file_count = 0
    for file_path in get_files(
        root,
        lambda filename, ext: ext.lower() not in validator_config.EXT_LOOKUP
        or filename in validator_config.IGNORE_FILES,
    ):
        file_count += 1
        logger.info("\nChecking File: %s", file_path)

        with open(file_path, encoding="utf-8") as f:
            file_contents = f.read()

        verify_no_deny_list_words(file_contents, file_path, errors)
        verify_no_secret_keys(file_contents, file_path, errors)
        verify_no_secret_keys(file_contents, file_path, errors)
        verify_snippet_start_end(file_contents, file_path, errors)

    print(f"{file_count} files scanned in {root}.\n")


def word_parts(contents: str):
    for word in contents.split():
        # split on / for URLs, to find invalid Host names
        for part in word.lower().split("/"):
            part = re.sub(r"^[.:]", "", re.sub(r"[.:]$", "", part))
            yield word, part


@dataclass
class DenyListWord(MetadataParseError):
    word: str = field(default="")


def verify_no_deny_list_words(
    file_contents: str, file_location: str, errors: MetadataErrors
) -> None:
    """Verify no words in the file are in the list of denied words."""
    for word, part in word_parts(file_contents):
        if part in validator_config.DENY_LIST:
            try:
                errors.append(DenyListWord(file=str(file_location), word=word))
            except DuplicateItemException:
                pass


@dataclass
class UnknownSampleFile(MetadataParseError):
    def message(self):
        return (
            f"File {self.file} was not found in the list of expected sample files. If this is a new sample file, add it to the EXPECTED_SAMPLE_FILES list in {__package__}.{__file__}.",
        )


@dataclass
class InvalidSampleDirectory(MetadataParseError):
    dir: str = field(default=-1)

    def message(self):
        return f"must be in the {self.dir} directory."


ONE_MB_AS_BYTES = 1000000
MAX_FILE_SIZE_MB = 10


@dataclass
class SampleFileTooLarge(MetadataParseError):
    size_in_mb: float = field(default="")

    def message(self):
        return f"maximum file size is {MAX_FILE_SIZE_MB}MB, file is {self.size_in_mb}"


@dataclass
class MissingSampleFile(MetadataParseError):
    samples_dir: str = field(default="")

    def message(self):
        return (
            f"Expected sample file was not found in '{self.samples_dir}'. If this file was intentionally removed, remove it from the EXPECTED_SAMPLE_FILES list in {__package__}.{__file__}.",
        )


def verify_sample_files(root_path: Path, errors: MetadataParseError) -> None:
    """Verify sample files meet the requirements and have not moved."""
    sample_files_folder = os.path.join(root_path, "resources/sample_files")
    media_folder = ".sample_media"
    file_list = []
    for path, _dirs, files in os.walk(sample_files_folder, topdown=True):
        for file_name in files:
            file_list.append(file_name)
            file_path = os.path.join(path, file_name)
            ext = os.path.splitext(file_name)[1].lstrip(".")
            if file_name not in validator_config.EXPECTED_SAMPLE_FILES:
                errors.append(UnknownSampleFile(file=file_path))
            if ext.lower() in validator_config.MEDIA_FILE_TYPES:
                if media_folder not in file_path:
                    errors.append(
                        InvalidSampleDirectory(file=file_path, dir=media_folder)
                    )
            size_in_mb = os.path.getsize(file_path) / ONE_MB_AS_BYTES
            if size_in_mb > MAX_FILE_SIZE_MB:
                errors.append(SampleFileTooLarge(file=file_path, size_in_mb=size_in_mb))

    for sample_file in validator_config.EXPECTED_SAMPLE_FILES:
        if sample_file not in file_list:
            errors.append(
                MissingSampleFile(file=sample_file, samples_dir=sample_files_folder)
            )


@dataclass
class PossibleSecretKey(MetadataParseError):
    word: str = field(default="")

    def message(self):
        return (
            f"{len(self.word)} character string '{self.word}' might be a secret access key. If not, add it to the allow list in {__package__}.validator_config.",
        )


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


@dataclass
class SnippetParseError(MetadataParseError):
    tag: str = field(default="")


@dataclass
class DuplicateSnippetTagInFile(SnippetParseError):
    def message(self):
        return f"Duplicate tag {self.tag}"


@dataclass
class SnippetNoMatchingStart(SnippetParseError):
    def message(self):
        return f"No matching start for {self.tag}"


@dataclass
class SnippetNoMatchingEnd(SnippetParseError):
    def message(self):
        return f"No matching end for {self.tag}"


# TODO move this to snippets
def verify_snippet_start_end(
    file_contents: str, file_location: Path, errors: MetadataErrors
):
    """Scan the file contents for snippet-start and snippet-end tags and verify
    that they are in matched pairs. Log errors and return the count of errors."""
    snippet_start = "snippet" + "-start:["
    snippet_end = "snippet" + "-end:["
    snippet_tags = set()
    for word in file_contents.split():
        if snippet_start in word:
            tag = word.split("[")[1]
            if tag in snippet_tags:
                errors.append(DuplicateSnippetTagInFile(file=file_location, tag=tag))
            else:
                snippet_tags.add(tag)
        elif snippet_end in word:
            tag = word.split("[")[1]
            if tag in snippet_tags:
                snippet_tags.remove(tag)
            else:
                errors.append(SnippetNoMatchingStart(file=file_location, tag=tag))

    for tag in snippet_tags:
        errors.append(SnippetNoMatchingEnd(file=file_location, tag=tag))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--quiet",
        action="store_true",
        help="Suppresses output of filenames while parsing. " "The default is False.",
    )
    parser.add_argument(
        "--root",
        help="The root path from which to search for files "
        "to check. The default is the current working "
        "folder.",
    )
    args = parser.parse_args()

    root_path = Path(
        os.path.abspath(".") if not args.root else os.path.abspath(args.root)
    )

    print("----------\n\nRun Tests\n")
    errors = MetadataErrors()
    check_files(root_path, args.quiet, errors)
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
