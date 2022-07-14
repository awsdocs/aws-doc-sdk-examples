# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../cw-ruby-example-send-events-ec2"

describe "#topic_exists?" do
  let(:topic_arn) { "arn:aws:sns:us-east-1:111111111111:aws-doc-sdk-examples-topic" }
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

  it "checks whether a topic exists" do
    expect(
      topic_exists?(sns_client, topic_arn)
    ).to be(true)
  end
end

describe "#create_topic" do
  let(:topic_name) { "aws-doc-sdk-examples-topic" }
  let(:email_address) { "mary@example.com" }
  let(:topic_arn) { "arn:aws:sns:us-east-1:111111111111:#{topic_name}" }
  let(:sns_client) do
    Aws::SNS::Client.new(
      stub_responses: {
        create_topic: {
          topic_arn: topic_arn
        },
        subscribe: {
          subscription_arn: "arn:aws:sns:us-east-1:111111111111:#{topic_name}:47301d57-8f71-4abd-958b-432ddEXAMPLE"
        }
      }
    )
  end

  it "creates a topic" do
    expect(
      create_topic(sns_client, topic_name, email_address)
    ).to eq(topic_arn)
  end
end

describe "#role_exists?" do
  let(:role_name) { "aws-doc-sdk-examples-cloudwatch-events-rule-role" }
  let(:role_arn) { "arn:aws:iam::111111111111:role/#{role_name}" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_roles: {
          roles: [
            arn: role_arn,
            path: "/",
            role_name: role_name,
            role_id: "AIDAJQABLZS4A3EXAMPLE",
            create_date: Time.iso8601("2020-11-17T14:19:00-08:00")
          ]
        }
      }
    )
  end

  it "checks whether a role exists" do
    expect(
      role_exists?(iam_client, role_arn)
    ).to be(true)
  end
end

describe "#create_role" do
  let(:role_name) { "aws-doc-sdk-examples-cloudwatch-events-rule-role" }
  let(:role_arn) { "arn:aws:iam::111111111111:role/#{role_name}" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_role: {
          role: {
            arn: role_arn,
            path: "/",
            role_name: role_name,
            role_id: "AIDAJQABLZS4A3EXAMPLE",
            create_date: Time.iso8601("2020-11-17T14:19:00-08:00")
          }
        }
      }
    )
  end

  it "creates a role" do
    expect(
      create_role(iam_client, role_name)
    ).to eq(role_arn)
  end
end

describe "#rule_exists?" do
  let(:rule_name) { "aws-doc-sdk-examples-ec2-state-change" }
  let(:cloudwatchevents_client) do
    Aws::CloudWatchEvents::Client.new(
      stub_responses: {
        list_rules: {
          rules: [
            {
              name: rule_name
            }
          ]
        }
      }
    )
  end

  it "checks whether a rule exists" do
    expect(
      rule_exists?(cloudwatchevents_client, rule_name)
    ).to be(true)
  end
end

describe "#rule_created?" do
  let(:rule_name) { "aws-doc-sdk-examples-ec2-state-change" }
  let(:rule_description) { "Triggers when any available EC2 instance starts." }
  let(:instance_state) { "running" }
  let(:role_arn) { "arn:aws:iam::111111111111:role/aws-doc-sdk-examples-cloudwatch-events-rule-role" }
  let(:target_id) { "sns-topic" }
  let(:topic_arn) { "arn:aws:sns:us-east-1:111111111111:aws-doc-sdk-examples-topic" }
  let(:cloudwatchevents_client) do
    Aws::CloudWatchEvents::Client.new(
      stub_responses: {
        put_rule: {
          rule_arn: "arn:aws:events:us-east-1:111111111111:rule/#{rule_name}"
        },
        put_targets: {}
      }
    )
  end

  it "creates a rule" do
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

describe "#log_group_exists?" do
  let(:log_group_name) { "aws-doc-sdk-examples-cloudwatch-log" }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        describe_log_groups: {
          log_groups: [
            log_group_name: log_group_name
          ]
        }
      }
    )
  end

  it "checks whether a log group exists" do
    expect(
      log_group_exists?(cloudwatchlogs_client, log_group_name)
    ).to be(true)
  end
end

describe "#log_group_created?" do
  let(:log_group_name) { "aws-doc-sdk-examples-cloudwatch-log" }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        create_log_group: {}
      }
    )
  end

  it "creates a log group" do
    expect(
      log_group_created?(cloudwatchlogs_client, log_group_name)
    ).to be(true)
  end
end

describe "#log_event" do
  let(:log_group_name) { "aws-doc-sdk-examples-cloudwatch-log" }
  let(:log_stream_name) { "#{Time.now.year}/#{Time.now.month}/#{Time.now.day}/" \
    "#{SecureRandom.uuid}"
  }
  let(:message) { "Instance 'i-033c48ef067af3dEX' restarted." }
  let(:sequence_token) { "495426724868310740095796045676567882148068632824696073EX" }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        put_log_events: {
          next_sequence_token: sequence_token
        }
      }
    )
  end

  it "logs an event" do
    expect(
      log_event(
        cloudwatchlogs_client,
        log_group_name,
        log_stream_name,
        message,
        sequence_token
       )
    ).to eq(sequence_token)
  end
end

describe "#instance_restarted?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:log_group_name) { "aws-doc-sdk-examples-cloudwatch-log" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        stop_instances: {
          stopping_instances: [
            {
              current_state: {
                code: 80,
                name: "stopped"
              },
              instance_id: instance_id,
              previous_state: {
                code: 16,
                name: "running"
              }
            }
          ]
        },
        start_instances: {
          starting_instances: [
            {
              current_state: {
                code: 16,
                name: "running"
              },
              instance_id: instance_id,
              previous_state: {
                code: 80,
                name: "stopped"
              }
            }
          ]
        },
        describe_instances: [
          {
            reservations: [
              instances: [
                instance_id: instance_id,
                state: {
                  code: 80,
                  name: "stopped"
                }
              ]
            ]
          },
          {
            reservations: [
              instances: [
                instance_id: instance_id,
                state: {
                  code: 16,
                  name: "running"
                }
              ]
            ]
          }
        ]
      }
    )
  end
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        create_log_stream: {}
      }
    )
  end

  it "restarts an instance and logs related events" do
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

describe "#display_rule_activity" do
  let(:rule_name) { "aws-doc-sdk-examples-ec2-state-change" }
  let(:start_time) { Time.now - 600 }
  let(:end_time) { Time.now }
  let(:period) { 60 }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        get_metric_statistics: {
          datapoints: [
            {
              sum: 1.0,
              timestamp: Time.iso8601("2020-11-17T14:19:00-08:00")
            },
            {
              sum: 2.0,
              timestamp: Time.iso8601("2020-11-17T14:32:00-08:00")
            }
          ]
        }
      }
    )
  end

  it "displays activity for a rule" do
    expect {
      display_rule_activity(
        cloudwatch_client,
        rule_name,
        start_time,
        end_time,
        period
      )
    }.not_to raise_error
  end
end

describe "#display_log_data" do
  let(:log_group_name) { "aws-doc-sdk-examples-cloudwatch-log" }
  let(:log_stream_name) { "#{Time.now.year}/#{Time.now.month}/#{Time.now.day}/" \
    "#{SecureRandom.uuid}"
  }
  let(:cloudwatchlogs_client) do
    Aws::CloudWatchLogs::Client.new(
      stub_responses: {
        describe_log_streams: {
          log_streams: [
            {
              log_stream_name: log_stream_name
            }
          ]
        },
        get_log_events: {
          events: [
            {
              message: "Instance 'i-033c48ef067af3dEX' stopped."
            },
            {
              message: "Instance 'i-033c48ef067af3dEX' restarted."
            }
          ]
        }
      }
    )
  end

  it "displays log streams data" do
    expect {
      display_log_data(cloudwatchlogs_client, log_group_name)
    }.not_to raise_error
  end
end
