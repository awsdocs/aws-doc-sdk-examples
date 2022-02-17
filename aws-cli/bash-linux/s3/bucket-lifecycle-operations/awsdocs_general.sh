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
#
# This script contains general purpose functions that are used throughout 
# the AWS Command Line Interface (AWS CLI) code samples that are maintained
# in the repo at https://github.com/awsdocs/aws-doc-sdk-examples
#
# They are intended to abstract functionality that is required for the tests
# to work without cluttering up the code. The intent is to ensure the purpose
# of the code is clear.

# Set global defaults:
VERBOSE=false

###############################################################################
# function run_test
#
# This function is used to perform a command and compare its output to both
# the expected error code and the expected output string. If there isn't a 
# match, then the function invokes the test_failed function.
###############################################################################
function run_test {
    local DESCRIPTION COMMAND EXPECTED_ERR_CODE EXPECTED_OUTPUT RESPONSE
    
    DESCRIPTION="$1"
    COMMAND="$2"
    EXPECTED_ERR_CODE="$3"
    if [[ -z "$4" ]]; then EXPECTED_OUTPUT="$4"; else EXPECTED_OUTPUT=""; fi
    
    iecho -n "Running test: $DESCRIPTION..."
    RESPONSE="$($COMMAND)"
    ERR="${?}"

    # Check to see if we got the expected error code.
    if [[ "$EXPECTED_ERR_CODE" -ne "$ERR" ]]; then
        test_failed "The test \"$DESCRIPTION\" returned an unexpected error code: $ERR"
    fi

    #now check the error message, if we provided other than "".
    if [[ -n "$EXPECTED_OUTPUT" ]]; then
        MATCH=$(echo "$RESPONSE" | grep "$EXPECTED_OUTPUT")
        # If there was no match (it's an empty string), then fail.
        if [[ -z "$MATCH" ]]; then
            test_failed "The test \"$DESCRIPTION\" returned an unexpected output: $RESPONSE"
        fi
    fi
    
    iecho "OK"
    ipause
}

###############################################################################
# function test_failed
#
# This function is used to terminate a failed test and to warn the customer
# about possible undeleted resources that could incur costs to their account.
###############################################################################

function test_failed {
    
    errecho ""
    errecho "===TEST FAILED==="
    errecho "$@"
    errecho ""
    errecho "    One or more of the tests failed to complete successfully. This means that any"
    errecho "    tests after the one that failed test didn't run and might have left resources"
    errecho "    still active in your account."
    errecho ""
    errecho "IMPORTANT:"
    errecho "    Resources created by this script can incur charges to your AWS account. If the"
    errecho "    script did not complete successfully, then you must review and manually delete"
    errecho "    any resources created by this script that were not automatically removed."
    errecho ""
    exit 1 
}


###############################################################################
# function errecho
#
# This function outputs everything sent to it to STDERR (standard error output).
###############################################################################
function errecho {
    printf "%s\n" "$*" 2>&1
}

###############################################################################
# function iecho
#
# This function enables the script to display the specified text only if 
# the global variable $VERBOSE is set to true.
###############################################################################
function iecho {
    if [[ $VERBOSE == true ]]; then
        echo "$@"
    fi
}

###############################################################################
# function ipause
#
# This function enables the script to pause after each command if interactive
# mode is set (by including -i on the script invocation command).
###############################################################################
function ipause {
    if [[ $INTERACTIVE == true ]]; then
        read -r -p "Press ENTER to continue..."
    fi
}

# Initialize the shell's RANDOM variable
RANDOM=$$
###############################################################################
# function generate_random_name
#
# This function generates a random file name with using the specified root
# followed by 4 groups that each have 4 digits.
# The default root name is "test"
function generate_random_name {

    ROOTNAME="test"
    if [[ -n $1 ]]; then
        ROOTNAME=$1
    fi

    # Initialize the filename variable
    FILENAME="$ROOTNAME"
    # Configure random number generator to issue numbers between 1000 and 9999, inclusive
    DIFF=$((9999-1000+1))

    for _ in {1..4}
    do
        rnd=$(($((RANDOM%DIFF))+X))
        # make sure that the number is 4 digits long
        while [ "${#rnd}" -lt 4 ]; do rnd="0$rnd"; done
        FILENAME+="-$rnd"
    done
    echo $FILENAME
}