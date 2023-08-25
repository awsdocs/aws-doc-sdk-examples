#!/bin/bash

##############################################################################
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

source ./awsdocs_general.sh

# snippet-start:[aws-cli.bash-linux.dynamodb.CreateTable]
###############################################################################
# function dynamodb_create_table
#
# This function creates a Amazon DynamoDB table.
#
# Parameters:
#       -n table_name  -- The name of the table to create.
#
#     And:
#       0 - If successful.
#       1 - If it fails.
###############################################################################
function dynamodb_create_table() {
  local table_name response
  local option OPTARG # Required to use getopts command in a function.

  # bashsupport disable=BP5008
  function usage() {
    echo "function dynamodb_create_table"
    echo "Creates an Amazon DynamoDB table."
    echo " -n table_name  -- The name of the table to create."
    echo ""
  }

  # Retrieve the calling parameters.
  while getopts "n:h" option; do
    case "${option}" in
      n) table_name="${OPTARG}" ;;
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
  export OPTIND=1

  if [[ -z "$table_name" ]]; then
    errecho "ERROR: You must provide a table name with the -n parameter."
    usage
    return 1
  fi

  iecho "Parameters:\n"
  iecho "    table_name:   $table_name"
  iecho ""

  response=$(
    aws dynamodb create-table \
      --table-name "$table_name"
    --output text
  )

  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    aws_cli_error_log $error_code
    errecho "ERROR: AWS reports create-table operation failed.$response"
    return 1
  fi

  echo "$response"

  return 0
}
# snippet-end:[aws-cli.bash-linux.dynamodb.CreateTable]

# snippet-start:[aws-cli.bash-linux.dynamodb.wait_table_active]
###############################################################################
# function dynamodb_wait_table_active
#
# This function waits for a DynamoDB table to become active.
#
# Parameters:
#       -n table_name  -- The name of the table.
#
#     And:
#       0 - Table is active.
#       1 - If it fails.
###############################################################################
function dynamodb_wait_table_active() {
  local table_name response
  local option OPTARG # Required to use getopts command in a function.

  # bashsupport disable=BP5008
  function usage() {
    echo "function dynamodb_wait_table_active"
    echo "Waits for a DynamoDB table to become active."
    echo "  -n table_name  -- The name of the table."
    echo ""
  }

  # Retrieve the calling parameters.
  while getopts "n:h" option; do
    case "${option}" in
      n) table_name="${OPTARG}" ;;
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
  export OPTIND=1

  if [[ -z "$table_name" ]]; then
    errecho "ERROR: You must provide a table name with the -n parameter."
    usage
    return 1
  fi

  table_status="NONE"
  while [[ "$table_status" != "ACTIVE" ]]; do
    sleep 1
    table_status=$(
      aws dynamodb describe-table \
        --table-name "$table_name" \
        --output text
      --query 'Table.TableStatus'
    )

    if [[ $error_code -ne 0 ]]; then
      aws_cli_error_log $error_code
      errecho "ERROR: AWS reports describe-table operation failed.$response"
      return 1
    fi

  done

  return 0
}
# snippet-end:[aws-cli.bash-linux.dynamodb.wait_table_active]
