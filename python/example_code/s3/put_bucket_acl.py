# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_bucket_acl.py demonstrates how to set the access control list for an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-03-07]
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

import copy
import logging
import boto3
from botocore.exceptions import ClientError


def get_bucket_acl(bucket_name):
    """Retrieve the access control list of an Amazon S3 bucket

    :param bucket_name: string
    :return: Dictionary defining the bucket's access control list consisting
     of owner and grants. If error, return None.
    """

    # Retrieve the bucket ACL
    s3 = boto3.client('s3')
    try:
        response = s3.get_bucket_acl(Bucket=bucket_name)
    except ClientError as e:
        # AllAccessDisabled error == bucket not found
        logging.error(e)
        return None

    # Return both the Owner and Grants keys
    # The Owner and Grants settings together form the Access Control Policy.
    # The Grants alone form the Access Control List.
    return {'Owner': response['Owner'], 'Grants': response['Grants']}


def put_bucket_acl(bucket_name, acl):
    """Set the access control list of an Amazon S3 bucket

    :param bucket_name: string
    :param acl: Dictionary defining the ACL consisting of grants and permissions
    :return: True if ACL was set, otherwise False
    """

    # Set the ACL
    s3 = boto3.client('s3')
    try:
        s3.put_bucket_acl(Bucket=bucket_name, AccessControlPolicy=acl)
    except ClientError as e:
        # AccessDenied error == bucket prohibits public access
        # AllAccessDisabled error == bucket not found
        # AmbiguousGrantByEmailAddress == email address is associated with
        #   multiple AWS accounts
        logging.error(e)
        return False
    return True


def main():
    """Exercise put_bucket_acl()"""

    # Assign these values before running the program
    test_bucket_name = 'BUCKET_NAME'
    new_grantee_canonical_user_id = 'AWS_USER_ID'
    # new_grantee_email = 'EMAIL_ADDRESS'   # Set AWS User ID or email, but not both
    new_grantee_permission = 'READ'         # Or 'FULL_CONTROL', etc.

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Get the bucket's current ACL
    acl = get_bucket_acl(test_bucket_name)
    if acl is None:
        exit(-1)

    # Add a new grant to the current ACL
    new_grant = {
        'Grantee': {
            'ID': new_grantee_canonical_user_id,
            'Type': 'CanonicalUser',
            #'EmailAddress': new_grantee_email,  # Set ID or Email
            #'Type': 'AmazonCustomerByEmail',
        },
        'Permission': new_grantee_permission,
    }
    # If we don't want to modify the original ACL variable, then we
    # must do a deepcopy
    modified_acl = copy.deepcopy(acl)
    modified_acl['Grants'].append(new_grant)

    # Put the updated bucket ACL
    if put_bucket_acl(test_bucket_name, modified_acl):
        logging.info(f'The ACL was set for {test_bucket_name}')


if __name__ == '__main__':
    main()
