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
# snippet-sourcedescription:[HlsJobCreationSample.py demonstrates how to create an HLS job.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-service:[elastictranscoder]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[]
# snippet-sourceauthor:[AWS]
import hashlib
import json

import boto.elastictranscoder

# This is the ID of the Elastic Transcoder pipeline that was created when
# setting up your AWS environment:
# http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html#python-pipeline
pipeline_id = 'Enter your pipeline id here.'

# This is the name of the input key that you would like to transcode.
input_key = 'Enter your input key here.'

# Region where the sample will be run
region = 'us-east-1'

# HLS Presets that will be used to create an adaptive bitrate playlist.
hls_64k_audio_preset_id = '1351620000001-200071';
hls_0400k_preset_id     = '1351620000001-200050';
hls_0600k_preset_id     = '1351620000001-200040';
hls_1000k_preset_id     = '1351620000001-200030';
hls_1500k_preset_id     = '1351620000001-200020';
hls_2000k_preset_id     = '1351620000001-200010';

# HLS Segment duration that will be targeted.
segment_duration = '2'

#All outputs will have this prefix prepended to their output key.
output_key_prefix = 'elastic-transcoder-samples/output/hls/'
    
# Creating client for accessing elastic transcoder 
transcoder_client = boto.elastictranscoder.connect_to_region(region)

# Setup the job input using the provided input key.
job_input = { 'Key': input_key }

# Setup the job outputs using the HLS presets.
output_key = hashlib.sha256(input_key.encode('utf-8')).hexdigest()
hls_audio = {
    'Key' : 'hlsAudio/' + output_key,
    'PresetId' : hls_64k_audio_preset_id,
    'SegmentDuration' : segment_duration
}
hls_400k = {
    'Key' : 'hls0400k/' + output_key,
    'PresetId' : hls_0400k_preset_id,
    'SegmentDuration' : segment_duration
}
hls_600k = {
    'Key' : 'hls0600k/' + output_key,
    'PresetId' : hls_0600k_preset_id,
    'SegmentDuration' : segment_duration
}
hls_1000k = {
    'Key' : 'hls1000k/' + output_key,
    'PresetId' : hls_1000k_preset_id,
    'SegmentDuration' : segment_duration
}
hls_1500k = {
    'Key' : 'hls1500k/' + output_key,
    'PresetId' : hls_1500k_preset_id,
    'SegmentDuration' : segment_duration
}
hls_2000k = {
    'Key' : 'hls2000k/' + output_key,
    'PresetId' : hls_2000k_preset_id,
    'SegmentDuration' : segment_duration
}
job_outputs = [ hls_audio, hls_400k, hls_600k, hls_1000k, hls_1500k, hls_2000k ]

# Setup master playlist which can be used to play using adaptive bitrate.
playlist = {
    'Name' : 'hls_' + output_key,
    'Format' : 'HLSv3',
    'OutputKeys' : map(lambda x: x['Key'], job_outputs)
}

# Creating the job.
create_job_request = {
    'pipeline_id' : pipeline_id,
    'input_name' : job_input,
    'output_key_prefix' : output_key_prefix + output_key +'/',
    'outputs' : job_outputs,
    'playlists' : [ playlist ]
}
create_job_result=transcoder_client.create_job(**create_job_request)
print 'HLS job has been created: ', json.dumps(create_job_result['Job'], indent=4, sort_keys=True)

