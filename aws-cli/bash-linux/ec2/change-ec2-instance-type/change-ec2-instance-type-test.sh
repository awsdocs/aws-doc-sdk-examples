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

source ./general.sh

function usage {
    echo "This script tests Amazon S3 bucket operations in the AWS CLI."
    echo "It creates a randomly named bucket, copies files to it, then"
    echo "deletes the files and the bucket."
    echo ""
    echo "To pause the script between steps so you can see the results in the"
    echo "AWS Management Console, include the parameter -i."
    echo ""
    echo "IMPORTANT: Running this script creates resources in your Amazon"
    echo "   account that can incur charges. It is your responsibility to"
    echo "   ensure that no resources are left in your account after the script"
    echo "   completes. If an error occurs during the operation of the script,"
    echo "   then resources can remain that you might need to delete manaully."
}

# Parse parameters
    if [ "$#" -eq "0" ]; then 
        interactive=false
    elif [ "$1" == "-i" ] && [ "$#" -eq "1" ]; then
        interactive=true
    else
        echo "Invalid parameter."
        usage
        exit 1
    fi

bucketname=$(generate-random-name s3test)
filename1=$(generate-random-name s3testfile)
filename2=$(generate-random-name s3testfile)
region="us-west-2"

iecho "bucketname=$bucketname"
iecho "filename1=$filename1"
iecho "filename2=$filename2"
iecho "region=$region"

iecho "Starting tests of bucket operations"

# The functions we want to test all come from this file 
source ./bucket-operations.sh

iecho "Creating bucket $bucketname..."
create-bucket $bucketname $region
if [ $? -ne 0 ]; then TESTS-FAILED; fi
ipause

iecho "Copying local file (copy of this script) to bucket..."
copy-file-to-bucket $bucketname ./$0 $filename1
if [[ $? -ne 0 ]]; then TESTS-FAILED; fi
ipause

iecho "Duplicating file in bucket..."
copy-item-in-bucket $bucketname $filename1 $filename2
if [[ $? -ne 0 ]]; then TESTS-FAILED; fi
ipause

iecho "Listing contents of bucket..."
list-items-in-bucket $bucketname
if [[ $? -ne 0 ]]; then TESTS-FAILED; fi
ipause

iecho "Deleting first file from bucket..."
delete-item-in-bucket $bucketname $filename1
if [[ $? -ne 0 ]]; then TESTS-FAILED; fi
ipause

iecho "Deleting second file from bucket..."
delete-item-in-bucket $bucketname $filename2
if [[ $? -ne 0 ]]; then TESTS-FAILED; fi
ipause

iecho "Deleting bucket..."
delete-bucket $bucketname
if [[ $? -ne 0 ]]; then TESTS-FAILED; fi

echo "Tests completed successfully."
