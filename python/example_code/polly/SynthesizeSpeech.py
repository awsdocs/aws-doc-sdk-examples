# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[SynthesizeSpeech.py demonstrates how to synthesize speech with shorter texts for near-real-time processing.]
# snippet-service:[polly]
# snippet-keyword:[Amazon Polly]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-sourcedate:[2019-03-13]
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

# snippet-start:[polly.python.SynthesizeSpeech.complete]
import boto3

# Synthesize the sample text, saving it in an MP3 audio file
polly_client = boto3.client('polly')
response = polly_client.synthesize_speech(VoiceId='Joanna',
                                          OutputFormat='mp3',
                                          Text='This is sample text to synthesize.')
with open('speech.mp3', 'w') as file:
    file.write(response['AudioStream'].read())
# snippet-end:[polly.python.SynthesizeSpeech.complete]
