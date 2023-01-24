# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
from scanner import Scanner
from render import Renderer


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('language', help="The language of the SDK. Must match a top-level field in sdks.yaml.")
    parser.add_argument('sdk_version', help="The major version of the SDK. Must match a version listed in sdks.yaml.")
    parser.add_argument('service', help="The targeted service. Must match a top-level field in services.yaml.")
    parser.add_argument('--svc_folder', help="Overrides the folder template to specify the service example folder.")
    parser.add_argument('--safe', action='store_true', help="Save a copy of the original README as README.old.md.")
    args = parser.parse_args()

    try:
        scanner = Scanner('.doc_gen/metadata', args.language, args.service)
        renderer = Renderer(scanner, args.sdk_version, args.safe, svc_folder=args.svc_folder)
        renderer.render()
    except Exception as err:
        print("*** Something went wrong! ***")
        raise err


if __name__ == '__main__':
    main()
