# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_bucket_acl.py demonstrates how to set the access control list for an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-13]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import logging
import boto3
from botocore.exceptions import ClientError


def put_bucket_acl(bucket_name, acl_canned):
    """Set the access control list of an Amazon S3 bucket

    :param bucket_name: string
    :param acl_canned: string for canned ACL ('private', 'public-read', etc.
    :return: True if ACL was set, otherwise False
    """

    # Set the ACL
    s3 = boto3.client('s3')
    try:
        s3.put_bucket_acl(Bucket=bucket_name, ACL=acl_canned)
    except ClientError as e:
        # AllAccessDisabled error == bucket not found
        # AccessDenied error == bucket prohibits public access
        logging.error(e)
        return False
    return True


def main():
    """Exercise put_bucket_acl()"""

    # Assign this value before running the program
    test_bucket_name = 'BUCKET_NAME'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Set a canned ACL
    success = put_bucket_acl(test_bucket_name, 'private')
    if success:
        logging.info(f'The ACL was set for {test_bucket_name}')


if __name__ == '__main__':
    main()
