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
        vers = ", ".join([str(v) for v in sdks[sdk]["sdk"]])
        lang_vers.append(f"{sdk}: {vers}")

    parser = argparse.ArgumentParser()
    parser.add_argument(
        "language",
        metavar="sdk_language",
        choices=scanner.sdks(),
        help="The language of the SDK. Choose from: %(choices)s.",
    )
    parser.add_argument(
        "sdk_version",
        help=f"The major version of the SDK. Must match a version of the specified SDK: {', '.join(lang_vers)}",
    )
    parser.add_argument(
        "service",
        metavar="service",
        choices=scanner.services(),
        help=f"The targeted service. Choose from: %(choices)s.",
    )
    parser.add_argument(
        "--svc_folder",
        help="Overrides the folder template to specify the service example folder.",
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
    args = parser.parse_args()

    if int(args.sdk_version) not in sdks[args.language]["sdk"]:
        parser.print_usage()
        print(
            f"writeme.py: error: argument sdk_verion: invalid choice for "
            f"{args.language}: {args.sdk_version} (for {args.language}, choose from "
            f"{', '.join([str(v) for v in sdks[args.language]['sdk']])})"
        )
        return

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    try:
        scanner.lang_name = args.language
        scanner.svc_name = args.service
        renderer = Renderer(
            scanner, args.sdk_version, args.safe, svc_folder=args.svc_folder
        )
        renderer.render()
    except Exception as err:
        print("*** Something went wrong! ***")
        raise err


if __name__ == "__main__":
    main()
