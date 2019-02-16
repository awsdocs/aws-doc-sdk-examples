# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_bucket_lifecycle_configuration.py demonstrates how to set the lifecycle configuration of an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-12]
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

# Lifecycle configuration settings
# The optional ID can be any descriptive string.
# The empty Prefix setting causes all objects in the bucket to transition.
# The 0 Days setting causes the transition to occur "immediately" (or within
# a short time period, less than one day)
lifecycle_config_settings = {
    'Rules': [
        {'ID': 'S3 Glacier Transition Rule',
         'Filter': {'Prefix': ''},
         'Status': 'Enabled',
         'Transitions': [
             {'Days': 0,
              'StorageClass': 'GLACIER'}
         ]}
    ]}


def put_bucket_lifecycle_configuration(bucket_name, lifecycle_config):
    """Set the lifecycle configuration of an Amazon S3 bucket

    :param bucket_name: string
    :param lifecycle_config: dict of lifecycle configuration settings
    :return: True if lifecycle configuration was set, otherwise False
    """

    # Set the configuration
    s3 = boto3.client('s3')
    try:
        s3.put_bucket_lifecycle_configuration(Bucket=bucket_name,
                                              LifecycleConfiguration=lifecycle_config)
    except ClientError as e:
        logging.error(e)
        return False
    return True


def main():
    """Exercise put_bucket_lifecycle_configuration()"""

    # Assign this value before running the program
    test_bucket_name = 'BUCKET_NAME'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Set the bucket's lifecycle configuration
    success = put_bucket_lifecycle_configuration(test_bucket_name,
                                                 lifecycle_config_settings)

    if success:
        logging.info(f'The lifecycle configuration was set for {test_bucket_name}')


if __name__ == '__main__':
    main()
