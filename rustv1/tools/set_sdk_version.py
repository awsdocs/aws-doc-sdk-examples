#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

try:
    from tomlkit import dumps, parse, TOMLDocument
except:
    print("Couldn't import tomlkit, either install it directly or instantiate a venv.")
    exit(1)

from argparse import ArgumentParser
from glob import glob
import logging

SDK_ORIGIN = "https://github.com/awslabs/aws-sdk-rust"
SDK_VERSION = "1.2.0"
SDK_CRATE_PREFIXES = ["aws-sdk-", "aws-config-"]
SMITHY_VERSION = "1.0.1"
SMITHY_CRATE_PREFIXES = ["aws-smithy-", "aws-config", "aws-credential-types"]
SMITHY_CRATE_OVERRIDES = {
    "aws-smithy-http": "0.60.0",
    "aws-smithy-types-convert": "0.60.0",
    "aws-smithy-client": "0.60.0",
}


def list_cargos(pathname: str):
    for file in glob(f"{pathname}/**/Cargo.toml", recursive=True):
        yield file


def read_cargo(pathname: str) -> TOMLDocument:
    with open(pathname, "+rt") as cargo:
        return parse(cargo.read())


def write_cargo(pathname: str, cargo: TOMLDocument) -> None:
    with open(pathname, "+wt") as file:
        file.write(dumps(cargo))


def update_dependency(
    dependencies: dict, name: str, version: str, use_branch: bool
) -> None:
    print("update", name, dependencies[name])

    if use_branch:
        dependencies[name].git = SDK_ORIGIN
        dependencies[name].branch = version
    else:
        if dependencies[name].get("features") is not None:
            print("\tbefore:", dependencies[name])
            dependencies[name].remove("git")
            dependencies[name].remove("branch")
            dependencies[name].add("version", version)
            print("\tafter:", dependencies[name])
        else:
            dependencies[name] = version


def update_sdk_dependencies(dependencies, branch: str) -> None:
    use_git = branch == "main" or branch == "next"
    crates = [crate for crate in dependencies]
    for name in crates:
        if isinstance(name, str):
            for prefix in SMITHY_CRATE_PREFIXES:
                if name.startswith(prefix):
                    if use_git:
                        update_dependency(dependencies, name, branch, True)
                    else:
                        if name in SMITHY_CRATE_OVERRIDES:
                            update_dependency(
                                dependencies, name, SMITHY_CRATE_OVERRIDES[name], False
                            )
                        else:
                            update_dependency(dependencies, name, SMITHY_VERSION, False)
            for prefix in SDK_CRATE_PREFIXES:
                if name.startswith(prefix):
                    if use_git:
                        update_dependency(dependencies, name, branch, True)
                    else:
                        update_dependency(dependencies, name, SDK_VERSION, False)


def update_sdk(cargo: TOMLDocument, branch: str) -> None:
    update_sdk_dependencies(cargo.get("dependencies", []), branch)
    update_sdk_dependencies(cargo.get("dev-dependencies", []), branch)


def update_cargo(pathname: str, branch: str, dry_run=False):
    logging.debug(f"Found Cargo.toml at {pathname}")
    cargo = read_cargo(pathname)
    update_sdk(cargo, branch)
    if not dry_run:
        logging.debug(f"Writing new content to {pathname}")
        write_cargo(pathname, cargo)


arg_parser = ArgumentParser()
arg_parser.add_argument(
    "--root",
    default="../",
    help="Root path for rustv1 cargos. Default ../ assumes running this script from its location in tools. Update single examples by setting root to their folder directly.",
)
arg_parser.add_argument("--branch", default="", help="Branch to use")
arg_parser.add_argument(
    "--dry-run",
    dest="dry_run",
    action="store_true",
    default=False,
    help="Don't write updated Cargo files.",
)
arg_parser.add_argument(
    "--verbose", action="store_true", default=False, help="Write verbose logging"
)


def main():
    args = arg_parser.parse_args()
    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)
    for f in list_cargos(args.root):
        update_cargo(f, args.branch, dry_run=args.dry_run)


if __name__ == "__main__":
    main()
