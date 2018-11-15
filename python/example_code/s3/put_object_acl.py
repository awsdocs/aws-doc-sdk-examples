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

def put_object_acl(bucket_name, object_name, acl_canned):
    """Set the access control list of an Amazon S3 bucket object.

    :param bucket_name: string
    :param object_name: string
    :param acl_canned: string for canned ACL ('private', 'public-read', etc.
    :return: True if ACL was set, otherwise False
    """

    s3 = boto3.client('s3')
    try:
        s3.put_object_acl(Bucket=bucket_name, Key=object_name, ACL=acl_canned)
    except Exception as err:
        # err.response['Error']['Code'] == 'NoSuchKey', etc.
        return False
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    test_object_name = 'test-object-name'

    # Set a canned ACL
    result = put_object_acl(test_bucket_name, test_object_name, acl_canned='public-read')

    if not result:
        print('ERROR: Could not set the ACL for {}'.format(test_object_name))
    else:
        print('The ACL was set for {}'.format(test_object_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_object_acl.py demonstrates how to set the access control list for an Amazon S3 bucket object.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-08]
# snippet-sourceauthor:[scalwas (AWS)]
