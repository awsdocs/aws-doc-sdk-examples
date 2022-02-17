# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-server-certificates'

describe '#list_server_certificate_names' do
  let(:server_certificate_name) { 'my-server-certificate' }
  let(:server_certificate_id) { 'AIDACKCEVSQ6C2EXAMPLE' }
  let(:server_certificate_arn) { "arn:aws:iam::111111111111:certificate/#{server_certificate_name}" }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_server_certificates: {
          server_certificate_metadata_list: [
            server_certificate_name: server_certificate_name,
            server_certificate_id: server_certificate_id,
            arn: server_certificate_arn,
            path: '/'
          ]
        }
      }
    )
  end

  it 'lists available server certificate names' do
    expect { list_server_certificate_names(iam_client) }.not_to raise_error
  end
end

describe '#server_certificate_name_changed?' do
  let(:server_certificate_current_name) { 'my-server-certificate' }
  let(:server_certificate_new_name) { 'my-changed-server-certificate' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        update_server_certificate: {}
      }
    )
  end

  it 'changes the name of a server certificate' do
    expect(
      server_certificate_name_changed?(
        iam_client,
        server_certificate_current_name,
        server_certificate_new_name
      )
    ).to be(true)
  end
end

describe '#server_certificate_deleted?' do
  let(:server_certificate_name) { 'my-server-certificate' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        delete_server_certificate: {}
      }
    )
  end

  it 'deletes a server certificate' do
    expect(server_certificate_deleted?(iam_client, server_certificate_name)).to be(true)
  end
end
