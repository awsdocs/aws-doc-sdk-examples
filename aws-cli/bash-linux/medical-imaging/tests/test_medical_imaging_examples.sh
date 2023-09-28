#!/bin/bash

###############################################################################
#
#    Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#    SPDX-License-Identifier: Apache-2.0
#
###############################################################################

###############################################################################
#
#     Before running this AWS CLI example, set up your development environment, including your credentials.
#
#     For more information, see the following documentation topic:
#
#     https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
#
###############################################################################

###############################################################################
# function main
#
# This function runs the IAM examples' tests.
###############################################################################
function main() {
# Set default values.
# bashsupport disable=BP2001
export INTERACTIVE=false
# bashsupport disable=BP2001
export VERBOSE=false

  source ./include_tests.sh
  {
    local current_directory
    current_directory=$(pwd)
    cd ..
    source ./medical_imaging_operations.sh
    # shellcheck disable=SC2164
    cd "$current_directory"
  }

  ###############################################################################
  # function usage
  #
  # This function prints usage information for the script.
  ###############################################################################
  function usage() {
    echo "This script tests Amazon IAM operations in the AWS CLI."
    echo ""
    echo "To pause the script between steps, so you can see the results in the"
    echo "AWS Management Console, include the parameter -i."
    echo ""
    echo "IMPORTANT: Running this script creates resources in your Amazon"
    echo "   account that can incur charges. It is your responsibility to"
    echo "   ensure that no resources are left in your account after the script"
    echo "   completes. If an error occurs during the operation of the script,"
    echo "   then resources can remain that you might need to delete manually."
  }

  local option OPTARG # Required to use getopts command in a function.

  # Retrieve the calling parameters
  while getopts "ivh" option; do
    case "${option}" in
      i)
        INTERACTIVE=true
        VERBOSE=true
        iecho
        ;;
      v) VERBOSE=true ;;
      h)
        usage
        return 0
        ;;
      \?)
        echo "Invalid parameter"
        usage
        return 1
        ;;
    esac
  done

  if [ "$INTERACTIVE" == "true" ]; then iecho "Tests running in interactive mode."; fi
  if [ "$VERBOSE" == "true" ]; then iecho "Tests running in verbose mode."; fi

  iecho "***************SETUP STEPS******************"
  local datastore_name
  datastore_name=$(generate_random_name iamtestcli)
  iecho "datastore_name=$datastore_name"
  iecho "**************END OF STEPS******************"

  local test_count=0

  run_test "$test_count Create a data store" \
    "imaging_create_datastore -n $datastore_name " \
    0
  test_count=$((test_count + 1))

  local datastore_id="$test_command_response"

  run_test "$test_count List datastores" \
    "imaging_list_datastores" \
    0

  local datastore_list="$test_command_response"
  local data_store_found=false
  local line
  while IFS=$'\n' read -r line; do
     IFS=$'\t' read -ra entries <<<"$line"
    if [ "${entries[1]}" == "$datastore_id" ]; then
      data_store_found=true
    fi
    if [ "${#entries[@]}" -ne 3 ]; then
      test_failed "Incorrect number of entries in list datastores response. $datastore_list"
    fi
  done <<<"$datastore_list"

  if [ "$data_store_found" == "false" ]; then
    test_failed "Datastore id $datastore_id not found in list-datastores response. $datastore_list"
  fi

  test_count=$((test_count + 1))

  run_test "$test_count. Get a data store" \
    "imaging_get_datastore -i $datastore_id " \
    0

  IFS=$'\t' read -ra datastore_get_result <<<"$test_command_response"
  if [ "${#datastore_get_result[@]}" -ne 6 ]; then
    test_failed "Incorrect number of entries in get-datastore response. ${#datastore_get_result[@]}}"
  fi

  local retrieved_datastore_id="${datastore_get_result[1]}"
  if [ "$retrieved_datastore_id" != "$datastore_id" ]; then
    test_failed "Incorrect data store id in get-datastore response. ${#datastore_get_result[@]}"
  fi

  test_count=$((test_count + 1))

  sleep 10

  run_test "$test_count. Delete a data store" \
    "imaging_delete_datastore -i $datastore_id " \
    0
  test_count=$((test_count + 1))

  echo "$test_count tests completed successfully."
}

main
