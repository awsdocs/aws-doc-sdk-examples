# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
from scanner import Scanner
from render import Renderer


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('language')
    parser.add_argument('sdk_version')
    parser.add_argument('service')
    parser.add_argument('--svc_folder', help="The folder where the service examples are stored.")
    parser.add_argument('--readme', help="The name to give the README. The default is README.md.", default='README.md')
    args = parser.parse_args()

    try:
        scanner = Scanner('.doc_gen/metadata', args.language, args.service)
        renderer = Renderer(scanner, args.sdk_version, args.readme, svc_folder=args.svc_folder)
        renderer.render()
    except Exception as err:
        print("*** Something went wrong! ***")
        raise err


if __name__ == '__main__':
    main()
