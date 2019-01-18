# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[MyCodeCommitFunction.py demonstrates how to use an AWS Lambda function to return the URLs used for cloning an AWS CodeCommit repository to a CloudWatch log.]
# snippet-service:[codecommit]
# snippet-keyword:[Python]
# snippet-keyword:[AWS CodeCommit]
# snippet-keyword:[Code Sample]
# snippet-keyword:[GetRepository]
# snippet-sourcetype:[full-example]
# snippet-sourceauthor:[AWS]
# snippet-sourcedate:[2016-03-07]
# snippet-start:[codecommit.python.MyCodeCommitFunction.complete]

import json
import boto3

codecommit = boto3.client('codecommit')

def lambda_handler(event, context):
    #Log the updated references from the event
    references = { reference['ref'] for reference in event['Records'][0]['codecommit']['references'] }
    print("References: "  + str(references))
    
    #Get the repository from the event and show its git clone URL
    repository = event['Records'][0]['eventSourceARN'].split(':')[5]
    try:
        response = codecommit.get_repository(repositoryName=repository)
        print("Clone URL: " +response['repositoryMetadata']['cloneUrlHttp'])
        return response['repositoryMetadata']['cloneUrlHttp']
    except Exception as e:
        print(e)
        print('Error getting repository {}. Make sure it exists and that your repository is in the same region as this function.'.format(repository))
        raise e

# snippet-end:[codecommit.python.MyCodeCommitFunction.complete]
