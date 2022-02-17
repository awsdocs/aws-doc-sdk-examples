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
# snippet-sourcedescription:[create_hls_job.py demonstrates how to create an Elastic Transcoder HLS job.]
# snippet-service:[elastictranscoder]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-sourcedate:[2019-02-04]
# snippet-sourceauthor:[AWS]
# snippet-start:[elastictranscoder.python.create_hls_job.complete]

import boto3
from botocore.exceptions import ClientError


def create_elastic_transcoder_hls_job(pipeline_id, input_file,
                                      outputs, output_file_prefix,
                                      playlists):
    """Create an Elastic Transcoder HSL job

    :param pipeline_id: string; ID of an existing Elastic Transcoder pipeline
    :param input_file: string; Name of existing object in pipeline's S3 input bucket
    :param outputs: list of dictionaries; Parameters defining each output file
    :param output_file_prefix: string; Prefix for each output file name
    :param playlists: list of dictionaries; Parameters defining each playlist
    :return Dictionary containing information about the job
            If job could not be created, returns None
    """

    etc_client = boto3.client('elastictranscoder')
    try:
        response = etc_client.create_job(PipelineId=pipeline_id,
                                         Input={'Key': input_file},
                                         Outputs=outputs,
                                         OutputKeyPrefix=output_file_prefix,
                                         Playlists=playlists)
    except ClientError as e:
        print(f'ERROR: {e}')
        return None
    return response['Job']


def main():
    """Exercise Elastic Transcoder create_job operation

    Before running this script, all Elastic Transcoder setup must be
    completed, such as defining the pipeline and specifying the S3 input
    and output buckets. Also, the file to transcode must exist in the S3
    input bucket.
    """

    # Job configuration settings. Set these values before running the script.
    pipeline_id = 'PIPELINE_ID'         # ID of an existing Elastic Transcoder pipeline
    input_file = 'FILE_TO_TRANSCODE'    # Name of an existing file in the S3 input bucket
    output_file = 'TRANSCODED_FILE'     # Desired root name of the transcoded output files

    # Other job configuration settings. Optionally change as desired.
    output_file_prefix = 'elastic-transcoder-samples/output/hls/'  # Prefix for all output files
    segment_duration = '2'  # Maximum segment duration in seconds

    # Elastic Transcoder presets used to create HLS multi-segment
    # output files in MPEG-TS format
    hls_64k_audio_preset_id = '1351620000001-200071'    # HLS Audio 64kb/second
    hls_0400k_preset_id = '1351620000001-200050'        # HLS 400k
    hls_0600k_preset_id = '1351620000001-200040'        # HLS 600k
    hls_1000k_preset_id = '1351620000001-200030'        # HLS 1M
    hls_1500k_preset_id = '1351620000001-200020'        # HLS 1.5M
    hls_2000k_preset_id = '1351620000001-200010'        # HLS 2M

    # Define the various outputs
    outputs = [
        {
            'Key': 'hlsAudio/' + output_file,
            'PresetId': hls_64k_audio_preset_id,
            'SegmentDuration': segment_duration,
        },
        {
            'Key': 'hls0400k/' + output_file,
            'PresetId': hls_0400k_preset_id,
            'SegmentDuration': segment_duration,
        },
        {
            'Key': 'hls0600k/' + output_file,
            'PresetId': hls_0600k_preset_id,
            'SegmentDuration': segment_duration,
        },
        {
            'Key': 'hls1000k/' + output_file,
            'PresetId': hls_1000k_preset_id,
            'SegmentDuration': segment_duration,
        },
        {
            'Key': 'hls1500k/' + output_file,
            'PresetId': hls_1500k_preset_id,
            'SegmentDuration': segment_duration,
        },
        {
            'Key': 'hls2000k/' + output_file,
            'PresetId': hls_2000k_preset_id,
            'SegmentDuration': segment_duration,
        },
    ]

    # Define the playlist
    playlists = [
        {
            'Name': 'hls_' + output_file,
            'Format': 'HLSv3',
            'OutputKeys': [x['Key'] for x in outputs]
        }
    ]

    # Create an HLS job in Elastic Transcoder
    job_info = create_elastic_transcoder_hls_job(pipeline_id,
                                                 input_file,
                                                 outputs, output_file_prefix,
                                                 playlists)
    if job_info is None:
        exit(1)

    # Output job ID and exit. Do not wait for the job to finish.
    print(f'Created Amazon Elastic Transcoder HLS job {job_info["Id"]}')


if __name__ == '__main__':
    main()
# snippet-end:[elastictranscoder.python.create_hls_job.complete]
