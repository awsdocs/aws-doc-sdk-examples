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

###############################################################################
# function iam_user_exists
#
# This function checks to see if the specified AWS Identity and Access Management (IAM) user already exists.
#
# Parameters:
#       $1 - The name of the IAM user to check.
#
# Returns:
#       0 - If the user already exists.
#       1 - If the user doesn't exist.
###############################################################################
function iam_user_exists() {
  local user_name
  user_name=$1

  # Check whether the IAM user already exists.
  # We suppress all output - we're interested only in the return code.

  errors=$(aws iam get-user \
    --user-name "$user_name" 2>&1 >/dev/null)

   if [[ ${?} -eq 0 ]]; then
     return 0 # 0 in Bash script means true.
   else
    if [[ $errors != *"error"*"(NoSuchEntity)"* ]]; then
      errecho "Error calling iam get-user $errors"
     fi

   return 1 # 1 in Bash script means false.
  fi
}

###############################################################################
# function create-create_iam_user
#
# This function creates the specified IAM user, unless
# it already exists.
#
# Parameters:
#       -u user_name  -- The name of the user to create.
#
#
# Returns:
#       0 - If successful.
#       1 - If it fails.
###############################################################################
function create_iam_user() {
  local user_name response
  local option OPTARG # Required to use getopts command in a function.

  # bashsupport disable=BP5008
  function usage() {
    echo "function create_iam_user"
    echo "Creates an WS Identity and Access Management (IAM) user. You must supply a user name:"
    echo "  -u user_name    The name of the user. It must be unique within the account."
    echo "  [-r region_code]    The code for an AWS Region in which the bucket is created."
    echo ""
  }

  # Retrieve the calling parameters.
  while getopts "u:h" option; do
    case "${option}" in
       u) user_name="${OPTARG}" ;;
       h)
        usage
        return 0
        ;;
      \?)
        ech o"Invalid parameter"
        usage
        return 1
        ;;
    esac
  done

 if [[ -z "$user_name" ]]; then
    errecho "ERROR: You must provide a user name with the -u parameter."
    usage
    return 1
  fi

  iecho "Parameters:\n"
  iecho "    User name:   $user_name"
  iecho ""

  # If the bucket already exists, we don't want to try to create it.
  if (iam_user_exists "$user_name"); then
    errecho "ERROR: A user with that name already exists in the account."
    return 1
  fi

  response=$(iam create-user \
    --user-name "$user_name")

  # shellcheck disable=SC2181
  if [[ ${?} -ne 0 ]]; then
    errecho "ERROR: AWS reports create-user operation failed.\n$response"
    return 1
  fi
}