# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[basic_lambda_function.py implements a basic AWS Lambda function.]
# snippet-service:[lambda]
# snippet-keyword:[AWS Lambda]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-06-14]
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


import logging
logging.basicConfig(format='%(levelname)s: %(asctime)s: %(message)s')
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):
    """Basic Lambda function template

    :param event: Dict (usually) of parameters passed to the function
    :param context: LambdaContext object of runtime data
    :return: Dict of key:value pairs
    """

    # Log the values received in the event argument
    logger.info(f'Request event: {event}')

    # Define default hard-coded return values
    response = {
        'uid': 'Example function ID',
        'return_val01': 'Return value #1',
        'return_val02': 'Return Value #2',
    }

    # Retrieve type of invocation (GET, PUT, etc.)
    if 'http_verb' in event:
        operation = event['http_verb'].upper()
        if operation == 'PUT':
            # Return the values passed to the function
            response = {
                'uid': event['functionID'],
                'return_val01': event['parameters']['parm01'],
                'return_val02': event['parameters']['parm02'],
            }

    logger.info(f'Response={response}')
    return response
