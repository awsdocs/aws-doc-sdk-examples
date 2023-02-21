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
source ./awsdocs_general.sh

###############################################################################
# function bucket_exists
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
function bucket_exists {
    be_bucketname=$1

    # Check whether the bucket already exists. 
    # We suppress all output - we're interested only in the return code.

    aws s3api head-bucket \
        --bucket $be_bucketname \
        >/dev/null 2>&1

    if [[ ${?} -eq 0 ]]; then
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
#       -b bucket_name  -- The name of the bucket to create
#       -r region_code  -- The code for an AWS Region in which to 
#                          create the bucket
# 
# Returns:
#       The URL of the bucket that was created.
#     And:
#       0 if successful
#       1 if it fails
###############################################################################
function create_bucket {
    local BUCKET_NAME REGION_CODE RESPONSE
    local OPTION OPTIND OPTARG # Required to use getopts command in a function 

    function usage {
        echo "function create_bucket"
        echo "Creates an Amazon S3 bucket. You must supply both of the following parameters:"
        echo "  -b bucket_name    The name of the bucket. It must be globally unique."
        echo "  -r region_code    The code for an AWS Region in which the bucket is created."
        echo ""
    }

    # Retrieve the calling parameters
    while getopts "b:r:" OPTION; do
        case "${OPTION}"
        in
            b)  BUCKET_NAME="${OPTARG}";;
            r)  REGION_CODE="${OPTARG}";;
            h)  usage; return 0;;
            \?) echo "Invalid parameter"; usage; return 1;; 
        esac
    done

    if [[ -z "$BUCKET_NAME" ]]; then
        errecho "ERROR: You must provide a bucket name with the -b parameter."
        usage
        return 1
    fi

    if [[ -z "$REGION_CODE" ]]; then
        errecho "ERROR: You must provide an AWS Region code with the -r parameter."
        usage
        return 1
    fi

    iecho "Parameters:\n"
    iecho "    Bucket name:   $BUCKET_NAME"
    iecho "    Region code:   $REGION_CODE"
    iecho ""
    
    
    # If the bucket already exists, we don't want to try to create it.
    if (bucket_exists $BUCKET_NAME); then 
        errecho "ERROR: A bucket with that name already exists. Try again."
        return 1
    fi

    # The bucket doesn't exist, so try to create it.
    
    RESPONSE=$(aws s3api create-bucket \
                --bucket $BUCKET_NAME \
                --create-bucket-configuration LocationConstraint=$REGION_CODE)

    if [[ ${?} -ne 0 ]]; then
        errecho "ERROR: AWS reports create-bucket operation failed.\n$RESPONSE"
        return 1
    fi
}

###############################################################################
# function copy_file_to_bucket
#
# This function creates a file in the specified bucket. 
#
# Parameters:
#       -b bucket_name$1 - The name of the bucket to copy the file to
#       $2 - The path and file name of the local file to copy to the bucket
#       $3 - The key (name) to call the copy of the file in the bucket
# 
# Returns:
#       0 if successful
#       1 if it fails
###############################################################################
function copy_file_to_bucket {
    cftb_bucketname=$1
    cftb_sourcefile=$2
    cftb_destfilename=$3
    local RESPONSE
    
    RESPONSE=$(aws s3api put-object \
                --bucket $cftb_bucketname \
                --body $cftb_sourcefile \
                --key $cftb_destfilename)

    if [[ ${?} -ne 0 ]]; then
        errecho "ERROR: AWS reports put-object operation failed.\n$RESPONSE"
        return 1
    fi
}

###############################################################################
# function copy_item_in_bucket
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
function copy_item_in_bucket {
    ciib_bucketname=$1
    ciib_sourcefile=$2
    ciib_destfile=$3
    local RESPONSE
    
    RESPONSE=$(aws s3api copy-object \
                --bucket $ciib_bucketname \
                --copy-source $ciib_bucketname/$ciib_sourcefile \
                --key $ciib_destfile)

    if [[ $? -ne 0 ]]; then
        errecho "ERROR:  AWS reports s3api copy-object operation failed.\n$RESPONSE"
        return 1
    fi
}

###############################################################################
# function list_items_in_bucket
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
function list_items_in_bucket {
    liib_bucketname=$1
    local RESPONSE

    RESPONSE=$(aws s3api list-objects \
                --bucket $liib_bucketname \
                --output text \
                --query 'Contents[].{Key: Key, Size: Size}' )

    if [[ ${?} -eq 0 ]]; then
        echo "$RESPONSE"
    else
        errecho "ERROR: AWS reports s3api list-objects operation failed.\n$RESPONSE"
        return 1
    fi
}

###############################################################################
# function delete_item_in_bucket
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
function delete_item_in_bucket {
    diib_bucketname=$1
    diib_key=$2
    local RESPONSE
    
    RESPONSE=$(aws s3api delete-object \
                --bucket $diib_bucketname \
                --key $diib_key)

    if [[ $? -ne 0 ]]; then
        errecho "ERROR:  AWS reports s3api delete-object operation failed.\n$RESPONSE"
        return 1
    fi
}

###############################################################################
# function delete_bucket
#
# This function deletes the specified bucket.
#
# Parameters:
#       $1 - The name of the bucket

# Returns:
#       0 if successful
#       1 if it fails
###############################################################################
 function delete_bucket {
    db_bucketname=$1
    local RESPONSE

    RESPONSE=$(aws s3api delete-bucket \
                --bucket $db_bucketname)

    if [[ $? -ne 0 ]]; then
        errecho "ERROR: AWS reports s3api delete-bucket failed.\n$RESPONSE"
        return 1
    fi
}
#// snippet-end:[s3.bash.bucket-operations.complete]
