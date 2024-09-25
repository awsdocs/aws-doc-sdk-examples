# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative '../list_apps'

describe ElasticBeanstalkManager do
  let(:eb_client) { Aws::ElasticBeanstalk::Client.new }
  let(:eb_manager) { ElasticBeanstalkManager.new(eb_client) }

  describe '#list_applications' do
    it 'logs application and environment details without error' do
      expect { eb_manager.list_applications }.not_to raise_error
    end
  end
end
