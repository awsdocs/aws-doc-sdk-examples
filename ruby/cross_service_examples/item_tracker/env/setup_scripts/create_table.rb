# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require 'yaml'
require 'aws-sdk-rdsdataservice'
require 'aws-sdk-rds'
require 'aws-sdk-ses'

# A simple class for checking for databases and tables in an Amazon Aurora DB cluster.
class SetupDatabase
  def initialize
    @config = YAML.safe_load(File.open(File.join(File.dirname(__FILE__), '../', 'config.yml')))
    @data_client = Aws::RDSDataService::Client.new
    @rds_client = Aws::RDS::Client.new
  end

  # Checks if database exists.
  # @return [Boolean] false if DBClusterNotFoundFault; else true.
  def database_exists?
    identifier = Aws::ARNParser.parse(@config['resource_arn'])
    @rds_client.wait_until(:db_cluster_available, db_cluster_identifier: identifier.resource) do |w|
      w.max_attempts = 5
      w.delay = 5
    end
  rescue Aws::RDS::Errors::DBClusterNotFoundFault
    false
  end

  # Checks if table exists in database.
  # @return [Boolean] true if table exists, false if not.
  def table_exists?
    resp = @data_client.execute_statement(
      {
        resource_arn: @config['resource_arn'],
        secret_arn: @config['secret_arn'],
        sql: 'show tables;',
        database: @config['database']
      }
    )

    resp[0].each do |table|
      return true if table[0].string_value == @config['table_name']
    end
    false
  end

  # NOTE: This code contains raw string substitution and is therefore vulnerable to SQL injection.
  # TODO: Refactor to use Rails ActiveRecord
  def create_table
    @data_client.execute_statement(
      {
        resource_arn: @config['resource_arn'],
        secret_arn: @config['secret_arn'],
        sql: 'CREATE TABLE work_items (work_item_id INT AUTO_INCREMENT PRIMARY KEY,
description VARCHAR(400),
guide VARCHAR(45),
status VARCHAR(400),
username VARCHAR(45),
archived TINYINT(4));',

        database: @config['database']
      }
    )
  end
end

if __FILE__ == $PROGRAM_NAME
  # Checks for a database cluster & creates a table if none exists.
  begin
    setup = SetupDatabase.new
    raise 'No DB cluster exists! Please run CDK script found in resources/cdk/aurora_serverless_app.' unless setup.database_exists?

    setup.create_table unless setup.table_exists?
  rescue StandardError => e
    raise "Failed while checking for or creating existing database/tables:\n#{e}"
  end
end
