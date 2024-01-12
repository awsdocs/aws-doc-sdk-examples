# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import yaml
from pathlib import Path
from sys import exit
from metadata import parse as parse_metadata
from metadata_errors import MetadataErrors
from metadata_validator import validate_metadata
from project_validator import check_files, verify_sample_files
from doc_gen import DocGen


def validate_zexii(metadata_path: Path, errors: MetadataErrors) -> None:
    doc_gen = errors.maybe_extend(DocGen.from_root(metadata_path))
    if doc_gen is None:
        return

    for path in metadata_path.glob("*_metadata.yaml"):
        if path.name == "cross_metadata.yaml":
            continue
        with open(path, encoding="utf-8") as file:
            meta = yaml.safe_load(file)
        errors.maybe_extend(parse_metadata(path.name, meta, doc_gen))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--root",
        default=f"{Path(__file__).parent / '..' / '..'}",
        help="The root path from which to search for files to check. The default is the root of the git repo (two up from this file).",
    )
    parser.add_argument(
        "--doc-gen",
        default=f"{Path(__file__).parent / '..' / '..' / '.doc_gen'}",
        help="The folder that contains schema and metadata files. The default is .doc_gen in the root of this repo.",
        required=False,
    )
    parser.add_argument(
        "--check-spdx",
        default=False,
        help="Verify all files start with SPDX header",
        required=False,
    )
    args = parser.parse_args()
    root_path = Path(args.root).resolve()
    doc_gen = Path(args.doc_gen).resolve()

    errors = MetadataErrors()

    check_files(root_path, errors, args.check_spdx)
    verify_sample_files(root_path, errors)
    validate_metadata(doc_gen, errors)
    validate_zexii(doc_gen / "metadata", errors)

    error_count = len(errors)
    if error_count > 0:
        for error in errors:
            print(str(error))
        print(f"{error_count} errors found, please fix them.")
    else:
        print("All checks passed, you are cleared to check in.")

    return error_count


if __name__ == "__main__":
    exit(main())
