# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-start-instance-i-123abc"

describe "#instance_started?" do
  let(:instance_id) { "i-033c48ef067af3dEX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instance_status: {
          instance_statuses: [
            {
              instance_state: {
                name: "stopped"
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
        describe_instances: {
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
      }
    )
  end

  it "starts an instance" do
    expect(instance_started?(ec2_client, instance_id)).to be(true)
  end
end
