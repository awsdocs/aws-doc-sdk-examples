#!/bin/bash
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#######################################
# description
# Arguments:
#  None
#######################################
function usage() {
  echo "This script tests the change_ec2_instance_type function by calling the"
  echo "function in a variety of ways and checking the output. It converts the"
  echo "instance between types t2.nano and t2.micro."
  echo ""
  echo "Parameters:"
  echo ""
  echo "  -v  Verbose. Shows diagnostic messages about the tests as they run."
  echo "  -i  Interactive. Pauses the script between steps so you can browse"
  echo "      the results in the AWS Management Console as they occur."
  echo ""
  echo "IMPORTANT: Running this test script creates an Amazon EC2 instance in"
  echo "   your Amazon account that can incur charges. It is your responsibility"
  echo "   to ensure that no resources are left in your account after the script"
  echo "   completes. If an error occurs during the operation of the script,this"
  echo "   instance can remain. Check for the instance and delete it manually to"
  echo "   avoid charges."
}

###############################################################################
# function main
#
# This function runs the Amazon EC2 change instance type tests.
###############################################################################
function main() {
  local option
  source ./awsdocs_general.sh

  source ./change_ec2_instance_type.sh

  # Set default values.
  interactive=false
  verbose=false

  # Retrieve the calling parameters.
  while getopts "ivh" option; do
    case "${option}" in
      i)
        interactive=true
        verbose=true
        ;;
      v)
        verbose=true
        ;;
      h)
        usage
        return 0
        ;;
      \?)
        echo "Invalid parameter."
        usage
        return 1
        ;;
    esac
  done

  if [ "$verbose" == "true" ]; then iecho "Tests running in verbose mode."; fi

  if [ "$interactive" == "true" ]; then iecho "Tests running in interactive mode."; fi

  iecho ""

  iecho "***************SETUP STEPS******************"

  # First, get the AMI ID for the one running the latest Amazon Linux 2.
  iecho -n "Retrieving the AMI ID for the latest Amazon Linux 2 AMI..."

  local ami_id
  ami_id=$(aws ec2 describe-images \
    --owners 'amazon' \
    --filters 'Name=name,Values=amzn2-ami-hvm-2.0.*-x86_64-gp2' 'Name=state,Values=available' \
    --query 'sort_by(Images, &CreationDate)[-1].[ImageId]' \
    --output 'text')

  # shellcheck disable=SC2181
  if [ ${?} -ne 0 ]; then
    echo "ERROR: Unable to retrieve latest Amazon Linux 2 AMI ID: $ami_id"
    echo "Tests canceled."
    return 1
  else
    iecho "retrieved $ami_id."
  fi

  # Now launch the instance as a t2.micro and capture its instance ID.
  # All other instance settings are left to default.
  iecho -n "Requesting new Amazon EC2 instance of type t2.micro..."

  local ec2_instance_id
  ec2_instance_id=$(aws ec2 run-instances \
    --image-id "$ami_id" \
    --instance-type t2.micro \
    --query 'Instances[0].InstanceId' \
    --output text)

  # shellcheck disable=SC2181
  if [ ${?} -ne 0 ]; then
    echo "ERROR: Unable to launch EC2 instance: $ec2_instance_id"
    echo "Tests canceled."
    return 1
  else
    iecho "launched. ID:$ec2_instance_id"
  fi

  iecho -n "Waiting for instance $ec2_instance_id to exist..."

  aws ec2 wait instance-exists \
    --instance-id "$ec2_instance_id"

  iecho "confirmed."

  iecho "***************END OF SETUP*****************"

  iecho ""

  run_test "1. Missing mandatory -i parameter" \
    "change_ec2_instance_type" \
    1 \
    "ERROR: You must provide an instance id."

  run_test "2. Missing mandatory -t parameter" \
    "change_ec2_instance_type -i abc" \
    1 \
    "ERROR: You must provide an instance type."

  run_test "3. Using an instance ID that doesn't exist" \
    "change_ec2_instance_type -i abc -t t2.micro" \
    1 \
    "ERROR: I can't find the instance."

  # Test changing to the same type. We can do this while the instance is starting up.
  run_test "4. Trying to change to same type" \
    "change_ec2_instance_type -v -i $ec2_instance_id -t t2.micro" \
    1 \
    "ERROR: Can't change instance type to the same type."

  iecho -n "Waiting for instance $ec2_instance_id to reach running state..."

  aws ec2 wait instance-running --instance-id "$ec2_instance_id" || {
    errecho "\nERROR: AWS reports that the Wait command failed.\n$response"
    return 1
  }

  iecho "running."

  # Test changing to t2.micro without -r : should still be in stopped state.
  run_test "5. Changing to type t2.nano without restart" \
    "change_ec2_instance_type -f -i $ec2_instance_id -t t2.nano" \
    0

  # Validate result was "t2.nano" and that it's in "stopped" state.
  get_instance_info "$ec2_instance_id"

  if [ "$existing_type" != "t2.nano" ]; then
    test_failed "Unable to validate change. Should be t2.nano. Found $existing_type."
  fi

  if [ "$existing_state" != "stopped" ]; then
    test_failed "Unable to validate state. Should be stopped. Found $existing_state."
  fi

  # Test changing back to t2.micro with -r. Should now be in running state
  run_test "6. Changing to type t2.micro with restart" \
    "change_ec2_instance_type -f -r -i $ec2_instance_id -t t2.micro" \
    0

  # Validate result was "t2.micro" and that it's in "running" state.
  get_instance_info "$ec2_instance_id"

  if [ "$existing_type" != "t2.micro" ]; then
    test_failed "Unable to validate change. Should be t2.micro. Found $existing_type."
  fi

  if [ "$existing_state" != "running" ]; then
    test_failed "Unable to validate state. Should be running. Found $existing_state."
  fi

  iecho ""

  iecho "*************TEAR DOWN STEPS****************"

  iecho -n "Requesting termination of instance $ec2_instance_id..."

  # Delete and terminate the instance.
  aws ec2 terminate-instances \
    --instance-ids "$ec2_instance_id" >> /dev/null

  # shellcheck disable=SC2181
  if [ ${?} -ne 0 ]; then
    errecho "**** ERROR ****"
    errecho "AWS reported a failure to terminate EC2 instance: $ec2_instance_id"
    errecho "You must terminate the instance using the AWS Management Console"
    errecho "or CLI commands. Failure to terminate the instance can result in"
    errecho "charges to your AWS account.\n"
  else
    iecho "request accepted."
  fi

  iecho -n "Waiting for instance $ec2_instance_id to terminate..."

  aws ec2 wait instance-terminated \
    --instance-id "$ec2_instance_id" || {
    errecho "ERROR - AWS reports that Wait command failed."
    errecho "You must ensure that the instance terminated successfully yourself using the"
    errecho "AWS Management Console or CLI commands. Failure to terminate the instance can"
    errecho "result in charges to your AWS account.\n"
    return 1
  }

  iecho "confirmed."

  iecho "************END OF TEAR DOWN****************"

  iecho ""

  echo "Tests completed successfully."

}

main "$@"
