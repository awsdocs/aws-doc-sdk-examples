# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Finds all modules in the Python folder that have unit tests and runs them all
sequentially as separate PyTest sessions.

This script must be run from the root of the GitHub repo.

    py -m python.test_tools.run_all_tests
"""

import os
import sys
import pytest


def main():
    """
    Finds all subfolders of the `python` folder that contain a `test` folder and
    assume the parent folder is testable.
    Runs each testable folder as a separate PyTest session.
    """
    test_dirs = []
    for root, dirs, files in os.walk('python'):
        if 'test' in dirs:
            test_dirs.append(root)

    original_path = sys.path.copy()
    root_dir = os.getcwd()
    for test_dir in test_dirs:
        test_path = os.path.join(root_dir, test_dir)
        sys.path.append(test_path)
        os.chdir(test_path)
        os.system('py -m pytest -m "not integ"')
        sys.path = original_path.copy()


if __name__ == '__main__':
    main()
