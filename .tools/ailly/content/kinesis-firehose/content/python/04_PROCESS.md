---
debug:
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
  finish: end_turn
  engine: bedrock
isolated: false
combined: true
prompt: |
  Persona: You are a guru-level solutions engineer with expertise in python and AWS kinesis-firehose.
  Task: Write code for a workflow using the AWS SDK for python to interface with the kinesis-firehose.
  Output:
     - Give me a perfectly-functional file based on the list of files you created in the previous step (03_FILES.md)
     - Each file should be wrapped in <file></file> parent tags that will allow an automated process to interpret them
     - Additionally, within these <file> tags, include a <name> tag identifying the file name and a <contents> tag with the raw file contents.
     - Do not include ANY formatting. For example: ticks or any other markdown language formatting.
     - Do not explain anything. Just provide the file contents in the format requested.
---
<file>
<name>main.py</name>
<contents>
import argparse
import logging
import sys

from kinesis_firehose_client import KinesisFirehoseClient
from config import get_config
from utils import validate_record_size

def main():
    # Parse command-line arguments
    parser = argparse.ArgumentParser(description='Kinesis Firehose Example')
    parser.add_argument('--stream-name', required=True, help='Kinesis Firehose Delivery Stream name')
    parser.add_argument('--bucket-name', required=True, help='S3 bucket name for data delivery')
    parser.add_argument('--records', nargs='+', required=True, help='Records to put into the Delivery Stream')
    args = parser.parse_args()

    # Get configuration
    config = get_config()

    # Set up logging
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

    # Validate record sizes
    for record in args.records:
        validate_record_size(record)

    # Create Kinesis Firehose client
    kinesis_client = KinesisFirehoseClient(config['aws_access_key_id'], config['aws_secret_access_key'], config['aws_region'])

    # Put records into the Delivery Stream
    for record in args.records:
        kinesis_client.put_record(args.stream_name, record)

    logging.info('Records successfully put into the Delivery Stream')

if __name__ == '__main__':
    main()
</contents>
</file>

<file>
<name>kinesis_firehose_client.py</name>
<contents>
import boto3
import backoff
import logging

from exceptions import KinesisFirehoseError

class KinesisFirehoseClient:
    def __init__(self, access_key_id, secret_access_key, region_name):
        self.client = boto3.client('firehose', aws_access_key_id=access_key_id, aws_secret_access_key=secret_access_key, region_name=region_name)

    @backoff.on_exception(backoff.expo, KinesisFirehoseError, max_tries=5)
    def put_record(self, delivery_stream_name, record):
        try:
            response = self.client.put_record(DeliveryStreamName=delivery_stream_name, Record={"Data": record})
            logging.info(f'Record put into Delivery Stream: {delivery_stream_name}')
        except Exception as e:
            logging.error(f'Error putting record into Delivery Stream: {delivery_stream_name}')
            raise KinesisFirehoseError(str(e))

    @backoff.on_exception(backoff.expo, KinesisFirehoseError, max_tries=5)
    def put_record_batch(self, delivery_stream_name, records):
        try:
            response = self.client.put_record_batch(DeliveryStreamName=delivery_stream_name, Records=[{"Data": record} for record in records])
            logging.info(f'{len(records)} records put into Delivery Stream: {delivery_stream_name}')
            if 'FailedPutCount' in response and response['FailedPutCount'] > 0:
                logging.warning(f"{response['FailedPutCount']} records failed to put into Delivery Stream: {delivery_stream_name}")
        except Exception as e:
            logging.error(f'Error putting record batch into Delivery Stream: {delivery_stream_name}')
            raise KinesisFirehoseError(str(e))
</contents>
</file>

<file>
<name>utils.py</name>
<contents>
import logging

from exceptions import InvalidRecordSizeError

MAX_RECORD_SIZE = 1024 * 1024  # 1 MB

def validate_record_size(record):
    record_size = len(record.encode('utf-8'))
    if record_size > MAX_RECORD_SIZE:
        logging.error(f'Record size ({record_size} bytes) exceeds the maximum allowed size ({MAX_RECORD_SIZE} bytes)')
        raise InvalidRecordSizeError(f'Record size ({record_size} bytes) exceeds the maximum allowed size ({MAX_RECORD_SIZE} bytes)')
</contents>
</file>

<file>
<name>config.py</name>
<contents>
import os

def get_config():
    config = {
        'aws_access_key_id': os.environ.get('AWS_ACCESS_KEY_ID'),
        'aws_secret_access_key': os.environ.get('AWS_SECRET_ACCESS_KEY'),
        'aws_region': os.environ.get('AWS_REGION', 'us-east-1')
    }
    return config
</contents>
</file>

<file>
<name>exceptions.py</name>
<contents>
class KinesisFirehoseError(Exception):
    pass

class InvalidRecordSizeError(Exception):
    pass
</contents>
</file>

<file>
<name>kinesis-firehose-example.ts</name>
<contents>
import * as cdk from 'aws-cdk-lib';
import * as kinesisfirehose from 'aws-cdk-lib/aws-kinesisfirehose';
import * as s3 from 'aws-cdk-lib/aws-s3';

class KinesisFirehoseExample extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create an S3 bucket for data delivery
    const bucket = new s3.Bucket(this, 'DataBucket');

    // Create a Kinesis Firehose Delivery Stream
    const deliveryStream = new kinesisfirehose.CfnDeliveryStream(this, 'DeliveryStream', {
      deliveryStreamType: 'DirectPut',
      extendedS3DestinationConfiguration: {
        bucketArn: bucket.bucketArn,
        roleArn: 'YOUR_IAM_ROLE_ARN',
      },
    });
  }
}

const app = new cdk.App();
new KinesisFirehoseExample(app, 'KinesisFirehoseExample');
</contents>
</file>

<file>
<name>test_kinesis_firehose_client.py</name>
<contents>
import unittest
from unittest.mock import MagicMock, patch
import boto3

from kinesis_firehose_client import KinesisFirehoseClient
from exceptions import KinesisFirehoseError

class TestKinesisFirehoseClient(unittest.TestCase):
    @patch('boto3.client')
    def test_put_record(self, mock_client):
        mock_response = {'ResponseMetadata': {'HTTPStatusCode': 200}}
        mock_client.return_value.put_record.return_value = mock_response

        access_key_id = 'YOUR_ACCESS_KEY_ID'
        secret_access_key = 'YOUR_SECRET_ACCESS_KEY'
        region_name = 'us-east-1'
        delivery_stream_name = 'test-delivery-stream'
        record = 'test record'

        kinesis_client = KinesisFirehoseClient(access_key_id, secret_access_key, region_name)
        kinesis_client.put_record(delivery_stream_name, record)

        mock_client.return_value.put_record.assert_called_with(DeliveryStreamName=delivery_stream_name, Record={'Data': record})

    @patch('boto3.client')
    def test_put_record_batch(self, mock_client):
        mock_response = {'ResponseMetadata': {'HTTPStatusCode': 200}, 'FailedPutCount': 0}
        mock_client.return_value.put_record_batch.return_value = mock_response

        access_key_id = 'YOUR_ACCESS_KEY_ID'
        secret_access_key = 'YOUR_SECRET_ACCESS_KEY'
        region_name = 'us-east-1'
        delivery_stream_name = 'test-delivery-stream'
        records = ['record1', 'record2', 'record3']

        kinesis_client = KinesisFirehoseClient(access_key_id, secret_access_key, region_name)
        kinesis_client.put_record_batch(delivery_stream_name, records)

        expected_records = [{'Data': record} for record in records]
        mock_client.return_value.put_record_batch.assert_called_with(DeliveryStreamName=delivery_stream_name, Records=expected_records)

    @patch('boto3.client')
    def test_put_record_error(self, mock_client):
        mock_client.return_value.put_record.side_effect = Exception('Error putting record')

        access_key_id = 'YOUR_ACCESS_KEY_ID'
        secret_access_key = 'YOUR_SECRET_ACCESS_KEY'
        region_name = 'us-east-1'
        delivery_stream_name = 'test-delivery-stream'
        record = 'test record'

        kinesis_client = KinesisFirehoseClient(access_key_id, secret_access_key, region_name)

        with self.assertRaises(KinesisFirehoseError):
            kinesis_client.put_record(delivery_stream_name, record)

if __name__ == '__main__':
    unittest.main()
</contents>
</file>

<file>
<name>test_utils.py</name>
<contents>
import unittest

from utils import validate_record_size
from exceptions import InvalidRecordSizeError

class TestUtils(unittest.TestCase):
    def test_validate_record_size_valid(self):
        record = 'a' * 1024 * 1024  # 1 MB
        validate_record_size(record)  # Should not raise an exception

    def test_validate_record_size_invalid(self):
        record = 'a' * (1024 * 1024 + 1)  # 1 MB + 1 byte
        with self.assertRaises(InvalidRecordSizeError):
            validate_record_size(record)

if __name__ == '__main__':
    unittest.main()
</contents>
</file>

<file>
<name>requirements.txt</name>
<contents>
boto3
backoff
</contents>
</file>

<file>
<name>README.md</name>
<contents>
# Kinesis Firehose Example

This is an example application that demonstrates how to use the AWS Kinesis Firehose service for PutRecord and PutRecordBatch operations, focusing on production-grade reliability and performance.

## Prerequisites

- Python 3.7 or later
- AWS account with appropriate permissions
- AWS credentials (access key and secret key)

## Setup

1. Clone the repository:

```
git clone https://github.com/your-repo/kinesis-firehose-example.git
cd kinesis-firehose-example
```

2. Install the required Python dependencies:

```
pip install -r requirements.txt
```

3. Set up the AWS credentials as environment variables:

```
export AWS_ACCESS_KEY_ID=your_access_key_id
export AWS_SECRET_ACCESS_KEY=your_secret_access_key
```

4. Deploy the AWS resources using the provided CDK script:

```
npx aws-cdk deploy
```

## Usage

Run the application with the required arguments:

```
python main.py --stream-name your_stream_name --bucket-name your_bucket_name --records "record1" "record2" "record3"
```

Replace `your_stream_name` and `your_bucket_name` with the names of the Kinesis Firehose Delivery Stream and S3 bucket created by the CDK script, respectively. The `--records` argument accepts a list of records to be put into the Delivery Stream.

## Testing

Run the unit tests:

```
python -m unittest discover -s tests -p 'test_*.py'
```

## Cleanup

To delete the AWS resources created by the CDK script, run:

```
npx aws-cdk destroy
```

## License

This project is licensed under the [MIT License](LICENSE).
</contents>
</file>

<file>
<name>.gitignore</name>
<contents>
__pycache__/
*.pyc
*.pyo
venv/
.env
</contents>
</file>