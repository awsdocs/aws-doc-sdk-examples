# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import os
import subprocess
import sys
import getopt
import glob
import re


# Script to run automated C++ tests.
#
# Types of automated tests.
# 1. Requires credentials, permissions, and AWS resources.
# 2. Requires credentials and permissions.
# 3. Does not require credentials (mocked if necessary).


def build_tests(service="*"):
    cmake_files = glob.glob( f"example_code/{service}/tests/CMakeLists.txt")
    cmake_files.extend(glob.glob( f"example_code/{service}/gtests/CMakeLists.txt"))

    subprocess.call(f"echo $PATH", shell=True)

    run_files = []

    if len (cmake_files) == 0:
        return [1, []]

    has_error = False
    base_dir = os.getcwd()
    build_dir = os.path.join(base_dir, "build_tests")

    os.makedirs(name=build_dir, exist_ok=True)

    for cmake_file in cmake_files :
        source_dir = os.path.dirname(cmake_file)
        module_build_dir = os.path.join(build_dir, source_dir)
        os.makedirs(name=module_build_dir, exist_ok=True)
        os.chdir(module_build_dir)
        result_code = subprocess.call(f"cmake {os.path.join(base_dir, source_dir)}", shell=True)
        if result_code != 0 :
            print(f"Error with cmake for {source_dir}")
            has_error = True
            continue

        result_code = subprocess.call("cmake --build .", shell=True)
        if result_code != 0 :
            has_error = True
            continue

        run_files.extend(glob.glob(f"{module_build_dir}/*_gtest"))

    if has_error :
        return [1, []]
    else:
        return [0, run_files]


def run_tests(run_files = [], type1=False, type2=False, type3=False):
    has_error = False
    filters = []
    if type1 :
        filters.append("*_1_")

    if type2 :
        filters.append("*_2_")

    if type3 :
        filters.append("*_3_")

    filter_arg = ""
    if len(filters) > 0:
        filter_arg = f"--gtest_filter={':'.join(filters)}"

    passed_tests = 0
    failed_tests = 0
    for run_file in run_files :
        run_test_cmd = f"{run_file} {filter_arg}"
        print(f"Calling '{run_test_cmd}'.")
        completed_process = subprocess.run(run_test_cmd, shell=True, capture_output=True)
        if completed_process.returncode != 0 :
            has_error = True

        print(completed_process.stderr.decode("utf-8"))

        output = completed_process.stdout.decode("utf-8")
        output = output.split('\n')
        for line in output:
            print(line)
            match = re.search("\[  PASSED  \] (\d+) test", line)
            if match is not None:
                passed_tests = passed_tests + int(match.group(1))
                continue
            match = re.search("\[  FAILED  \] (\d+) test", line)
            if match is not None:
                failed_tests = failed_tests + int(match.group(1))
                continue

    print('-'*88)
    print(f"{passed_tests} tests passed.")
    print(f"{failed_tests} tests failed.")

    if has_error:
        return 1
    else :
        return 0


def main(argv):
    type1 = False
    type2 = False
    type3 = False
    service = "*"

    opts, args = getopt.getopt(argv, "h123s:")
    for opt, arg in opts:
        if opt == '-h':
            print('run_automated_tests.py -1 -2 -3 -s <service>')
            print('Where:')
            print(' 1. Requires credentials and pre-configured resources.')
            print(' 2. Requires credentials.')
            print(' 3. Does not require credentials.')
            print(' s. Test this service (regular expression).')
            sys.exit()
        elif opt in ("-1"):
            type1 = True
        elif opt in ("-2"):
            type2 = True
        elif opt in ("-3"):
            type3 = True
        elif opt in ("-s"):
            service = arg

    [err_code, run_files] = build_tests(service=service)

    if err_code == 0 :
        err_code = run_tests(run_files = run_files, type1=type1, type2=type2, type3=type3)

    return err_code


if __name__ == "__main__":
    result = main(sys.argv[1:])

    exit(result)


