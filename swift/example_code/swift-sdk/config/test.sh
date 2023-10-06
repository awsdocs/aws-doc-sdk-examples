#!/bin/bash

stack_name="swift-sdk-test-config-example-v1"

# Set up the test environment. If an error occurs, abort the test and return
# an appropriate status code.

cmd_output=$(aws cloudformation create-stack --template-body file://test-cfn.yaml --stack-name "$stack_name")
cmd_exit_code=$?

if [[ "$cmd_exit_code" -ne 0 ]]; then
    echo "Unable to create test environment. Is the AWS CLI installed and configured?"
    exit "$cmd_exit_code"
fi

# Give the stack time to show up. This probably should be monitoring for it to
# appear instead.

sleep 5

# Run the program and capture the output so we can verify that the results
# match our expectations.

cmd_output=$(swift run)
cmd_exit_code=$?

# Make sure the HTTP Host header matches the configuration's setting for
# us-east-1. If any of these checks fail, the exit code is at least 100.
# Each error then gets a specific value added to the exit code, letting the
# result code be used to see which error(s) occurred.
#
# * Region mismatch:        1
# * Retry mode mismatch:    2
#
# If a Region mismatch occurs but the retry mode matches, the exit code is
# 101. If the retry mode mismatches but everything else is correct, the exit
# code  is 102. If both the Region and retry mode mismatch, the exit code is
# 103.

config_exit_code="0"
if [[ "$cmd_output" != *"Host: s3.us-east-1.amazonaws.com"* ]]; then
    echo "Error: The HTTP Host should be s3.us-east-1.amazonaws.com but is not."
    config_exit_code=$(($config_exit_code + "1"))
fi
if [[ "$cmd_output" != *"cfg/retry-mode#adaptive"* ]]; then
    echo "Error: The retry mode is not set to 'adaptive' as expected."
    config_exit_code=$(($config_exit_code + "2"))
fi
if [[ "$config_exit_code" -ne "0" ]]; then
    config_exit_code=$(($config_exit_code + "100"))
fi

# Remove the test bucket.

cmd_output=$(aws s3 rb s3://$stack_name)
cmd_exit_code=$?

if [[ "$cmd_exit_code" -ne 0 ]]; then
    echo "Error deleting the test bucket '$stack_name'."
    # exit $cmd_exit_code
fi

# Tear down the test environment.

cmd_output=$(aws cloudformation delete-stack --stack-name "$stack_name")
cmd_exit_code=$?

if [[ "$cmd_exit_code" -ne 0 ]]; then
    echo "Error removing the test stack '$stack_name'."
    # exit $cmd_exit_code
fi

# Return the configuration test status code if there is one. Otherwise return
# the most recent command exit code.

if [[ "$config_exit_code" -ne "0" ]]; then
    exit "$config_exit_code"
fi

exit "$cmd_exit_code"