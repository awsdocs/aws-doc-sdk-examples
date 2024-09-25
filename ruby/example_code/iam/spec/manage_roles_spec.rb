# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'rspec'
require_relative '../manage_roles'

describe RoleManager do
  before(:all) do
    @iam_client = Aws::IAM::Client.new
    @role_manager = RoleManager.new(@iam_client)
    @role_name = "rspec-test-role-#{Time.now.to_i}"
    @assume_role_policy_document = {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Effect' => 'Allow',
          'Principal' => { 'Service' => 'ec2.amazonaws.com' },
          'Action' => 'sts:AssumeRole'
        }
      ]
    }
    @policy_arns = ['arn:aws:iam::aws:policy/ReadOnlyAccess']
  end

  describe '#create_role' do
    it 'creates a role and returns its ARN' do
      role_arn = @role_manager.create_role(@role_name, @assume_role_policy_document, @policy_arns)
      expect(role_arn).to be_a(String)
      expect(role_arn).to include('arn:aws:iam::')
    end
  end

  describe '#list_roles' do
    it 'lists available roles' do
      roles = @role_manager.list_roles(1000) # Adjust count as necessary
      expect(roles).to include(@role_name)
    end
  end

  describe '#get_role' do
    it 'retrieves data about a specific role' do
      role = @role_manager.get_role(@role_name)
      expect(role).to be_a(Aws::IAM::Types::Role)
      expect(role.role_name).to eq(@role_name)
    end
  end

  describe '#delete_role' do
    it 'deletes a specified role' do
      expect { @role_manager.delete_role(@role_name) }.not_to raise_error
    end
  end

  describe '#create_service_linked_role' do
    it 'creates a service-linked role and returns its name' do
      role_name = @role_manager.create_service_linked_role('autoscaling.amazonaws.com',
                                                           'Role for AutoScaling services', "test-#{Time.now.to_i}")
      expect(role_name).not_to be_nil
      @role_manager.delete_service_linked_role(role_name)
    end
  end
end
