# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-get-instance-info-by-tag"

describe "#list_instance_ids_states" do
  let(:tag_key) { "my-key" }
  let(:tag_value) { "my-value" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_instances: {
          reservations: [
            instances: [
              { instance_id: "i-abc123-1", state: { name: "running" }, tags: [key: "my-key", value: "my-value"] },
              { instance_id: "i-abc123-2", state: { name: "running" }, tags: [key: "my-key", value: "my-value"] },
              { instance_id: "i-abc123-3", state: { name: "running" }, tags: [key: "my-key", value: "my-value"] }
            ]
          ]
        }
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "lists instance IDs and states" do
    expect { list_instance_ids_states_by_tag(ec2_resource, tag_key, tag_value) }.not_to raise_error
  end
end
