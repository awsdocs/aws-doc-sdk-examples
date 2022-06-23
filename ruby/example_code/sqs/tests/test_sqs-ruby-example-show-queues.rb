# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../sqs-ruby-example-show-queues'

describe '#list_queue_urls' do
  let(:sqs_client) do
    Aws::SQS::Client.new(
      stub_responses: {
        list_queues: {
          queue_urls: [
            'https://sqs.us-west-2.amazonaws.com/111111111111/my-queue',
            'https://sqs.us-west-2.amazonaws.com/111111111111/my-queue-2'
          ]
        }
      }
    )
  end

  it 'lists available queue URLs' do
    expect{ list_queue_urls(sqs_client) }.to_not raise_error
  end
end

describe '#list_queue_attributes' do
  let(:queue_name) { 'my-queue' }
  let(:queue_url) { 'https://sqs.us-west-2.amazonaws.com/111111111111/' + queue_name }
  let(:sqs_client) do
    Aws::SQS::Client.new(
      stub_responses: {
        get_queue_attributes: {
          attributes: {
           'QueueArn' => 'arn:aws:sqs:us-west-2:992648334831:my-queue',
           'ApproximateNumberOfMessages' => '2',
           'ApproximateNumberOfMessagesNotVisible' => '0',
           'ApproximateNumberOfMessagesDelayed' => '0',
           'CreatedTimestamp' => '1612302412',
           'LastModifiedTimestamp' => '1612302412',
           'VisibilityTimeout' => '30',
           'MaximumMessageSize' => '262144',
           'MessageRetentionPeriod' => '345600',
           'DelaySeconds' => '0',
           'ReceiveMessageWaitTimeSeconds' => '0'
          }
        }
      }
    )
  end

  it 'lists a queue\'s attributes' do
    expect{ list_queue_attributes(sqs_client, queue_url) }.to_not raise_error
  end
end
