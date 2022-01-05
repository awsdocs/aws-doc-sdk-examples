# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Demonstrates how to manipulate Amazon S3 versioned objects in batches by creating jobs
that call AWS Lambda functions to perform processing. The demo has three phases: setup,
processing, and teardown.
"""

from contextlib import contextmanager
from io import BytesIO
import json
import logging
import random
from shutil import get_terminal_size
from sys import stdout
import time
from urllib import parse
import uuid
from zipfile import ZipFile

import boto3
from botocore.exceptions import ClientError

import versioning

logging.basicConfig(
    format='%(levelname)s:%(message)s', level=logging.INFO, stream=stdout)
logger = logging.getLogger(__name__)

iam = boto3.resource('iam')
s3 = boto3.resource('s3')
s3control = boto3.client('s3control')
sts = boto3.client('sts')
aws_lambda = boto3.client('lambda')


@contextmanager
def header():
    """Prints a header wrapped in full-screen rules."""
    width = get_terminal_size((80, 20))[0]
    print('-'*width)
    yield
    print('-'*width)


def custom_retry(callback, error_code, max_tries):
    """
    Retries the callback function with an exponential backoff algorithm until
    the callback succeeds, raises a different error than the expected error,
    or exceeds the maximum number of tries.

    :param callback: The function to call.
    :param error_code: The expected error. When this error is raised, the callback is
                       retried. Otherwise, the error is raised.
    :param max_tries: The maximum number of times to try the callback function.
    :return: The response from the callback function when the callback function
             succeeds. Otherwise, None.
    """
    sleepy_time = 1
    tries = 1
    response = None
    while tries <= max_tries:
        try:
            response = callback()
            logger.debug("Successfully ran on try %s.", tries)
            break
        except ClientError as error:
            if error.response['Error']['Code'] == error_code:
                logger.debug("Got retryable error %s.", error_code)
                time.sleep(sleepy_time)
                sleepy_time = min(sleepy_time*2, 32)
                tries += 1
                if tries == max_tries:
                    logger.error("Call never succeeded after %s tries.", tries)
                    raise
            else:
                raise
    return response


def create_iam_role(role_name):
    """
    Creates an AWS Identity and Access Management (IAM) role and attached policy
    that has the permissions needed by the Lambda functions used in this demo.

    :param role_name: The name of the role.
    :return: The created role object.
    """
    policy_name = f'{role_name}-policy'

    try:
        # Defines the trust relationship that lets Lambda receive batch events.
        lambda_and_s3_batch_assume_role_policy = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "lambda.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                },
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "batchoperations.s3.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }

        role = iam.create_role(
            RoleName=role_name,
            AssumeRolePolicyDocument=json.dumps(lambda_and_s3_batch_assume_role_policy)
        )
        iam.meta.client.get_waiter('role_exists').wait(RoleName=role_name)
        logger.info("Created role %s.", role.name)
    except ClientError:
        logger.exception("Couldn't create role %s.", role_name)
        raise

    try:
        # The s3:ListBucket action is needed or Lambda functions receive AccessDenied
        # instead of NoSuchKey when they call s3.Object.get() on objects
        # that have been deleted.
        s3_and_invoke_policy = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "AwsVersionDemoPolicy",
                    "Effect": "Allow",
                    "Action": [
                        "s3:PutObject",
                        "s3:GetObject",
                        "s3:DeleteObjectVersion",
                        "s3:ListBucketVersions",
                        "s3:ListBucket",
                        "s3:DeleteObject",
                        "s3:GetObjectVersion",
                        "lambda:InvokeFunction"
                    ],
                    "Resource": "*"
                }
            ]
        }

        policy = iam.create_policy(
            PolicyName=policy_name,
            PolicyDocument=json.dumps(s3_and_invoke_policy)
        )
        iam.meta.client.get_waiter('policy_exists').wait(PolicyArn=policy.arn)
        logger.info("Created policy %s with arn %s.", policy_name, policy.arn)

        role.attach_policy(PolicyArn=policy.arn)
        time.sleep(1)
        logger.info("Attached policy %s to role %s.", policy_name, role.name)
    except ClientError:
        logger.exception("Couldn't create or attach policy %s.", policy_name)
        raise

    return role


def create_lambda_function(iam_role, function_name, function_file_name, handler,
                           description):
    """
    Creates a Lambda function.

    :param iam_role: The IAM role associated with the function.
    :param function_name: The name of the function.
    :param function_file_name: The local file name that contains the function code.
    :param handler: The fully qualified name of the handler function.
    :param description: A friendly description of the function's purpose.
    :return: The Amazon Resource Name (ARN) of the created function.
    """
    buffer = BytesIO()
    with ZipFile(buffer, 'w') as zipped:
        zipped.write(function_file_name)
    buffer.seek(0)
    zip_contents = buffer.read()

    try:
        print(f"Creating Lambda function {function_name}...")
        response = custom_retry(
            lambda: aws_lambda.create_function(
                FunctionName=function_name,
                Runtime='python3.8',
                Role=iam_role.arn,
                Handler=handler,
                Code={
                    'ZipFile': zip_contents,
                },
                Description=description,
                Publish=True
            ), 'InvalidParameterValueException', 5)
        function_arn = response['FunctionArn']
        logger.info("Created function '%s' with ARN: '%s'.",
                    function_name, response['FunctionArn'])
    except ClientError:
        logger.exception("Couldn't create function %s.", function_name)
        raise

    return function_arn


def create_and_fill_bucket(file_name, bucket_name, obj_prefix):
    """
    Creates a version-enabled bucket and fills it with initial stanza objects.

    :param file_name: The file that contains the poem to upload.
    :param bucket_name: The name of the bucket to create.
    :param obj_prefix: The prefix to assign to the uploaded stanzas.
    :return: The created bucket and stanza objects.
    """
    with open(file_name) as file:
        stanzas = file.read().split('\n\n')

    bucket = versioning.create_versioned_bucket(bucket_name, obj_prefix)

    try:
        # Fill the bucket with initial stanza objects.
        stanza_objects = []
        for index, stanza in enumerate(stanzas):
            obj = bucket.Object(f"{obj_prefix}stanza-{index}")
            obj.put(Body=bytes(stanza, 'utf-8'))
            stanza_objects.append(obj)
        print(f"Added {len(stanza_objects)} stanzas as objects to {bucket.name}.")
    except ClientError:
        logger.exception("Couldn't put initial stanza objects into bucket %s.",
                         bucket.name)
        raise

    return bucket, stanza_objects


def prepare_for_random_revisions(bucket, stanza_objects):
    """
    Makes a manifest to do a series of revisions as a batch.

    The manifest contains randomly picked revision types that are each packed
    with an object key as a pipe-delimited string.

    :param bucket: The bucket that contains the stanzas.
    :param stanza_objects: The stanza objects.
    :return: The manifest as a list of lines in CSV format.
    """
    revisions = ['lower', 'upper', 'reverse', 'delete']
    manifest_lines = []
    for _ in range(5):
        for stanza_obj in stanza_objects:
            revision = parse.quote(
                f"{stanza_obj.key}|{revisions[random.randrange(0, len(revisions))]}")
            manifest_lines.append(f"{bucket.name},{revision}")
    return manifest_lines


def prepare_for_revival(bucket, obj_prefix):
    """
    Makes a manifest for reviving any deleted objects in the bucket. A deleted
    object is one that has a delete marker as its latest version.

    :param bucket: The bucket that contains the stanzas.
    :param obj_prefix: The prefix of the uploaded stanzas.
    :return: The manifest as a list of lines in CSV format.
    """
    try:
        response = s3.meta.client.list_object_versions(
            Bucket=bucket.name, Prefix=f'{obj_prefix}stanza')
        manifest_lines = [
            f"{bucket.name},{parse.quote(marker['Key'])},{marker['VersionId']}"
            for marker in response['DeleteMarkers']
            if marker['IsLatest']
        ]
    except ClientError:
        logger.exception("Couldn't get object versions from %s.", bucket.name)
        raise
    return manifest_lines


def prepare_for_cleanup(bucket, obj_prefix, stanza_objects):
    """
    Makes a manifest for cleaning up all delete markers in the bucket. In practice,
    a large number of delete markers can slow down bucket performance so cleaning
    them up is a best practice.

    This function first creates a bunch of delete markers interspersed with
    non-delete marker versions by deleting and putting objects in a loop.

    :param bucket: The bucket that contains the stanzas.
    :param obj_prefix: The prefix of the uploaded stanzas.
    :param stanza_objects: The stanza objects.
    :return: The manifest as a list of lines in CSV format.
    """
    try:
        for stanza in stanza_objects:
            body = stanza.get()['Body'].read()
            for index in range(1, 7):
                if index & 0x1:
                    stanza.delete()
                else:
                    stanza.put(Body=body)
    except ClientError:
        logger.exception("Preparation for cleanup phase failed.")
        raise

    try:
        response = s3.meta.client.list_object_versions(
            Bucket=bucket.name, Prefix=f'{obj_prefix}stanza')

        version_count = len(response['Versions']) + len(response['DeleteMarkers'])
        print(f"Created a mess of delete markers. There are currently {version_count} "
              f"versions in {bucket.name}.")

        manifest_lines = [
            f"{bucket.name},{parse.quote(marker['Key'])},{marker['VersionId']}"
            for marker in response['DeleteMarkers']
        ]
    except ClientError:
        logger.exception("Couldn't get object versions from %s.", bucket.name)
        raise
    else:
        return manifest_lines


def create_batch_job(job, manifest):
    """
    Creates an Amazon S3 batch job. The manifest is uploaded to the S3
    bucket and the job is created. Then Amazon S3 processes the job asynchronously.
    Jobs can be queried or canceled by using the returned job ID.

    :param job: The information about the job to create.
    :param manifest: The manifest that defines the objects affected by the job.
    :return: The ID of the created job.
    """
    manifest_obj = manifest['bucket'].Object(manifest['key'])
    manifest_e_tag = None
    try:
        # Upload the manifest so the batch system can find it.
        response = manifest_obj.put(Body=bytes('\n'.join(manifest['lines']), 'utf-8'))
        if 'ETag' in response:
            manifest_e_tag = response['ETag']
        logger.info("Uploaded job manifest %s to bucket %s.",
                    manifest_obj.key, manifest['bucket'].name)
    except ClientError:
        logger.exception("Couldn't upload job manifest %s to bucket %s.",
                         manifest_obj.key, manifest['bucket'].name)
        raise

    manifest_fields = ['Bucket', 'Key']
    if manifest['has_versions']:
        manifest_fields.append('VersionId')
    try:
        response = s3control.create_job(
            AccountId=job['account_id'],
            ConfirmationRequired=False,
            Description=job['description'],
            Priority=1,
            RoleArn=job['role_arn'],
            Operation={
                'LambdaInvoke': {
                    'FunctionArn': job['function_arn']
                }
            },
            Manifest={
                'Spec': {
                    'Format': 'S3BatchOperations_CSV_20180820',
                    'Fields': manifest_fields
                },
                'Location': {
                    'ObjectArn':
                        f"arn:aws:s3:::{manifest['bucket'].name}/{manifest['key']}",
                    'ETag': manifest_e_tag if manifest_e_tag else manifest_obj.e_tag
                }
            },
            Report={
                'Bucket': f"arn:aws:s3:::{manifest['bucket'].name}",
                'Format': 'Report_CSV_20180820',
                'Enabled': True,
                'Prefix': manifest['obj_prefix'],
                'ReportScope': 'AllTasks'
            }
        )
        logger.info("Created job %s.", response['JobId'])
    except ClientError:
        logger.exception("Couldn't create job to run function %s on manifest %s.",
                         job['function_arn'], manifest_obj.key)
        raise

    return response['JobId']


def report_job_status(account_id, job_id):
    """
    Polls the specified job every second and reports the current status until
    the job completes.

    :param account_id: The ID of the account that owns the job.
    :param job_id: The ID of the job.
    """
    try:
        # Get job status until the job is complete, failed, or canceled.
        job_status = None
        print(f"Status of job {job_id}:")
        while job_status not in ('Complete', 'Failed', 'Cancelled'):
            prev_job_status = job_status
            job_status = s3control.describe_job(
                AccountId=account_id, JobId=job_id)['Job']['Status']
            if prev_job_status != job_status:
                print(job_status, end='')
            else:
                print('.', end='')
            stdout.flush()
            time.sleep(1)
        print('')
    except ClientError:
        logger.exception("Couldn't get status for job %s.", job_id)
        raise


def setup_demo(role_name, bucket_name, function_info, obj_prefix):
    """
    Sets up the demo. Creates the IAM role, Lambda functions, and S3 bucket
    that the demo uses.

    This function also has the side effect of filling the function_info
    dictionary with the Amazon Resource Names (ARNs) of the created functions.

    :param role_name: The name to give the IAM role.
    :param bucket_name: The name to give the S3 bucket.
    :param function_info: Information about the Lambda functions.
    :param obj_prefix: The prefix to assign to created resources.
    :return: The created role, bucket, and stanza objects.
    """
    with header():
        print("Setup phase!")

    print("Creating an IAM role for the Lambda function...")
    role = create_iam_role(role_name)

    print("Creating Lambda functions to handle batch operations...")
    for function_name in function_info.keys():
        info = function_info[function_name]
        info['arn'] = create_lambda_function(
            role, function_name, info['file_name'], info['handler'],
            info['description'])

    print("Creating a version-enabled bucket and filling it with initial stanzas...")
    bucket, stanza_objects = create_and_fill_bucket(
        'father_william.txt', bucket_name, obj_prefix)

    return role, bucket, stanza_objects


def usage_demo_batch_operations(
        role_arn, function_info, bucket, stanza_objects, obj_prefix):
    """
    Performs the main processing part of the usage demonstration.

    :param role_arn: The ARN of the role that the created jobs use.
    :param function_info: Information about the Lambda functions used in the demo.
    :param bucket: The bucket that contains all of the objects created by the demo.
    :param stanza_objects: The initial set of stanza objects created during setup.
    :param obj_prefix: The prefix to assign to resources and objects.
    """
    with header():
        print("Main processing phase!")

    account_id = sts.get_caller_identity()['Account']
    job = {'account_id': account_id, 'role_arn': role_arn}
    manifest = {'bucket': bucket, 'obj_prefix': obj_prefix, 'has_versions': False}

    with header():
        print("Creating a batch job to perform a series of random revisions on "
              "each stanza...")
    revision_manifest = prepare_for_random_revisions(bucket, stanza_objects)
    job['description'] = "Perform a series of random revisions to each stanza."
    job['function_arn'] = function_info['revise_stanza']['arn']
    manifest['key'] = f"{obj_prefix}revision-manifest.csv"
    manifest['lines'] = revision_manifest
    job_id = create_batch_job(job, manifest)
    report_job_status(account_id, job_id)

    try:
        print("The poetic product, after revisions:")
        stanza_objs = bucket.objects.filter(Prefix=f'{obj_prefix}stanza')
        stanza_count = len(list(stanza_objs))
        if stanza_count == 0:
            print("We deleted all of our stanzas!")
        else:
            print(f"Our poem is now only {stanza_count} stanzas.")
            for stanza_obj in stanza_objs:
                print(stanza_obj.get()['Body'].read().decode('utf-8'))
    except ClientError:
        logger.exception("Couldn't get stanzas from bucket %s.", bucket.name)
        raise

    # Revive deleted stanzas.
    with header():
        print("Creating a batch job to revive any stanzas that were deleted as part of "
              "the random revisions...")
    revival_manifest = prepare_for_revival(bucket, obj_prefix)
    job['description'] = "Remove delete markers."
    job['function_arn'] = function_info['remove_delete_marker']['arn']
    manifest['key'] = f"{obj_prefix}revival-manifest.csv"
    manifest['lines'] = revival_manifest
    manifest['has_versions'] = True
    job_id = create_batch_job(job, manifest)
    report_job_status(account_id, job_id)

    try:
        stanza_count = len(list(bucket.objects.filter(Prefix=f'{obj_prefix}stanza')))
        print(f"There are now {stanza_count} stanzas in {bucket.name}.")
    except ClientError:
        logger.exception("Couldn't get stanzas from bucket %s.", bucket.name)
        raise

    # Clean up all the delete markers.
    with header():
        print("Creating a batch job to clean up excess delete markers sprinkled "
              "throughout the bucket...")
    cleanup_manifest = prepare_for_cleanup(bucket, obj_prefix, stanza_objects)
    job['description'] = "Clean up all delete markers."
    job['function_arn'] = function_info['remove_delete_marker']['arn']
    manifest['key'] = f"{obj_prefix}cleanup-manifest.csv"
    manifest['lines'] = cleanup_manifest
    job_id = create_batch_job(job, manifest)
    report_job_status(account_id, job_id)

    try:
        version_count = len(list(bucket.object_versions.filter(
            Prefix=f'{obj_prefix}stanza')))
        print(f"After cleanup, there are now {version_count} versions "
              f"in {bucket.name}.")
    except ClientError:
        logger.exception("Couldn't get stanzas from bucket %s.", bucket.name)
        raise


def teardown_demo(role_name, function_info, bucket_name):
    """
    Tears down the demo. Deletes everything the demo created and returns the
    AWS account to its initial state. This is a good-faith effort. You should
    verify that all resources are deleted.

    :param role_name: The name of the IAM role to delete.
    :param function_info: Information about the Lambda functions to delete.
    :param bucket_name: The name of the bucket to delete. All objects in this bucket
                        are also deleted.
    """
    with header():
        print("Teardown phase!")

    print("\nDetaching policies and deleting the IAM role...")
    role = iam.Role(role_name)
    try:
        for policy in role.attached_policies.all():
            policy_name = policy.policy_name
            role.detach_policy(PolicyArn=policy.arn)
            policy.delete()
            logger.info("Detached and deleted policy %s.", policy_name)
        role.delete()
        logger.info("Deleted role %s.", role_name)
    except ClientError as error:
        logger.warning("Couldn't delete role %s because %s.", role_name, error)

    print("\nDeleting Lambda functions...")
    for function_name in function_info.keys():
        try:
            aws_lambda.delete_function(FunctionName=function_name)
            logger.info("Deleted Lambda function %s.", function_name)
        except ClientError as error:
            logger.warning(
                "Couldn't delete Lambda function %s because %s", function_name, error)

    print("\nEmptying and deleting the bucket...")
    bucket = s3.Bucket(bucket_name)
    try:
        bucket.object_versions.delete()
        print(f"Permanently deleted everything in {bucket.name}.")
    except ClientError as error:
        logger.warning("Couldn't empty bucket %s because %s.", bucket.name, error)

    try:
        bucket.delete()
        print(f"Deleted bucket {bucket.name}.")
    except ClientError as error:
        logger.warning("Couldn't delete bucket %s because %s.", bucket.name, error)


def main():
    """
    Kicks off the demo.
    """
    prefix = 'demo-versioning'
    obj_prefix = f'{prefix}/'
    bucket_name = f'{prefix}-bucket-{uuid.uuid1()}'
    role_name = f'{prefix}-s3-batch-role-{time.time_ns()}'
    function_info = {
        'revise_stanza': {
            'file_name': 'revise_stanza.py',
            'handler': 'revise_stanza.lambda_handler',
            'description': 'Applies a revision to a stanza.',
            'arn': None},
        'remove_delete_marker': {
            'file_name': 'remove_delete_marker.py',
            'handler': 'remove_delete_marker.lambda_handler',
            'description': 'Removes a delete marker from an object.',
            'arn': None}
    }

    with header():
        print("Welcome to the usage demonstration of Amazon S3 batch versioning.\n")
        print("This demonstration manipulates Amazon S3 objects in batches "
              "by creating jobs that call AWS Lambda functions to perform processing. "
              "It uses the stanzas from the poem 'You Are Old, Father William' "
              "by Lewis Carroll, treating each stanza as a separate object.")

    print("Let's do the demo.")
    role, bucket, stanza_objects = \
        setup_demo(role_name, bucket_name, function_info, obj_prefix)

    usage_demo_batch_operations(
        role.arn, function_info, bucket, stanza_objects, obj_prefix)

    teardown_demo(role_name, function_info, bucket_name)
    with header():
        print("Demo done!")


if __name__ == '__main__':
    main()
