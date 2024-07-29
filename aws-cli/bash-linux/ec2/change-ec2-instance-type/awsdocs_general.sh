#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


###############################################################################
# function run_test
#
# This function is used to perform a command and compare its output to both
# the expected error code and the expected output string. If there isn't a
# match, then the function invokes the test_failed function.
###############################################################################
function run_test() {
  local description command expected_err_code expected_output response

  description="$1"
  command="$2"
  expected_err_code="$3"
  if [[ -z "$4" ]]; then expected_output="$4"; else expected_output=""; fi

  iecho -n "Running test: $description..."
  response="$($command)"
  local err="${?}"

  # Check to see if we got the expected error code.
  if [[ "$expected_err_code" -ne "$err" ]]; then
    test_failed "The test \"$description\" returned an unexpected error code: $err"
  fi

  # Now check the error message, if we provided other than "".
  if [[ -n "$expected_output" ]]; then
    local match
    match=$(echo "$response" | grep "$expected_output")
    # If there was no match (it's an empty string), then fail.
    if [[ -z "$match" ]]; then
      test_failed "The test \"$description\" returned an unexpected output: $response"
    fi
  fi

  iecho "OK"
  ipause
}

###############################################################################
# function test_failed
#
# This function is used to terminate a failed test and to warn the customer
# about possible undeleted resources that could incur costs to their account.
###############################################################################
function test_failed() {
  errecho ""
  errecho "===TEST FAILED==="
  errecho "$@"
  errecho ""
  errecho "    One or more of the tests failed to complete successfully. This means that"
  errecho "    any tests after the one that failed didn't run and might have left resources"
  errecho "    still active in your account."
  errecho ""
  errecho "IMPORTANT:"
  errecho "    Resources created by this script can incur charges to your AWS account. If the"
  errecho "    script didn't complete successfully, then you must review and manually delete"
  errecho "    any resources created by this script that were not automatically removed."
  errecho ""
  exit 1
}

###############################################################################
# function errecho
#
# This function outputs everything sent to it to STDERR (standard error output).
###############################################################################
function errecho() {
  printf "%b\n" "$*" 1>&2
}

###############################################################################
# function iecho
#
# This function enables the script to display the specified text only if
# the global variable $verbose is set to true.
###############################################################################
function iecho() {
  if [[ $verbose == true ]]; then
    echo -e "$@"
  fi
}

###############################################################################
# function ipause
#
# This function enables the script to pause after each command if interactive
# mode is set (by including -i on the script invocation command).
###############################################################################
function ipause() {
  if [[ $interactive == true ]]; then
    read -r -p "Press ENTER to continue..."
  fi
}

###############################################################################
# function generate_random_name
#
# This function generates a random file name with using the specified root,
# followed by 4 groups that each have 4 digits.
# The default root name is "test".
###############################################################################
function generate_random_name() {
  local rootname="test"
  if [[ -n $1 ]]; then
    rootname=$1
  fi

  # Initialize the filename variable
  local filename="$rootname"
  # Configure random number generator to issue numbers between 1000 and 9999,
  # inclusive.
  local diff
  diff=$((9999 - 1000 + 1))

  for _ in {1..4}; do
    local rnd
    rnd=$(($((RANDOM % diff)) + X))
    # Make sure that the number is 4 digits long.
    while [ "${#rnd}" -lt 4 ]; do rnd="0$rnd"; done
    filename+="-$rnd"
  done
  echo "$filename"
}

###############################################################################
# function main
#
###############################################################################
function main() {
  echo "Hello world"
  verbose=false
  interactive=false

}

# bashsupport disable=BP5001
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  main "$@"
fi

