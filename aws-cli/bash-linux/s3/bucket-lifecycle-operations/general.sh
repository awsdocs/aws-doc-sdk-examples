# No 'shebang' (#!) because this script is intended to 'source', not run.
###############################################################################
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# 
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.
###############################################################################

# This script contains general purpose functions that are used throughout 
# the AWS Command Line Interpreter (AWS CLI) code samples that are maintained
# in the repo at https://github.com/awsdocs/aws-doc-sdk-examples
#
# They are intended to abstract functionality that is required for the tests
# to work without cluttering up the code. The intent is to ensure the purpose
# of the code is clear.



###############################################################################
# function TESTS-FAILED
#
# This function is used to terminate a failed test and to warn the customer
# about possible undeleted resources that could incur costs to their account.
###############################################################################

function TESTS-FAILED {
    echo ""
    echo "TESTS FAILED"
    echo "One or more of the tests failed to complete successfully. This means"
    echo "that any tests that were supposed to run after the failed test did't"
    echo "run and might have left resources still active in your account."
    echo ""
    echo "IMPORTANT:"
    echo "    Resources created by this script can incur charges to your AWS"
    echo "    account. If the script did not complete successfully, then you"
    echo "    must review and manually delete any resources created by this"
    echo "    script that were not automatically removed."
    exit 1 
}

###############################################################################
# function iecho
#
# This function enables the script to display the specified text only if 
# interactive mode is set (by including -i on the script invocation command).
###############################################################################
function iecho {
    if [[ $interactive = true ]]; then
        echo $@
    fi
}

###############################################################################
# function ipause
#
# This function enables the script to pause after each command if interactive
# mode is set (by including -i on the script invocation command).
###############################################################################
function ipause {
    if [[ $interactive = true ]]; then
        read -p "Press ENTER to continue..."
    fi
}


# Initialize the shell's RANDOM variable
RANDOM=$$

###############################################################################
# function generate-random-name
#
# This function generates a random file name with using the specified root
# followed by 4 groups that each have 4 digits.
# The default rootname is "test"
function generate-random-name {

    ROOTNAME="test"
    if [[ -n $1 ]]; then
        ROOTNAME=$1
    fi

    # Initialize the filename variable
    FILENAME="$ROOTNAME"
    # Configure random number generator to issue numbers between 1000 and 9999, inclusive
    DIFF=$((9999-1000+1))

    for i in {1..4}
    do
        rnd=$(($(($RANDOM%$DIFF))+X))
        # make sure that the number is 4 digits long
        while [ "${#rnd}" -lt 4 ]; do rnd="0$rnd"; done
        FILENAME+="-$rnd"
    done
    echo $FILENAME
}