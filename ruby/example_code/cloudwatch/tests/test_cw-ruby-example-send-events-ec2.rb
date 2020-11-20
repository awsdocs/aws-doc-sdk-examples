# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../cw-ruby-example-send-events-ec2'

describe '#topic_exists?' do
  let(:topic_arn) { 'arn:aws:sns:us-east-1:111111111111:aws-doc-sdk-examples-topic' }
  let(:sns_client) do
    Aws::SNS::Client.new(
      stub_responses: {
        list_topics: {
          topics: [
            topic_arn: topic_arn
          ]
        }
      }
    )
  end

  it 'checks whether a topic exists' do
    expect(
      topic_exists?(sns_client, topic_arn)
    ).to be(true)
  end
end

# TODO: Continue from here.
=begin
describe '#create_topic' do
  let(:topic_name) { 'aws-doc-sdk-examples-topic' }
  let(:email_address) { 'mary@example.com' }
  let(:sns_client) do
    Aws::SNS::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      create_topic(sns_client, topic_name, email_address)
    ).to be(true)
  end
end

describe '#role_found?' do
  let(:roles) { '' }
  let(:role_arn) { '' }

  it '' do
    expect(
      role_found?(roles, role_arn)
    ).to be(true)
  end
end

describe '#role_exists?' do
  let(:role_arn) { '' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      role_exists?(iam_client, role_arn)
    ).to be(true)
  end
end

describe '#create_role' do
  let(:role_name) { '' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      create_role(iam_client, role_name)
    ).to be(true)
  end
end

describe '#rule_found?' do
  let(:rules) { '' }
  let(:rule_name) { '' }

  it '' do
    expect(
      rule_found?(rules, rule_name)
    ).to be(true)
  end
end

describe '#rule_exists?' do
  let(:rule_name) { '' }
  let(:cloudwatchevents_client) do
    Aws::CloudWatchEvents::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      rule_exists?(cloudwatchevents_client, rule_name)
    ).to be(true)
  end
end

describe '#rule_created?' do
  let(:rule_name) { '' }
  let(:rule_description) { '' }
  let(:instance_state) { '' }
  let(:role_arn) { '' }
  let(:target_id) { '' }
  let(:topic_arn) { '' }
  let(:cloudwatchevents_client) do
    Aws::CloudWatchEvents::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      rule_created?(
        cloudwatchevents_client,
        rule_name,
        rule_description,
        instance_state,
        role_arn,
        target_id,
        topic_arn
      )
    ).to be(true)
  end
end

describe '#log_group_exists?' do
  let(:log_group_name) { '' }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      log_group_exists?(cloudwatchlogs_client, log_group_name)
    ).to be(true)
  end
end

describe '#log_group_created?' do
  let(:log_group_name) { '' }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      log_group_created?(cloudwatchlogs_client, log_group_name)
    ).to be(true)
  end
end

describe '#log_event' do
  let(:log_group_name) { '' }
  let(:log_stream_name) { '' }
  let(:message) { '' }
  let(:sequence_token) { '' }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      log_event(
        cloudwatchlogs_client,
        log_group_name,
        log_stream_name,
        message,
        sequence_token
       )
    ).to be(true)
  end
end

describe '#instance_restarted?' do
  let(:instance_id) { '' }
  let(:log_group_name) { '' }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      instance_restarted?(
        ec2_client,
        cloudwatchlogs_client,
        instance_id,
        log_group_name
      )
    ).to be(true)
  end
end

describe '#display_rule_activity' do
  let(:rule_name) { '' }
  let(:start_time) { '' }
  let(:end_time) { '' }
  let(:period) { '' }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      display_rule_activity(
        cloudwatch_client,
        rule_name,
        start_time,
        end_time,
        period
      )
    ).to be(true)
  end
end

describe '#display_log_data' do
  let(:log_group_name) { '' }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        do_something: {}
      }
    )
  end

  it '' do
    expect(
      display_log_data(cloudwatchlogs_client, log_group_name)
    ).to be(true)
  end
end
=end
