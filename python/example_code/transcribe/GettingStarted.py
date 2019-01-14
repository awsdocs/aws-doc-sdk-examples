# snippet-sourcedescription:[GettingStarted.py provides an example of using Amazon Transcribe to transcribe an audio file.]
# snippet-service:[transcribe]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon Transcribe]
# snippet-keyword:[Code Sample]
# snippet-keyword:[start_transcription_job]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-01-10]
# snippet-sourceauthor:[AWS]

# COPYRIGHT:
# 
# Copyright 2018-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# 
# Licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License.
# A copy of the License is located at
# 
#     http://www.apache.org/licenses/LICENSE-2.0
# 
# or in the "license" file accompanying this file. This file is distributed
# on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
# express or implied. See the License for the specific language governing
# permissions and limitations under the License.
# 
# snippet-start:[transcribe.python.start-transcription-job]
from __future__ import print_function

import time
import boto3

transcribe = boto3.client('transcribe')

job_name = "job name"
job_uri = "https://S3 endpoint/test-transcribe/answer2.wav"

transcribe.start_transcription_job(
    TranscriptionJobName=job_name,
    Media={'MediaFileUri': job_uri},
    MediaFormat='wav',
    LanguageCode='en-US'

)

while True:
    status = transcribe.get_transcription_job(TranscriptionJobName=job_name)
    if status['TranscriptionJob']['TranscriptionJobStatus'] in ['COMPLETED', 'FAILED']:
        break
    print("Not ready yet...")
    time.sleep(5)
print(status)

# snippet-end:[transcribe.python.start-transcription-job]
