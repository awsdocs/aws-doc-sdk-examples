#!/usr/bin/env bash
<<METADATA-DO-NOT-REMOVE
# * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# *
# * This file is licensed under the Apache License, Version 2.0 (the "License").
# * You may not use this file except in compliance with the License. A copy of
# * the License is located at
# *
# * http://aws.amazon.com/apache2.0/
# *
# * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# * CONDITIONS OF ANY KIND, either express or implied. See the License for the
# * specific language governing permissions and limitations under the License.

// snippet-service:[s3]
// snippet-sourcesyntax:[bash]
// snippet-keyword:[bash]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[s3api:HeadBucket]
// snippet-keyword:[s3:mb]
// snippet-keyword:[s3api:wait bucket-exists]
// snippet-keyword:[s3api:wait bucket-not-exists]
// snippet-keyword:[s3api:wait object-exists]
// snippet-keyword:[s3api:wait object-not-exists]
// snippet-keyword:[s3api:PutObject]
// snippet-keyword:[s3:cp]
// snippet-keyword:[s3:ls]
// snippet-keyword:[s3:rm]
// snippet-keyword:[s3:rb]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-02-06]
// snippet-sourceauthor:[AWS]
METADATA-DO-NOT-REMOVE
#// snippet-start:[s3.bash.bucket-operations.complete]

# This function returns 0 if the specified bucket exists, 1 if it doesn't.
function bucket-exists {
    bucketname=$1

    # Check whether the bucket already exists. This check suppresses 
    # all output - we're interested only in the return code.

    aws s3api head-bucket \
        --bucket $bucketname \
        >/dev/null 2>&1

    if [[ $? -eq 0 ]]; then
        return 0        # 0 in Bash script means true
    else
        return 1        # 1 in Bash script means false 
    fi
}

# This function creates the specified bucket in the specified AWS Region, unless
# it already exists. Before returning, the function confirms that the bucket is
# available for access.
function create-bucket {
    bucketname=$1
    regionname=$2

    # If the bucket already exists, we don't want to try to create it.
    if (bucket-exists $bucketname); then 
        echo "ERROR: A bucket with the generated name already exists. Try again."
        exit 1
    fi

    # The bucket doesn't exist, so try to create it.

    aws s3 mb \
        s3://$bucketname \
        --region $regionname \
        >/dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports create-bucket operation failed: $? - quitting."
        exit 1
    fi

    aws s3api wait bucket-exists --bucket $bucketname
}

# This function creates a file in the specified bucket. The contents of the new 
# file are a copy of this script file. Before returning, the function confirms 
# that the file is available to access.
function populate-bucket {
    bucketname=$1
    filename=$2

    aws s3api put-object \
        --bucket $bucketname \
        --key $filename \
        --body ./$0 \
         >/dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports put-object operation failed: $? - quitting."
        exit 1
    fi

    aws s3api wait object-exists --bucket $bucketname --key $filename
}

# This function creates a copy of the specified file in the bucket. Before returning,
# the function confirms that the copy of the file is ready to access.
function copy-item-in-bucket {
    bucketname=$1
    sourcefile=$2
    destfile=$3

    aws s3 cp \
        s3://$bucketname/$sourcefile \
        s3://$bucketname/$destfile \
        --quiet

    if [[ $? -ne 0 ]]; then
        echo "ERROR:  AWS reports s3 cp operation failed: $? - quitting."
        exit 1
    fi

    aws s3api wait object-exists --bucket $bucketname --key $destfile
}

# This function displays a list of the items in the bucket in text format.
function list-items-in-bucket {
    bucketname=$1

    aws s3 ls \
        s3://$bucketname \
        --output text

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports s3 ls operation failed: $? - quitting."
        exit 1
    fi
}

# This function deletes the specified file from the specified bucket. Before returning,
# the function confirms that the file no longer exists.
function delete-item-in-bucket {
    bucketname=$1
    filename=$2

    aws s3 rm \
        s3://$bucketname/$filename \
        --quiet

    if [[ $? -ne 0 ]]; then
        echo "ERROR:  AWS reports s3 rm operation failed: $? - quitting."
        exit 1
    fi

    aws s3api wait object-not-exists --bucket $bucketname --key $filename
}

# This function deletes the bucket. It first confirms that the bucket does exist.
# Then it deletes the bucket. Before returning, the function confirms that
# the bucket no longer exists.
function delete-bucket {
    bucketname=$1

    aws s3 rb \
        s3://$bucketname \
        --force \
        >/dev/null

    if [[ $? -ne 0 ]]; then
        echo "ERROR: AWS reports s3 rb failed: $? - quitting."
        exit 1
    fi

    aws s3api wait bucket-not-exists \
        --bucket $bucketname
}
#// snippet-start:[s3.bash.bucket-operations.complete]
