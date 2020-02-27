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
source ./bucket_operations.sh

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

    # Set default values.
    INTERACTIVE=false
    VERBOSE=false

    # Retrieve the calling parameters
    while getopts "ivh" OPTION; do
        case "${OPTION}"
        in
            i)  INTERACTIVE=true;VERBOSE=true; iecho;;
            v)  VERBOSE=true;;
            h)  usage; return 0;;
            \?) echo "Invalid parameter"; usage; return 1;; 
        esac
    done


if [ "$INTERACTIVE" == "true" ]; then iecho "Tests running in interactive mode."; fi
if [ "$VERBOSE" == "true" ]; then iecho "Tests running in verbose mode."; fi

iecho "***************SETUP STEPS******************"
BUCKETNAME=$(generate_random_name s3test)
REGION="us-west-2"
FILENAME1=$(generate_random_name s3testfile)
FILENAME2=$(generate_random_name s3testfile)

iecho "BUCKETNAME=$BUCKETNAME"
iecho "REGION=$REGION"
iecho "FILENAME1=$FILENAME1"
iecho "FILENAME2=$FILENAME2"

iecho "**************END OF STEPS******************"

run_test "1. Creating bucket with missing bucket_name" \
         "create_bucket -r $REGION" \
         1 \
         "ERROR: You must provide a bucket name" \


run_test "2. Creating bucket with missing region_name" \
         "create_bucket -b $BUCKETNAME" \
         1 \
         "ERROR: You must provide an AWS Region code"

run_test "3. Creating bucket with valid parameters" \
         "create_bucket -r $REGION -b $BUCKETNAME" \
         0

run_test "4. Creating bucket with duplicate name and region" \
         "create_bucket -r $REGION -b $BUCKETNAME" \
         1 \
         "ERROR: A bucket with that name already exists"

run_test "5. Copying local file (copy of this script) to bucket" \
         "copy_file_to_bucket $BUCKETNAME ./$0 $FILENAME1" \
         0

run_test "6. Duplicating existing file in bucket" \
         "copy_item_in_bucket $BUCKETNAME $FILENAME1 $FILENAME2" \
         0

run_test "7. Listing contents of bucket" \
         "list_items_in_bucket $BUCKETNAME" \
         0

run_test "8. Deleting first file from bucket" \
         "delete_item_in_bucket $BUCKETNAME $FILENAME1" \
         0

run_test "9. Deleting second file from bucket" \
         "delete_item_in_bucket $BUCKETNAME $FILENAME2" \
         0

run_test "10. Deleting bucket" \
         "delete_bucket $BUCKETNAME" \
         0

echo "Tests completed successfully."
