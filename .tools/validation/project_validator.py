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
from pathlib import Path
from pathspec import GitIgnoreSpec
from typing import Generator

from validator_config import ALLOW_LIST, DENY_LIST

logger = logging.getLogger(__name__)

# Only files with these extensions are scanned.
EXT_LOOKUP = {
    "c": "C",
    "cpp": "C++",
    "cs": "C#",
    "go": "Go",
    "html": "JavaScript",
    "java": "Java",
    "js": "JavaScript",
    "kt": "Kotlin",
    "php": "PHP",
    "py": "Python",
    "rb": "Ruby",
    "rs": "Rust",
    "swift": "Swift",
    "ts": "TypeScript",
    "sh": "AWS-CLI",
    "cmd": "AWS-CLI",
    "json": "JSON",
    "yml": "YAML",
    "yaml": "YAML",
    "md": "Markdown",
}

# If you get a lot of false-flagged 40-character errors
# in specific folders or files, you can omit them from
# these scans by adding them to the following lists.
# However, because this script is mostly run as a GitHub
# action in a clean environment (aside from testing),
# exhaustive ignore lists shouldn't be necessary.

# Files to skip.
IGNORE_FILES = {
    ".moviedata.json",
    ".travis.yml",
    "AssemblyInfo.cs",
    "moviedata.json",
    "movies.json",
    "package-lock.json",
}

# Sample files.
EXPECTED_SAMPLE_FILES = {
    "README.md",
    "chat_sfn_state_machine.json",
    "market_2.jpg",
    "movies.json",
    "sample_cert.pem",
    "sample_private_key.pem",
    "sample_saml_metadata.xml",
    "speech_sample.mp3",
    "spheres_2.jpg",
}

# Media file types.
MEDIA_FILE_TYPES = {"mp3", "wav"}


def match_path_to_specs(path: Path, specs: list[GitIgnoreSpec]) -> bool:
    """
    Return True if we should skip this path, that is, it is matched by a .gitignore.
    """
    for spec in specs:
        if spec.match_file(path):
            return True
    return False


def walk_with_gitignore(
    root: Path, specs: list[GitIgnoreSpec] = []
) -> Generator[Path, None, None]:
    """
    Starting from a root directory, walk the file system yielding a path for each file.
    However, it also reads `.gitignore` files, so that it behaves like `git ls-files`.
    It does not actively use `git ls-files` because it wouldn't catch new files without
    fiddling with a number of flags.
    """
    gitignore = root / ".gitignore"
    if gitignore.exists():
        with open(root / ".gitignore", "r", encoding="utf-8") as gitignore:
            specs = [*specs, GitIgnoreSpec.from_lines(gitignore.readlines())]
    for entry in os.scandir(root):
        if not match_path_to_specs(entry.path, specs):
            path = Path(entry.path)
            if entry.is_dir():
                yield from walk_with_gitignore(path, specs)
            else:
                yield path


def get_files(root: Path) -> Generator[Path, None, None]:
    """
    Yield non-skipped files, that is, anything not matching git ls-files and not
    in the "to skip" files that are in git but are machine generated, so we don't
    want to validate them.
    """
    for path in walk_with_gitignore(root):
        filename = path.parts[-1]
        ext = os.path.splitext(filename)[1].lstrip(".")
        if ext.lower() in EXT_LOOKUP and filename not in IGNORE_FILES:
            yield path


def check_files(root: Path):
    """
    Walk a folder system, scanning all files with specified extensions.
    Errors are logged and counted and the count of errors is returned.

    :param root: The root folder to start the walk.
    :return: The number of errors found in the scanned files.
    """
    file_count = 0
    error_count = 0
    for file_path in get_files(root):
        file_count += 1
        logger.info("\nChecking File: %s", file_path)

        with open(file_path, encoding="utf-8") as f:
            file_contents = f.read()

        error_count += verify_no_deny_list_words(file_contents, file_path)
        error_count += verify_no_secret_keys(file_contents, file_path)
        error_count += verify_no_secret_keys(file_contents, file_path)
        error_count += verify_snippet_start_end(file_contents, file_path)

    print(f"{file_count} files scanned in {root}.\n")
    return error_count


def word_parts(contents: str):
    for word in contents.split():
        # split on / for URLs, to find invalid Host names
        for part in word.lower().split("/"):
            part = re.sub(r"^[.:]", "", re.sub(r"[.:]$", "", part))
            yield word, part


def verify_no_deny_list_words(file_contents: str, file_location: Path):
    """Verify no words in the file are in the list of denied words."""
    error_count = 0
    for word, part in word_parts(file_contents):
        if part in DENY_LIST:
            logger.error("Word '%s' in %s is not allowed.", word, file_location)
            error_count += 1
    return error_count


def verify_sample_files(root_path: Path) -> int:
    """Verify sample files meet the requirements and have not moved."""
    sample_files_folder = os.path.join(root_path, "resources/sample_files")
    media_folder = ".sample_media"
    ONE_MB_AS_BYTES = 1000000
    MAX_FILE_SIZE_MB = 10
    error_count = 0
    file_list = []
    for path, dirs, files in os.walk(sample_files_folder, topdown=True):
        for file_name in files:
            file_list.append(file_name)
            file_path = os.path.join(path, file_name)
            ext = os.path.splitext(file_name)[1].lstrip(".")
            if file_name not in EXPECTED_SAMPLE_FILES:
                logger.error(
                    "File '%s' in %s was not found in the list of expected sample files. If this is a new sample file, add it to the EXPECTED_SAMPLE_FILES list in pre_validate.py.",
                    file_name,
                    sample_files_folder,
                )
                error_count += 1
            if ext.lower() in MEDIA_FILE_TYPES:
                if media_folder not in file_path:
                    logger.error(
                        "File '%s' in %s must be in the %s directory.",
                        file_name,
                        sample_files_folder,
                        media_folder,
                    )
                    error_count += 1
            if (os.path.getsize(file_path) / ONE_MB_AS_BYTES) > MAX_FILE_SIZE_MB:
                logger.error(
                    "File '%s' in %s is larger than the allowed size for a sample file.",
                    file_name,
                    sample_files_folder,
                )
                error_count += 1

    for sample_file in EXPECTED_SAMPLE_FILES:
        if sample_file not in file_list:
            logger.error(
                "Expected sample file '%s' was not found in '%s'. If this file was intentionally removed, remove it from the EXPECTED_SAMPLE_FILES list in pre_validate.py.",
                sample_file,
                sample_files_folder,
            )
            error_count += 1

    return error_count


def verify_no_secret_keys(file_contents: str, file_location: Path) -> int:
    """Verify the file does not contain 20- or 40- length character strings,
    which might be secret keys. Allow strings in the allowlist in
    https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/.github/pre_validate/pre_validate.py.
    """
    error_count = 0
    twenties = re.findall(
        "[^A-Z0-9][A][ACGIKNPRS][A-Z]{2}[A-Z0-9]{16}[^A-Z0-9]", file_contents
    )
    for word in twenties:
        if word[1:-1] in ALLOW_LIST:
            continue
        logger.error(
            "20 character string '%s' found in %s and might be a secret "
            "access key. If not, add it to the allow list in https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/.github/pre_validate/pre_validate.py.",
            {word[1:-1]},
            file_location,
        )
        error_count += 1

    forties = re.findall(
        "[^a-zA-Z0-9/+=][a-zA-Z0-9/+=]{40}[^a-zA-Z0-9/+=]", file_contents
    )
    for word in forties:
        if word[1:-1] in ALLOW_LIST:
            continue
        logger.error(
            "40 character string '%s' found in %s and might be a secret "
            "access key. If not, add it to the allow list in https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/.github/pre_validate/pre_validate.py",
            {word[1:-1]},
            file_location,
        )
        error_count += 1

    return error_count


def verify_snippet_start_end(file_contents: str, file_location: Path) -> int:
    """Scan the file contents for snippet-start and snippet-end tags and verify
    that they are in matched pairs. Log errors and return the count of errors."""
    error_count = 0
    snippet_start = "snippet" + "-start:["
    snippet_end = "snippet" + "-end:["
    snippet_tags = set()
    for word in file_contents.split():
        if snippet_start in word:
            tag = word.split("[")[1]
            if tag in snippet_tags:
                logger.error("Duplicate tag %s found in %s.", tag[:-1], file_location)
                error_count += 1
            else:
                snippet_tags.add(tag)
        elif snippet_end in word:
            tag = word.split("[")[1]
            if tag in snippet_tags:
                snippet_tags.remove(tag)
            else:
                logger.error(
                    "End tag %s with no matching start tag " "found in %s.",
                    tag[:-1],
                    file_location,
                )
                error_count += 1

    for tag in snippet_tags:
        logger.error(
            "Start tag %s with no matching end tag found in %s.",
            tag[:-1],
            file_location,
        )
        error_count += 1

    return error_count


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
    error_count = check_files(root_path, args.quiet)
    error_count += verify_sample_files(root_path)
    if error_count > 0:
        print(f"{error_count} errors found, please fix them.")
    else:
        print("All checks passed, you are cleared to check in.")
    # Travis CI reports an error if the script exits with a non-zero code.
    sys.exit(error_count)


if __name__ == "__main__":
    main()
