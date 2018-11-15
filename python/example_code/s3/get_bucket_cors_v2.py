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


def get_bucket_cors(bucket_name):
    """Retrieve the CORS configuration rules of an Amazon S3 bucket.

    :param bucket_name: string
    :return: List of the bucket's CORS configuration rules. If no CORS configuration exists, return empty list. If error, return None.
    """

    s3 = boto3.client('s3')
    try:
        response = s3.get_bucket_cors(Bucket=bucket_name)
    except Exception as err:
        if err.response['Error']['Code'] == 'NoSuchCORSConfiguration':
            return []
        else:
            # err.response['Error']['Code'] == 'NoSuchBucket', etc.
            return None
    return response['CORSRules']


def main():
    test_bucket_name = 'test-bucket-name'
    cors_rules = get_bucket_cors(test_bucket_name)

    if cors_rules is None:
        print('ERROR: Could not retrieve bucket CORS configuration rules')
    elif not cors_rules:
        print('Bucket does not have a CORS configuration.')
    else:
        # Output the rules
        for rule in cors_rules:
            print('CORS Rule:')
            print('  Allowed Origins:')
            for origin in rule['AllowedOrigins']:
                print('    {}'.format(origin))
            print('  Allowed Methods:')
            for method in rule['AllowedMethods']:
                print('    {}'.format(method))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[get_bucket_cors.py demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-08]
# snippet-sourceauthor:[scalwas (AWS)]
