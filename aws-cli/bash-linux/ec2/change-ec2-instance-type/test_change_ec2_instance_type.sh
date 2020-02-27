#!/usr/bin/env bash

###############################################################################
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
#
# You may not use this file except in compliance with the License. A copy of
# the License is located at http://aws.amazon.com/apache2.0/.
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.
###############################################################################

source ./awsdocs_general.sh
source ./change_ec2_instance_type.sh

function usage {
    echo "This script tests the change_ec2_instance_type function by calling the"
    echo "function in a variety of ways and checking the output. It converts the"
    echo "instance between types t2.nano and t2.micro."
    echo ""
    echo "Parameters:"
    echo ""
    echo "  -v  Verbose. Shows diagnostic messages about the tests as they run."
    echo "  -i  Interactive. Pauses the script between steps so you can browse"
    echo "      results in the AWS Management Console as they occur."
    echo ""
    echo "IMPORTANT: Running this test script creates an Amazon EC2 instance in"
    echo "   in your Amazon account that can incur charges. It is your"
    echo "   responsibility to ensure that no resources are left in your"
    echo "   account after the script completes. If an error occurs during the"
    echo "   operation of the script, then this instance can remain. You must"
    echo "   check for the instance and delete it manually to avoid charges."
}

# Set default values.
INTERACTIVE=false

# Retrieve the calling parameters
while getopts "ivh" OPTION; do
    case "${OPTION}"
    in
        i)
            INTERACTIVE=true
            VERBOSE=true
            ;;
        v)
            VERBOSE=true
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

if [ "$VERBOSE" == "true" ]; then iecho "Tests running in verbose mode."; fi
if [ "$INTERACTIVE" == "true" ]; then iecho "Tests running in interactive mode."; fi

iecho ""
iecho "***************SETUP STEPS******************"
    # First, get the AMI ID for the latest AMI that runs Amazon Linix 2.
    iecho -n "Retrieving the AMI ID for the latest Amazon Linux 2 AMI..."
    AMI_ID=$(aws ec2 describe-images \
                --owners 'amazon' \
                --filters 'Name=name,Values=amzn2-ami-hvm-2.0.????????-x86_64-gp2' 'Name=state,Values=available' \
                --query 'sort_by(Images, &CreationDate)[-1].[ImageId]' \
                --output 'text')
    if [ ${?} -ne 0 ]; then
        echo "ERROR: Unable to retrieve latest Amazon Linux 2 AMI ID: $AMI_ID"
        echo "Tests cancelled."
        return 1
    else
        iecho "retrieved $AMI_ID."
    fi

    # Now launch the instance as a t2.micro and capture its instance ID.
    # All other instance settings are left to default.
    iecho -n "Requesting new Amazon EC2 instance of type t2.micro..."
    EC2_INSTANCE_ID=$(aws ec2 run-instances \
                        --image-id "$AMI_ID" \
                        --instance-type t2.micro \
                        --query 'Instances[0].InstanceId' \
                        --output text)
    if [ ${?} -ne 0 ]; then
        echo "ERROR: Unable to launch EC2 instance: $EC2_INSTANCE_ID"
        echo "Tests cancelled."
        return 1
    else
        iecho "launched. ID:$EC2_INSTANCE_ID"
    fi

    iecho -n "Waiting for instance $EC2_INSTANCE_ID to exist..."
    aws ec2 wait instance-exists \
        --instance-id "$EC2_INSTANCE_ID"
    iecho "confirmed."

iecho "***************END OF SETUP*****************"
iecho ""


run_test "1. Missing mandatory -i parameter" \
         "change_ec2_instance_type" \
         1 \
         "ERROR: You must provide an instance id"

run_test "2. Missing mandatory -t parameter" \
         "change_ec2_instance_type -i abc" \
         1 \
         "ERROR: You must provide an instance type"

run_test "3. Using an instance ID that doesn't exist" \
         "change_ec2_instance_type -i abc -t t2.micro" \
         1 \
         "ERROR: I can't find the instance"

# Test changing to same type - we can do this while the instance is starting up.
run_test "4. Trying to change to same type" \
         "change_ec2_instance_type -v -i $EC2_INSTANCE_ID -t t2.micro" \
         1 \
         "ERROR: Can't change instance type to the same type"

iecho -n "Waiting for instance $EC2_INSTANCE_ID to reach running state..."
RESPONSE=$(aws ec2 wait instance-running --instance-id "$EC2_INSTANCE_ID")
if [[ ${?} -ne 0 ]]; then
    errecho "\nERROR: AWS reports that the Wait command failed.\n$RESPONSE"
    return 1
fi 
iecho "running."

# Test changing to t2.micro without -r : should still be in stopped state
run_test "5. Change to type t2.nano and do not restart" \
         "change_ec2_instance_type -f -i $EC2_INSTANCE_ID -t t2.nano" \
         0

         # Validate result was "t2.nano" and that it's in "stopped" state.
         get_instance_info "$EC2_INSTANCE_ID"
         if [ "$EXISTING_TYPE" != "t2.nano" ]; then
             test_failed "Unable to validate change. Should be t2.nano. Found $EXISTING_TYPE."
         fi
         if [ "$EXISTING_STATE" != "stopped" ]; then
             test_failed "Unable to validate state. Should be stopped. Found $EXISTING_STATE."
         fi

# Test changing back to t2.micro with -r : should now be in running state
run_test "6. Change to type t2.micro and restart" \
         "change_ec2_instance_type -f -r -i $EC2_INSTANCE_ID -t t2.micro" \
         0

         # Validate result was "t2.micro" and that it's in "running" state.
         get_instance_info "$EC2_INSTANCE_ID"
         if [ "$EXISTING_TYPE" != "t2.micro" ]; then
             test_failed "Unable to validate change. Should be t2.micro. Found $EXISTING_TYPE."
         fi
         if [ "$EXISTING_STATE" != "running" ]; then
             test_failed "Unable to validate state. Should be running. Found $EXISTING_STATE."
         fi

iecho ""
iecho "*************TEAR DOWN STEPS****************"
    iecho -n "Requesting termination of instance $EC2_INSTANCE_ID..."
# Delete and terminate the instance
    RESPONSE=$(aws ec2 terminate-instances \
                        --instance-ids "$EC2_INSTANCE_ID"
              )
    if [ ${?} -ne 0 ]; then
        errecho "**** ERROR ****"
        errecho "AWS reported a failure to terminate EC2 instance: $EC2_INSTANCE_ID"
        errecho "You must terminate the instance yourself using the AWS Management"
        errecho "Console or CLI commands. Failure to terminate the instance can" 
        errecho "result in charges to your AWS account.\n"
    else
        iecho "request accepted."
    fi

    iecho -n "Waiting for instance $EC2_INSTANCE_ID to terminate..."
    aws ec2 wait instance-terminated \
        --instance-id "$EC2_INSTANCE_ID"
    iecho "confirmed."
    if [[ ${?} -ne 0 ]]; then
        errecho "ERROR - AWS reports that Wait command failed."
        errecho "You must ensure that the instance terminated successfully yourself using the"
        errecho "AWS Management Console or CLI commands. Failure to terminate the instance can" 
        errecho "result in charges to your AWS account.\n"
        return 1
    fi 


iecho "************END OF TEAR DOWN****************"
iecho ""

echo "Tests completed successfully."
