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