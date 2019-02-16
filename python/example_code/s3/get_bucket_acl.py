# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[get_bucket_acl.py demonstrates how to retrieve the access control list of an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-15]
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


def get_bucket_acl(bucket_name):
    """Retrieve the access control list of an Amazon S3 bucket

    :param bucket_name: string
    :return: Bucket access control list of grantees and permissions. If error,
    return None.
    """

    # Retrieve the bucket ACL
    s3 = boto3.client('s3')
    try:
        response = s3.get_bucket_acl(Bucket=bucket_name)
    except ClientError as e:
        # AllAccessDisabled error == bucket not found
        logging.error(e)
        return None
    return response['Grants']


def main():
    """ Exercise get_bucket_acl()"""

    # Assign this value before running the program
    test_bucket_name = 'BUCKET_NAME'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Retrieve the bucket ACL
    acl = get_bucket_acl(test_bucket_name)
    if acl is not None:
        # Output the bucket ACL grantees and permissions
        for grantee in acl:
            # The grantee type determines the grantee_identifier
            grantee_type = grantee['Grantee']['Type']
            if grantee_type == 'CanonicalUser':
                grantee_identifier = grantee['Grantee']['DisplayName']
            elif grantee_type == 'AmazonCustomerByEmail':
                grantee_identifier = grantee['Grantee']['EmailAddress']
            elif grantee_type == 'Group':
                grantee_identifier = grantee['Grantee']['URI']
            else:
                grantee_identifier = 'Unknown'
            logging.info(f'Grantee: {grantee_identifier}, '
                         f'Permissions: {grantee["Permission"]}')


if __name__ == '__main__':
    main()
