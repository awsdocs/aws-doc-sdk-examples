# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-terminate-instance-i-123abc"

describe "#instance_terminated?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instance_status: {
          instance_statuses: [
            {
              instance_state: {
                name: "running"
              }
            }
          ]
        },
        terminate_instances: {
          terminating_instances: [
            {
              current_state: {
                code: 48,
                name: "terminated"
              },
              instance_id: instance_id,
              previous_state: {
                code: 16,
                name: "running"
              }
            }
          ]
        },
        describe_instances: {
          reservations: [
            instances: [
              instance_id: instance_id,
              state: {
                code: 48,
                name: "terminated"
              }
            ]
          ]
        }
      }
    )
  end

  it "terminates an instance" do
    expect(instance_terminated?(ec2_client, instance_id)).to be(true)
  end
end
