# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require_relative '../update_app'
require 'rspec'

describe RailsAppDeployer do
  let(:eb_client) { Aws::ElasticBeanstalk::Client.new }
  let(:s3_client) { Aws::S3::Client.new }
  let(:app_deployer) { RailsAppDeployer.new(eb_client, s3_client, 'MyRailsApp') }

  describe '#deploy' do
    it 'successfully deploys the application' do
      expect { app_deployer.deploy }.not_to raise_error
    end
  end
end
