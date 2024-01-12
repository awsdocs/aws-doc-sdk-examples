#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from dataclasses import dataclass
from pathlib import Path
import re
from sys import argv

from metadata_errors import MetadataError, MetadataErrors
import validator_config


@dataclass
class InvalidSPDX(MetadataError):
    has_copyright: bool = True
    has_license: bool = True
    has_bom: bool = False

    def message(self):
        message = "Invalid SPDX"
        if not self.has_copyright:
            message += " Missing Copyright line"
        if not self.has_license:
            message += " Missing License line"
        if self.has_bom:
            message += " Has BOM"
        return message


@dataclass
class MissingSPDX(MetadataError):
    def message(self):
        return "Missing SPDX"


def verify_spdx(file_contents: str, file_location: Path, errors: MetadataErrors):
    """Verify the file starts with an SPDX comment, possibly following a shebang line"""
    if file_location.suffix in validator_config.IGNORE_SPDX_SUFFIXES:
        return
    has_bom = file_contents.startswith("\uFEFF")
    lines = file_contents.splitlines()
    if len(lines) < 2:
        return
    if (
        lines[0].startswith("#!")
        or lines[0].startswith("// swift-tools-version:")
        or lines[0] == "<?php"
    ):
        lines = lines[1:]
    if len(lines) < 2:
        return
    # First line may be a start of comment
    has_copyright = (
        False if re.match(validator_config.SPDX_COPYRIGHT, lines[0]) is None else True
    )
    has_license = (
        False if re.match(validator_config.SPDX_LICENSE, lines[1]) is None else True
    )
    if not (has_copyright and has_license) or has_bom:
        file_has_copyright = (
            False
            if re.match(validator_config.SPDX_COPYRIGHT, file_contents) is None
            else True
        )
        file_has_license = (
            False
            if re.match(validator_config.SPDX_LICENSE, file_contents) is None
            else True
        )
        if file_has_copyright or file_has_license or has_bom:
            errors.append(
                InvalidSPDX(
                    file=file_location,
                    has_copyright=has_copyright,
                    has_license=has_license,
                    has_bom=has_bom,
                )
            )
        else:
            errors.append(
                MissingSPDX(
                    file=file_location,
                )
            )


def main():
    for p in argv[1:]:
        p = Path(p)
        with open(p, encoding="utf8") as f:
            contents = f.readlines()
        prefix = "#" if p.suffix == ".py" or p.suffix == ".sh" else "//"
        offset = (
            1
            if contents[0].startswith("#!")
            or contents[0].startswith("<?php")
            or contents[0].startswith("// swift-tools-version")
            else 0
        )
        contents.insert(
            offset,
            prefix
            + " Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.\n",
        )
        contents.insert(offset + 1, prefix + " SPDX-License-Identifier: Apache-2.0\n")
        with open(p, "wt", encoding="utf-8-sig") as f:
            f.writelines(contents)


if __name__ == "__main__":
    main()
