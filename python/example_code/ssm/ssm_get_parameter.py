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
# snippet-sourcedescription:[ssm_get_parameter demonstrates how to get information about SSM parameters]
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


def get_parameter(parameter_name, with_decryption):
    """Get parameter details in AWS SSM

    :param parameter_name: Name of the parameter to fetch details from SSM
    :param with_decryption: return decrypted value for secured string params, ignored for String and StringList
    :return: Return parameter details if exist else None
    """
    ssm_client = boto3.client('ssm')

    try:
        result = ssm_client.get_parameter(
            Name=parameter_name,
            WithDecryption=with_decryption
        )
    except ClientError as e:
        logging.error(e)
        return None
    return result


def get_parameters(parameter_names, with_decryption):
    """Get multiple parameter details in AWS SSM

    :param parameter_names: List of parameter names to fetch details from AWS SSM
    :param with_decryption: return decrypted value for secured string params, ignored for String and StringList
    :return: Return parameter details if exist else None
    """
    ssm_client = boto3.client('ssm')

    try:
        result = ssm_client.get_parameters(
            Names=parameter_names,
            WithDecryption=with_decryption
        )
    except ClientError as e:
        logging.error(e)
        return None
    return result


def main():
    # Assign these values before running the program
    parameter_name = 'test_param'
    with_decryption = False

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # get the parameter details for parameter_name
    result = get_parameter(parameter_name, with_decryption)

    # print parameter value, version
    if result:
        logging.info("Name: " + result['Parameter']['Name'])
        logging.info("Value: " + result['Parameter']['Value'])
        logging.info("Version: " + str(result['Parameter']['Version']))

    # get multiple parameter details

    parameter_names = ['test_param1', 'test_param2']
    result = get_parameters(parameter_names, with_decryption)

    # print parameter value, version for all the params
    if result:
        for parameter_details in result['Parameters']:
            logging.info("Name: " + parameter_details['Name'])
            logging.info("Value: " + parameter_details['Value'])
            logging.info("Version: " + str(parameter_details['Version']))


if __name__ == '__main__':
    main()
