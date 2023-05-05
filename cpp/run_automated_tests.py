# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Script to run automated C++ tests.
#
# Types of automated tests:
# 1. Requires credentials, permissions, and AWS resources.
# 2. Requires credentials and permissions.
# 3. Does not require credentials (mocked if necessary).
#
# For example, the following command builds and runs tests of type 2 and 3.
#
#   'python3 run_automated_tests.py -23'
#
# The service can be specified with the -s option, which takes a regular expression.
#
# For example, the following command builds and runs tests of type 2 and 3 in service s3.
#
#   'python3 run_automated_tests.py -23 -s s3'
#


import os
import subprocess
import sys
import getopt
import glob
import re
import datetime


def build_tests(service="*"):
    cmake_files = glob.glob( f"example_code/{service}/tests/CMakeLists.txt")
    cmake_files.extend(glob.glob( f"example_code/{service}/gtests/CMakeLists.txt"))

    run_files = []

    if len (cmake_files) == 0:
        return [1, []]

    has_error = False
    base_dir = os.getcwd()
    build_dir = os.path.join(base_dir, "build_tests")

    os.makedirs(name=build_dir, exist_ok=True)

    cmake_args = os.getenv("EXTRA_CMAKE_ARGS")

    for cmake_file in cmake_files :
        source_dir = os.path.dirname(cmake_file)
        module_build_dir = os.path.join(build_dir, source_dir)
        os.makedirs(name=module_build_dir, exist_ok=True)
        os.chdir(module_build_dir)

        cmake_command = ['cmake']
        if cmake_args is not None:
            cmake_command.append(cmake_args)
        cmake_command.append(os.path.join(base_dir, source_dir))

        result_code = subprocess.call(cmake_command, shell=False)
        if result_code != 0 :
            print(f"Error with cmake for {source_dir}")
            has_error = True
            continue

        result_code = subprocess.call(['cmake', '--build', '.'], shell=False)
        if result_code != 0 :
            has_error = True
            continue

        run_files.extend(glob.glob(f"{module_build_dir}/*_gtest"))
        run_files.extend(glob.glob(f"{module_build_dir}/Debug/*_gtest.exe"))

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
        print(f"Calling '{run_file} {filter_arg}'.")
        proc = subprocess.Popen([run_file, filter_arg], stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        for line in proc.stdout:
            line = line.decode("utf-8")
            sys.stdout.write(line)

            match = re.search("\[  PASSED  \] (\d+) test", line)
            if match is not None:
                passed_tests = passed_tests + int(match.group(1))
                continue
            match = re.search("\[  FAILED  \] (\d+) test", line)
            if match is not None:
                failed_tests = failed_tests + int(match.group(1))
                continue

        proc.wait()

        if proc.returncode != 0 :
            has_error = True

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

    start_time = datetime.datetime.now()
    [err_code, run_files] = build_tests(service=service)

    if err_code == 0 :
        err_code = run_tests(run_files = run_files, type1=type1, type2=type2, type3=type3)

    print(f"Execution duration - {datetime.datetime.now() - start_time}")

    return err_code


if __name__ == "__main__":
    result = main(sys.argv[1:])

    exit(result)


