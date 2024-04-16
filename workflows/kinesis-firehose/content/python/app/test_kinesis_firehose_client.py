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