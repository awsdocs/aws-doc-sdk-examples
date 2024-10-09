# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
# frozen_string_literal: true

require 'json'
require 'aws-sdk-polly'
require 'aws-sdk-s3'
require 'logger'

def lambda_handler(event, context)
  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  # Create an instance of the Polly client
  polly_client = Aws::Polly::Client.new(region: event['region'])

  resp = polly_client.synthesize_speech({
                                          engine: 'neural',
                                          output_format: 'mp3',
                                          text: event['translated_text'],
                                          voice_id: 'Ruth'
                                        })

  logger.info(resp.to_s)

  # Define the bucket name and file name for the MP3 file in S3
  bucket_name = event['bucket']
  object_key = "#{event['object']}.mp3"

  s3_client = Aws::S3::Client.new(region: event['region'])

  # Put the MP3 file to S3
  s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: resp.audio_stream
  )

  logger.info("Key: #{key}")

  key
end
