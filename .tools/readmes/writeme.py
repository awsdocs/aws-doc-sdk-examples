#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# flake8: noqa: F401 for side effects
import update

import argparse
import config
import logging
import os
import sys
from pathlib import Path
from typing import Optional

from render import Renderer, MissingMetadataError
from scanner import Scanner

logging.basicConfig(level=os.environ.get("LOGLEVEL", "INFO").upper())


from aws_doc_sdk_examples_tools.doc_gen import DocGen


def prepare_scanner(doc_gen: DocGen) -> Optional[Scanner]:
    for path in (doc_gen.root / ".doc_gen/metadata").glob("*_metadata.yaml"):
        doc_gen.process_metadata(path)
    doc_gen.collect_snippets()
    doc_gen.validate()
    if doc_gen.errors:
        logging.error("There were errors loading metadata")
        logging.info(doc_gen.errors)
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
    args = parser.parse_args()

    if "all" in args.languages:
        args.languages = [*languages]

    if "all" in args.services:
        args.services = [*doc_gen.services.keys()]

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    logging.debug(f"Args configuration: {args}")

    if args.dry_run:
        logging.info("Dry run, no changes will be made.")

    skipped = []
    failed = []
    written = []
    non_writeme = []
    unchanged = []

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
                if renderer.lang_config is None:
                    continue

                logging.debug("Rendering %s", id)
                result, updated = renderer.render()

                if result is None:
                    if (
                        renderer.lang_config["service_folder"]
                        not in renderer.lang_config.get(
                            "service_folder_overrides", {}
                        ).values()
                        and renderer.readme_filename.exists()
                    ):
                        non_writeme.append(id)
                    else:
                        skipped.append(id)
                elif args.dry_run:
                    if not renderer.check():
                        failed.append(id)
                elif not updated:
                    unchanged.append(id)
                else:
                    renderer.write()
                    written.append(id)
            except FileNotFoundError:
                skipped.append(id)
            except MissingMetadataError as mme:
                logging.error(mme)
                failed.append(id)
            except Exception:
                logging.exception("Exception rendering %s", id)
                failed.append(id)

    done_list = "\n\t".join(sorted(written))
    skip_list = "\n\t".join(sorted(skipped))
    non_writeme_list = "\n\t".join(sorted(non_writeme))
    unchanged_list = "\n\t".join(sorted(unchanged))
    logging.debug(f"Skipped:\n\t{skip_list}")
    logging.info(f"Wrote:\n\t{done_list}\nUnchanged:\n\t{unchanged_list}")
    logging.warning(f"Non-WRITEME READMES:\n\t{non_writeme_list}")
    if len(failed) > 0:
        failed_list = "\n\t".join(failed)
        logging.error(f"READMEs with incorrect formatting:\n\t{failed_list}")
        logging.error("Rerun writeme.py to update README links and sections.")
    logging.info("Run complete.")
    return len(failed)


if __name__ == "__main__":
    sys.exit(main())
