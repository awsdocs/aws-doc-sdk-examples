# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-create-instance"

describe "#instance_created?" do
  let(:image_id) { "ami-0947d2ba12EXAMPLE" }
  let(:key_pair_name) { "my-key-pair" }
  let(:tag_key) { "my-key" }
  let(:tag_value) { "my-value" }
  let(:instance_id) { "i-01c7a43263ddbc7EX" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        run_instances: {
          instances: [
            instance_id: instance_id
          ]
        },
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
        create_tags: {}
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates an instance" do
    expect(
      instance_created?(
        ec2_resource,
        image_id,
        key_pair_name,
        tag_key,
        tag_value
      )
    ).to be(true)
  end
end
