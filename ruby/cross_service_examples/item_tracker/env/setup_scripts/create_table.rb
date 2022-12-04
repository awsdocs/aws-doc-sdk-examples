# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "multi_json"
require "yaml"
require "json"
require "rspec"
require "aws-sdk-rdsdataservice"
require "aws-sdk-rds"
require "aws-sdk-ses"

class SetupDatabase

  def initialize
    # @config = YAML.load(File.read("config.yml"))
    @config = YAML.safe_load(File.open(File.join(File.dirname(__FILE__), '../', "config.yml")))

    @data_client = Aws::RDSDataService::Client.new
    @rds_client = Aws::RDS::Client.new
  end

  # Checks if database exists
  # @return [Boolean] false if DBClusterNotFoundFault; else true.
  def database_exists?
    identifier = @config["resource_arn"].split(":cluster:")[1]
    @rds_client.describe_db_clusters({db_cluster_identifier: identifier })
    true
  rescue Aws::RDS::Errors::DBClusterNotFoundFault => e
    false
  end

  # Checks if table exists in database
  # @return [Boolean] true if table exists, false if not.
  def table_exists?
    resp = @data_client.execute_statement({
                                      resource_arn: @config["resource_arn"],
                                      secret_arn: @config["secret_arn"],
                                      sql: "show tables;",
                                      database: @config["database"],
                                    })

    resp[0].each { |table|
      if table[0].string_value == @config["table_name"]
        return true
      end
    }
    false
  end

  # NOTE: This code contains raw string substitution and is therefore vulnerable to SQL injection
  # TODO: Refactor to use Rails ActiveRecord
  def create_table
    sql = "CREATE TABLE #{@config['table_name']} (work_item_id INT AUTO_INCREMENT PRIMARY KEY, description VARCHAR(400), guide VARCHAR(45), status VARCHAR(400), username VARCHAR(45), archived TINYINT(4));"
    @data_client.execute_statement({
                               resource_arn: @config["resource_arn"],
                               secret_arn: @config["secret_arn"],
                               sql: sql,
                               database: @config["database"],
                             })
  end

end

# check for database cluster & create table if none exists
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
