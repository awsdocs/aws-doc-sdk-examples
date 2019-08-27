# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[get_bucket_cors.py demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
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


def get_bucket_cors(bucket_name):
    """Retrieve the CORS configuration rules of an Amazon S3 bucket

    :param bucket_name: string
    :return: List of the bucket's CORS configuration rules. If no CORS
    configuration exists, return empty list. If error, return None.
    """

    # Retrieve the CORS configuration
    s3 = boto3.client('s3')
    try:
        response = s3.get_bucket_cors(Bucket=bucket_name)
    except ClientError as e:
        if e.response['Error']['Code'] == 'NoSuchCORSConfiguration':
            return []
        else:
            # AllAccessDisabled error == bucket not found
            logging.error(e)
            return None
    return response['CORSRules']


def main():
    """Exercise get_bucket_cors()"""

    # Assign this value before running the program
    test_bucket_name = 'BUCKET_NAME'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Retrieve the CORS configuration
    cors_rules = get_bucket_cors(test_bucket_name)

    if cors_rules is not None:
        if not cors_rules:
            logging.info('Bucket does not have a CORS configuration.')
        else:
            # Output the rules
            for rule in cors_rules:
                logging.info('CORS Rule:')
                logging.info('  Allowed Origins:')
                for origin in rule['AllowedOrigins']:
                    logging.info(f'    {origin}')
                logging.info('  Allowed Methods:')
                for method in rule['AllowedMethods']:
                    logging.info(f'    {method}')


if __name__ == '__main__':
    main()
