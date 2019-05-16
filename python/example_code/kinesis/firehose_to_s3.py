# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[firehose_to_s3.py demonstrates how to create and use an Amazon Kinesis Data Firehose delivery stream to Amazon S3.]
# snippet-service:[firehose]
# snippet-keyword:[Amazon Kinesis Data Firehose]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-05-15]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


import json
import logging
import time
import boto3
from botocore.exceptions import ClientError


def get_firehose_arn(firehose_name):
    """Retrieve the ARN of the specified Firehose

    :param firehose_name: Firehose stream name
    :return: If the Firehose stream exists, return ARN, else None
    """

    # Try to get the description of the Firehose
    firehose_client = boto3.client('firehose')
    try:
        result = firehose_client.describe_delivery_stream(DeliveryStreamName=firehose_name)
    except ClientError as e:
        logging.error(e)
        return None
    return result['DeliveryStreamDescription']['DeliveryStreamARN']


def firehose_exists(firehose_name):
    """Check if the specified Firehose exists

    :param firehose_name: Firehose stream name
    :return: True if Firehose exists, else False
    """

    # Try to get the description of the Firehose
    if get_firehose_arn(firehose_name) is None:
        return False
    return True


def get_iam_role_arn(iam_role_name):
    """Retrieve the ARN of the specified IAM role

    :param iam_role_name: IAM role name
    :return: If the IAM role exists, return ARN, else None
    """

    # Try to retrieve information about the role
    iam_client = boto3.client('iam')
    try:
        result = iam_client.get_role(RoleName=iam_role_name)
    except ClientError as e:
        logging.error(e)
        return None
    return result['Role']['Arn']


def iam_role_exists(iam_role_name):
    """Check if the specified IAM role exists

    :param iam_role_name: IAM role name
    :return: True if IAM role exists, else False
    """

    # Try to retrieve information about the role
    if get_iam_role_arn(iam_role_name) is None:
        return False
    return True


def create_iam_role_for_firehose_to_s3(iam_role_name, s3_bucket,
                                       firehose_src_stream=None):
    """Create an IAM role for a Firehose delivery system to S3

    :param iam_role_name: Name of IAM role
    :param s3_bucket: ARN of S3 bucket
    :param firehose_src_stream: ARN of source Kinesis Data Stream. If
        Firehose data source is via direct puts then arg should be None.
    :return: ARN of IAM role. If error, returns None.
    """

    # Firehose trusted relationship policy document
    firehose_assume_role = {
        'Version': '2012-10-17',
        'Statement': [
            {
                'Sid': '',
                'Effect': 'Allow',
                'Principal': {
                    'Service': 'firehose.amazonaws.com'
                },
                'Action': 'sts:AssumeRole'
            }
        ]
    }
    iam_client = boto3.client('iam')
    try:
        result = iam_client.create_role(RoleName=iam_role_name,
                                        AssumeRolePolicyDocument=json.dumps(firehose_assume_role))
    except ClientError as e:
        logging.error(e)
        return None
    firehose_role_arn = result['Role']['Arn']

    # Define and attach a policy that grants sufficient S3 permissions
    policy_name = 'firehose_s3_access'
    s3_access = {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "",
                "Effect": "Allow",
                "Action": [
                    "s3:AbortMultipartUpload",
                    "s3:GetBucketLocation",
                    "s3:GetObject",
                    "s3:ListBucket",
                    "s3:ListBucketMultipartUploads",
                    "s3:PutObject"
                ],
                "Resource": [
                    f"{s3_bucket}/*",
                    f"{s3_bucket}"
                ]
            }
        ]
    }
    try:
        iam_client.put_role_policy(RoleName=iam_role_name,
                                   PolicyName=policy_name,
                                   PolicyDocument=json.dumps(s3_access))
    except ClientError as e:
        logging.error(e)
        return None

    # If the Firehose source is a Kinesis data stream then access to the
    # stream must be allowed.
    if firehose_src_stream is not None:
        policy_name = 'firehose_kinesis_access'
        kinesis_access = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "",
                    "Effect": "Allow",
                    "Action": [
                        "kinesis:DescribeStream",
                        "kinesis:GetShardIterator",
                        "kinesis:GetRecords"
                    ],
                    "Resource": [
                        f"{firehose_src_stream}"
                    ]
                }
             ]
        }
        try:
            iam_client.put_role_policy(RoleName=iam_role_name,
                                       PolicyName=policy_name,
                                       PolicyDocument=json.dumps(kinesis_access))
        except ClientError as e:
            logging.error(e)
            return None

    # Return the ARN of the created IAM role
    return firehose_role_arn


def create_firehose_to_s3(firehose_name, s3_bucket_arn, iam_role_name,
                          firehose_src_type='DirectPut',
                          firehose_src_stream=None):
    """Create a Kinesis Firehose delivery stream to S3

    The data source can be either a Kinesis Data Stream or puts sent directly
    to the Firehose stream.

    :param firehose_name: Delivery stream name
    :param s3_bucket_arn: ARN of S3 bucket
    :param iam_role_name: Name of Firehose-to-S3 IAM role. If the role doesn't
        exist, it is created.
    :param firehose_src_type: 'DirectPut' or 'KinesisStreamAsSource'
    :param firehose_src_stream: ARN of source Kinesis Data Stream. Required if
        firehose_src_type is 'KinesisStreamAsSource'
    :return: ARN of Firehose delivery stream. If error, returns None.
    """

    # Create Firehose-to-S3 IAM role if necessary
    if iam_role_exists(iam_role_name):
        # Retrieve its ARN
        iam_role = get_iam_role_arn(iam_role_name)
    else:
        iam_role = create_iam_role_for_firehose_to_s3(iam_role_name,
                                                      s3_bucket_arn,
                                                      firehose_src_stream)
        if iam_role is None:
            # Error creating IAM role
            return None

    # Create the S3 configuration dictionary
    # Both BucketARN and RoleARN are required
    # Set the buffer interval=60 seconds (Default=300 seconds)
    s3_config = {
        'BucketARN': s3_bucket_arn,
        'RoleARN': iam_role,
        'BufferingHints': {
            'IntervalInSeconds': 60,
        },
    }

    # Create the delivery stream
    # By default, the DeliveryStreamType='DirectPut'
    firehose_client = boto3.client('firehose')
    try:
        if firehose_src_type == 'KinesisStreamAsSource':
            # Define the Kinesis Data Stream configuration
            kinesis_config = {
                'KinesisStreamARN': firehose_src_stream,
                'RoleARN': iam_role,
            }
            result = firehose_client.create_delivery_stream(
                DeliveryStreamName=firehose_name,
                DeliveryStreamType=firehose_src_type,
                KinesisStreamSourceConfiguration=kinesis_config,
                ExtendedS3DestinationConfiguration=s3_config)
        else:
            result = firehose_client.create_delivery_stream(
                DeliveryStreamName=firehose_name,
                DeliveryStreamType=firehose_src_type,
                ExtendedS3DestinationConfiguration=s3_config)
    except ClientError as e:
        logging.error(e)
        return None
    return result['DeliveryStreamARN']


def wait_for_active_firehose(firehose_name):
    """Wait until the Firehose delivery stream is active

    :param firehose_name: Name of Firehose delivery stream
    :return: True if delivery stream is active. Otherwise, False.
    """

    # Wait until the stream is active
    firehose_client = boto3.client('firehose')
    while True:
        try:
            # Get the stream's current status
            result = firehose_client.describe_delivery_stream(DeliveryStreamName=firehose_name)
        except ClientError as e:
            logging.error(e)
            return False
        status = result['DeliveryStreamDescription']['DeliveryStreamStatus']
        if status == 'ACTIVE':
            return True
        if status == 'DELETING':
            logging.error(f'Firehose delivery stream {firehose_name} is being deleted.')
            return False
        time.sleep(2)


def main():
    """Exercise Kinesis Firehose methods"""

    # Assign these values before running the program
    # If the specified IAM role does not exist, it will be created
    firehose_name = 'firehose_to_s3_stream'
    bucket_arn = 'arn:aws:s3:::BUCKET_NAME'
    iam_role_name = 'firehose_to_s3'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # If Firehose doesn't exist, create it
    if not firehose_exists(firehose_name):
        # Create a Firehose delivery stream to S3. The Firehose will receive
        # data from direct puts.
        firehose_arn = create_firehose_to_s3(firehose_name, bucket_arn, iam_role_name)
        if firehose_arn is None:
            exit(1)
        logging.info(f'Created Firehose delivery stream to S3: {firehose_arn}')

        # Wait for the stream to become active
        if not wait_for_active_firehose(firehose_name):
            exit(1)
        logging.info('Firehose stream is active')

    # Put records into the Firehose stream
    test_data_file = 'kinesis_test_data.txt'
    firehose_client = boto3.client('firehose')
    with open(test_data_file, 'r') as f:
        logging.info('Putting 20 records into the Firehose one at a time')
        for i in range(20):
            # Read a record of test data
            line = next(f)

            # Put the record into the Firehose stream
            try:
                firehose_client.put_record(DeliveryStreamName=firehose_name,
                                           Record={'Data': line})
            except ClientError as e:
                logging.error(e)
                exit(1)

        # Put 200 records in a batch
        logging.info('Putting 200 records into the Firehose in a batch')
        batch = [{'Data': next(f)} for x in range(200)]  # Read 200 records

        # Put the batch into the Firehose stream
        try:
            result = firehose_client.put_record_batch(DeliveryStreamName=firehose_name,
                                                      Records=batch)
        except ClientError as e:
            logging.error(e)
            exit(1)

        # Did any records in the batch not get processed?
        num_failures = result['FailedPutCount']
        '''
        # Test: Simulate a failed record
        num_failures = 1
        failed_rec_index = 3
        result['RequestResponses'][failed_rec_index]['ErrorCode'] = 404
        '''
        if num_failures:
            # Resend failed records
            logging.info(f'Resending {num_failures} failed records')
            rec_index = 0
            for record in result['RequestResponses']:
                if 'ErrorCode' in record:
                    # Resend the record
                    firehose_client.put_record(DeliveryStreamName=firehose_name,
                                               Record=batch[rec_index])

                    # Stop if all failed records have been resent
                    num_failures -= 1
                    if not num_failures:
                        break
                rec_index += 1
    logging.info('Test data sent to Firehose stream')


if __name__ == '__main__':
    main()
