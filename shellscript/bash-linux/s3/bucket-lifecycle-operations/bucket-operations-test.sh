#!/usr/bin/env bash

function pause {
    if [[ $interactive = true ]]; then
        read -p "Press ENTER to continue..."
    fi
}

function usage {
    echo "This script tests Amazon S3 bucket operations in the AWS CLI."
    echo "It creates a randomly named bucket, copies files to it, then"
    echo "deletes the files and the bucket."
    echo ""
    echo "To pause the script between steps so you can see the results in the"
    echo "AWS browser console, include the parameter -i."
}

# Parse parameters
    if [ $# -gt 1 ]
    then
        echo "Too many parameters."
        usage
        exit 1
    fi

    if [[ $# == 0 ]]; then
        interactive=false
    elif [[ "$1" == "-i" ]]; then
        interactive=true
    else
        echo "Invalid parameter."
        usage
        exit 1
   fi

# Initialize the shell scripts RANDOM variable
RANDOM=$$
# Configure random number generator to issue numbers between 1000 and 9999 inclusive
DIFF=$((9999-1000+1))
# generate 3 random numbers for the bucket name
R1=$(($(($RANDOM%$DIFF))+X))
R2=$(($(($RANDOM%$DIFF))+X))
R3=$(($(($RANDOM%$DIFF))+X))

bucketname="testbucket-$R1-$R2-$R3"
filename="testfile.txt"
region="us-west-2"

echo "Starting tests of bucket operations"
 
source ./bucket-operations.sh

echo "Creating bucket $bucketname..."
create-bucket $bucketname $region
pause
echo "Copying local file to bucket..."
populate-bucket $bucketname $filename
pause
echo "Duplicating file in bucket..."
copy-item-in-bucket $bucketname $filename $filename-2
pause
echo "Listing contents of bucket..."
list-items-in-bucket $bucketname
pause
echo "Deleting first file from bucket..."
delete-item-in-bucket $bucketname $filename
pause
echo "Deleting second file from bucket..."
delete-item-in-bucket $bucketname $filename-2
pause
echo "Deleting bucket..."
delete-bucket $bucketname
echo "Tests completed successfully."
