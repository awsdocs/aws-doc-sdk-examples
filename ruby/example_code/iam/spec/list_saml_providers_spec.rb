# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative('../list_saml_providers')

describe SamlProviderLister do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:manager) { SamlProviderLister.new(iam_client) }

  describe '#list_saml_providers' do
    it 'logs the ARNs of up to the specified number of SAML providers' do
      expect { manager.list_saml_providers(10) }.not_to raise_error
    end
  end
end
