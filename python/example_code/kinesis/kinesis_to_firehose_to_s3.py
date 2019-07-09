# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[kinesis_to_firehose_to_s3.py demonstrates how to create a Kinesis-to-Firehose-to-S3 data stream.]
# snippet-service:[kinesis]
# snippet-keyword:[Amazon Kinesis Data Streams]
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
import firehose_to_s3 as fh_s3


def create_kinesis_stream(stream_name, num_shards=1):
    """Create a Kinesis data stream

    :param stream_name: Data stream name
    :param num_shards: Number of stream shards
    :return: True if creation of stream was started. Otherwise, False.
    """

    # Create the data stream
    kinesis_client = boto3.client('kinesis')
    try:
        kinesis_client.create_stream(StreamName=stream_name,
                                     ShardCount=num_shards)
    except ClientError as e:
        logging.error(e)
        return False
    return True


def get_kinesis_arn(stream_name):
    """Retrieve the ARN for a Kinesis data stream

    :param stream_name: Kinesis data stream name
    :return: ARN of stream. If error, return None.
    """

    # Retrieve stream info
    kinesis_client = boto3.client('kinesis')
    try:
        result = kinesis_client.describe_stream_summary(StreamName=stream_name)
    except ClientError as e:
        logging.error(e)
        return None
    return result['StreamDescriptionSummary']['StreamARN']


def wait_for_active_kinesis_stream(stream_name):
    """Wait for a new Kinesis data stream to become active

    :param stream_name: Data stream name
    :return: True if steam is active. False if error creating stream.
    """

    # Wait until the stream is active
    kinesis_client = boto3.client('kinesis')
    while True:
        try:
            # Get the stream's current status
            result = kinesis_client.describe_stream_summary(StreamName=stream_name)
        except ClientError as e:
            logging.error(e)
            return False
        status = result['StreamDescriptionSummary']['StreamStatus']
        if status == 'ACTIVE':
            return True
        if status == 'DELETING':
            logging.error(f'Kinesis stream {stream_name} is being deleted.')
            return False
        time.sleep(5)


def main():
    """Exercise Kinesis Data Streams methods"""

    # Assign these values before running the program
    kinesis_name = 'kinesis_test_stream'
    number_of_shards = 1
    firehose_name = 'firehose_kinesis_test_stream'
    bucket_arn = 'arn:aws:s3:::BUCKET_NAME'
    iam_role_name = 'kinesis_to_firehose_to_s3'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Create a Kinesis stream (this is an asynchronous method)
    success = create_kinesis_stream(kinesis_name, number_of_shards)
    if not success:
        exit(1)

    # Wait for the stream to become active
    logging.info(f'Waiting for new Kinesis stream {kinesis_name} to become active...')
    if not wait_for_active_kinesis_stream(kinesis_name):
        exit(1)
    logging.info(f'Kinesis stream {kinesis_name} is active')

    # Retrieve the Kinesis stream's ARN
    kinesis_arn = get_kinesis_arn(kinesis_name)

    # Create a Firehose delivery stream as a consumer of the Kinesis stream
    firehose_src_type = 'KinesisStreamAsSource'
    firehose_arn = fh_s3.create_firehose_to_s3(firehose_name,
                                               bucket_arn,
                                               iam_role_name,
                                               firehose_src_type,
                                               kinesis_arn)
    if firehose_arn is None:
        exit(1)
    logging.info(f'Created Firehose delivery stream to S3: {firehose_arn}')

    # Wait for the Firehose to become active
    if not fh_s3.wait_for_active_firehose(firehose_name):
        exit(1)
    logging.info('Firehose stream is active')

    # Put records into the Kinesis stream. Firehose will grab them and forward
    # them to S3.
    # JSON Test Data: { "ticker_symbol": string,
    #                   "sector": string,
    #                   "change": float,
    #                   "price": float }
    # Depending on the app, the "sector" value of the test data might be a
    # reasonable partition key, provided the number of records for each sector
    # are roughly equal, resulting in an even distribution of records between
    # all shards. Alternatively, the "change" or "price" values or a
    # concatenation of the two values might make a reasonable, albeit more
    # random, partition key.
    # Note: Because the example uses a single shard by default, the partition
    # key has no effect, but it is still required.
    test_data_file = 'kinesis_test_data.txt'
    kinesis_client = boto3.client('kinesis')
    with open(test_data_file, 'rb') as f:
        logging.info('Putting 20 records into the Kinesis stream one at a time')
        for i in range(20):
            # Read a record of test data
            line = next(f)

            # Extract the "sector" value to use as the partition key
            sector = json.loads(line)['sector']

            # Put the record into the stream
            try:
                kinesis_client.put_record(StreamName=kinesis_name,
                                          Data=line,
                                          PartitionKey=sector)
            except ClientError as e:
                logging.error(e)
                exit(1)

        # Put 200 records in a list
        logging.info('Putting 200 records into the Firehose in a batch')
        batch = [{'Data': next(f)} for x in range(200)]
        [record.update({'PartitionKey': json.loads(record['Data'])['sector']}) for record in batch]

        # Put the list into the Kinesis stream
        try:
            result = kinesis_client.put_records(StreamName=kinesis_name,
                                                Records=batch)
        except ClientError as e:
            logging.error(e)
            exit(1)

        # Did any records in the batch not get processed?
        num_failures = result['FailedRecordCount']
        '''
        # Test: Simulate a failed record
        num_failures = 1
        failed_rec_index = 3
        result['Records'][failed_rec_index]['ErrorCode'] = 404
        '''
        if num_failures:
            # Resend failed records
            logging.info(f'Resending {num_failures} failed records')
            rec_index = 0
            for record in result['Records']:
                if 'ErrorCode' in record:
                    # Resend the record
                    kinesis_client.put_record(StreamName=kinesis_name,
                                              Data=batch[rec_index]['Data'],
                                              PartitionKey=batch[rec_index]['PartitionKey'])

                    # Stop if all failed records have been resent
                    num_failures -= 1
                    if not num_failures:
                        break
                rec_index += 1
    logging.info('Test data sent to Kinesis stream')


if __name__ == '__main__':
    main()
