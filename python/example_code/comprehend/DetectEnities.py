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

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[DetectEntities.py demonstrates how to determine the named entities in a document.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Comprehend]
# snippet-keyword:[DetectEntities]
# snippet-keyword:[entities]
# snippet-service:[comprehend]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[ (AWS)]
# snippet-start:[comprehend.python.DetectEntities.complete]

import boto3
import json

comprehend = boto3.client(service_name='comprehend', region_name='region')
text = "It is raining today in Seattle"

print('Calling DetectEntities')
print(json.dumps(comprehend.detect_entities(Text=text, LanguageCode='en'), sort_keys=True, indent=4))
print('End of DetectEntities\n')



              
# snippet-end:[polly.python.SyntesizeSpeech.complete]
  