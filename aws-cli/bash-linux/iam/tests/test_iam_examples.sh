#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

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

    source ./iam_operations.sh

    source ./iam_create_user_assume_role_scenario.sh
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
  local user_name
  user_name=$(generate_random_name iamtestcli)
  iecho "user_name=$user_name"
  iecho "**************END OF STEPS******************"

  local test_count=0

  run_test "$test_count Test if non-existing user is exists" \
    "iam_user_exists $user_name " \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Creating user with missing username" \
    "iam_create_user " \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Creating user with valid parameters" \
    "iam_create_user -u $user_name " \
    0

  local user_arn="$test_command_response"
  test_count=$((test_count + 1))

  local user_arn="$test_command_response"

  run_test "$test_count. Test if existing user is exists" \
    "iam_user_exists $user_name " \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. Creating user with duplicate name" \
    "iam_create_user -u $user_name " \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Listing users" \
    "iam_list_users " \
    0
  test_count=$((test_count + 1))

  local user_values
  IFS=$'\t ' read -r -a user_values <<<"$test_command_response"
  if [[ "${#user_values[@]}" -lt "2" ]]; then
    test_failed "Listing users returned less than 2 users."
  fi

  local access_key_file_name="test.pem"

  run_test "$test_count. Creating access key without a username" \
    "iam_create_user_access_key -f $access_key_file_name " \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Creating access key with file" \
    "iam_create_user_access_key -u $user_name -f $access_key_file_name " \
    0
  test_count=$((test_count + 1))

  local key_name1
  key_name1=$(cut -f 2 "$access_key_file_name")

  rm $access_key_file_name

  run_test "$test_count. Creating access key without file" \
    "iam_create_user_access_key -u $user_name " \
    0
  test_count=$((test_count + 1))
  local access_key_values
  IFS=$'\t ' read -r -a access_key_values <<<"$test_command_response"
  local key_name2=${access_key_values[0]}

  run_test "$test_count. Listing access keys without user" \
    "iam_list_access_keys" \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Listing access keys" \
    "iam_list_access_keys -u $user_name " \
    0
  test_count=$((test_count + 1))

  IFS=$'\t ' read -r -a access_key_values <<<"$test_command_response"
  if [[ "${#access_key_values[@]}" -ne "2" ]]; then
    test_failed "Listing access keys returned incorrect number of keys."
  fi

  local assume_role_policy_document="{
    \"Version\": \"2012-10-17\",
    \"Statement\": [{
        \"Effect\": \"Deny\",
        \"Principal\": {\"AWS\": \"$user_arn\"},
        \"Action\": \"sts:AssumeRole\"
        }]
    }"

  run_test "$test_count. Creating role with missing name" \
    "iam_create_role -p $assume_role_policy_document " \
    1
  test_count=$((test_count + 1))
  local test_role_name
  test_role_name=$(generate_random_name iamtestcli)
  run_test "$test_count. Creating role with missing policy document" \
    "iam_create_role -n $test_role_name " \
    1
  test_count=$((test_count + 1))

  # Wait for user to be created.
  sleep 10

  # run_test seems to have issues with the policy document being passed in as a string.
  iam_create_role -n "$test_role_name" -p "$assume_role_policy_document" 1>/dev/null
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Creating role with correct parameters failed with error code"
  fi
  test_count=$((test_count + 1))

  local policy_name
  policy_name=$(generate_random_name "iamtestcli")
  local policy_document="{
                \"Version\": \"2012-10-17\",
                \"Statement\": [{
                    \"Effect\": \"Deny\",
                    \"Action\": \"s3:ListAllMyBuckets\",
                    \"Resource\": \"arn:aws:s3:::*\"}]}"

  local policy_arn
  policy_arn=$(iam_create_policy -n "$policy_name" -p "$policy_document")
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "Creating role with policy failed."
  fi
  test_count=$((test_count + 1))

  run_test "$test_count. Attaching policy to role" \
    "iam_attach_role_policy -n $test_role_name -p $policy_arn " \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. Detaching policy from role" \
    "iam_detach_role_policy -n $test_role_name -p $policy_arn " \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. Deleting policy" \
    "iam_delete_policy -n $policy_arn " \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. Deleting role" \
    "iam_delete_role -n $test_role_name " \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. deleting access key" \
    "iam_delete_access_key -u $user_name -k $key_name1" \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. deleting access key" \
    "iam_delete_access_key -u $user_name -k $key_name2" \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. deleting user" \
    "iam_delete_user -u $user_name " \
    0
  test_count=$((test_count + 1))

  # bashsupport disable=BP2001
  export mock_input="True"

  # bashsupport disable=BP2001
  export mock_input_array=("iamtestcli_scenario")

  run_test "$test_count. iam assume role scenario" \
    iam_create_user_assume_role \
    0

  unset mock_input

  echo "$test_count tests completed successfully."
}

main
