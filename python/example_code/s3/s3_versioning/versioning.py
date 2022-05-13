# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Demonstrates how to set up an Amazon S3 bucket for versioning, and how to perform
basic tasks on a version-enabled bucket.

For more detail, including usage and testing instructions, see the README.

Running this demo uses your default AWS credentials to create resources in your
account and my incur charges.
"""

import logging
from operator import attrgetter
from shutil import get_terminal_size
from sys import stdout
import uuid

import boto3
from botocore.exceptions import ClientError

logging.basicConfig(
    format='%(levelname)s:%(message)s', level=logging.INFO, stream=stdout)
logger = logging.getLogger(__name__)

s3 = boto3.resource('s3')


# snippet-start:[s3.python.versioning.create_versioned_bucket]
def create_versioned_bucket(bucket_name, prefix):
    """
    Creates an Amazon S3 bucket, enables it for versioning, and configures a lifecycle
    that expires noncurrent object versions after 7 days.

    Adding a lifecycle configuration to a versioned bucket is a best practice.
    It helps prevent objects in the bucket from accumulating a large number of
    noncurrent versions, which can slow down request performance.

    Usage is shown in the usage_demo_single_object function at the end of this module.

    :param bucket_name: The name of the bucket to create.
    :param prefix: Identifies which objects are automatically expired under the
                   configured lifecycle rules.
    :return: The newly created bucket.
    """
    try:
        bucket = s3.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={
                'LocationConstraint': s3.meta.client.meta.region_name
            }
        )
        logger.info("Created bucket %s.", bucket.name)
    except ClientError as error:
        if error.response['Error']['Code'] == 'BucketAlreadyOwnedByYou':
            logger.warning("Bucket %s already exists! Using it.", bucket_name)
            bucket = s3.Bucket(bucket_name)
        else:
            logger.exception("Couldn't create bucket %s.", bucket_name)
            raise

    try:
        bucket.Versioning().enable()
        logger.info("Enabled versioning on bucket %s.", bucket.name)
    except ClientError:
        logger.exception("Couldn't enable versioning on bucket %s.", bucket.name)
        raise

    try:
        expiration = 7
        bucket.LifecycleConfiguration().put(
            LifecycleConfiguration={
                'Rules': [{
                    'Status': 'Enabled',
                    'Prefix': prefix,
                    'NoncurrentVersionExpiration': {'NoncurrentDays': expiration}
                }]
            }
        )
        logger.info("Configured lifecycle to expire noncurrent versions after %s days "
                    "on bucket %s.", expiration, bucket.name)
    except ClientError as error:
        logger.warning("Couldn't configure lifecycle on bucket %s because %s. "
                       "Continuing anyway.", bucket.name, error)

    return bucket
# snippet-end:[s3.python.versioning.create_versioned_bucket]


# snippet-start:[s3.python.versioning.rollback_object]
def rollback_object(bucket, object_key, version_id):
    """
    Rolls back an object to an earlier version by deleting all versions that
    occurred after the specified rollback version.

    Usage is shown in the usage_demo_single_object function at the end of this module.

    :param bucket: The bucket that holds the object to roll back.
    :param object_key: The object to roll back.
    :param version_id: The version ID to roll back to.
    """
    # Versions must be sorted by last_modified date because delete markers are
    # at the end of the list even when they are interspersed in time.
    versions = sorted(bucket.object_versions.filter(Prefix=object_key),
                      key=attrgetter('last_modified'), reverse=True)

    logger.debug(
        "Got versions:\n%s",
        '\n'.join([f"\t{version.version_id}, last modified {version.last_modified}"
                   for version in versions]))

    if version_id in [ver.version_id for ver in versions]:
        print(f"Rolling back to version {version_id}")
        for version in versions:
            if version.version_id != version_id:
                version.delete()
                print(f"Deleted version {version.version_id}")
            else:
                break

        print(f"Active version is now {bucket.Object(object_key).version_id}")
    else:
        raise KeyError(f"{version_id} was not found in the list of versions for "
                       f"{object_key}.")
# snippet-end:[s3.python.versioning.rollback_object]


# snippet-start:[s3.python.versioning.revive_object]
def revive_object(bucket, object_key):
    """
    Revives a versioned object that was deleted by removing the object's active
    delete marker.
    A versioned object presents as deleted when its latest version is a delete marker.
    By removing the delete marker, we make the previous version the latest version
    and the object then presents as *not* deleted.

    Usage is shown in the usage_demo_single_object function at the end of this module.

    :param bucket: The bucket that contains the object.
    :param object_key: The object to revive.
    """
    # Get the latest version for the object.
    response = s3.meta.client.list_object_versions(
        Bucket=bucket.name, Prefix=object_key, MaxKeys=1)

    if 'DeleteMarkers' in response:
        latest_version = response['DeleteMarkers'][0]
        if latest_version['IsLatest']:
            logger.info("Object %s was indeed deleted on %s. Let's revive it.",
                        object_key, latest_version['LastModified'])
            obj = bucket.Object(object_key)
            obj.Version(latest_version['VersionId']).delete()
            logger.info("Revived %s, active version is now %s  with body '%s'",
                        object_key, obj.version_id, obj.get()['Body'].read())
        else:
            logger.warning("Delete marker is not the latest version for %s!",
                           object_key)
    elif 'Versions' in response:
        logger.warning("Got an active version for %s, nothing to do.", object_key)
    else:
        logger.error("Couldn't get any version info for %s.", object_key)
# snippet-end:[s3.python.versioning.revive_object]


# snippet-start:[s3.python.versioning.permanently_delete_object]
def permanently_delete_object(bucket, object_key):
    """
    Permanently deletes a versioned object by deleting all of its versions.

    Usage is shown in the usage_demo_single_object function at the end of this module.

    :param bucket: The bucket that contains the object.
    :param object_key: The object to delete.
    """
    try:
        bucket.object_versions.filter(Prefix=object_key).delete()
        logger.info("Permanently deleted all versions of object %s.", object_key)
    except ClientError:
        logger.exception("Couldn't delete all versions of %s.", object_key)
        raise
# snippet-end:[s3.python.versioning.permanently_delete_object]


# snippet-start:[python.example_code.s3.Scenario_ObjectVersions]
def usage_demo_single_object(obj_prefix='demo-versioning/'):
    """
    Demonstrates usage of versioned object functions. This demo uploads a stanza
    of a poem and performs a series of revisions, deletions, and revivals on it.

    :param obj_prefix: The prefix to assign to objects created by this demo.
    """
    with open('father_william.txt') as file:
        stanzas = file.read().split('\n\n')

    width = get_terminal_size((80, 20))[0]
    print('-'*width)
    print("Welcome to the usage demonstration of Amazon S3 versioning.")
    print("This demonstration uploads a single stanza of a poem to an Amazon "
          "S3 bucket and then applies various revisions to it.")
    print('-'*width)
    print("Creating a version-enabled bucket for the demo...")
    bucket = create_versioned_bucket('bucket-' + str(uuid.uuid1()), obj_prefix)

    print("\nThe initial version of our stanza:")
    print(stanzas[0])

    # Add the first stanza and revise it a few times.
    print("\nApplying some revisions to the stanza...")
    obj_stanza_1 = bucket.Object(f'{obj_prefix}stanza-1')
    obj_stanza_1.put(Body=bytes(stanzas[0], 'utf-8'))
    obj_stanza_1.put(Body=bytes(stanzas[0].upper(), 'utf-8'))
    obj_stanza_1.put(Body=bytes(stanzas[0].lower(), 'utf-8'))
    obj_stanza_1.put(Body=bytes(stanzas[0][::-1], 'utf-8'))
    print("The latest version of the stanza is now:",
          obj_stanza_1.get()['Body'].read().decode('utf-8'),
          sep='\n')

    # Versions are returned in order, most recent first.
    obj_stanza_1_versions = bucket.object_versions.filter(Prefix=obj_stanza_1.key)
    print(
        "The version data of the stanza revisions:",
        *[f"    {version.version_id}, last modified {version.last_modified}"
            for version in obj_stanza_1_versions],
        sep='\n'
    )

    # Rollback two versions.
    print("\nRolling back two versions...")
    rollback_object(bucket, obj_stanza_1.key, list(obj_stanza_1_versions)[2].version_id)
    print("The latest version of the stanza:",
          obj_stanza_1.get()['Body'].read().decode('utf-8'),
          sep='\n')

    # Delete the stanza
    print("\nDeleting the stanza...")
    obj_stanza_1.delete()
    try:
        obj_stanza_1.get()
    except ClientError as error:
        if error.response['Error']['Code'] == 'NoSuchKey':
            print("The stanza is now deleted (as expected).")
        else:
            raise

    # Revive the stanza
    print("\nRestoring the stanza...")
    revive_object(bucket, obj_stanza_1.key)
    print("The stanza is restored! The latest version is again:",
          obj_stanza_1.get()['Body'].read().decode('utf-8'),
          sep='\n')

    # Permanently delete all versions of the object. This cannot be undone!
    print("\nPermanently deleting all versions of the stanza...")
    permanently_delete_object(bucket, obj_stanza_1.key)
    obj_stanza_1_versions = bucket.object_versions.filter(Prefix=obj_stanza_1.key)
    if len(list(obj_stanza_1_versions)) == 0:
        print("The stanza has been permanently deleted and now has no versions.")
    else:
        print("Something went wrong. The stanza still exists!")

    print(f"\nRemoving {bucket.name}...")
    bucket.delete()
    print(f"{bucket.name} deleted.")
    print("Demo done!")
# snippet-end:[python.example_code.s3.Scenario_ObjectVersions]


def main():
    """Kick off the demo."""
    usage_demo_single_object()


if __name__ == '__main__':
    main()
