# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
from argparse import ArgumentParser
from pathlib import Path
import logging

from .doc_gen import DocGen, DocGenEncoder

logging.basicConfig(level=logging.INFO)


def main():
    parser = ArgumentParser(description="Parse examples from example metadata.")
    parser.add_argument(
        "--from-root",
        action="extend",
        nargs="+",
        required=True,
        type=str,
        help="Generate from a path. Expects a path to a directory with a .doc_gen sub-directory.",
    )
    parser.add_argument(
        "--write-json",
        default="doc_gen.json",
        type=str,
        help="Output a JSON version of the computed DocGen with some properties stripped out. Includes any errors.",
    )
    parser.add_argument(
        "--write-snippets",
        default="doc_gen_snippets.json",
        type=str,
        help="Output a JSON version of the computed DocGen with only snippets and snippet files. Separates snippet content from metadata content.",
    )

    parser.add_argument(
        "--strict",
        action="store_true",
        help="Exit with non-zero code if errors are present. By default errors are written to the output.",
    )

    parser.add_argument(
        "--skip-entity-expansion",
        action="store_true",
        help="Do not expand entities. Entities are expanded by default.",
    )

    args = parser.parse_args()

    merged_doc_gen = DocGen.empty()
    for root in args.from_root:
        unmerged_doc_gen = DocGen.from_root(Path(root))
        merged_doc_gen.merge(unmerged_doc_gen)

    merged_doc_gen.validate()
    merged_doc_gen.fill_missing_fields()

    if not args.skip_entity_expansion:
        # Replace entities
        merged_doc_gen.expand_entity_fields(merged_doc_gen)

    if args.strict and merged_doc_gen.errors:
        logging.error("Errors found in metadata: %s", merged_doc_gen.errors)
        exit(1)

    serialized = json.dumps(merged_doc_gen, cls=DocGenEncoder)

    with open(args.write_json, "w") as out:
        out.write(serialized)

    if args.write_snippets:
        for root in args.from_root:
            merged_doc_gen.collect_snippets(Path(root))

        serialized_snippets = json.dumps(
            {
                "snippets": merged_doc_gen.snippets,
                "snippet_files": merged_doc_gen.snippet_files,
            },
            cls=DocGenEncoder,
        )
        with open(args.write_snippets, "w") as out:
            out.write(serialized_snippets)


if __name__ == "__main__":
    main()
