# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

RSpec.describe EnableResource do
  let(:enableresource_client) { Aws::EnableResource::Client.new(stub_responses true)}
  let(:enableresource) do
  EnableResource.new(
  enableresource_client: enableresource_client
    )
  end

describe '#enableresource' do
   it 'enables the resource with the ARN to publish to the topic' do
     enableresource_client.stub_responses(
     :enable_resource, :resources => [
      {  :attribute_name => "Policy",
         :attribute_value => "policy"},
       { :attribute_name => "AnotherPolicy",
         :attribute_value => "anotherpolicy"}
        ]
        )
    enableresource.enableresources()
     end



