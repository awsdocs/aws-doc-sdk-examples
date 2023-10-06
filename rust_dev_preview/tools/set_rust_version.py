#!/usr/bin/env python3

try:
    import tomlkit
except:
    print("Couldn't import tomlkit, either install it directly or instantiate a venv.")
    exit(1)

from glob import glob
import argparse
import logging
import pathlib


def read_toml(path: pathlib.Path) -> tomlkit.TOMLDocument:
    with open(path, "rt") as file:
        return tomlkit.parse(file.read())


def write_toml(path: pathlib.Path, toml: tomlkit.TOMLDocument):
    with open(path, "wt") as file:
        tomlkit.dump(toml, file)


def update_toolchains(root: pathlib.Path, channel: str, dry_run: bool):
    for file in glob(f"{root}/rust_dev_preview/**/rust-toolchain.toml", recursive=True):
        toolchain_toml = read_toml(file)
        logging.debug(
            f"Setting {file} to {channel} from {toolchain_toml['toolchain']['channel']}"
        )
        toolchain_toml["toolchain"]["channel"] = channel
        if not dry_run:
            write_toml(file, toolchain_toml)


def update_actions(root: pathlib.Path, channel: str, dry_run: bool):
    rust_yaml = root / ".github" / "workflows" / "rust.yml"
    with open(rust_yaml, "rt") as file:
        action = file.readlines()
    found = None
    for i, line in enumerate(action):
        if "toolchain:" in line:
            sep = line.find(":")
            found = i, line[sep + 2 :]
            action[i] = f'{line[: sep + 1]} "{channel}"\n'
    if found == None:
        raise Exception(f"Did not find toolchain entry in {rust_yaml}")
    logging.debug(f"Setting {rust_yaml} to {channel} from {found}")
    if not dry_run:
        with open(rust_yaml, "wt") as file:
            file.writelines(action)


argument_parser = argparse.ArgumentParser()
argument_parser.add_argument(
    "--root",
    type=pathlib.Path,
    default=pathlib.Path(__file__).absolute().parent.parent.parent,
    help="Root path for aws-doc-sdk-examples. Default ../../ assumes running this script from its location in tools.",
)
argument_parser.add_argument(
    "channel", help="Toolchain channel to use for Rust code examples."
)
argument_parser.add_argument(
    "--dry-run",
    dest="dry_run",
    action="store_true",
    default=False,
    help="Don't write updated files.",
)
argument_parser.add_argument(
    "--verbose", action="store_true", default=False, help="Write verbose logging"
)


def main():
    args = argument_parser.parse_args()
    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)
    update_toolchains(args.root, args.channel, args.dry_run)
    update_actions(args.root, args.channel, args.dry_run)


if __name__ == "__main__":
    main()
