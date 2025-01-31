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
# This function runs the Amazon EC2 examples' tests.
###############################################################################
function main() {
  # bashsupport disable=BP5006,BP2001
  export INTERACTIVE=false
  # bashsupport disable=BP5006,BP2001
  export VERBOSE=false

  source ./include_tests.sh
  {
    local current_directory
    current_directory=$(pwd)
    cd ..

    # shellcheck disable=SC1091
    source ./ec2_operations.sh

    # shellcheck disable=SC1091
    source ./get_started_with_ec2_instances.sh
    # shellcheck disable=SC2164
    cd "$current_directory"
  }

  ###############################################################################
  # function usage
  #
  # This function prints usage information for the script.
  ###############################################################################
  function usage() {
    echo "This script tests Amazon EC2 operations in the AWS CLI."
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
    # bashsupport disable=BP5006
    case "${option}" in
      i)
        # bashsupport disable=BP5006
        INTERACTIVE=true
        # bashsupport disable=BP5006
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
  local test_key_name
  test_key_name=$(generate_random_name "test-key")
  local temp_dir
  temp_dir=$(mktemp -d)
  local key_file_name="$temp_dir/${test_key_name}.pem"
  local test_sg_name
  test_sg_name=$(generate_random_name "test-sg")
  local test_sg_description="Test security group"

  iecho "**************END OF STEPS******************"

  run_test "Creating a new EC2 key pair" \
    "ec2_create_keypair -n $test_key_name -f $key_file_name" \
    0

  run_test "Creating a new EC2 key pair missing name" \
    "ec2_create_keypair  -f $key_file_name" \
    1

  run_test "Creating a new EC2 key pair missing file" \
    "ec2_create_keypair -n $test_key_name" \
    1

  export exit_on_failure=false
  run_test "Describing EC2 key pairs" \
    "ec2_describe_key_pairs" \
    0

local security_group_id
  security_group_id=$(ec2_create_security_group -n "$test_sg_name" -d "$test_sg_description")
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "ec2_create_security_group failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi

  echo "Verifying security group was added."
  local security_group
  security_group=$(aws ec2 describe-security-groups --group-names "$test_sg_name" --query 'SecurityGroups[0].GroupName' --output text)
  if [ "$security_group" != "$test_sg_name" ]; then
    test_failed "Error: Failed to create the EC2 security group $test_sg_name"
  else
    ((test_succeeded_count++))
  fi
  # Test case: Creating a new EC2 security group without a name
  run_test "Creating a new EC2 security group without a name" \
    "ec2_create_security_group -d '$test_sg_description'" \
    1

  # Test case: Creating a new EC2 security group with an empty name
  run_test "Creating a new EC2 security group with an empty name" \
    "ec2_create_security_group -n '' -d '$test_sg_description'" \
    1

  run_test "Describing the newly created EC2 security group" \
    "ec2_describe_security_groups -g $security_group_id" \
    0

  run_test "Describing all security groups" \
    "ec2_describe_security_groups" \
    0

  local public_ip
  public_ip=$(curl -s http://checkip.amazonaws.com)

  run_test "Authorizing security group ingress" \
    "ec2_authorize_security_group_ingress -g $security_group_id -i $public_ip -p tcp -f 22 -t 22" \
    0

  local parameters
  echo "Testing ssm_get_parameters_by_path"
  parameters="$(ssm_get_parameters_by_path -p "/aws/service/ami-amazon-linux-latest")"
  error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "ssm_get_parameters_by_path failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi

  local image_ids=""
  mapfile -t parameters <<<"$parameters"
  # bashsupport disable=BP2001
  for image_name_and_id in "${parameters[@]}"; do
    IFS=$'\t' read -ra values <<<"$image_name_and_id"
    if [[ "${values[0]}" == *"amzn2"* ]]; then
      image_ids+="${values[1]} "
    fi
  done

  echo "Testing ec2_describe_images"
  local images
  images="$(ec2_describe_images -i "$image_ids")"
  error_code=${?}
  if [[ $error_code -ne 0 ]]; then
    test_failed "ec2_describe_images failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi
  # bashsupport disable=BP2001
  export list_result
  new_line_and_tab_to_list "$images"
  images=("${list_result[@]}")

  local architecture=${images[1]}
  local image_id=${images[2]}

  echo "Testing ec2_describe_instance_types"
  local instance_types
  instance_types="$(ec2_describe_instance_types -a "${architecture}" -t "*.micro,*.small")"
  error_code=${?}
  if [[ $error_code -ne 0 ]]; then
    test_failed "ec2_describe_instance_types failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi

  mapfile -t instance_types <<<"$instance_types"
  local instance_type=${instance_types[0]}

  echo "Testing ec2_run_instances"
  local instance_id
  instance_id=$(ec2_run_instances -i "$image_id" -t "$instance_type" -k "$test_key_name" -s "$security_group_id")
  error_code=${?}
  if [[ $error_code -ne 0 ]]; then
    test_failed "ec2_run_instances failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi

  run_test "Waiting for EC2 running" \
    "ec2_wait_for_instance_running -i $instance_id" \
    0

  run_test "Stopping the EC2 instance" \
    "ec2_stop_instances -i $instance_id" \
    0

  run_test "Waiting for EC2 stopped" \
    "ec2_wait_for_instance_stopped -i $instance_id" \
    0

  run_test "Starting the EC2 instance" \
    "ec2_start_instances -i $instance_id" \
    0

  run_test "Waiting for EC2 running" \
    "ec2_wait_for_instance_running -i $instance_id" \
    0

  echo "Allocating an elastic address"
  local result
  result=$(ec2_allocate_address -d vpc)
  error_code=${?}
  if [[ $error_code -ne 0 ]]; then
    test_failed "ec2_allocate_address failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi

  local elastic_ip allocation_id
  elastic_ip=$(echo "$result" | awk '{print $1}')
  allocation_id=$(echo "$result" | awk '{print $2}')

  echo "Associating an elastic address"
  local association_id
  association_id=$(ec2_associate_address -i "$instance_id" -a "$allocation_id")
  error_code=${?}
  if [[ $error_code -ne 0 ]]; then
    test_failed "ec2_associate_address failed with error code.  $error_code"
  else
    echo "OK"
    ((test_succeeded_count++))
  fi

  run_test "Disassociate an elastic address" \
    "ec2_disassociate_address -a $association_id" \
    0
  run_test "releasing an elastic address" \
    "ec2_release_address -a $allocation_id" \
    0

  run_test "Terminating the Ec2 instance" \
    "ec2_terminate_instances  -i $instance_id" \
    0

  run_test "Waiting for EC2 instance to terminate" \
    "ec2_wait_for_instance_terminated -i $instance_id" \
    0

  run_test "Deleting the EC2 key pair" \
    "ec2_delete_keypair -n $test_key_name" \
    0
  rm -f "$key_file_name"

  run_test "Deleting the EC2 security group" \
    "ec2_delete_security_group -i $security_group_id" \
    0

 # bashsupport disable=BP2001
  export mock_input="True"
  # bashsupport disable=BP2001
  export mock_input_array_index=0

  # bashsupport disable=BP2001
  export mock_input_array=(
    "bash-tests"
    "y"
    "bash-tests"
    ""
    "1"
    "1"
    "n"
    ""
    "n"
    ""
    "n"
    ""
    "n"
    ""
    "y"
  )
  export mock_input_array_index=0
  # Enter a unique name for your key:
  # Do you want to list some of your key pairs? (y/n)
  # Enter a unique name for your security group:
  # press return to add this rule to your security group.
  # Please enter the number of the AMI you want to use:
  # Which one do you want to use?
  # Do you want to connect now? (y/n)
  # Press Enter when you're ready to continue the demo:
  # Do you want to connect now? (y/n)
  # Press Enter when you're ready to continue the demo:
  # Do you want to connect now? (y/n)
  # Press Enter when you're ready to continue the demo:
  # Do you want to connect now? (y/n)
  # Press Enter when you're ready to continue the demo:
  # Do you want to delete the resources created in this demo: (y/n)

  get_started_with_ec2_instances
  local error_code=${?}

  if [[ $error_code -ne 0 ]]; then
    test_failed "get_started_with_ec2_instances failed with error code.  $error_code"
  else
    echo "OK"
    test_succeeded_count=$((test_succeeded_count + 1))
  fi

  unset mock_input

  # shellcheck disable=SC2154
  echo "$test_succeeded_count tests completed successfully."

  # shellcheck disable=SC2154
  echo "$test_failed_count tests failed."
}

main
