# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative '../list_stacks'

describe StackLister do
  let(:eb_client) { Aws::ElasticBeanstalk::Client.new }
  let(:stack_lister) { StackLister.new(eb_client, 'java') }

  describe '#list_stacks' do
    it 'successfully lists available solution stacks' do
      expect { stack_lister.list_stacks }.not_to raise_error
    end

    it 'logs at least one stack' do
      expect_any_instance_of(Logger).to receive(:info).at_least(:once)
      stack_lister.list_stacks
    end
  end
end
