# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../ec2-ruby-example-key-pairs"

describe "#key_pair_created?" do
  let(:key_pair_name) { "delete-this-key-pair--only-a-test" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        create_key_pair: {
          key_name: key_pair_name,
          key_fingerprint: "EX:AM:PL:E4:ac:f2:42:71:6d:26:26:8f:31:b7:67:a2:6E:XA:MP:LE",
          key_pair_id: "key-08021f7c59EXAMPLE",
          key_material: "-----BEGIN RSA PRIVATE KEY-----\n_omitted_for_brevity\n-----END RSA PRIVATE KEY-----"
        }
      }
    )
  end
  let(:ec2_resource) { Aws::EC2::Resource.new(client: ec2_client) }

  it "creates a key pair" do
    expect(key_pair_created?(ec2_client, key_pair_name)).to be(true)
  end
end

describe "#describe_key_pairs" do
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        describe_key_pairs: {
          key_pairs: [
            {
              key_name: "my-key-pair-1",
              key_fingerprint: "EX:AM:PL:E4:ac:f2:42:71:6d:26:26:8f:31:b7:67:a2:6E:XA:MP:LE"
            },
            {
              key_name: "my-key-pair-2",
              key_fingerprint: "EX:AM:PL:E4:bd:f2:42:71:6d:26:26:8f:31:b7:67:a2:6E:XA:MP:LE"
            }
          ]
        }
      }
    )
  end

  it "lists information about available key pairs" do
    expect { describe_key_pairs(ec2_client) }.not_to raise_error
  end
end

describe "#key_pair_deleted?" do
  let(:key_pair_name) { "my-key-pair" }
  let(:ec2_client) do
    Aws::EC2::Client.new(
      stub_responses: {
        delete_key_pair: {}
      }
    )
  end

  it "deletes a key pair" do
    expect(key_pair_deleted?(ec2_client, key_pair_name)).to be(true)
  end
end
