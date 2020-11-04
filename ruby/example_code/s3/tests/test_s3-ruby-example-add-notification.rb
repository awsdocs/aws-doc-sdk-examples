# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-add-notification'

describe '#bucket_notification_configuration_set?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:events) { ['s3:ObjectCreated:*'] }
  let(:send_to_type) { 'sns' }
  let(:resource_arn) { 'arn:aws:sns:us-east-1:111111111111:my-topic' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_bucket_notification_configuration: {}
      }
    )
  end

  it 'configures a notification for a bucket' do
    expect(
      bucket_notification_configuration_set?(
        s3_client,
        bucket_name,
        events,
        send_to_type,
        resource_arn
      )
    ).to be(true)
  end
end
