# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
A Lambda handler that receives an Amazon S3 batch event. The handler unpacks the
event and applies the specified revision to the specified object.
"""

# snippet-start:[s3.python.lambda.revise_stanza]
import logging
from urllib import parse
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
logger.setLevel('INFO')

s3 = boto3.resource('s3')


def lambda_handler(event, context):
    """
    Applies the specified revision to the specified object.

    :param event: The S3 batch event that contains the ID of the object to revise
                  and the revision type to apply.
    :param context: Context about the event.
    :return: A result structure that Amazon S3 uses to interpret the result of the
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
    # The revision type is packed with the object key as a pipe-delimited string.
    obj_key, revision = \
        parse.unquote(task['s3Key'], encoding='utf-8').split('|')
    bucket_name = task['s3BucketArn'].split(':')[-1]

    logger.info("Got task: apply revision %s to %s.", revision, obj_key)

    try:
        stanza_obj = s3.Bucket(bucket_name).Object(obj_key)
        stanza = stanza_obj.get()['Body'].read().decode('utf-8')
        if revision == 'lower':
            stanza = stanza.lower()
        elif revision == 'upper':
            stanza = stanza.upper()
        elif revision == 'reverse':
            stanza = stanza[::-1]
        elif revision == 'delete':
            pass
        else:
            raise TypeError(f"Can't handle revision type '{revision}'.")

        if revision == 'delete':
            stanza_obj.delete()
            result_string = f"Deleted stanza {stanza_obj.key}."
        else:
            stanza_obj.put(Body=bytes(stanza, 'utf-8'))
            result_string = f"Applied revision type '{revision}' to " \
                            f"stanza {stanza_obj.key}."

        logger.info(result_string)
        result_code = 'Succeeded'
    except ClientError as error:
        if error.response['Error']['Code'] == 'NoSuchKey':
            result_code = 'Succeeded'
            result_string = f"Stanza {obj_key} not found, assuming it was deleted " \
                            f"in an earlier revision."
            logger.info(result_string)
        else:
            result_code = 'PermanentFailure'
            result_string = f"Got exception when applying revision type '{revision}' " \
                            f"to {obj_key}: {error}."
            logger.exception(result_string)
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
# snippet-end:[s3.python.lambda.revise_stanza]
