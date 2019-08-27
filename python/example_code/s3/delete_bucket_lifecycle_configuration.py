# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_bucket_lifecycle_configuration.py demonstrates how to delete the lifecycle configuration of an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
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


def delete_bucket_lifecycle_configuration(bucket_name):
    """Delete the lifecycle configuration of an Amazon S3 bucket

    :param bucket_name: string
    :return: True if bucket lifecycle configuration was deleted, otherwise
    False. Note: If the bucket does not have a lifecycle configuration, the
    method returns True.
    """

    # Delete the configuration
    s3 = boto3.client('s3')
    try:
        s3.delete_bucket_lifecycle(Bucket=bucket_name)
    except ClientError as e:
        # e.response['Error']['Code'] == 'AllAccessDisabled' (bucket does not
        # exist), etc.
        logging.error(e)
        return False
    return True


def main():
    """Exercise delete_bucket_lifecycle_configuration"""

    # Assign this value before running the program
    test_bucket_name = 'BUCKET_NAME'
    test_bucket_name = 'scalwas-bucket-name'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Delete the configuration
    success = delete_bucket_lifecycle_configuration(test_bucket_name)
    if success:
        logging.info(f'Deleted the lifecycle configuration of {test_bucket_name}')


if __name__ == '__main__':
    main()
