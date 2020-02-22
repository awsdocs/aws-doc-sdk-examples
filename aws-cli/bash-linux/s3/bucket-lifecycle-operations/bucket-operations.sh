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
#// snippet-start:[s3.bash.bucket-operations.complete]


###############################################################################
# function bucket-exists
#
# This function checks to see if the specified bucket already exists.
#
# Parameters:
#       $1 - The name of the bucket to check
# 
# Returns:
#       0 if the bucket already exists
#       1 if the bucket doesn't exist
###############################################################################
function bucket-exists {
    be_bucketname=$1

    # Check whether the bucket already exists. 
    # We suppress all output - we're interested only in the return code.

    aws s3api head-bucket \
        --bucket $be_bucketname \
        >/dev/null 2>&1

    if [[ $? -eq 0 ]]; then
        return 0        # 0 in Bash script means true.
    else
        return 1        # 1 in Bash script means false.
    fi
}
###############################################################################
# function create-bucket
#
# This function creates the specified bucket in the specified AWS Region, unless
# it already exists.
# 
# Parameters:
#       $1 - The name of the bucket to create
#       $2 - The AWS Region in which to create the bucket
# 
# Returns:
#       The URL of the bucket that was created.
#     And:
#       0 if successful
#       1 if it fails
###############################################################################
function create-bucket {
    cb_bucketname=$1
    cb_regionname=$2

    # If the bucket already exists, we don't want to try to create it.
    if (bucket-exists $cb_bucketname); then 
        echo "ERROR: A bucket with the generated name already exists. Try again."
        exit 1
    fi

    # The bucket doesn't exist, so try to create it.
    
    aws s3api create-bucket \
        --bucket $cb_bucketname \
        --create-bucket-configuration LocationConstraint=$cb_regionname \
        --output text 

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports create-bucket operation failed: $? - quitting."
        return 1
    fi
}

###############################################################################
# function copy-file-to-bucket
#
# This function creates a file in the specified bucket. 
#
# Parameters:
#       $1 - The name of the bucket to copy the file to
#       $2 - The path and file name of the local file to copy to the bucket
#       $3 - The key (name) to call the copy of the file in the bucket
# 
# Returns:
#       0 if successful
#       1 if it fails
###############################################################################
function copy-file-to-bucket {
    cftb_bucketname=$1
    cftb_sourcefile=$2
    cftb_destfilename=$3

    aws s3api put-object \
        --bucket $cftb_bucketname \
        --body $cftb_sourcefile \
        --key $cftb_destfilename \
         >/dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports put-object operation failed: $? - quitting."
        return 1
    fi
}

###############################################################################
# function copy-item-in-bucket
#
# This function creates a copy of the specified file in the same bucket.
#
# Parameters:
#       $1 - The name of the bucket to copy the file from and to
#       $2 - The key of the source file to copy
#       $3 - The key of the destination file
# 
# Returns:
#       0 if successful
#       1 if it fails
###############################################################################
function copy-item-in-bucket {
    ciib_bucketname=$1
    ciib_sourcefile=$2
    ciib_destfile=$3

    aws s3api copy-object \
        --bucket $ciib_bucketname \
        --copy-source $ciib_bucketname/$ciib_sourcefile \
        --key $ciib_destfile \
        > /dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR:  AWS reports s3api copy-object operation failed: $? - quitting."
        return 1
    fi
}

###############################################################################
# function list-items-in-bucket
#
# This function displays a list of the files in the bucket with each file's 
# size. The function uses the --query parameter to retrieve only the Key and 
# Size fields from the Contents collection.
#
# Parameters:
#       $1 - The name of the bucket
# 
# Returns:
#       The list of files in text format
#     And:
#       0 if successful
#       1 if it fails
###############################################################################
function list-items-in-bucket {
    liib_bucketname=$1

    aws s3api list-objects \
        --bucket $liib_bucketname \
        --output text \
        --query 'Contents[].{Key: Key, Size: Size}'
  
    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports s3api list-objects operation failed: $? - quitting."
        return 1
    fi
}

###############################################################################
# function delete-item-in-bucket
#
# This function deletes the specified file from the specified bucket. 
#
# Parameters:
#       $1 - The name of the bucket
#       $2 - The key (file name) in the bucket to delete

# Returns:
#       0 if successful
#       1 if it fails
###############################################################################
function delete-item-in-bucket {
    diib_bucketname=$1
    diib_key=$2

    aws s3api delete-object \
        --bucket $diib_bucketname \
        --key $diib_key \
        > /dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR:  AWS reports s3api delete-object operation failed: $? - quitting."
        return 1
    fi
}

###############################################################################
# function delete-bucket
#
# This function deletes the specified bucket.
#
# Parameters:
#       $1 - The name of the bucket

# Returns:
#       0 if successful
#       1 if it fails
###############################################################################
 function delete-bucket {
    db_bucketname=$1

    aws s3api delete-bucket \
        --bucket $db_bucketname \
        >/dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports s3api delete-bucket failed: $? - quitting."
        return 1
    fi
}
#// snippet-end:[s3.bash.bucket-operations.complete]
