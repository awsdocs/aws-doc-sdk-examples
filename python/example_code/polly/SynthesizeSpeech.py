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
# snippet-sourcedescription:[SynthesizeSpeech.py demonstrates how to synthesize speech with shorter texts for near-real time processing. .]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Polly]
# snippet-keyword:[SynthesizeSpeech]
# snippet-keyword:[speech]
# snippet-service:[polly]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[ (AWS)]
# snippet-start:[polly.python.SynthesizeSpeech.complete]

import boto3

polly_client = boto3.Session(
                aws_access_key_id='',                     
                aws_secret_access_key='',
                region_name='us-west-2').client('polly')

response = polly_client.synthesize_speech(VoiceId='Joanna',
                OutputFormat='mp3', 
                Text = 'This is a sample text to be synthesized.')

file = open('speech.mp3', 'w')
file.write(response['AudioStream'].read())
file.close()

              
# snippet-end:[polly.python.SynthesizeSpeech.complete]
  
