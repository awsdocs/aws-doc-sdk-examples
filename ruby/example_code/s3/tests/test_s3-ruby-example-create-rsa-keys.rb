# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-create-rsa-keys'

describe '#public_and_private_key_created?' do
  let(:passphrase) { 'my-passphrase' }

  it 'confirms whether the public and private key pair was created' do
    mock_public_key_file = double('File')
    allow(mock_public_key_file).to receive(:write).with(/-----BEGIN PUBLIC KEY-----/)
    allow(mock_public_key_file).to receive(:close).with(no_args)

    mock_private_key_file = double('File')
    allow(mock_private_key_file).to receive(:write).with(/-----BEGIN RSA PRIVATE KEY-----/)
    allow(mock_private_key_file).to receive(:close).with(no_args)

    expect(
      public_and_private_key_created?(
        mock_public_key_file,
        mock_private_key_file,
        passphrase
      )
    ).to eq(true)
  end
end
