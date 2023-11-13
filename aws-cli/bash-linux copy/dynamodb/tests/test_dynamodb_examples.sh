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
# This function runs the Amazon DynamoDB examples' tests.
###############################################################################
function main() {
  # bashsupport disable=BP2001
  export INTERACTIVE=false
  # bashsupport disable=BP2001
  export VERBOSE=false

# shellcheck disable=SC1091
  source ./include_tests.sh
  {
    local current_directory
    current_directory=$(pwd)
    cd ..
    # shellcheck disable=SC1091
    source ./dynamodb_operations.sh
    # shellcheck disable=SC1091
    source ./scenario_getting_started_movies.sh
    # shellcheck disable=SC2164
    cd "$current_directory"
  }

  ###############################################################################
  # function usage
  #
  # This function prints usage information for the script.
  ###############################################################################
  function usage() {
    echo "This script tests Amazon DynamoDB operations in the AWS CLI."
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
  local test_table_name
  test_table_name=$(generate_random_name testcli)
  local test_provisioned_throughput="ReadCapacityUnits=5,WriteCapacityUnits=5"
  local test_key_schema_json_file="test_dynamodb_key_schema.json"
  local test_attr_definitions_json_file="test_dynamodb_attr_def.json"
  local test_key_json_file="test_dynamodb_key.json"
  local test_item_json_file="test_dynamodb_item.json"
  local test_batch_json_file="test_dynamodb_batch.json"
  local test_attribute_names_json_file="test_dynamodb_attribute_names.json"
  local test_attributes_values_json_file="test_dynamodb_attribute_values.json"
  local test_requested_values_json_file="test_dynamodb_requested_values.json"
  iecho "**************END OF STEPS******************"

  run_test "Creating table with missing parameters" \
    "dynamodb_create_table " \
    1

  echo '[
  {"AttributeName": "year", "KeyType": "HASH"},
   {"AttributeName": "title", "KeyType": "RANGE"}
  ]' >"$test_key_schema_json_file"

  echo '[
  {"AttributeName": "year", "AttributeType": "N"},
   {"AttributeName": "title", "AttributeType": "S"}
  ]' >"$test_attr_definitions_json_file"

  run_test "Creating table" \
    "dynamodb_create_table -n $test_table_name -a $test_attr_definitions_json_file -k $test_key_schema_json_file -p $test_provisioned_throughput " \
    0

  export exit_on_failure=false

  run_test "Waiting for table to become active" \
    "dynamodb_wait_table_active -n $test_table_name " \
    0

  run_test "Describing table" \
    "dynamodb_describe_table -n $test_table_name " \
    0

   # shellcheck disable=SC2154
  if [[ "$test_command_response" != "ACTIVE" ]];  then
    test_failed "Table is not active."
    return 1
  fi

  run_test "Listing tables" \
    "dynamodb_list_tables " \
    0

  local table_names
  IFS=$'\n' read -r -d '' -a table_names <<<"$test_command_response"

  local found=false
  local listed_table_name
  for listed_table_name in "${table_names[@]}"; do
    if [[ "$listed_table_name" == "$test_table_name" ]]; then
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
}' >"$test_item_json_file"

  run_test "Putting item into table" \
    "dynamodb_put_item -n $test_table_name -i $test_item_json_file " \
    0

  echo '{
  "year": {"N" :"1979"},
  "title": {"S" :  "Great movie"}
  }' >"$test_key_json_file"

  echo '{
  ":r": {"N" :"8"},
  ":p": {"S" : "some totally different stuff"}
 }' >"$test_item_json_file"

  local update_expression="SET info.rating = :r, info.plot = :p"

  test_count=$((test_count + 1))
  echo -n "Running test $test_count: Updating item..."
  dynamodb_update_item -n "$test_table_name" -k "$test_key_json_file" -e "$update_expression" -v "$test_item_json_file" 1>/dev/null
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Updating item failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  run_test "getting item without query" \
    "dynamodb_get_item -n $test_table_name -k $test_key_json_file  " \
    0

  run_test "getting item with query" \
    "dynamodb_get_item -n $test_table_name -k $test_key_json_file -q [Item.title,Item.year,Item.info.M.rating,Item.info.M.plot] " \
    0

  run_test "Deleting item from table" \
    "dynamodb_delete_item -n $test_table_name -k $test_key_json_file " \
    0

  echo "{ \"$test_table_name\" : $(<../movie_files/movies_0.json) }" >"$test_batch_json_file"

  run_test "Batch write items into table" \
    "dynamodb_batch_write_item -i $test_batch_json_file " \
    0

  echo '{
    "#n": "year"
    }' >"$test_attribute_names_json_file"

  echo '{
    ":v": {"N" :"2013"}
    }' >"$test_attributes_values_json_file"

  test_count=$((test_count + 1))
  echo -n "Running test $test_count: Querying table without projection expression..."
  local response
  response=$(dynamodb_query -n "$test_table_name" -k "#n=:v" -a "$test_attribute_names_json_file" -v "$test_attributes_values_json_file")
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Querying table failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  test_count=$((test_count + 1))
  echo -n "Running test $test_count: Querying table with projection expression..."
  local response
  response=$(dynamodb_query -n "$test_table_name" -k "#n=:v" -a "$test_attribute_names_json_file" \
    -v "$test_attributes_values_json_file" -p "title,info.plot")
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Querying table failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  echo '{
    "#n": "title"
    }' >"$test_attribute_names_json_file"

  echo '{
    ":v1": {"S" : "Age"}
    }' >"$test_attributes_values_json_file"

  test_count=$((test_count + 1))
  echo -n "Running test $test_count: Scanning table without projection expression..."
  local response
  response=$(dynamodb_scan -n "$test_table_name" -f "contains(#n,:v1)" -a "$test_attribute_names_json_file" -v "$test_attributes_values_json_file")
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Scanning table failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  test_count=$((test_count + 1))
  echo -n "Running test $test_count: Scanning table with projection expression..."
  local response
  response=$(dynamodb_scan -n "$test_table_name" -f "contains(#n,:v1)" -a "$test_attribute_names_json_file" \
    -v "$test_attributes_values_json_file" -p "title,info.plot")
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Scanning table failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  # bashsupport disable=GrazieInspection
  echo "{
    \"$test_table_name\" : {
        \"Keys\": [
          {
            \"year\": { \"N\": \"2013\" },
             \"title\": {\"S\": \"Prisoners\"}
          },
           {
            \"year\": { \"N\": \"2013\" },
             \"title\": {\"S\": \"Rush\"}
          },
           {
            \"year\": { \"N\": \"2012\" },
             \"title\": {\"S\": \"Pitch Perfect\"}
          }
      ]
     }
  }" >$test_requested_values_json_file

  run_test "Batch get items into table" \
    "dynamodb_batch_get_item -i $test_requested_values_json_file " \
    0

  export skip_tests=false
  run_test " deleting table" \
    "dynamodb_delete_table -n $test_table_name " \
    0

  # bashsupport disable=BP2001
  export mock_input="True"

  # bashsupport disable=BP2001
  export mock_input_array=(
    "testcli_scenario"
    "Frogs"
    "1979"
    "1"
    "Croak"
    "2"
    "Hop"
    "y"
    "1979"
    "n"
    "1974"
    "1979"
    "y"
    "y")
  # Enter a name for a new DynamoDB table:
  # Enter the title of a movie you want to add to the table:
  # What year was it released?
  # On a scale of 1 - 10, how do you rate it?
  # Summarize the plot for me:
  # what new rating would you give it?
  # You summarized the plot as 'Croak'.What would you say now?
  # Let's move on...do you want to get info about 'The Lord of the Rings: The Fellowship of the Ring'? (y/n)
  # Enter a year between 1972 and 2018:
  # Try another year? (y/n)
  # Enter a year between 1972 and 2018:
  # Enter another year:
  # Do you want to remove 'Frogs'? (y/n)
  # Do you want to delete the table 'testcli_scenario'? (y/n)

  {
    local current_directory
    current_directory=$(pwd)
    cd ..

    #    dynamodb_getting_started_movies
    run_test "dynamodb getting started with movies." \
      dynamodb_getting_started_movies \
      0
    # shellcheck disable=SC2164
    cd "$current_directory"
  }

  unset mock_input

  rm "$test_item_json_file"
  rm "$test_key_json_file"
  rm "$test_key_schema_json_file"
  rm "$test_attr_definitions_json_file"
  rm "$test_batch_json_file"
  rm "$test_attribute_names_json_file"
  rm "$test_attributes_values_json_file"
  rm "$test_requested_values_json_file"

 # shellcheck disable=SC2154
  echo "$test_succeeded_count tests completed successfully."
  echo "$test_failed_count tests failed."
}

main
