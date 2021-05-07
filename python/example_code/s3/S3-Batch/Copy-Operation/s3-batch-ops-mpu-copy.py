import boto3
import os
from urllib import parse
from botocore.client import Config
from botocore.exceptions import ClientError as S3ClientError
from boto3.s3.transfer import TransferConfig
import logging

# Define Environmental Variables
target_bucket = os.environ['destination_bucket']
my_max_pool_connections = int(os.environ['max_pool_connections'])
my_max_concurrency = int(os.environ['max_concurrency'])
my_multipart_chunksize = int(os.environ['multipart_chunksize'])
my_max_attempts = int(os.environ['max_attempts'])

# Set and Declare COnfiguration Parameters
transfer_config = TransferConfig(max_concurrency=my_max_concurrency, multipart_chunksize=my_multipart_chunksize)
config = Config(max_pool_connections=my_max_pool_connections, retries = {'max_attempts': my_max_attempts})


# Instantiate S3Client
s3Client = boto3.resource('s3',config=config)

# # Set up logging
# logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(asctime)s: %(message)s')
logger = logging.getLogger(__name__)
logger.setLevel('INFO')

# Enable Verbose logging for Troubleshooting
# boto3.set_stream_logger("")


def lambda_handler(event, context):
    # Parse job parameters from Amazon S3 batch operations
    jobId = event['job']['id']
    invocationId = event['invocationId']
    invocationSchemaVersion = event['invocationSchemaVersion']

    # Prepare results
    results = []

    # Parse Amazon S3 Key, Key Version, and Bucket ARN
    taskId = event['tasks'][0]['taskId']
    # use unquote_plus to handle various characters in S3 Key name
    s3Key = parse.unquote_plus(event['tasks'][0]['s3Key'], encoding='utf-8')
    s3VersionId = event['tasks'][0]['s3VersionId']
    s3BucketArn = event['tasks'][0]['s3BucketArn']
    s3Bucket = s3BucketArn.split(':')[-1]

    try:
        # Prepare result code and string
        resultCode = None
        resultString = None
        # Construct Copy Object
        copy_source = {'Bucket': s3Bucket, 'Key': s3Key}
        if s3VersionId is not None:
            copy_source['VersionId'] = s3VersionId

        # Construct New Path
        # Construct New Key
        newKey = s3Key
        newBucket = target_bucket

        # Initiate the Actual Copy Operation and include transfer config option
        logger.info(f"starting copy between SOURCEBUCKET: {s3Bucket} and DESTINATIONBUCKET: {newBucket}")
        response = s3Client.meta.client.copy(copy_source, newBucket, newKey, Config=transfer_config)
        # Confirm copy was successful
        logger.info("Successfully completed the copy process!")

        # Mark as succeeded
        resultCode = 'Succeeded'
        resultString = str(response)
    except S3ClientError as e:
        # log errors, some errors does not have a response, so handle them
        logger.error(f"Unable to complete requested operation, see Clienterror details below:")
        if e.response is not None:
            logger.error(e.response)
            errorCode = e.response.get('Error').get('Code')
            errorMessage = e.response.get('Error').get('Message')
            errorS3RequestID = e.response.get('ResponseMetadata').get('RequestId')
            errorS3ExtendedRequestID = e.response.get('ResponseMetadata').get('HostId')
            if errorCode == 'RequestTimeout':
                resultCode = 'TemporaryFailure'
                resultString = 'Retry request to Amazon S3 due to timeout.'
            else: # Ensure the Batch Reporting includes Support Information
                resultCode = 'PermanentFailure'
                resultString = '{}: {}: {}: {}'.format(errorCode, errorMessage, errorS3RequestID, errorS3ExtendedRequestID)
        else:
            logger.error(e)
            resultCode = 'PermanentFailure'
            resultString = '{}'.format(e)
    except Exception as e:
        # log errors, some errors does not have a response, so handle them
        logger.error(f"Unable to complete requested operation, see AWS Service error details below:")
        if e.response is not None:
            logger.error(e.response)
            errorCode = e.response.get('Error').get('Code')
            errorMessage = e.response.get('Error').get('Message')
            errorS3RequestID = e.response.get('ResponseMetadata').get('RequestId')
            errorS3ExtendedRequestID = e.response.get('ResponseMetadata').get('HostId')
            resultString = '{}: {}: {}: {}'.format(errorCode, errorMessage, errorS3RequestID, errorS3ExtendedRequestID)

        else:
            logger.error(e)
            resultString = 'Exception: {}'.format(e)
        resultCode = 'PermanentFailure'
    finally:
        results.append({
            'taskId': taskId,
            'resultCode': resultCode,
            'resultString': resultString
        })

    return {
        'invocationSchemaVersion': invocationSchemaVersion,
        'treatMissingKeysAs': 'PermanentFailure',
        'invocationId': invocationId,
        'results': results
    }
