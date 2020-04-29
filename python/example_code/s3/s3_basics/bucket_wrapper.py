# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrate basic bucket operations in Amazon S3.
    Learn how to create, list, delete, and configure buckets.
    Usage is shown in the test/test_bucket_wrapper.py file.

Running the tests
    The best way to learn how to use this service is to run the tests.
    For instructions on testing, see the README.

Running the code
    Run the usage_demo function in a command window or individual functions in
    the Python shell to make calls to your AWS account.
    For instructions on running the code, see the README.

Additional information
    Running this code might result in charges to your AWS account.
"""

import json
import logging
import uuid

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

s3_resource = boto3.resource('s3')


def get_s3(region=None):
    """Get a Boto 3 S3 resource with a specific Region or with your default Region."""
    global s3_resource
    if not region or s3_resource.meta.client.meta.region_name == region:
        return s3_resource
    else:
        return boto3.resource('s3', region_name=region)


def create_bucket(name, region=None):
    """
    Create an Amazon S3 bucket with the specified name and in the specified Region.

    Usage is shown in usage_demo at the end of this module.

    :param name: The name of the bucket to create. This name must be globally unique
                 and must adhere to bucket naming requirements.
    :param region: The Region in which to create the bucket. If this is not specified,
                   the Region configured in your shared credentials is used. If no
                   Region is configured, 'us-east-1' is used.
    :return: The newly created bucket.
    """
    s3 = get_s3(region)

    try:
        if region:
            bucket = s3.create_bucket(
                Bucket=name,
                CreateBucketConfiguration={
                    'LocationConstraint': region
                }
            )
        else:
            bucket = s3.create_bucket(Bucket=name)

        bucket.wait_until_exists()

        logger.info("Created bucket '%s' in region=%s", bucket.name,
                    s3.meta.client.meta.region_name)
    except ClientError as error:
        logger.exception("Couldn't create bucket named '%s' in region=%s.",
                         name, region)
        if error.response['Error']['Code'] == 'IllegalLocationConstraintException':
            logger.error("When the session Region is anything other than us-east-1, "
                         "you must specify a LocationConstraint that matches the "
                         "session Region. The current session Region is %s and the "
                         "LocationConstraint Region is %s.",
                         s3.meta.client.meta.region_name, region)
        raise error
    else:
        return bucket


def bucket_exists(bucket_name):
    """
    Determine whether a bucket with the specified name exists.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to check.
    :return: True when the bucket exists; otherwise, False.
    """
    s3 = get_s3()
    try:
        s3.meta.client.head_bucket(Bucket=bucket_name)
        logger.info("Bucket %s exists.", bucket_name)
        exists = True
    except ClientError:
        logger.warning("Bucket %s doesn't exist or you don't have access to it.",
                       bucket_name)
        exists = False
    return exists


def get_buckets():
    """
    Get the buckets in all Regions for the current account.

    Usage is shown in usage_demo at the end of this module.

    :return: The list of buckets.
    """
    s3 = get_s3()
    try:
        buckets = list(s3.buckets.all())
        logger.info("Got buckets: %s.", buckets)
    except ClientError:
        logger.exception("Couldn't get buckets.")
        raise
    else:
        return buckets


def delete_bucket(bucket):
    """
    Delete a bucket. The bucket must be empty or an error is raised.

    Usage is shown in usage_demo at the end of this module.

    :param bucket: The bucket to delete.
    """
    try:
        bucket.delete()
        bucket.wait_until_not_exists()
        logger.info("Bucket %s successfully deleted.", bucket.name)
    except ClientError:
        logger.exception("Couldn't delete bucket %s.", bucket.name)
        raise


def grant_log_delivery_access(bucket_name):
    """
    Grant the AWS Log Delivery group write access to the specified bucket so that
    Amazon S3 can deliver access logs to the bucket. This is the only recommended
    use of an S3 bucket ACL.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to receive access logs.
    """

    s3 = get_s3()
    try:
        acl = s3.Bucket(bucket_name).Acl()
        # Putting an ACL overwrites the existing ACL. If you want to preserve
        # existing grants, append new grants to the list of existing grants.
        grants = acl.grants if acl.grants else []
        grants.append({
            'Grantee': {
                'Type': 'Group',
                'URI': 'http://acs.amazonaws.com/groups/s3/LogDelivery'
            },
            'Permission': 'WRITE'
        })
        acl.put(
            AccessControlPolicy={
                'Grants': grants,
                'Owner': acl.owner
            }
        )
        logger.info("Granted log delivery access to bucket '%s'", bucket_name)
    except ClientError:
        logger.exception("Couldn't add ACL to bucket '%s'.", bucket_name)
        raise


def get_acl(bucket_name):
    """
    Get the ACL of the specified bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to retrieve.
    :return: The ACL of the bucket.
    """
    s3 = get_s3()
    try:
        acl = s3.Bucket(bucket_name).Acl()
        logger.info("Got ACL for bucket %s owned by %s.",
                    bucket_name, acl.owner['DisplayName'])
    except ClientError:
        logger.exception("Couldn't get ACL for bucket %s.", bucket_name)
        raise
    else:
        return acl


def put_cors(bucket_name, cors_rules):
    """
    Apply CORS rules to a bucket. CORS rules specify the HTTP actions that are
    allowed from other domains.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket where the rules are applied.
    :param cors_rules: The CORS rules to apply.
    """
    s3 = get_s3()
    try:
        s3.Bucket(bucket_name).Cors().put(CORSConfiguration={
            'CORSRules': cors_rules
        })
        logger.info("Put CORS rules %s for bucket '%s'.", cors_rules, bucket_name)
    except ClientError:
        logger.exception("Couldn't put CORS rules for bucket %s.", bucket_name)
        raise


def get_cors(bucket_name):
    """
    Get the CORS rules for the specified bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to check.
    :return The CORS rules for the specified bucket.
    """
    s3 = get_s3()
    try:
        cors = s3.Bucket(bucket_name).Cors()
        logger.info("Got CORS rules %s for bucket '%s'.", cors.cors_rules, bucket_name)
    except ClientError:
        logger.exception(("Couldn't get CORS for bucket %s.", bucket_name))
        raise
    else:
        return cors


def delete_cors(bucket_name):
    """
    Delete the CORS rules from the specified bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to update.
    """
    s3 = get_s3()
    try:
        s3.Bucket(bucket_name).Cors().delete()
        logger.info("Deleted CORS from bucket '%s'.", bucket_name)
    except ClientError:
        logger.exception("Couldn't delete CORS from bucket '%s'.", bucket_name)
        raise


def put_policy(bucket_name, policy):
    """
    Apply a security policy to a bucket. Policies typically grant users the ability
    to perform specific actions, such as listing the objects in the bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to receive the policy.
    :param policy: The policy to apply to the bucket.
    """
    s3 = get_s3()
    try:
        # The policy must be in JSON format.
        s3.Bucket(bucket_name).Policy().put(Policy=json.dumps(policy))
        logger.info("Put policy %s for bucket '%s'.", policy, bucket_name)
    except ClientError:
        logger.exception("Couldn't apply policy to bucket '%s'.", bucket_name)
        raise


def get_policy(bucket_name):
    """
    Get the security policy of a bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The bucket to retrieve.
    :return: The security policy of the specified bucket.
    """
    s3 = get_s3()
    try:
        policy = s3.Bucket(bucket_name).Policy()
        logger.info("Got policy %s for bucket '%s'.", policy.policy, bucket_name)
    except ClientError:
        logger.exception("Couldn't get policy for bucket '%s'.", bucket_name)
        raise
    else:
        return json.loads(policy.policy)


def delete_policy(bucket_name):
    """
    Delete the security policy from the specified bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to update.
    """
    s3 = get_s3()
    try:
        s3.Bucket(bucket_name).Policy().delete()
        logger.info("Deleted policy for bucket '%s'.", bucket_name)
    except ClientError:
        logger.exception("Couldn't delete policy for bucket '%s'.", bucket_name)
        raise


def put_lifecycle_configuration(bucket_name, lifecycle_rules):
    """
    Apply a lifecycle configuration to a bucket. The lifecycle configuration can
    be used to archive or delete the objects in the bucket according to specified
    parameters, such as a number of days.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to update.
    :param lifecycle_rules: The lifecycle rules to apply.
    """
    s3 = get_s3()
    try:
        s3.Bucket(bucket_name).LifecycleConfiguration().put(
            LifecycleConfiguration={
                'Rules': lifecycle_rules
            }
        )
        logger.info("Put lifecycle rules %s for bucket '%s'.", lifecycle_rules,
                    bucket_name)
    except ClientError:
        logger.exception("Couldn't put lifecycle rules for bucket '%s'.", bucket_name)
        raise


def get_lifecycle_configuration(bucket_name):
    """
    Get the lifecycle configuration of the specified bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to retrieve.
    :return: The lifecycle rules of the specified bucket.
    """
    s3 = get_s3()
    try:
        config = s3.Bucket(bucket_name).LifecycleConfiguration()
        logger.info("Got lifecycle rules %s for bucket '%s'.",
                    config.rules, bucket_name)
    except:
        logger.exception("Couldn't get lifecycle configuration for bucket '%s'.",
                         bucket_name)
        raise
    else:
        return config.rules


def delete_lifecycle_configuration(bucket_name):
    """
    Remove the lifecycle configuration from the specified bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket to update.
    """
    s3 = get_s3()
    try:
        s3.Bucket(bucket_name).LifecycleConfiguration().delete()
        logger.info("Deleted lifecycle configuration for bucket '%s'.", bucket_name)
    except ClientError:
        logger.exception("Couldn't delete lifecycle configuration for bucket '%s'.",
                         bucket_name)
        raise


def generate_presigned_post(bucket_name, object_key, expires_in):
    """
    Generate a presigned Amazon S3 POST request to upload a file.
    A presigned POST can be used for a limited time to let someone without an AWS
    account upload a file to a bucket.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket that receives the posted object.
    :param object_key: The object key to identify the uploaded object.
    :param expires_in: The number of seconds the presigned POST is valid.
    :return: A dictionary that contains the URL and form fields that contain
             required access data.
    """
    s3 = get_s3()
    try:
        response = s3.meta.client.generate_presigned_post(
            Bucket=bucket_name, Key=object_key, ExpiresIn=expires_in)
        logger.info("Got presigned POST URL: %s", response['url'])
    except ClientError:
        logger.exception("Couldn't get a presigned POST URL for bucket '%s' "
                         "and object '%s'", bucket_name, object_key)
        raise
    return response


def generate_presigned_url(bucket_name, client_method, method_parameters,
                           expires_in):
    """
    Generate a presigned Amazon S3 URL that can be used to perform an action on
    a bucket. A presigned URL can be used for a limited time to let someone without
    an AWS account perform an action.

    Usage is shown in usage_demo at the end of this module.

    :param bucket_name: The name of the bucket on which the action can be performed.
    :param client_method: The name of the client method that the URL performs.
    :param method_parameters: The parameters of the specified client method.
    :param expires_in: The number of seconds the presigned URL is valid for.
    :return: The presigned URL.
    """
    s3 = get_s3()
    try:
        url = s3.meta.client.generate_presigned_url(
            ClientMethod=client_method,
            Params=method_parameters,
            ExpiresIn=expires_in
        )
        logger.info("Got presigned URL: %s", url)
    except ClientError:
        logger.exception("Couldn't get a presigned URL for bucket '%s' "
                         "and client method '%s'.",
                         bucket_name, client_method)
        raise
    return url


def usage_demo():
    """Demonstrates ways to use the functions in this module."""
    prefix = 'usage-demo-bucket-wrapper-'

    created_buckets = [create_bucket(prefix + str(uuid.uuid1()),
                                     s3_resource.meta.client.meta.region_name)
                       for _ in range(3)]
    for bucket in created_buckets:
        print(f"Created bucket {bucket.name}.")

    bucket_to_delete = created_buckets.pop()
    if bucket_exists(bucket_to_delete.name):
        print(f"Bucket exists: {bucket_to_delete.name}.")
    delete_bucket(bucket_to_delete)
    print(f"Deleted bucket {bucket_to_delete.name}.")
    if not bucket_exists(bucket_to_delete.name):
        print(f"Bucket no longer exists: {bucket_to_delete.name}.")

    buckets = [b for b in get_buckets() if b.name.startswith(prefix)]
    for bucket in buckets:
        print(f"Got bucket {bucket.name}.")

    bucket = created_buckets[0]
    grant_log_delivery_access(bucket.name)
    acl = get_acl(bucket.name)
    print(f"Bucket {bucket.name} has ACL grants: {acl.grants}.")

    put_rules = [{
        'AllowedOrigins': ['http://www.example.com'],
        'AllowedMethods': ['PUT', 'POST', 'DELETE'],
        'AllowedHeaders': ['*']
    }]
    put_cors(bucket.name, put_rules)
    get_rules = get_cors(bucket.name)
    print(f"Bucket {bucket.name} has CORS rules: {json.dumps(get_rules.cors_rules)}.")
    delete_cors(bucket.name)

    put_policy_desc = {
        'Version': '2012-10-17',
        'Id': str(uuid.uuid1()),
        'Statement': [{
            'Effect': 'Allow',
            'Principal': {'AWS': 'arn:aws:iam::111122223333:user/Martha'},
            'Action': [
                's3:GetObject',
                's3:ListBucket'
            ],
            'Resource': [
                f'arn:aws:s3:::{bucket.name}/*',
                f'arn:aws:s3:::{bucket.name}'
            ]
        }]
    }
    try:
        put_policy(bucket.name, put_policy_desc)
        policy = get_policy(bucket.name)
        print(f"Bucket {bucket.name} has policy {json.dumps(policy)}.")
        delete_policy(bucket.name)
    except ClientError as error:
        if error.response['Error']['Code'] == 'MalformedPolicy':
            print("Couldn't put the policy because the specified principal user does "
                  "not exist. For this request to succeed, you must replace the user "
                  "ARN with an actual AWS user.")
        else:
            raise

    put_rules = [{
        'ID': str(uuid.uuid1()),
        'Filter': {
            'And': {
                'Prefix': 'monsters/',
                'Tags': [{'Key': 'type', 'Value': 'zombie'}]
            }
        },
        'Status': 'Enabled',
        'Expiration': {'Days': 28}
    }]
    put_lifecycle_configuration(bucket.name, put_rules)
    get_rules = get_lifecycle_configuration(bucket.name)
    print(f"Bucket {bucket.name} has lifecycle configuration {json.dumps(get_rules)}.")
    delete_lifecycle_configuration(bucket.name)

    url = generate_presigned_url(
        bucket.name,
        'list_objects',
        {'Bucket': bucket.name},
        10
    )
    print(f"Generated a pre-signed URL that can be used to list the objects in "
          f"bucket {bucket.name}. The URL is {url}.")

    for bucket in created_buckets:
        bucket.delete()
        print(f"Deleted bucket {bucket.name}.")


def main():
    go = input("Running the usage demonstration uses your default AWS account "
               "credentials and might incur charges on your account. Do you want "
               "to continue (y/n)? ")
    if go.lower() == 'y':
        print("Starting the usage demo. Enjoy!")
        usage_demo()
    else:
        print("Thanks anyway!")


if __name__ == '__main__':
    main()
