# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[ssm_delete_parameter demonstrates how to delete SSM parameters]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS System Manager]
# snippet-service:[ssm]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2020-09-08]
# snippet-sourceauthor:[nprajilesh]


import boto3
import logging
from botocore.exceptions import ClientError


def delete_parameter(parameter_name):
    """Delete parameter in AWS SSM

    :param parameter_name: Name of the parameter to delete from AWS SSM
    """
    ssm_client = boto3.client('ssm')

    try:
        ssm_client.delete_parameter(
            Name=parameter_name
        )
    except ClientError as e:
        logging.error(e)


def delete_parameters(parameter_names):
    """Delete multiple parameters in AWS SSM

    :param parameter_names: List of parameter names to delete from AWS SSM
    """
    ssm_client = boto3.client('ssm')

    try:
        ssm_client.delete_parameters(
            Names=parameter_names
        )
    except ClientError as e:
        logging.error(e)


def main():
    # Assign these values before running the program
    parameter_name = 'test_param'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # delete parameter from SSM
    delete_parameter(parameter_name)

    # delete multiple parameters from SSM
    parameter_names = ['test_param1', 'test_param2']
    delete_parameters(parameter_names)


if __name__ == '__main__':
    main()
