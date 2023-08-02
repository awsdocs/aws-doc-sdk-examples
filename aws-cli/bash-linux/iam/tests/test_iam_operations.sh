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

function main() {
  source ./test_general.sh
  {
    current_directory=$(pwd)
    cd ..
    source ./iam_operations.sh
    cd $current_directory
  }

  function usage() {
    echo "This script tests Amazon IAM operations in the AWS CLI."
     echo ""
    echo "To pause the script between steps so you can see the results in the"
    echo "AWS Management Console, include the parameter -i."
    echo ""
    echo "IMPORTANT: Running this script creates resources in your Amazon"
    echo "   account that can incur charges. It is your responsibility to"
    echo "   ensure that no resources are left in your account after the script"
    echo "   completes. If an error occurs during the operation of the script,"
    echo "   then resources can remain that you might need to delete manually."
  }

  # Set default values.
  INTERACTIVE=false
  VERBOSE=false

  # Retrieve the calling parameters
  while getopts "ivh" OPTION; do
    case "${OPTION}" in
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
  REGION="us-east-1"
#  FILENAME1=$(generate_random_name s3clitestfile)
#  FILENAME2=$(generate_random_name s3clitestfile)
#
  iecho "user_name=$user_name"
#  iecho "REGION=$REGION"
#  iecho "FILENAME1=$FILENAME1"
#  iecho "FILENAME2=$FILENAME2"
#
  iecho "**************END OF STEPS******************"

  local test_count=1
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

# run_test "$test_count. Creating access key without a file name" \
#    "iam_create_user_access_key -u $user_name " \
#    0
#  test_count=$((test_count + 1))

  local access_key_file_name="test.pem"

 run_test "$test_count. Creating access key without a user name" \
    "iam_create_user_access_key -f $access_key_file_name " \
    1
  test_count=$((test_count + 1))

  run_test "$test_count. Creating access key" \
    "iam_create_user_access_key -u $user_name -f $access_key_file_name " \
    0
  test_count=$((test_count + 1))

  local key_name
  key_name=$(cut -f 2 "$access_key_file_name")

  run_test "$test_count. deleting access key"  \
    "iam_delete_access_key -u $user_name -k $key_name" \
    0
  test_count=$((test_count + 1))

  run_test "$test_count. deleting user"  \
    "iam_delete_user -u $user_name " \
    0
  test_count=$((test_count + 1))

#  run_test "5. Copying local file (copy of this script) to bucket" \
#    "copy_file_to_bucket $BUCKETNAME ./$0 $FILENAME1" \
#    0
#
#  run_test "6. Duplicating existing file in bucket" \
#    "copy_item_in_bucket $BUCKETNAME $FILENAME1 $FILENAME2" \
#    0
#
#  run_test "7. Listing contents of bucket" \
#    "list_items_in_bucket $BUCKETNAME" \
#    0
#
#  run_test "8. Deleting first file from bucket" \
#    "delete_item_in_bucket $BUCKETNAME $FILENAME1" \
#    0
#
#  run_test "9. Deleting second file from bucket" \
#    "delete_item_in_bucket $BUCKETNAME $FILENAME2" \
#    0
#
#  run_test "10. Deleting bucket" \
#    "delete_bucket $BUCKETNAME" \
#    0
#
#  mock_input="True"
#  mock_input_array=("README.md" "y" "y" "y")
#
#  run_test "11. s3 getting started scenario" \
#    s3_getting_started \
#    0
#
#  unset mock_input

  echo "$test_count tests completed successfully."
}

main
