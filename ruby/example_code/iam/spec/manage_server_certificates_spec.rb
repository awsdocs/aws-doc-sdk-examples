# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require_relative '../manage_server_certificates'
require 'rspec'

describe ServerCertificateManager do
  before(:each) do
    @iam_client = Aws::IAM::Client.new
    @manager = ServerCertificateManager.new(@iam_client)
  end

  describe '#list_server_certificate_names', :integ do
    it 'logs server certificate names' do
      expect { @manager.list_server_certificate_names }.not_to raise_error
    end
  end
end
