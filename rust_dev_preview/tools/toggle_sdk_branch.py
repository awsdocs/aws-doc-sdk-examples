#!/usr/bin/env python3

try:
    from tomlkit import dumps, parse, TOMLDocument
except:
    print("Couldn't import tomlkit, either install it directly or instantiate a venv.")
    exit(1)

from argparse import ArgumentParser
from glob import glob
from typing import Union
import logging

SDK_ORIGIN = "https://github.com/awslabs/aws-sdk-rust"


def list_cargos(pathname: str):
    for file in glob(f"{pathname}/**/Cargo.toml", recursive=True):
        yield file


def read_cargo(pathname: str) -> TOMLDocument:
    with open(pathname, "+rt") as cargo:
        return parse(cargo.read())


def write_cargo(pathname: str, cargo: TOMLDocument) -> None:
    with open(pathname, "+wt") as file:
        file.write(dumps(cargo))


def update_sdk_dependencies(cargo: TOMLDocument, branch: Union["main", "next"]) -> None:
    dependencies = cargo.get("dependencies", [])
    for name in dependencies:
        if isinstance(name, str):
            dependency = dependencies[name]
            if (
                not isinstance(dependency, str)
                and dependency.get("git", None) == SDK_ORIGIN
                and dependency.get("branch", None) is not None
            ):
                logging.debug(
                    f"Found SDK crate {name} currently on {dependency['branch']}"
                )
                dependency["branch"] = branch


def update_cargo(pathname: str, branch: Union["main", "next"], dry_run=False):
    logging.debug(f"Found Cargo.toml at {pathname}")
    cargo = read_cargo(pathname)
    update_sdk_dependencies(cargo, branch)
    if not dry_run:
        logging.debug(f"Writing new content to {pathname}")
        write_cargo(pathname, cargo)


arg_parser = ArgumentParser()
arg_parser.add_argument(
    "--root",
    default="../",
    help="Root path for rust_dev_preview cargos. Default ../ assumes running this script from its location in tools. Update single examples by setting root to their folder directly.",
)
arg_parser.add_argument(
    "--branch", default="main", choices=["main", "next"], help="Branch to use"
)
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
