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

#
# Purpose
#
# Demonstrates using the AWS SDK for C++ to create an IAM user, create an IAM role, and apply the role to the user.
#
# 1. Create a user.
# 2. Create a role.
# 3. Create an IAM policy.
# 4. Assume the new role using the AWS Security Token Service (STS).
# 5. List objects in the bucket (this should fail).
# 6. Attach the policy to the role.
# 7. List objects in the bucket (this should succeed).
# 8. Delete all the created resources.


get_input_result=""

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
      mock_input_array=("${mock_input_array[@]:1}")
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
#
# Returns:
#       0
###############################################################################
function clean_up() {
  if [ -z "${mock_input+x}" ]; then
    read -r get_input_result
  else
    if [ -n "${mock_input_array[*]}" ]; then
      get_input_result="${mock_input_array[0]}"
      mock_input_array=("${mock_input_array[@]:1}")
      echo -n "$get_input_result"
    else
      get_input_result="y"
      echo "MOCK_INPUT_ARRAY is empty" 1>&2
    fi
  fi
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
  local end=$2
  for ((i = 0; i < end; i++)); do
    echo -n "$1"
  done
  echo
}

# snippet-start:[aws-cli.bash-linux.iam.iam_create_user_assume_role]
###############################################################################
# function iam_create_user_assume_role
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
function iam_create_user_assume_role() {
  {
    if [ "$IAM_OPERATIONS_SOURCED" != "True" ]; then
       # shellcheck disable=SC1091
      source ./iam_operations.sh
    fi
  }

  echo_repeat "*" 88
  echo "Welcome to the IAM create user and assume role demo."
  echo
  echo "This demo will create an IAM user, create an IAM role, and apply the role to the user."
  echo_repeat "*" 88

  echo -n "Enter a name for a new IAM user: "
  get_input
  user_name=$get_input_result

 if (iam_create_user -u "$user_name"); then
     echo "Created demo IAM user named $user_name"
   else
     errecho "The user failed to create. This demo will exit."
     return 1
   fi

   access_key_file_name="test.pem"
   if (iam_create_user_access_key -u "$user_name" -f "$access_key_file_name"); then
     echo "Created access key file $access_key_file_name"
   else
     errecho "The access key file failed to create. This demo will exit."
     return 1
   fi

   key_name=$(cut -f 1 "$access_key_file_name")

   if (iam_delete_user -u "$user_name"); then
     echo "Deleted IAM user named $user_name"
   else
     errecho "The user failed to delete."
     return 1
   fi

  result=0
  return $result
}
# snippet-end:[aws-cli.bash-linux.iam.iam_create_user_assume_role]

###############################################################################
# function main
#
###############################################################################
function main() {
  iam_create_user_assume_role
}

# bashsupport disable=BP5001
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  main
fi

