# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Kinesis to create and
manage streams.
"""

import json
import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.KinesisStream.class]
class KinesisStream:
    """Encapsulates a Kinesis stream."""
    def __init__(self, kinesis_client):
        """
        :param kinesis_client: A Boto3 Kinesis client.
        """
        self.kinesis_client = kinesis_client
        self.name = None
        self.details = None
        self.stream_exists_waiter = kinesis_client.get_waiter('stream_exists')
# snippet-end:[python.example_code.kinesis.KinesisStream.class]

    def _clear(self):
        """
        Clears property data of the stream object.
        """
        self.name = None
        self.details = None

    def arn(self):
        """
        Gets the Amazon Resource Name (ARN) of the stream.
        """
        return self.details['StreamARN']

# snippet-start:[python.example_code.kinesis.CreateStream]
    def create(self, name, wait_until_exists=True):
        """
        Creates a stream.

        :param name: The name of the stream.
        :param wait_until_exists: When True, waits until the service reports that
                                  the stream exists, then queries for its metadata.
        """
        try:
            self.kinesis_client.create_stream(StreamName=name, ShardCount=1)
            self.name = name
            logger.info("Created stream %s.", name)
            if wait_until_exists:
                logger.info("Waiting until exists.")
                self.stream_exists_waiter.wait(StreamName=name)
                self.describe(name)
        except ClientError:
            logger.exception("Couldn't create stream %s.", name)
            raise
# snippet-end:[python.example_code.kinesis.CreateStream]

# snippet-start:[python.example_code.kinesis.DescribeStream]
    def describe(self, name):
        """
        Gets metadata about a stream.

        :param name: The name of the stream.
        :return: Metadata about the stream.
        """
        try:
            response = self.kinesis_client.describe_stream(StreamName=name)
            self.name = name
            self.details = response['StreamDescription']
            logger.info("Got stream %s.", name)
        except ClientError:
            logger.exception("Couldn't get %s.", name)
            raise
        else:
            return self.details
# snippet-end:[python.example_code.kinesis.DescribeStream]

# snippet-start:[python.example_code.kinesis.DeleteStream]
    def delete(self):
        """
        Deletes a stream.
        """
        try:
            self.kinesis_client.delete_stream(StreamName=self.name)
            self._clear()
            logger.info("Deleted stream %s.", self.name)
        except ClientError:
            logger.exception("Couldn't delete stream %s.", self.name)
            raise
# snippet-end:[python.example_code.kinesis.DeleteStream]

# snippet-start:[python.example_code.kinesis.PutRecord]
    def put_record(self, data, partition_key):
        """
        Puts data into the stream. The data is formatted as JSON before it is passed
        to the stream.

        :param data: The data to put in the stream.
        :param partition_key: The partition key to use for the data.
        :return: Metadata about the record, including its shard ID and sequence number.
        """
        try:
            response = self.kinesis_client.put_record(
                StreamName=self.name,
                Data=json.dumps(data),
                PartitionKey=partition_key)
            logger.info("Put record in stream %s.", self.name)
        except ClientError:
            logger.exception("Couldn't put record in stream %s.", self.name)
            raise
        else:
            return response
# snippet-end:[python.example_code.kinesis.PutRecord]

# snippet-start:[python.example_code.kinesis.GetRecords]
    def get_records(self, max_records):
        """
        Gets records from the stream. This function is a generator that first gets
        a shard iterator for the stream, then uses the shard iterator to get records
        in batches from the stream. Each batch of records is yielded back to the
        caller until the specified maximum number of records has been retrieved.

        :param max_records: The maximum number of records to retrieve.
        :return: Yields the current batch of retrieved records.
        """
        try:
            response = self.kinesis_client.get_shard_iterator(
                StreamName=self.name, ShardId=self.details['Shards'][0]['ShardId'],
                ShardIteratorType='LATEST')
            shard_iter = response['ShardIterator']
            record_count = 0
            while record_count < max_records:
                response = self.kinesis_client.get_records(
                    ShardIterator=shard_iter, Limit=10)
                shard_iter = response['NextShardIterator']
                records = response['Records']
                logger.info("Got %s records.", len(records))
                record_count += len(records)
                yield records
        except ClientError:
            logger.exception("Couldn't get records from stream %s.", self.name)
            raise
# snippet-end:[python.example_code.kinesis.GetRecords]
