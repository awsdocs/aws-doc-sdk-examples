#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import config
import logging
import os
import sys
from pathlib import Path
from render import Renderer
from scanner import Scanner


def main():
    scanner = Scanner(".doc_gen/metadata")
    sdks = scanner.sdks()
    lang_vers = []
    for sdk in sdks:
        for v in sdks[sdk]["sdk"]:
            lang_vers.append(f"{sdk}:{v}")

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--languages",
        choices=lang_vers + ["all"],
        nargs="+",
        help="The languages of the SDK. Choose from: %(choices)s.",
        default=["all"],
    )
    parser.add_argument(
        "--services",
        choices={**scanner.services(), "all": {}},
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
        args.languages = lang_vers

    if "all" in args.services:
        args.services = [*scanner.services().keys()]

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    logging.debug(f"Args configuration: {args}")

    if args.dry_run:
        logging.info("Dry run, no changes will be made.")

    skipped = []
    failed = []

    for language_and_version in args.languages:
        (language, version) = language_and_version.split(":")
        if int(version) not in sdks[language]["sdk"]:
            logging.debug(f"Skipping {language}:{version}")
        else:
            for service in args.services:
                try:
                    scanner.set_example(language, service)
                    logging.debug(f"Rendering {language}:{version}:{service}")
                    renderer = Renderer(scanner, int(version), args.safe)

                    result = renderer.render()
                    if result is None:
                        continue
                    if args.dry_run:
                        if not renderer.check():
                            failed.append(f"{language}:{version}:{service}")
                    else:
                        renderer.write()
                except FileNotFoundError:
                    skip = f"{language}:{version}:{service}"
                    skipped.append(skip)
                except Exception:
                    skip = f"{language}:{version}:{service}"
                    logging.exception(
                        f"Exception rendering {skip}",
                    )

    skip_list = "\n\t".join(skipped)
    logging.info(f"Run complete. Skipped: {skip_list}")
    if len(failed) > 0:
        failed_list = "\n\t".join(failed)
        logging.warning(f"READMEs with incorrect formatting:\n\t{failed_list}")
    return len(failed)


if __name__ == "__main__":
    sys.exit(main())
