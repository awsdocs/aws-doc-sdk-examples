# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-reboot-instance-i-123abc"

describe "#request_instance_reboot" do
  let(:instance_id) { "i-123abc" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instances: {
          reservations: [
            instances: [
              instance_id: instance_id,
              state: {
                name: "running"
              }
            ]
          ]
        },
        reboot_instances: {}
      }
    )
  end

  it "sends an instance reboot request" do
    expect { request_instance_reboot(ec2_client, instance_id) }.not_to raise_error
  end
end
