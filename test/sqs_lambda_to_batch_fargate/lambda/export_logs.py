import boto3
import json
import os
import logging
import time

s3_client = boto3.client('s3')
s3_resource = boto3.resource('s3')
logs_client = boto3.client('logs')

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def handler(event, context):
    logger.info(f'INCOMING EVENT: {event}')
    if 'Batch Job State Change' in event['detail-type']:
        if 'FAILED' in event['detail']['status'] or 'SUCCEEDED' in event['detail']['status']:
            logger.info(f"BUCKET_NAME: {os.environ['BUCKET_NAME']}")
            try:
                log_group_name = '/aws/batch/job'

                # Get most recent stream
                log_streams = logs_client.describe_log_streams(
                    logGroupName=log_group_name,
                    orderBy='LastEventTime',
                    descending=True,
                    limit=1
                )

                # Get log events from the stream
                log_events = logs_client.get_log_events(
                    logGroupName=log_group_name,
                    logStreamName=log_streams['logStreams'][0]['logStreamName'],
                    startFromHead=True
                )

                # Generate a timestamp for the current time
                timestamp_str = int(time.time())
                file_name = f"{timestamp_str}.log"
                with open(f"/tmp/{file_name}", 'w') as output_file:
                    for event in log_events['events']:
                        log_entry = f"[{event['timestamp']}], {event['message']}\n"
                        output_file.write(log_entry)

                s3_client.upload_file(f"/tmp/{file_name}", os.environ['BUCKET_NAME'], file_name)

                s3_client.put_object(Body=f"/tmp/{file_name}", Bucket='datastack-biglogbucketad8dbf9d-1gnhomq809ht8', Key=f"{os.environ['LANGUAGE_NAME']}/{file_name})

                logger.info(f'Log data saved successfully: {file_name}')

                return {
                    'statusCode': 200,
                    'body': json.dumps('Log data saved to S3 successfully!')
                }

            except Exception as e:
                logger.error(json.dumps(f'Error: {str(e)}'))
                return {
                    'statusCode': 500,
                    'body': json.dumps(f'Error: {str(e)}')
                }
        elif 'TIMED_OUT' in event['detail']['status']:
            raise Exception("Job timed out. Contact application owner or increase time out threshold")
        else:
            logger.info(f"Non-triggering Batch status: STATUS: {event['detail']['status']}")
            return
    else:
        logger.info(f"Non-triggering Batch event: {event['detail-type']}")
        return
