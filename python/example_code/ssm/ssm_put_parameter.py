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
# snippet-sourcedescription:[ssm_put_parameter demonstrates how to create a new AWS SSM parameter]
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


def put_parameter(parameter_name, parameter_value, parameter_type):
    """Creates new parameter in AWS SSM

    :param parameter_name: Name of the parameter to create in AWS SSM
    :param parameter_value: Value of the parameter to create in AWS SSM
    :param parameter_type: Type of the parameter to create in AWS SSM ('String'|'StringList'|'SecureString')
    :return: Return version of the parameter if successfully created else None
    """
    ssm_client = boto3.client('ssm')

    try:
        result = ssm_client.put_parameter(
            Name=parameter_name,
            Value=parameter_value,
            Type=parameter_type
        )
    except ClientError as e:
        logging.error(e)
        return None
    return result['Version']


def put_parameter_with_overwrite(parameter_name, parameter_value, parameter_type):
    """Creates new parameter in AWS SSM

    :param parameter_name: Name of the parameter to create in AWS SSM
    :param parameter_value: Value of the parameter to create in AWS SSM
    :param parameter_type: Type of the parameter to create in AWS SSM ('String'|'StringList'|'SecureString')
    :return: Return version of the parameter if successfully created else None
    """
    ssm_client = boto3.client('ssm')

    try:
        result = ssm_client.put_parameter(
            Name=parameter_name,
            Value=parameter_value,
            Type=parameter_type,
            Overwrite=True
        )
    except ClientError as e:
        logging.error(e)
        return None
    return result['Version']


def main():
    # Assign these values before running the program
    # If the specified specified parameter already exist in SSM, ParameterAlreadyExists error will be thrown
    parameter_name = 'test_param'
    parameter_value = 'test_value'
    parameter_type = 'String'  # ('String'|'StringList'|'SecureString')

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')
    result = put_parameter(parameter_name, parameter_value, parameter_type)
    logging.info(result)

    # using put parameter with overwrite,
    # If the specified parameter already exist a new version will be created
    result = put_parameter_with_overwrite(parameter_name, parameter_value, parameter_type)
    logging.info(result)


if __name__ == '__main__':
    main()
