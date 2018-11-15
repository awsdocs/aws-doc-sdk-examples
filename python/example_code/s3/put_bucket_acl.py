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


def put_bucket_acl(bucket_name, acl_canned):
    """Set the access control list of an Amazon S3 bucket.

    :param bucket_name: string
    :param acl_canned: string for canned ACL ('private', 'public-read', etc.
    :return: True if ACL was set, otherwise False
    """

    s3 = boto3.client('s3')
    try:
        s3.put_bucket_acl(Bucket=bucket_name, ACL=acl_canned)
    except Exception as e:
        # e.response['Error']['Code'] == 'AllAccessDisabled' if bucket does not exist
        return False
    return True


def main():
    test_bucket_name = 'test-bucket-name'

    # Set a canned ACL
    result = put_bucket_acl(test_bucket_name, 'public-read')

    if not result:
        print('ERROR: Could not set the ACL for {}'.format(test_bucket_name))
    else:
        print('The ACL was set for {}'.format(test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_bucket_acl.py demonstrates how to set the access control list for an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-08]
# snippet-sourceauthor:[scalwas (AWS)]
