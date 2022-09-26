# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# create_job_status_notification.rb demonstrates how to create a notification handler for an
# Amazon Elastic Transcoder job using the AWS SDK for Ruby.


# snippet-start:[elastictranscoder.ruby.create_job_status_notification.import]
require "aws-sdk-elastictranscoder"
require "openssl"
require_relative "sqs_queue_notification_worker"
require "aws-sdk-s3"
require "aws-sdk-sqs"

# set clients required
transcoder_client = Aws::ElasticTranscoder::Client.new
sqs_client = Aws::SQS::Client.new
s3_resource = Aws::S3::Resource.new

# set custom names
transcoder_pipeline = "transcoder-pipeline-#{rand(10**4)}"
bucket_name = "transcoder-bucket-#{rand(10**4)}"
sqs_queue_name = "transcoder-sqs-#{rand(10**4)}"


s3_resource.create_bucket(bucket: bucket_name)
resp = sqs_client.create_queue({queue_name: sqs_queue_name})
sqs_queue_url = resp.queue_url

resp = transcoder_client.create_pipeline({
                                           name: transcoder_pipeline, # required
                                           input_bucket: bucket_name, # required
                                           output_bucket: bucket_name,
                                           role: "arn:aws:iam::260778392212:role/Elastic_Transcoder_Default_Role" # required
                                         })

pipeline_id = resp["pipeline"]["id"]

input_key = ".Jabberwocky.mp3"

# This will generate a 480p 16:9 mp4 output.
preset_id = "1351620000001-000020"

# All outputs will have this prefix prepended to their output key.
output_key_prefix = "elastic-transcoder-samples/output/"

def create_elastic_transcoder_job(pipeline_id, input_key, preset_id, output_key_prefix)
  # Create the client for Elastic Transcoder.
  transcoder_client = Aws::ElasticTranscoder::Client.new

  # Setup the job input using the provided input key.
  input = { key: input_key }

  # Setup the job input using the provided input key.
  output = {
    key: OpenSSL::Digest::SHA256.new(input_key.encode("UTF-8")).to_s,
    preset_id: preset_id
  }

  # Create a job on the specified pipeline and return the job ID.
  transcoder_client.create_job(
    pipeline_id: pipeline_id,
    input: input,
    output_key_prefix: output_key_prefix,
    outputs: [output]
  )[:job][:id]
end

job_id = create_elastic_transcoder_job(pipeline_id, input_key, preset_id, output_key_prefix)
puts "Waiting for job to complete: " + job_id

# Create SQS notification worker which polls for notifications.  Register a
# handler which will stop the worker when the job we just created completes.
notification_worker = SqsQueueNotificationWorker.new(sqs_queue_url)
completion_handler = lambda { |notification| notification_worker.stop if (notification["jobId"] == job_id && ["COMPLETED", "ERROR"].include?(notification["state"])) }
notification_worker.add_handler(completion_handler)
notification_worker.start
# snippet-end:[elastictranscoder.ruby.create_job_status_notification.import]
