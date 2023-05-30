# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "yaml"
require "aws-sdk-glue"
require 'optparse'

# A simple class for checking for databases and tables in an Amazon Aurora DB cluster.
class SetupGlueStack

    def initialize
        @config = YAML.safe_load(File.open(File.join(File.dirname(__FILE__), "../", "setup_scenario_getting_started.yaml")))
        @glue_client = Aws::Glue::Client.new
    end

    # Deploys scaffold resources used by the example. The resources are
    # defined in the CloudFormation script. They're deployed as a CloudFormation stack
    # so you can manage and destroy them by using CloudFormation actions.
    #
    # :param cloud_formation_script: The path to a CloudFormation script.
    # :param stack_name: The name of the CloudFormation stack.
    # :param cf_resource: A Boto3 CloudFormation resource.
    # :return: A dict of outputs from the stack.
    def deploy(cloud_formation_script, stack_name, cf_resource)
        with open(cloud_formation_script) as setup_file:
            setup_template = setup_file.read()
        print(f"Creating {stack_name}.")
        stack = cf_resource.create_stack(
            StackName=stack_name,
            TemplateBody=setup_template,
            Capabilities=['CAPABILITY_NAMED_IAM'])
        print("Waiting for stack to deploy.  This typically takes a minute or two.")
        waiter = cf_resource.meta.client.get_waiter('stack_create_complete')
        waiter.wait(StackName=stack.name)
        stack.load()
        print(f"Stack status: {stack.stack_status}")
        print("Created resources:")
        for resource in stack.resource_summaries.all():
            print(f"\t{resource.resource_type}, {resource.physical_resource_id}")
        print("Outputs:")
        outputs = {}
        for oput in stack.outputs:
            outputs[oput['OutputKey']] = oput['OutputValue']
            print(f"\t{oput['OutputKey']}: {oput['OutputValue']}")
        return outputs
    end

    # Destroys the resources managed by the CloudFormation stack, and the CloudFormation
    # stack itself.
    #
    # :param stack: The CloudFormation stack that manages the example resources.
    # :param cf_resource: A Boto3 CloudFormation resource.
    # :param s3_resource: A Boto3 S3 resource.
    def destroy(stack, cf_resource, s3_resource):
        bucket_name = None
        for oput in stack.outputs:
            if oput['OutputKey'] == 'BucketName'
                bucket_name = oput['OutputValue']
            end
        end
        if bucket_name is not None:
            print(f"Deleting all objects in bucket {bucket_name}.")
            s3_resource.Bucket(bucket_name).objects.delete()
        end
        print(f"Deleting {stack.name}.")
        stack.delete()
        print("Waiting for stack removal.")
        waiter = cf_resource.meta.client.get_waiter('stack_delete_complete')
        waiter.wait(StackName=stack.name)
        print("Stack delete complete.")
    end

    def main()
        options = {}
        OptionParser.new do |opts|
          opts.banner = "Usage: script.rb [options]"
          opts.on("-a", "--action ACTION", "Indicates the action that the script performs.") do |action|
            options[:action] = action
          end
          opts.on("-s", "--script SCRIPT", "The name of the CloudFormation script to use to deploy resources.") do |script|
            options[:script] = script
          end
        end.parse!

        puts '-' * 88
        puts "Welcome to the AWS Glue getting started with crawlers and jobs scenario."
        puts '-' * 88

        # Access the action and script using options[:action] and options[:script] respectively.
    end

if __FILE__ == $0
    # Checks for a database cluster & creates a table if none exists.
  begin
    setup = SetupDatabase.new
    if setup.database_exists?
      unless setup.table_exists?
        setup.create_table
      end
    else
      raise "No DB cluster exists! Please run CDK script found in resources/cdk/aurora_serverless_app."
    end
  rescue StandardError => e
    raise "Failed while checking for or creating existing database/tables:\n#{e}"
  end
end
