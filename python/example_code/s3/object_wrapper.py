# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrate basic object operations in Amazon Simple Storage Service (S3).
    Learn how to put, get, delete, and configure objects.
    Usage is shown in the test/test_object_wrapper.py file.

Running the tests
    The best way to learn how to use this service is to run the tests.
    For instructions on testing, see the ReadMe.

Running the code
    Run individual functions in the Python shell to make calls to your AWS account.
    For instructions on running the code, see the ReadMe.

Additional information
    Running this code might result in charges to your AWS account.
"""

import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def put_object(bucket, object_key, data):
    """
    Upload data to a bucket and identify it with the specified object key.

    :param bucket: The bucket to receive the data.
    :param object_key: The key of the object in the bucket.
    :param data: The data to upload. This can either be bytes or a string. When this
                 argument is a string, it is interpreted as a file name, which is
                 opened in read bytes mode.
    """
    put_data = data
    if isinstance(data, str):
        try:
            put_data = open(data, 'rb')
        except IOError:
            logger.exception("Expected file name or binary data, got '%s'.", data)
            raise

    try:
        obj = bucket.Object(object_key)
        obj.put(Body=put_data)
        obj.wait_until_exists()
        logger.info("Put object '%s' to bucket '%s'.", object_key, bucket.name)
    except ClientError:
        logger.exception("Couldn't put object '%s' to bucket '%s'.",
                         object_key, bucket.name)
        raise
    finally:
        if getattr(put_data, 'close', None):
            put_data.close()


def get_object(bucket, object_key):
    """
    Gets an object from a bucket.

    :param bucket: The bucket that contains the object.
    :param object_key: The key of the object to retrieve.
    :return: The object data in bytes.
    """
    try:
        body = bucket.Object(object_key).get()['Body'].read()
        logger.info("Got object '%s' from bucket '%s'.", object_key, bucket.name)
    except ClientError:
        logger.exception(("Couldn't get object '%s' from bucket '%s'.",
                          object_key, bucket.name))
        raise
    else:
        return body


def list_objects(bucket, prefix=None):
    """
    Lists the objects in a bucket, optionally filtered by a prefix.

    :param bucket: The bucket to query.
    :param prefix: When specified, only objects that start with this prefix are listed.
    :return: The list of objects.
    """
    try:
        if not prefix:
            objects = list(bucket.objects.all())
        else:
            objects = list(bucket.objects.filter(Prefix=prefix))
        logger.info("Got objects %s from bucket '%s'",
                    [o.key for o in objects], bucket.name)
    except ClientError:
        logger.exception("Couldn't get objects for bucket '%s'.", bucket.name)
        raise
    else:
        return objects


def copy_object(source_bucket, source_object_key, dest_bucket, dest_object_key):
    """
    Copies an object from one bucket to another.

    :param source_bucket: The bucket that contains the source object.
    :param source_object_key: The key of the source object.
    :param dest_bucket: The bucket that receives the copied object.
    :param dest_object_key: The key of the copied object.
    :return: The new copy of the object.
    """
    try:
        obj = dest_bucket.Object(dest_object_key)
        obj.copy_from(CopySource={
            'Bucket': source_bucket.name,
            'Key': source_object_key
        })
        obj.wait_until_exists()
        logger.info("Copied object from %s/%s to %s/%s.",
                    source_bucket.name, source_object_key,
                    dest_bucket.name, dest_object_key)
    except ClientError:
        logger.exception("Couldn't copy object from %s/%s to %s/%s.",
                         source_bucket.name, source_object_key,
                         dest_bucket.name, dest_object_key)
        raise
    else:
        return obj


def delete_object(bucket, object_key):
    """
    Remove an object from a bucket.

    :param bucket: The bucket that contains the object.
    :param object_key: The key of the object to delete.
    """
    try:
        obj = bucket.Object(object_key)
        obj.delete()
        obj.wait_until_not_exists()
        logger.info("Deleted object '%s' from bucket '%s'.", object_key, bucket.name)
    except ClientError:
        logger.exception("Couldn't delete object '%s' from bucket '%s'.",
                         object_key, bucket.name)
        raise


def delete_objects(bucket, object_keys):
    """
    Removes a list of objects from a bucket.
    This operation is done as a batch in a single request.

    :param bucket: The bucket that contains the objects.
    :param object_keys: The list of keys that identify the objects to remove.
    :return: The response that contains data about which objects were deleted
             and any that could not be deleted.
    """
    try:
        response = bucket.delete_objects(Delete={
            'Objects': [{
                'Key': key
            } for key in object_keys]
        })
        if 'Deleted' in response:
            logger.info("Deleted objects '%s' from bucket '%s'.",
                        [del_obj['Key'] for del_obj in response['Deleted']],
                        bucket.name)
        if 'Errors' in response:
            logger.warning(
                "Sadly, could not delete objects '%s' from bucket '%s'.",
                [f"{del_obj['Key']}: {del_obj['Code']}"
                 for del_obj in response['Errors']],
                bucket.name)
    except ClientError:
        logger.exception()
        raise
    else:
        return response


def empty_bucket(bucket):
    """
    Remove all objects from a bucket.

    :param bucket: The bucket to empty.
    """
    try:
        bucket.objects.delete()
        logger.info("Emptied bucket '%s'.", bucket.name)
    except ClientError:
        logger.exception("Couldn't empty bucket '%s'.", bucket.name)
        raise


def put_acl(bucket, object_key, email):
    """
    Applies an ACL to an object that grants read access to an AWS user identified
    by email address.

    :param bucket: The bucket that contains the object.
    :param object_key: The key of the object to update.
    :param email: The email address of the user to grant access.
    """
    try:
        acl = bucket.Object(object_key).Acl()
        # Putting an ACL overwrites the existing ACL, so append new grants
        # if you want to preserve existing grants.
        grants = acl.grants if acl.grants else []
        grants.append({
            'Grantee': {
                'Type': 'AmazonCustomerByEmail',
                'EmailAddress': email
            },
            'Permission': 'READ'
        })
        acl.put(
            AccessControlPolicy={
                'Grants': grants,
                'Owner': acl.owner
            }
        )
        logger.info("Granted read access to %s.", email)
    except ClientError:
        logger.exception("Couldn't add ACL to object '%s'.", object_key)
        raise


def get_acl(bucket, object_key):
    """
    Get the ACL of an object.

    :param bucket: The bucket that contains the object.
    :param object_key: The key of the object to retrieve.
    :return: The ACL of the object.
    """
    try:
        acl = bucket.Object(object_key).Acl()
        logger.info("Got ACL for object %s owned by %s.",
                    object_key, acl.owner['DisplayName'])
    except ClientError:
        logger.exception("Couldn't get ACL for object %s.", object_key)
        raise
    else:
        return acl
