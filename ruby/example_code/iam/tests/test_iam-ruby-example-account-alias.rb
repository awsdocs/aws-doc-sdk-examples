# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-account-alias'

describe '#list_aliases' do
  let(:account_alias) { 'my-alias' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_account_aliases: {
          account_aliases: [
            account_alias
          ]
        }
      }
    )
  end

  it 'lists information about account aliases' do
    expect { list_aliases(iam_client) }.not_to raise_error
  end
end

describe '#alias_created?' do
  let(:account_alias) { 'my-alias' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_account_alias: {}
      }
    )
  end

  it 'creates an account alias' do
    expect(alias_created?(iam_client, account_alias)).to be(true)
  end
end

describe '#alias_deleted?' do
  let(:account_alias) { 'my-alias' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        delete_account_alias: {}
      }
    )
  end

  it 'deletes an account alias' do
    expect(alias_deleted?(iam_client, account_alias)).to be(true)
  end
end
