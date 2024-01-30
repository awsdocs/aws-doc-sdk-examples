# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
from pathlib import Path
from sys import exit

from doc_gen import DocGen


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--root",
        default=f"{Path(__file__).parent.parent.parent}",
        help="The root path from which to search for files to check. The default is the root of the git repo (two up from this file).",
    )
    parser.add_argument(
        "--doc-gen",
        default=f"{Path(__file__).parent.parent.parent / '.doc_gen'}",
        help="The folder that contains schema and metadata files. The default is .doc_gen in the root of this repo.",
        required=False,
    )
    parser.add_argument(
        "--check-spdx",
        default=True,
        help="Verify all files start with SPDX header",
        required=False,
    )
    args = parser.parse_args()
    root_path = Path(args.root).resolve()

    doc_gen = DocGen.from_root(root=root_path)
    doc_gen.collect_snippets(snippets_root=root_path)
    doc_gen.validate(args.check_spdx)

    error_count = len(doc_gen.errors)
    if error_count > 0:
        print(f"{doc_gen.errors}")
        print(f"{error_count} errors found, please fix them.")
    else:
        print("All checks passed, you are cleared to check in.")

    return error_count


if __name__ == "__main__":
    exit(main())
