# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
A Lambda handler that receives an Amazon S3 batch event. The handler unpacks the
event and removes the specified delete marker from the bucket.
"""

# snippet-start:[s3.python.lambda.remove_delete_marker]
import logging
from urllib import parse
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
logger.setLevel('INFO')

s3 = boto3.client('s3')


def lambda_handler(event, context):
    """
    Removes a delete marker from the specified versioned object.

    :param event: The S3 batch event that contains the ID of the delete marker
                  to remove.
    :param context: Context about the event.
    :return: A result structure that Amazon S3 uses to interpret the result of the
             operation. When the result code is TemporaryFailure, S3 retries the
             operation.
    """
    # Parse job parameters from Amazon S3 batch operations
    invocation_id = event['invocationId']
    invocation_schema_version = event['invocationSchemaVersion']

    results = []
    result_code = None
    result_string = None

    task = event['tasks'][0]
    task_id = task['taskId']

    try:
        obj_key = parse.unquote(task['s3Key'], encoding='utf-8')
        obj_version_id = task['s3VersionId']
        bucket_name = task['s3BucketArn'].split(':')[-1]

        logger.info("Got task: remove delete marker %s from object %s.",
                    obj_version_id, obj_key)

        try:
            # If this call does not raise an error, the object version is not a delete
            # marker and should not be deleted.
            response = s3.head_object(
                Bucket=bucket_name, Key=obj_key, VersionId=obj_version_id)
            result_code = 'PermanentFailure'
            result_string = f"Object {obj_key}, ID {obj_version_id} is not " \
                            f"a delete marker."

            logger.debug(response)
            logger.warning(result_string)
        except ClientError as error:
            delete_marker = error.response['ResponseMetadata']['HTTPHeaders'] \
                .get('x-amz-delete-marker', 'false')
            if delete_marker == 'true':
                logger.info("Object %s, version %s is a delete marker.",
                            obj_key, obj_version_id)
                try:
                    s3.delete_object(
                        Bucket=bucket_name, Key=obj_key, VersionId=obj_version_id)
                    result_code = 'Succeeded'
                    result_string = f"Successfully removed delete marker " \
                                    f"{obj_version_id} from object {obj_key}."
                    logger.info(result_string)
                except ClientError as error:
                    # Mark request timeout as a temporary failure so it will be retried.
                    if error.response['Error']['Code'] == 'RequestTimeout':
                        result_code = 'TemporaryFailure'
                        result_string = f"Attempt to remove delete marker from  " \
                                        f"object {obj_key} timed out."
                        logger.info(result_string)
                    else:
                        raise
            else:
                raise ValueError(f"The x-amz-delete-marker header is either not "
                                 f"present or is not 'true'.")
    except Exception as error:
        # Mark all other exceptions as permanent failures.
        result_code = 'PermanentFailure'
        result_string = error
        logger.exception(error)
    finally:
        results.append({
            'taskId': task_id,
            'resultCode': result_code,
            'resultString': result_string
        })
    return {
        'invocationSchemaVersion': invocation_schema_version,
        'treatMissingKeysAs': 'PermanentFailure',
        'invocationId': invocation_id,
        'results': results
    }
# snippet-end:[s3.python.lambda.remove_delete_marker]
