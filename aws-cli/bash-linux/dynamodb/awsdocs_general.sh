#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#bashsupport disable=BP5001

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
# This script contains general purpose functions that are used throughout
# the AWS Command Line Interface (AWS CLI) code samples that are maintained
# in the repo at https://github.com/awsdocs/aws-doc-sdk-examples
#
# Set global defaults:
# bashsupport disable=BP5006
VERBOSE=false

# snippet-start:[aws-cli.bash-linux.dynamodb.errecho]
###############################################################################
# function errecho
#
# This function outputs everything sent to it to STDERR (standard error output).
###############################################################################
function errecho() {
  printf "%s\n" "$*" 1>&2
}
# snippet-end:[aws-cli.bash-linux.dynamodb.errecho]

# snippet-start:[aws-cli.bash-linux.dynamodb.iecho]
###############################################################################
# function iecho
#
# This function enables the script to display the specified text only if
# the global variable $VERBOSE is set to true.
###############################################################################
function iecho() {
  if [[ $VERBOSE == true ]]; then
    echo "$@"
  fi
}
# snippet-end:[aws-cli.bash-linux.dynamodb.iecho]

###############################################################################
# function ipause
#
# This function enables the script to pause after each command if interactive
# mode is set (by including -i on the script invocation command).
###############################################################################
function ipause() {
  if [[ $INTERACTIVE == true ]]; then
    read -r -p "Press ENTER to continue..."
  fi
}

# Initialize the shell's RANDOM variable.
RANDOM=$$
###############################################################################
# function generate_random_name
#
# This function generates a random file name with using the specified root
# followed by 4 groups that each have 4 digits.
# The default root name is "test".
function generate_random_name() {

  local rootname="test"
  if [[ -n $1 ]]; then
    rootname=$1
  fi

  # Initialize the filename variable.
  local filename="$rootname"
  # Configure random number generator to issue numbers between 1000 and 9999, inclusive.
  local diff
  diff=$((9999 - 1000 + 1))

  # bashsupport disable=BP2001
  for _ in {1..4}; do
    local rnd
    rnd=$(($((RANDOM % diff)) + X))
    # Make sure that the number is 4 digits long.
    while [ "${#rnd}" -lt 4 ]; do rnd="0$rnd"; done
    filename+="-$rnd"
  done
  echo "$filename"
}

# snippet-start:[aws-cli.bash-linux.dynamodb.aws_cli_error_log]
##############################################################################
# function aws_cli_error_log()
#
# This function is used to log the error messages from the AWS CLI.
#
# See https://docs.aws.amazon.com/cli/latest/topic/return-codes.html#cli-aws-help-return-codes.
#
# The function expects the following argument:
#         $1 - The error code returned by the AWS CLI.
#
#  Returns:
#          0: - Success.
#
##############################################################################
function aws_cli_error_log() {
  local err_code=$1
  errecho "Error code : $err_code"
  if [ "$err_code" == 1 ]; then
    errecho "  One or more S3 transfers failed."
  elif [ "$err_code" == 2 ]; then
    errecho "  Command line failed to parse."
  elif [ "$err_code" == 130 ]; then
    errecho "  Process received SIGINT."
  elif [ "$err_code" == 252 ]; then
    errecho "  Command syntax invalid."
  elif [ "$err_code" == 253 ]; then
    errecho "  The system environment or configuration was invalid."
  elif [ "$err_code" == 254 ]; then
    errecho "  The service returned an error."
  elif [ "$err_code" == 255 ]; then
    errecho "  255 is a catch-all error."
  fi

  return 0
}
# snippet-end:[aws-cli.bash-linux.dynamodb.aws_cli_error_log]
