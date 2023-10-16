#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import config
import logging
from scanner import Scanner
from render import Renderer


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
        help="This tool is in development. You must pass --dry-run=false to have it run.",
        default=True,  # Change this to default false when we're ready to use this generally.
    )
    parser.add_argument("--no-dry-run", dest="dry_run", action="store_false")
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

    for language_and_version in args.languages:
        (language, version) = language_and_version.split(":")
        if int(version) not in sdks[language]["sdk"]:
            logging.debug(f"Skipping {language}:{version}")
        else:
            for service in args.services:
                try:
                    scanner.set_example(language, service)
                    logging.debug(f"Rendering {language}:{version}:{service}")
                    if not args.dry_run:
                        Renderer(scanner, int(version), args.safe).render()
                except Exception as err:
                    skip = f"{language}:{version}:{service}"
                    logging.error(
                        f"Exception rendering {skip} - {err}",
                    )
                    skipped.append(skip)

    skip_list = "\n\t".join(skipped)
    logging.info(f"Run complete. Skipped: {skip_list}")


if __name__ == "__main__":
    main()
