#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import os
import pathlib
import subprocess
from typing import List, Tuple


def swiftbuild(
    test: bool, run: bool, clean: bool, packages: List[pathlib.Path], swiftc_options: List[str]
) -> None:
    """
    Build (or test) one or more Swift projects.

    Args:
        test (bool): Whether to run tests after building.
        run (bool): Whether to run each project after building.
        clean (bool): Whether or not to erase the build artifacts before
        building.
        packages (List[pathlib.Path]): List of package directories to build.
        swiftc_options (List[str]): Additional options to pass to the Swift compiler.
    """
    results: Tuple[Tuple[pathlib.Path, int], ...] = ()
    num_packages_found = 0
    for directory in packages:
        if directory.exists() and directory.is_dir():
            path = directory.expanduser().resolve()
            output, is_package = build_package(test, run, clean, path, swiftc_options)
            if is_package:
                num_packages_found += 1
                results += ((path, output),)

    # Display a table of build results.
    if num_packages_found != 0:
        print("{0: <65} {1}".format("Example", "Status"))
        print("-" * 65, "-" * 6)

    fails = 0
    for path, value in results:
        outcome_str = "OK" if value == 0 else "Fail"
        if value != 0:
            fails += 1

        parent = path.parts[-2] if len(path.parts) > 1 else ""
        short_path = f"{parent}{os.sep}{path.name}"
        short_path = str(short_path)
        if len(short_path) > 64:
            short_path = f"...{short_path[-61:]}"
        print("{0:.<65} {1}".format(f"{short_path} ", outcome_str))

    print(f"\nBuilt {num_packages_found} project(s) with {fails} failure(s).")
    print_configuration(test, swiftc_options)


def print_configuration(test: bool, swiftc_options: List[str]) -> None:
    """
    Print the build configuration.

    Args:
        test (bool): Whether tests are enabled.
        swiftc_options (List[str]): Additional options to pass to the Swift compiler.
    """
    print("Build options:")
    if test:
        print("    Tests enabled")

    if swiftc_options:
        for opt in swiftc_options:
            print(f"    {opt}")
    elif not test:
        print("    None")


def is_package_dir(directory: pathlib.Path) -> bool:
    """
    Determine if the specified path contains a Swift package.

    Args:
        directory (pathlib.Path): Directory to check for a Swift package.

    Returns:
        bool: True if the directory contains a Package.swift file, False otherwise.
    """
    packagefile_path = directory / "Package.swift"
    return packagefile_path.is_file()


def build_package(
    test: bool, run: bool, clean: bool, package: pathlib.Path, swiftc_options: List[str]
) -> Tuple[int, bool]:
    """
    Build one package, given a specific directory. Build and test if the `test` argument is True.

    Args:
        test (bool): Whether to run tests after building.
        run (bool): Whether to run the package after building.
        package (pathlib.Path): Directory of the package to build.
        swiftc_options (List[str]): Additional options to pass to the Swift compiler.

    Returns:
        Tuple[int, bool]: A tuple containing the return code from the build command and
                          a boolean indicating if the directory is a Swift package.
    """
    is_package = is_package_dir(package)

    if not is_package:
        return 1, is_package

    build_command = "test" if test else "run" if run else "build"
    command_gerund = "testing" if test else "running" if run else "building"

    test_script = package / "test.sh"
    if test and test_script.exists() and test_script.is_file():
        command_parts = ["bash", str(test_script)]
    else:
        command_parts = ["swift", build_command]
        for opt in swiftc_options:
            command_parts += ["-Xswiftc", opt]

    print(f"Now {command_gerund} project in {package}...")

    # If cleaning requested, do that first.
    if clean:
        print("Cleaning the build artifacts...")
        results = subprocess.run(["rm", "-rf", ".build"], cwd=package)

    results = subprocess.run(command_parts, cwd=package)
    print("=" * 72, "\n")
    return results.returncode, is_package


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Build (and optionally test) one or more Swift projects."
    )
    parser.add_argument(
        "-r",
        "--run",
        action="store_true",
        help="build and run each project",
        dest="run",
    )
    parser.add_argument(
        "-c",
        "--clean",
        action="store_true",
        help="clean the build artifacts first"
    )
    parser.add_argument(
        "-x",
        "--purge",
        action="store_true",
        help="purge global build package cache"
    )
    parser.add_argument(
        "-t",
        "--test",
        action="store_true",
        help="run tests on projects after building them",
    )
    parser.add_argument(
        "-p",
        "--parseable",
        action="store_true",
        help="enable parseable output",
        dest="parseable",
    )
    parser.add_argument(
        "-w",
        "--hide-warnings",
        action="store_true",
        help="hide build warnings",
        dest="hide_warnings",
    )
    parser.add_argument(
        "packages",
        metavar="PACKAGE_DIR",
        type=pathlib.Path,
        nargs="*",
        help="Directory of a project to build.",
    )

    args = parser.parse_args()

    swiftc_options = []
    if args.hide_warnings:
        swiftc_options.append("-suppress-warnings")
    if args.parseable:
        swiftc_options.append("-parseable-output")

    if args.purge:
        print("Purging the global package cache...")
        home = pathlib.Path.home() / ".swiftpm"
        try:
            results = subprocess.run(["rm", "-rf", "cache"], cwd=home)
        except:
            print("   - No cache present")

    package_list = [pathlib.Path().absolute()] if not args.packages else args.packages
    if not args.packages:
        cwd_path = pathlib.Path().absolute()
        if not is_package_dir(cwd_path):
            package_list = [item for item in cwd_path.iterdir() if item.is_dir()]

    swiftbuild(args.test, args.run, args.clean, package_list, swiftc_options)
