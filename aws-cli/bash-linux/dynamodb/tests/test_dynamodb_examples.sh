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

  source ./include_tests.sh
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
  local provisioned_throughput="ReadCapacityUnits=5,WriteCapacityUnits=5"
  local key_schema_json_file="test_dynamodb_key_schema.json"
  local attr_definitions_json_file="test_dynamodb_attr_def.json"
  local key_json_file="test_dynamodb_key.json"
  local item_json_file="test_dynamodb_item.json"
  local batch_json_file="test_dynamodb_batch.json"
  iecho "**************END OF STEPS******************"

  run_test "Creating table with missing parameters" \
    "dynamodb_create_table " \
    1

  echo '[
  {"AttributeName": "year", "KeyType": "HASH"},
   {"AttributeName": "title", "KeyType": "RANGE"}
  ]' >"$key_schema_json_file"

  echo '[
  {"AttributeName": "year", "AttributeType": "N"},
   {"AttributeName": "title", "AttributeType": "S"}
  ]' >"$attr_definitions_json_file"

  run_test "Creating table" \
    "dynamodb_create_table -n $table_name -a $attr_definitions_json_file -k $key_schema_json_file -p $provisioned_throughput " \
    0

  exit_on_failure=false

  run_test "Waiting for table to become active" \
    "dynamodb_wait_table_active -n $table_name " \
    0

  run_test "Listing tables" \
    "dynamodb_list_tables " \
    0

  local table_names
  IFS=$'\n' read -r -d '' -a table_names <<<"$test_command_response"

  local found=false
  local listed_table_name
  for listed_table_name in "${table_names[@]}"; do
    if [[ "$listed_table_name" == "$table_name" ]]; then
      found=true
      break
    fi
  done

  if [[ "$found" == "false" ]]; then
    test_failed "Table was not found in the list of tables."
  else
    echo "Table found in the list of tables."
  fi

  echo '{
  "year": {"N" :"1979"},
  "title": {"S" :  "Great movie"},
  "info": {"M" : {"plot": {"S" : "some stuff"}, "rating": {"N" :"10"} } }
}' >"$item_json_file"

  run_test "Putting item into table" \
    "dynamodb_put_item -n $table_name -i $item_json_file " \
    0

  echo '{
  "year": {"N" :"1979"},
  "title": {"S" :  "Great movie"}
  }' >"$key_json_file"

  echo '{
  ":r": {"N" :"8"},
  ":p": {"S" : "some totally different stuff"}
 }' >"$item_json_file"

  local update_expression="SET info.rating = :r, info.plot = :p"

  test_count=$((test_count + 1))
  echo -n "Running test $test_count: Updating item..."
  dynamodb_update_item -n "$table_name" -k "$key_json_file" -e "$update_expression" -v "$item_json_file" 1>/dev/null
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Updating item failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  run_test "getting item without query" \
   "dynamodb_get_item -n $table_name -k $key_json_file  " \
   0
  echo "$test_command_response"

  run_test "getting item with query" \
   "dynamodb_get_item -n $table_name -k $key_json_file -q [Item.title,Item.year,Item.info.M.rating,Item.info.M.plot] " \
   0
   echo "$test_command_response"

  dynamodb_get_item -n "$table_name" -k $key_json_file -q "[Item.title,Item.year,Item.info.M.rating,Item.info.M.plot]"


  echo "{ \"$table_name\" : $(<../movie_files/movies_0.json) }" >"$batch_json_file"

  run_test "Batch write items into table" \
    "dynamodb_batch_write_item -i $batch_json_file " \
    0

  skip_tests=false
  run_test " deleting table" \
    "dynamodb_delete_table -n $table_name " \
    0

  rm "$item_json_file"
  rm "$key_json_file"
  rm "$key_schema_json_file"
  rm "$attr_definitions_json_file"
  rm "$batch_json_file"

  echo "$test_succeeded_count tests completed successfully."
  echo "$test_failed_count tests failed."
}

main
