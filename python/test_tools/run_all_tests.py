# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Finds all modules in the Python folder that have unit tests and runs them all
sequentially as separate PyTest sessions.

This script must be run from the root of the GitHub repo.

    py -m python.test_tools.run_all_tests
"""

import argparse
import os
import platform
import sys
import pytest

IGNORE_FOLDERS = {
    "venv",
    ".venv",
    "__pycache__",
    ".pytest_cache",
    "node_modules",
}


def main():
    """
    Finds all subfolders of the `python` folder that contain a `test` folder and
    assume the parent folder is testable.
    Runs each testable folder as a separate PyTest session.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--integ", action="store_true", help="When specified, run integration tests."
    )
    args = parser.parse_args()

    test_dirs = []
    for root, dirs, files in os.walk("python"):
        dirs[:] = [d for d in dirs if d not in IGNORE_FOLDERS]
        if "test" in dirs:
            test_dirs.append(root)

    original_path = sys.path.copy()
    root_dir = os.getcwd()
    for test_dir in test_dirs:
        test_path = os.path.join(root_dir, test_dir)
        sys.path.append(test_path)
        os.chdir(test_path)
        test_kind = "integ" if args.integ else "not integ"
        if platform.system() == "Windows":
            os.system(f'py -m pytest -m "{test_kind}"')
        else:
            os.system(f'python -m pytest -m "{test_kind}"')
        sys.path = original_path.copy()


if __name__ == "__main__":
    main()
