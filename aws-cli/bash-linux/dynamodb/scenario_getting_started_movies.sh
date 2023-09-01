#!/bin/bash
# bashsupport disable=BP2002

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

#
# Purpose
#
# Demonstrates using the AWS SDK for C++ to create an IAM user, create an IAM role, and apply the role to the user.
#
# 1. Create a user.
# 2. Create a role.
# 3. Create an IAM policy allowing the user to list Amazon S3 buckets.
# 4. Attach the IAM policy to the role.
# 6. Attempt to list the buckets using the new user's credentials. This should fail.
# 7. Let the new user assume the created role.
# 8. List objects in the bucket using the assumed role's credentials. This should succeed.
# 9. Delete all the created resources.

###############################################################################
# function get_input
#
# This function gets user input from the command line.
#
# Outputs:
#   User input to stdout.
#
# Returns:
#       0
###############################################################################
function get_input() {

  if [ -z "${mock_input+x}" ]; then
    read -r get_input_result
  else

    if [ -n "${mock_input_array[*]}" ]; then
      get_input_result="${mock_input_array[0]}"
      # bashsupport disable=BP2001
      # shellcheck disable=SC2206
      export mock_input_array=(${mock_input_array[@]:1})
      echo -n "$get_input_result"
    else
      get_input_result="y"
      echo "MOCK_INPUT_ARRAY is empty" 1>&2
    fi
  fi
}

###############################################################################
# function clean_up
#
# This function cleans up the created resources.
#
# Returns:
#       0 - If successful.
#       1 - If an error occurred.
###############################################################################
function clean_up() {
  local result=0
  local table_name=$1

  if [ -n "$table_name" ]; then
    if (dynamodb_delete_table -n "$table_name"); then
      echo "Deleted DynamoDB table named $table_name"
    else
      errecho "The table failed to delete."
      result=1
    fi
  fi

  return $result
}

###############################################################################
# function yes_no_input
#
# This function requests a yes/no answer from the user, following to a prompt.
#
# Parameters:
#       $1 - The prompt.
#
# Returns:
#       0 - If yes.
#       1 - If no.
###############################################################################
function yes_no_input() {
  if [ -z "$1" ]; then
    echo "Internal error yes_no_input"
    return 1
  fi

  local index=0
  local response="N"
  while [[ $index -lt 10 ]]; do
    index=$((index + 1))
    echo -n "$1"
    get_input
    response=$(echo "$get_input_result" | tr '[:upper:]' '[:lower:]')
    if [ "$response" = "y" ] || [ "$response" = "n" ]; then
      break
    else
      echo -e "\nPlease enter or 'y' or 'n'."
    fi
  done

  echo

  if [ "$response" = "y" ]; then
    return 0
  else
    return 1
  fi
}

###############################################################################
# function echo_repeat
#
# This function prints a string 'n' times to stdout.
#
# Parameters:
#       $1 - The string.
#       $2 - Number of times to print the string.
#
# Outputs:
#   String 'n' times to stdout.
#
# Returns:
#       0
###############################################################################
function echo_repeat() {
  local end=$2 i
  for ((i = 0; i < end; i++)); do
    echo -n "$1"
  done
  echo
}

# snippet-start:[aws-cli.bash-linux.dynamodb.scenario_getting_started_movies]
###############################################################################
# function dynamodb_getting_started_movies
#
# Scenario to create an IAM user, create an IAM role, and apply the role to the user.
#
#     "IAM access" permissions are needed to run this code.
#     "STS assume role" permissions are needed to run this code. (Note: It might be necessary to
#           create a custom policy).
#
# Returns:
#       0 - If successful.
#       1 - If an error occurred.
###############################################################################
function dynamodb_getting_started_movies() {
  source ./dynamodb_operations.sh



  local table_name
  echo -n "Enter a name for a new DynamoDB table: "
  get_input
  table_name=$get_input_result

  local provisioned_throughput="ReadCapacityUnits=5,WriteCapacityUnits=5"
  local key_schema_json_file="dynamodb_key_schema.json"
  local attribute_definitions_json_file="dynamodb_attr_def.json"

 echo '[
  {"AttributeName": "year", "KeyType": "HASH"},
   {"AttributeName": "title", "KeyType": "RANGE"}
  ]' >"$key_schema_json_file"

  echo '[
  {"AttributeName": "year", "AttributeType": "N"},
   {"AttributeName": "title", "AttributeType": "S"}
  ]' >"$attr_definitions_json_file"

  dynamodb_create_table -n "$table_name" -a "$attribute_definitions_json_file" -s "$key_schema_json_file" -p "$provisioned_throughput"

  rm -f "$key_schema_json_file"
  rm -f "$attr_definitions_json_file"
  
  # shellcheck disable=SC2181
  if [[ ${?} == 0 ]]; then
    echo "Created a DynamoDB table named $table_name"
  else
    errecho "The table failed to create. This demo will exit."
    return 1
  fi



  clean_up "$table_name"

  # shellcheck disable=SC2181
  if [[ ${?} -ne 0 ]]; then
    result=1
  fi

  return $result
}
# snippet-end:[aws-cli.bash-linux.dynamodb.scenario_getting_started_movies]

###############################################################################
# function main
#
###############################################################################
function main() {
  get_input_result=""

  dynamodb_getting_started_movies
}

# bashsupport disable=BP5001
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  main
fi
