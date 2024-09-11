#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import logging
import os
import sys
from difflib import unified_diff
from pathlib import Path
from typing import Optional

import config

# flake8: noqa: F401 for side effects
import update
from render import MissingMetadataError, Renderer, RenderStatus
from scanner import Scanner

logging.basicConfig(level=os.environ.get("LOGLEVEL", "INFO").upper(), force=True)


from aws_doc_sdk_examples_tools.doc_gen import DocGen


def prepare_scanner(doc_gen: DocGen) -> Optional[Scanner]:
    for path in (doc_gen.root / ".doc_gen/metadata").glob("*_metadata.yaml"):
        doc_gen.process_metadata(path)
    doc_gen.collect_snippets()
    doc_gen.validate()
    if doc_gen.errors:
        error_strings = [str(error) for error in doc_gen.errors]
        failed_list = "\n".join(f"DocGen Error: {e}" for e in error_strings)
        print(f"Metadata errors encountered:\n\t{failed_list}")
        return None
    scanner = Scanner(doc_gen)

    # Preload cross-content examples
    scanner.load_crosses()

    return scanner


def main():
    # Load all examples immediately for cross references. Trades correctness for speed
    doc_gen = DocGen.from_root(Path(__file__).parent.parent.parent, incremental=True)

    languages = doc_gen.languages()
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--languages",
        choices=[*languages] + ["all"],
        nargs="+",
        help="The languages of the SDK. Choose from: %(choices)s.",
        default=["all"],
    )

    parser.add_argument(
        "--services",
        choices=[*doc_gen.services.keys()] + ["all"],
        nargs="+",
        help="The targeted service. Choose from: %(choices)s.",
        default=["all"],
    )
    parser.add_argument(
        "--safe",
        action="store_true",
        help=f"Save a copy of the original README as the 'saved_readme' value specified in config.py ({config.saved_readme}).",
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="When set, output verbose debugging info.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        dest="dry_run",
        help="In dry run, compare current vs generated and exit with failure if they do not match.",
        default=False,  # Change this to default false when we're ready to use this generally.
    )
    parser.add_argument("--no-dry-run", dest="dry_run", action="store_false")
    parser.add_argument("--check", dest="dry_run", action="store_true")
    parser.add_argument("--diff", action="store_true", default=False)
    args = parser.parse_args()

    if "all" in args.languages:
        args.languages = [*languages]

    if "all" in args.services:
        args.services = [*doc_gen.services.keys()]

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    logging.debug(f"Args configuration: {args}")

    if args.dry_run:
        print("Dry run, no changes will be made.")

    skipped = []
    failed = []
    written = []
    non_writeme = []
    unchanged = []
    no_folder = []

    scanner = prepare_scanner(doc_gen)
    if scanner is None:
        return -1

    renderer = Renderer(scanner)
    for service in args.services:
        for language_and_version in args.languages:
            (language, version) = language_and_version.split(":")
            id = f"{language}:{version}:{service}"
            try:
                renderer.set_example(service, language, int(version), args.safe)

                logging.debug("Rendering %s", id)
                render_status = renderer.render()
                logging.debug("Status %s", render_status)

                if render_status == RenderStatus.UPDATED:
                    if args.dry_run:
                        failed.append(id)
                        if args.diff:
                            print_diff(renderer, id)
                    else:
                        renderer.write()
                        written.append(id)
                elif render_status == RenderStatus.UNCHANGED:
                    unchanged.append(id)
                elif render_status == RenderStatus.UNMANAGED:
                    non_writeme.append(id)
                elif render_status == RenderStatus.NO_EXAMPLES:
                    skipped.append(id)
                elif render_status == RenderStatus.NO_FOLDER:
                    no_folder.append(id)
                elif render_status == RenderStatus.UNIMPLEMENTED:
                    pass
            except FileNotFoundError as fnfe:
                logging.debug(fnfe, exc_info=True)
                skipped.append(id)
            except MissingMetadataError as mme:
                logging.debug(mme, exc_info=True)
                failed.append(id)
            except Exception as e:
                logging.error(e, exc_info=True)
                failed.append(id)

    skip_list = "\n".join(f"Skipped {f}" for f in sorted(skipped))
    logging.debug(skip_list or "(None Skipped)")
    if unchanged:
        unchanged_list = "\n".join(f"Unchanged {f}" for f in sorted(unchanged))
        print(unchanged_list)
    if non_writeme:
        non_writeme_list = "\n".join(f"Non-WRITEME: {f}" for f in sorted(non_writeme))
        print(non_writeme_list)
    if no_folder:
        no_folder_list = "\n".join(f"No folder: {f}" for f in sorted(no_folder))
        print(no_folder_list)
    if not args.dry_run:
        done_list = "\n".join(f"Wrote {f}" for f in sorted(written))
        print(done_list or "(None Written)")
    if failed:
        failed_list = "\n".join(f"Incorrect: {f}" for f in sorted(failed))
        print(failed_list)
        print("Rerun writeme.py to update README links and sections.")
    print("WRITEME Run completed.")
    return len(failed)


def print_diff(renderer, id):
    current = renderer.read_current().split("\n")
    expected = renderer.readme_text.split("\n")
    diff = unified_diff(current, expected, f"{id}/current", f"{id}/expected")
    print("\n".join(diff))


if __name__ == "__main__":
    sys.exit(main())
