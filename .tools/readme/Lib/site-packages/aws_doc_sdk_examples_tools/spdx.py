#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from dataclasses import dataclass
from pathlib import Path
from typing import List
import re
from sys import argv

from .metadata_errors import MetadataError, MetadataErrors
from aws_doc_sdk_examples_tools import validator_config


@dataclass
class InvalidSPDX(MetadataError):
    has_copyright: bool = True
    has_license: bool = True

    def message(self):
        message = "Invalid SPDX"
        if self.has_copyright:
            message += " Invalid Copyright line"
        if self.has_license:
            message += " Invalid License line"
        return message


@dataclass
class MissingSPDX(MetadataError):
    def message(self):
        return "Missing SPDX"


RE_COPYRIGHT = validator_config.SPDX_LEADER + validator_config.SPDX_COPYRIGHT
RE_LICENSE = validator_config.SPDX_LEADER + validator_config.SPDX_LICENSE


def skip_first_line(lines: List[str]) -> bool:
    return (
        lines[0].startswith("#!")
        or lines[0].startswith("// swift-tools-version:")
        or lines[0] == "<?php"
    )


def verify_spdx(file_contents: str, file_location: Path, errors: MetadataErrors):
    """Verify the file starts with an SPDX comment, possibly following a shebang line"""
    if file_location.suffix in validator_config.IGNORE_SPDX_SUFFIXES:
        return
    lines = file_contents.splitlines()
    if len(lines) == 0:
        return
    if skip_first_line(lines):
        lines = lines[1:]
    if len(lines) < 2:
        return
    # First line may be a start of comment
    has_copyright = re.match(RE_COPYRIGHT, lines[0]) is not None
    has_license = re.match(RE_LICENSE, lines[1]) is not None
    if not (has_copyright and has_license):
        file_has_copyright = (
            re.match(validator_config.SPDX_COPYRIGHT, file_contents) is not None
        )
        file_has_license = (
            re.match(validator_config.SPDX_LICENSE, file_contents) is not None
        )
        if file_has_copyright or file_has_license:
            errors.append(
                InvalidSPDX(
                    file=file_location,
                    has_copyright=has_copyright,
                    has_license=has_license,
                )
            )
        else:
            errors.append(
                MissingSPDX(
                    file=file_location,
                )
            )


def insert_spdx(path: Path):
    with open(path, encoding="utf8") as f:
        contents = f.readlines()
    prefix = "//"
    if path.suffix in [".py", ".sh", ".rb"]:
        prefix = "#"
    if path.suffix in [".abap"]:
        prefix = '"'
    offset = 1 if skip_first_line(contents) else 0
    contents.insert(
        offset,
        prefix
        + " Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n",
    )
    contents.insert(offset + 1, prefix + " SPDX-License-Identifier: Apache-2.0\n")
    with open(path, "wt", encoding="utf-8-sig") as f:
        f.writelines(contents)


def main():
    for p in argv[1:]:
        p = Path(p)
        insert_spdx(p)


if __name__ == "__main__":
    main()
