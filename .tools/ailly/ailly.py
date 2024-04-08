# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import subprocess
import shutil
import sys

from argparse import ArgumentParser
from pathlib import Path

# AILLY = "@ailly/cli@1.0.1"
AILLY = "@ailly/cli"

# Throughout this wrapper, `subprocess.run(shell=True)` is used for Windows
# support in running node, npm, and npx. While this script is already run in a
# shell, so shell escaping already rather involves being on the other side of
# the airtight hatchway, care is taken to only write strings defined in this file
# itself into the subprocess. If a security auditor just ran a SASL and thinks
# this is a thing that's a problem, please bring a proof of exploit.


def ensure_npx():
    """Ensure `npx` is available in this system."""
    subprocess.run(
        ["node", "--version"], shell=True, capture_output=True
    ).check_returncode()
    subprocess.run(
        ["npm", "install", AILLY], shell=True, capture_output=True
    ).check_returncode()
    subprocess.run(
        ["npx", AILLY, "--help"], shell=True, capture_output=True
    ).check_returncode()
    subprocess.run(["npx", AILLY, "--version"], shell=True).check_returncode()


def prepare_scouts(example: Path):
    scouts = example / ".scouts"
    try:
        shutil.rmtree(scouts)
    except Exception:
        pass
    scouts.mkdir()


# Find <language>/<service>/ as <example>
# Create <example>/.scouts/[...languages]
# For each output [target language], execute
#   npx ailly \
#       --engine bedrock \
#       --plugin file://${PWD}/plugin.mjs \
#       --root ../../<example> \
#       --out ../../<example>/.scouts/[target language] \
#       --prompt "Translate to [target language]."
def run_ailly(source: str, example: Path, target: str):
    base = Path(__file__).parent
    engine = ["--engine", "bedrock"]
    plugin = ["--plugin", (base / "plugin.mjs").as_uri()]
    root = ["--root", example]
    out = ["--out", example / ".scouts" / target]
    prompt = [
        "--prompt",
        f"Translate the final block of code from {source} to {target} programming language.",
    ]
    logging.info("Converting %s to %s", example, target)
    args = ["npx", AILLY, *engine, *plugin, *root, *out, *prompt, "--isolated", "."]
    print("cd '" + str(base) + "' ; " + " ".join(f"'{str(arg)}'" for arg in args))
    subprocess.run(args, cwd=base, shell=True)


def main():
    parser = ArgumentParser()
    parser.add_argument(
        "--language",
        choices=["rustv1", "javascriptv3", "gov2"],
        default="javascriptv3",
        help="The languages of the SDK. Choose from: %(choices)s.",
    )
    parser.add_argument(
        "--service",
        choices=["cloudwatch-logs", "dynamodb", "s3"],  # scanner.services(),
        default="s3",
        help="The targeted service. Choose from: %(choices)s.",
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="When set, output verbose debugging info.",
    )
    parser.add_argument(
        "--npx-check",
        default=False,  # TODO True
        help=f"Verify `npx` is available in the Python environment's shell path and ensure ailly is installed at {AILLY}",
    )
    parser.add_argument(
        "--clean",
        default=False,
        help="Clean the .scouts target folder before generating",
    )
    parser.add_argument("--skip-npx-check", action="store_false", dest="npx_check")
    args = parser.parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    if args.npx_check:
        ensure_npx()

    # targets = ["rust", "python"]
    targets = ["kotlin"]

    example = (
        Path(__file__).parent.parent.parent
        / args.language
        / "example_code"
        / args.service
    )
    if args.clean:
        prepare_scouts(example)

    for target in targets:
        run_ailly(args.language, example, target)


if __name__ == "__main__":
    sys.exit(main())
