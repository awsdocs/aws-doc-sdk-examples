# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[aws_service.py demonstrates how to create an API Gateway REST interface to an AWS service.]
# snippet-service:[apigateway]
# snippet-keyword:[API Gateway]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-08-14]
# snippet-sourceauthor:[AWS]

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


import argparse
import json
import logging
import os
import boto3
from botocore.exceptions import ClientError
import requests

# Amazon Translate target language code
# To translate text to another language, change this setting to the desired code
TARGET_LANGUAGE_CODE = 'fr'

# Configuration settings. Change as desired
REGION = 'us-east-1'                # AWS region in which to create resources
API_NAME = 'AmazonTranslateAPI'     # API name in API Gateway
API_RESOURCE_NAME = 'translate'     # As in https:/my_api/translate
API_STAGE = 'dev'                   # API Gateway stage
API_QUERY_PARAM = 'text'            # Required query parameter
API_TRANSLATE_IAM_ROLE = 'apigateway_translate_role'    # IAM role name
CONFIG_FILENAME = 'config.txt'      # Disk file to store API settings


def create_iam_role_for_apigateway(iam_role_name):
    """Create an IAM role to enable the API Gateway to call the
    Amazon Translate service

    :param iam_role_name: Name of IAM role
    :return: ARN of IAM role. If error, returns None.
    """

    # API Gateway trusted relationship policy document
    apigateway_assume_role = {
        'Version': '2012-10-17',
        'Statement': [
            {
                'Sid': '',
                'Effect': 'Allow',
                'Principal': {
                    'Service': 'apigateway.amazonaws.com'
                },
                'Action': 'sts:AssumeRole'
            }
        ]
    }
    iam_client = boto3.client('iam')
    try:
        result = iam_client.create_role(RoleName=iam_role_name,
                                        AssumeRolePolicyDocument=json.dumps(apigateway_assume_role))
    except ClientError as e:
        logging.error(e)
        return None
    apigateway_role_arn = result['Role']['Arn']

    # Attach the AWS-managed TranslateReadOnly policy to the role
    apigateway_policy_arn = 'arn:aws:iam::aws:policy/TranslateReadOnly'
    try:
        iam_client.attach_role_policy(RoleName=iam_role_name,
                                      PolicyArn=apigateway_policy_arn)
    except ClientError as e:
        logging.error(e)
        return None

    '''
    # Debug: Verify policy is attached to the role
    try:
        response = iam_client.list_attached_role_policies(RoleName=iam_role_name)
    except ClientError as e:
        logging.error(e)
    else:
        for policy in response['AttachedPolicies']:
            logging.debug(f'Role: {iam_role_name}, '
                          f'Attached Policy: {policy["PolicyName"]}, '
                          f'ARN: {policy["PolicyArn"]}')
    '''

    # Return the ARN of the created IAM role
    return apigateway_role_arn


def delete_iam_role(role_name):
    """Detach all managed policies from an IAM role and delete the role

    :param role_name: String name of IAM role to delete
    """

    # Retrieve all policies attached to the role
    iam_client = boto3.client('iam')
    try:
        response = iam_client.list_attached_role_policies(RoleName=role_name)
    except ClientError as e:
        logging.error(e)
        return

    # Detach each policy
    while True:
        for policy in response['AttachedPolicies']:
            try:
                iam_client.detach_role_policy(RoleName=role_name,
                                              PolicyArn=policy['PolicyArn'])
            except ClientError as e:
                logging.error(e)
                # Process next attached policy

        # Is there another batch of policies?
        if response['IsTruncated']:
            # Get another batch
            try:
                response = iam_client.list_attached_role_policies(Marker=response['Marker'])
            except ClientError as e:
                logging.error(e)
                break
        else:
            logging.info(f'Detached all policies from IAM role {role_name}')
            break

    # Delete the role
    try:
        iam_client.delete_role(RoleName=role_name)
    except ClientError as e:
        logging.error(e)
    else:
        logging.info(f'Deleted IAM role: {role_name}')


def create_rest_api_for_aws_service(api_name, region):
    """Create a REST API for the Amazon Translate service

    The REST API defines a child called API_RESOURCE_NAME and a stage
    called API_STAGE.

    The service infrastructure creates an API key, which must be included in
    an X-API-Key header of each API call.

    :param api_name: Name of the REST API
    :param region: Region in which to create the API resources
    :return: URL of API. If error, returns None.
    """

    # Create initial REST API
    api_client = boto3.client('apigateway', region_name=REGION)
    try:
        result = api_client.create_rest_api(name=api_name,
                                            apiKeySource='HEADER')
    except ClientError as e:
        logging.error(e)
        return None
    api_id = result['id']
    logging.info(f'Created REST API: {result["name"]}, ID: {api_id}')

    # Get the ID of the API's root resource
    try:
        result = api_client.get_resources(restApiId=api_id)
    except ClientError as e:
        logging.error(e)
        return None
    root_id = None
    for item in result['items']:
        if item['path'] == '/':
            root_id = item['id']
            break
    if root_id is None:
        logging.error('Could not retrieve the ID of the API\'s root resource.')
        return None

    # Define a child resource called API_RESOURCE_NAME under the root resource
    try:
        result = api_client.create_resource(restApiId=api_id,
                                            parentId=root_id,
                                            pathPart=API_RESOURCE_NAME)
    except ClientError as e:
        logging.error(e)
        return None
    resource_id = result['id']

    # Create a request validator to validate the query string parameters
    # and headers
    try:
        result = api_client.create_request_validator(restApiId=api_id,
                                                     name='TranslateAPIValidator',
                                                     # validateRequestBody=False,
                                                     validateRequestParameters=True)
    except ClientError as e:
        logging.error(e)
        return None
    validator_id = result['id']

    # Define a GET method on the /API_RESOURCE_NAME resource
    # The method accepts a required 'text' query parameter.
    request_parameters = {f'method.request.querystring.{API_QUERY_PARAM}': True}
    try:
        api_client.put_method(restApiId=api_id,
                              resourceId=resource_id,
                              httpMethod='GET',
                              authorizationType='NONE',
                              apiKeyRequired=True,
                              requestParameters=request_parameters,
                              requestValidatorId=validator_id)
    except ClientError as e:
        logging.error(e)
        return None

    # Set the content-type of the API's GET method response to JSON
    content_type = {'application/json': 'Empty'}
    try:
        api_client.put_method_response(restApiId=api_id,
                                       resourceId=resource_id,
                                       httpMethod='GET',
                                       statusCode='200',
                                       responseModels=content_type)
    except ClientError as e:
        logging.error(e)
        return None

    # Create IAM role with permissions to access the Amazon Translate service
    iam_role_arn = create_iam_role_for_apigateway(API_TRANSLATE_IAM_ROLE)
    if iam_role_arn is None:
        return None

    # Construct the URI for the Amazon Translate.TranslateText service
    # Note: A bug exists in the processing of the Translate URI. Specifically,
    # passing the correct service value of 'translate' to put_integration()
    # does not link the integration to the Translate service. Instead, to link
    # the integration, the service must be specified as 'Translate' (uppercase
    # 'T'). However, having 'Translate' in the URI causes the subsequent call
    # to create_deployment() to fail with an unrecognized service. The
    # workaround is to use the 'translate' service in the URI for both
    # put_integration() and create_deployment(). Then to link the integration
    # to the service, call update_integration() to replace the URI to use
    # 'Translate'. After this bug is fixed, the translate_uri_updated variable
    # defined below can be removed, along with the call to update_integration()
    # later in this function.
    translate_uri = f'arn:aws:apigateway:{region}:translate:action/TranslateText'
    translate_uri_updated = f'arn:aws:apigateway:{region}:Translate:action/TranslateText'

    # Specify the request headers that the Translate service requires
    # Other AWS services would have their own unique requirements
    request_parameters = {'integration.request.header.Content-Type': "'application/x-amz-json-1.1'",
                          'integration.request.header.X-Amz-Target': "'AWSShineFrontendService_20170701.TranslateText'"}

    # Define the TranslateText arguments
    translate_args = {
        'SourceLanguageCode': 'auto',
        'TargetLanguageCode': TARGET_LANGUAGE_CODE,
        'Text': f"$input.params('{API_QUERY_PARAM}')"
    }
    request_templates = {'application/json': json.dumps(translate_args)}
    try:
        api_client.put_integration(restApiId=api_id,
                                   resourceId=resource_id,
                                   httpMethod='GET',
                                   type='AWS',
                                   integrationHttpMethod='POST',
                                   credentials=iam_role_arn,
                                   requestParameters=request_parameters,
                                   requestTemplates=request_templates,
                                   uri=translate_uri,
                                   passthroughBehavior='WHEN_NO_TEMPLATES')
    except ClientError as e:
        logging.error(e)
        return None

    # Set the content-type of the TranslateText function to JSON
    content_type = {'application/json': ''}
    try:
        api_client.put_integration_response(restApiId=api_id,
                                            resourceId=resource_id,
                                            httpMethod='GET',
                                            statusCode='200',
                                            responseTemplates=content_type)
    except ClientError as e:
        logging.error(e)
        return None

    # Deploy the API
    try:
        api_client.create_deployment(restApiId=api_id,
                                     stageName=API_STAGE)
    except ClientError as e:
        logging.error(e)
        return None

    # Create an API key
    try:
        result = api_client.create_api_key(name='TranslateAPIKey',
                                           description=f'API key for {API_NAME}',
                                           enabled=True)
    except ClientError as e:
        logging.error(e)
        return None
    api_key_id = result['id']
    api_key_value = result['value']

    # Create a usage plan
    api_stages = [
        {
            'apiId': api_id,
            'stage': API_STAGE,
            'throttle': {
                f'/{API_RESOURCE_NAME}/GET': {
                    'burstLimit': 100,
                    'rateLimit': 10.0,
                }
            }
        }
    ]
    throttle = {
        'burstLimit': 100,
        'rateLimit': 10.0,
    }
    quota = {
        'limit': 100,
        'period': 'DAY',
    }
    try:
        result = api_client.create_usage_plan(name='TranslateAPIUsagePlan',
                                              description=f'Usage plan for {API_NAME}',
                                              apiStages=api_stages,
                                              throttle=throttle,
                                              quota=quota)
    except ClientError as e:
        logging.error(e)
        return None
    usage_plan_id = result['id']

    # Attach the API key to the usage plan
    try:
        result = api_client.create_usage_plan_key(usagePlanId=usage_plan_id,
                                                  keyId=api_key_id,
                                                  keyType='API_KEY')
    except ClientError as e:
        logging.error(e)
        return None

    # Workaround for the 'translate/Translate' bug
    patch_uri = [{
        'path': '/uri',
        'value': translate_uri_updated,
        'op': 'replace'
    }]
    try:
        api_client.update_integration(restApiId=api_id,
                                      resourceId=resource_id,
                                      httpMethod='GET',
                                      patchOperations=patch_uri)
    except ClientError as e:
        logging.error(e)
        # Note: When the bug is fixed, this operation will be unnecessary and
        # will probably fail. Thus, in the case of failure, rather than
        # returning None here, we instead continue program execution.

    # Construct the API URL
    api_url = f'https://{api_id}.execute-api.{region}.amazonaws.com/{API_STAGE}/{API_RESOURCE_NAME}'
    logging.info(f'API base URL: {api_url}')

    # Save the API settings for subsequent use and deletion
    save_config_file(api_url, api_key_id, api_key_value, usage_plan_id)
    return api_url


def get_rest_api_id(api_name, region):
    """Retrieve the ID of an API Gateway REST API

    :param api_name: Name of API Gateway REST API
    :param region: Region API is located
    :return: Retrieved API ID. If API not found or error, returns None.
    """

    # Retrieve a batch of APIs
    api_client = boto3.client('apigateway', region_name=region)
    try:
        apis = api_client.get_rest_apis()
    except ClientError as e:
        logging.error(e)
        return None

    # Search the batch
    while True:
        for api in apis['items']:
            if api['name'] == api_name:
                # Found the API we're searching for
                return api['id']

        # Is there another batch of APIs?
        if 'position' in apis:
            # Get another batch
            try:
                apis = api_client.get_rest_apis(position=apis['position'])
            except ClientError as e:
                logging.error(e)
                return None
        else:
            # API not found
            logging.error(f'API {api_name} was not found.')
            return None


def delete_rest_api(api_name, region):
    """Delete an API Gateway API object, including all resources, stages, etc.

    :param api_name: Name of API object to delete
    :param region: Region API is located
    """

    # Delete all versions of the API Gateway object and associated resources
    api_client = boto3.client('apigateway', region_name=region)
    api_id = get_rest_api_id(api_name, region)
    if api_id is not None:
        try:
            api_client.delete_rest_api(restApiId=api_id)
        except ClientError as e:
            logging.error(e)

    # Delete the API Gateway-Translate IAM role
    delete_iam_role(API_TRANSLATE_IAM_ROLE)

    # Delete the usage plan and API key
    config_settings = load_config_file()
    if config_settings is not None:
        try:
            api_client.delete_usage_plan_key(usagePlanId=config_settings['usagePlanId'],
                                             keyId=config_settings['apiKeyId'])
            api_client.delete_usage_plan(usagePlanId=config_settings['usagePlanId'])
            api_client.delete_api_key(apiKey=config_settings['apiKeyId'])
        except ClientError as e:
            logging.error(e)

    # Delete the configuration file
    delete_config_file()


def translate_text(text):
    """Translate text

    The translation is performed by making an HTTPS call. No need for AWS
    credentials or an AWS SDK.

    Uses the Python requests package (pip install requests)

    :param text: Text to translate
    :return: String of translated text. If error, returns None.
    """

    # Load the API URL and API key value from the config file
    config_settings = load_config_file()
    if config_settings is None:
        logging.error('The Translate configuration settings do not exist')
        return None

    payload = {f'{API_QUERY_PARAM}': text}
    headers = {'X-API-Key': config_settings['apiKeyValue']}
    try:
        response = requests.get(config_settings['url'],
                                params=payload,
                                headers=headers,
                                timeout=30)
    except requests.exceptions.RequestException as e:
        logging.error(e)
        return None

    # Process the response
    if response.status_code == 403:
        logging.error('Access to the Translate API is forbidden')
        return None

    if response.status_code == 200:
        # Decode the bytes content to a UTF-8 string and load into a dict
        text_decoded = response.content.decode('utf-8')
        translated_text = json.loads(text_decoded)
        logging.debug(f'Translation Response: {text_decoded}')

        # Return the translated text
        if 'TranslatedText' in translated_text:
            return translated_text['TranslatedText']

        # Error occurred during translation
        if '__type' in translated_text:
            logging.error(translated_text['__type'])
        else:
            logging.error('Unknown error occurred during translation')
        return None


def save_config_file(api_url, api_key_id, api_key_value, usage_plan_id):
    """Save the API's settings in a disk file

    :param api_url: API URL
    :param api_key_id: API key ID
    :param api_key_value: Value of API key
    :param usage_plan_id: Usage plan ID
    """

    # Store the API's configuration settings in a disk file
    with open(CONFIG_FILENAME, 'w') as fp:
        fp.write(api_url + '\n')
        fp.write(api_key_id + '\n')
        fp.write(api_key_value + '\n')
        fp.write(usage_plan_id + '\n')


def load_config_file():
    """Read the Translate API's configuration settings from a disk file

    :return: Dict of configuration settings. If error, return None.
    """

    # Does the URL file exist?
    if not os.path.isfile(CONFIG_FILENAME):
        logging.error(f'The {CONFIG_FILENAME} file does not exist')
        return None

    # Read the URL file
    with open(CONFIG_FILENAME) as fp:
        url = fp.readline().rstrip()
        api_key_id = fp.readline().rstrip()
        api_key_value = fp.readline().rstrip()
        usage_plan_id = fp.readline().rstrip()

    # Return config settings in a dict
    config_settings = {
        'url': url,
        'apiKeyId': api_key_id,
        'apiKeyValue': api_key_value,
        'usagePlanId': usage_plan_id,
    }
    return config_settings


def delete_config_file():
    """Delete the saved-URL disk file"""

    if os.path.isfile(CONFIG_FILENAME):
        os.remove(CONFIG_FILENAME)


def load_src_text_file(src_text_file):
    """Read a file containing text to translate

    :param src_text_file: File with text to translate
    :return: String of text to translate. If error, return None.
    """

    # Does the file exist?
    if not os.path.isfile(src_text_file):
        logging.error(f'The {src_text_file} file does not exist')
        return None

    # Read the file
    with open(src_text_file) as fp:
        text = fp.read()
    return text


def main():
    """Exercise the module's API Gateway functions"""

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Process command-line arguments
    arg_parser = argparse.ArgumentParser(description='WebSocket Example')
    arg_parser.add_argument('-d', '--delete', action='store_true',
                            help='delete allocated resources')
    arg_parser.add_argument('-f', '--file', help='file with text to translate')
    arg_parser.add_argument('-t', '--text', help='text to translate')
    args = arg_parser.parse_args()
    delete_resources = args.delete
    src_text_file = args.file
    src_text = args.text

    # Delete the allocated API Gateway resources?
    if delete_resources:
        delete_rest_api(API_NAME, REGION)
        logging.info(f'Deleted API: {API_NAME}')
        exit(0)

    # File with text to translate?
    if src_text_file is not None:
        src_text = load_src_text_file(src_text_file)
        if src_text is None:
            exit(1)
        # Else fall through to the next code block to translate the text

    # Text to translate?
    if src_text is not None:
        # Pass the text to the API service
        translated_text = translate_text(src_text)
        if translated_text is not None:
            logging.info(f'Translated Text: {translated_text}')

            # If src_text was read from a file, write the translated text
            # to a file
            if src_text_file is not None:
                filename = f'{src_text_file}.{TARGET_LANGUAGE_CODE}'
                with open(filename, 'w') as fp:
                    fp.write(translated_text)
                logging.info(f'Saved translated text to {filename}')
        exit(0)

    # Create an API Gateway REST interface for the Amazon Translate service
    api_url = create_rest_api_for_aws_service(API_NAME, REGION)
    if api_url is None:
        exit(1)
    logging.info(f'Translate service TranslateText URL: {api_url}')
    logging.info('To translate text with the API, run the program with the option -t "Text to translate"')


if __name__ == '__main__':
    main()
