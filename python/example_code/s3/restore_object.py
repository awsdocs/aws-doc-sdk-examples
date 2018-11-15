# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


import boto3


def restore_object(bucket_name, object_name, days, retrieval_type='Standard'):
    """Restore an archived S3 Glacier object in an Amazon S3 bucket.

    :param bucket_name: string
    :param object_name: string
    :param days: number of days to retain restored object
    :param retrieval_type: 'Standard' | 'Expedited' | 'Bulk'
    :return: True if a request to restore archived object was submitted, otherwise False
    """

    # Create request to restore object
    request = {'Days': days,
               'GlacierJobParameters': {'Tier': retrieval_type}}

    s3 = boto3.client('s3')
    try:
        s3.restore_object(Bucket=bucket_name, Key=object_name, RestoreRequest=request)
    except Exception as e:
        # e.response['Error']['Code'] == 'NoSuchBucket', 'NoSuchKey', 'InvalidObjectState' (object's storage class is
        # not GLACIER)
        return False
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    test_object_name = 'test-object-name'

    # Restore archived object for two days. Expedite the restoration.
    result = restore_object(test_bucket_name, test_object_name, 2, 'Expedited')

    if not result:
        print('ERROR: Could not restore {0} in {1}'.format(test_object_name, test_bucket_name))
    else:
        print('Submitted request to restore {0} in {1}'.format(test_object_name, test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[restore_object.py demonstrates how to restore an archived S3 Glacier object in an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-14]
# snippet-sourceauthor:[scalwas (AWS)]
