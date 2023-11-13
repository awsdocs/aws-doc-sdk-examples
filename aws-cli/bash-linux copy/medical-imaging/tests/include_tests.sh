#!/bin/bash
#bashsupport disable=BP5001

################################################################################
##
##    Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
##    SPDX-License-Identifier: Apache-2.0
##
################################################################################

################################################################################
##
##     Before running this AWS CLI example, set up your development environment, including your credentials.
##
##     For more information, see the following documentation topic:
##
##     https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
##
################################################################################
#
##############################################################################
#
# This script contains general purpose functions that are used for testing
# the AWS Command Line Interface (AWS CLI) code samples that are maintained
# in the repo at https://github.com/awsdocs/aws-doc-sdk-examples
#

test_command_response=""

###############################################################################
# function run_test
#
# This function is used to perform a command and compare its output to both
# the expected error code and the expected output string. If there isn't a
# match, then the function invokes the test_failed function.
###############################################################################
function run_test() {
  local description command expected_err_code expected_output

  description="$1"
  command="$2"
  expected_err_code="$3"
  if [[ -n "$4" ]]; then expected_output="$4"; else expected_output=""; fi

  iecho -n "Running test: $description..."
  test_command_response="$($command)"
  local err="${?}"

  # Check to see if we got the expected error code.
  if [[ "$expected_err_code" -ne "$err" ]]; then
    test_failed "The test \"$description\" returned an unexpected error code: $err. $test_command_response"
  fi

  # Check the error message, if we provided other than "".
  if [[ -n "$expected_output" ]]; then
    local match
    match=$(echo "$test_command_response" | grep "$expected_output")
    # If there was no match (it's an empty string), then fail.
    if [[ -z "$match" ]]; then
      test_failed "The test \"$description\" returned an unexpected output: $test_command_response"
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
  errecho "    One or more of the tests failed to complete successfully. If there were any"
  errecho "    tests after the one that failed, they didn't run. As a result, you might have"
  errecho "    resources still active in your account."
  errecho ""
  errecho "IMPORTANT:"
  errecho "    Resources created by this script can incur charges to your AWS account. If the"
  errecho "    script did not complete successfully, then you must review and manually delete"
  errecho "    any resources created by this script that were not automatically removed."
  errecho ""
  exit 1
}
