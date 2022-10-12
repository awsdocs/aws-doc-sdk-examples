# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-get-all-instance-info"

describe "#list_instance_ids_states" do
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instances: {
          reservations: [
            instances: [
              { instance_id: "i-abc123-1", state: { name: "running" } },
              { instance_id: "i-abc123-2", state: { name: "running" } },
              { instance_id: "i-abc123-3", state: { name: "running" } }
            ]
          ]
        }
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "lists instance IDs and states" do
    expect { list_instance_ids_states(ec2_resource) }.not_to raise_error
  end
end
