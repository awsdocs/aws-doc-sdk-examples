#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from pathlib import Path
from sys import argv


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
