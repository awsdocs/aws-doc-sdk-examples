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

# Set default values.

###############################################################################
# function main
#
# This function runs the IAM examples' tests.
###############################################################################
function main() {
  # bashsupport disable=BP2001
  export INTERACTIVE=false
  # bashsupport disable=BP2001
  export VERBOSE=false

  source ./test_general.sh
  {
    local current_directory
    current_directory=$(pwd)
    cd ..
    source ./dynamodb_operations.sh
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
  local table_name
  table_name=$(generate_random_name testcli)
  iecho "user_name=$table_name"
  local attr_definitions="AttributeName=year,AttributeType=N"
  local key_schema="AttributeName=year,KeyType=HASH"
  local provisioned_throughput="ReadCapacityUnits=5,WriteCapacityUnits=5"
  local item_json_file="test_dynamodb_item.json"
  iecho "**************END OF STEPS******************"

  local test_count=0

  run_test "$test_count. Creating user with missing username" \
    "dynamodb_create_table " \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Creating user with valid parameters" \
    "dynamodb_create_table -n $table_name -a $attr_definitions -k $key_schema -p $provisioned_throughput " \
    0


  run_test "$test_count. Waiting for table to become active" \
    "dynamodb_wait_table_active -n $table_name "  \
    0
  test_count=$((test_count + 1))

  echo '{
  "year": {"N" :"1979"},
  "title": {"S" :  "Great movie"},
  "info": {"M" : {"plot": {"S" : "some stuff"}, "rating": {"N" :"10"} } }
}' > "$item_json_file"

  run_test "$test_count. Putting item into table" \
    "dynamodb_put_item -n $table_name -i $item_json_file " \
    0
    test_count=$((test_count + 1))

  run_test "$test_count. deleting table" \
    "dynamodb_delete_table -n $table_name " \
    0
  test_count=$((test_count + 1))

  rm "$item_json_file"

  echo "$test_count tests completed successfully."
}

main
