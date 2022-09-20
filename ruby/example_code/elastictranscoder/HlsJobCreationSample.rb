# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# eb_list_stacks.rb demonstrates how to create an HLS job using the AWS SDK for Ruby.


# snippet-start:[elastictranscoder.ruby.create_hls_job.import]
require 'aws-sdk-elastictranscoder'

client = Aws::ElasticTranscoder::Client.new

# resp = client.test_role({
#                           role: "arn:aws:iam:260778392212::role/Elastic_Transcoder_Default_Role", # required
#                           input_bucket: "example-code-bucket-171", # required
#                           output_bucket: "example-code-bucket-171", # required
#                           topics: ["arn:aws:sns:260778392212::aws-doc-sdk-examples-topic"] # required
#                         })

resp = client.create_pipeline({
                                name: "test1", # required
                                input_bucket: "example-code-bucket-171", # required
                                output_bucket: "example-code-bucket-171",
                                role: "arn:aws:iam::260778392212:role/Elastic_Transcoder_Default_Role" # required
                              })

pipeline_id = resp["pipeline"]["id"]

# This is the name of the input key that you would like to transcode.
input_key = 'movie.mp4'

# HLS Presets that will be used to create an adaptive bitrate playlist.
hls_64k_audio_preset_id = '1351620000001-200071'
hls_0400k_preset_id     = '1351620000001-200050'
hls_0600k_preset_id     = '1351620000001-200040'
hls_1000k_preset_id     = '1351620000001-200030'
hls_1500k_preset_id     = '1351620000001-200020'
hls_2000k_preset_id     = '1351620000001-200010'

# HLS Segment duration that will be targeted.
segment_duration = '2'

# All outputs will have this prefix prepended to their output key.
output_key_prefix = 'elastic-transcoder-samples/output/hls/'

# Create the client for Elastic Transcoder.
transcoder_client = Aws::ElasticTranscoder::Client.new

# Setup the job input using the provided input key.
input = { key: input_key }

# Setup the job outputs using the HLS presets.
output_key = OpenSSL::Digest::SHA256.new(input_key.encode('UTF-8'))

hls_audio = {
  key: 'hlsAudio/' + output_key,
  preset_id: hls_64k_audio_preset_id,
  segment_duration: segment_duration
}

hls_400k = {
  key: 'hls0400k/' + output_key,
  preset_id: hls_0400k_preset_id,
  segment_duration: segment_duration
}

hls_600k = {
  key: 'hls0600k/' + output_key,
  preset_id: hls_0600k_preset_id,
  segment_duration: segment_duration
}

hls_1000k = {
  key: 'hls1000k/' + output_key,
  preset_id: hls_1000k_preset_id,
  segment_duration: segment_duration
}

hls_1500k = {
  key: 'hls1500k/' + output_key,
  preset_id: hls_1500k_preset_id,
  segment_duration: segment_duration
}

hls_2000k = {
  key: 'hls2000k/' + output_key,
  preset_id: hls_2000k_preset_id,
  segment_duration: segment_duration
}

outputs = [hls_audio, hls_400k, hls_600k, hls_1000k, hls_1500k, hls_2000k]
playlist = {
  name: 'hls_' + output_key,
  format: 'HLSv3',
  output_keys: outputs.map { |output| output[:key] }
}

job = transcoder_client.create_job(
  pipeline_id: pipeline_id,
  input: input,
  output_key_prefix: output_key_prefix + output_key + '/',
  outputs: outputs,
  playlists: [playlist]
)[:job]

puts 'HLS job has been created: ' + JSON.pretty_generate(job)
# snippet-end:[elastictranscoder.ruby.create_hls_job.import]
