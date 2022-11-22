# frozen_string_literal: true

require 'multi_json'
require 'yaml'
require 'json'
require 'rspec'
require 'aws-sdk-rdsdataservice'
require 'aws-sdk-rds'
require 'aws-sdk-ses'
require 'pry'

# config and clients
client = Aws::RDSDataService::Client.new
rds_client = Aws::RDS::Client.new
config = YAML.load(File.read('config.yml'))

# helper method
def check_database(resource_arn)
  identifier = resource_arn.split(":cluster:")[1]
  rds_client.describe_db_clusters({db_cluster_identifier: identifier })
rescue Aws::RDS::Errors::DBClusterNotFoundFault => e
  raise "\n ############# No DB cluster exists #############\n Please run CDK script found in resources/cdk/aurora_serverless_app."
end

# check for database cluster & create table if none exists
begin
  check_database(config['resource_arn'])
  resp = client.execute_statement({
                                    resource_arn: config['resource_arn'],
                                    secret_arn: config['secret_arn'],
                                    sql: 'show tables;',
                                    database: config['database'],
                                  })

  has_table = false
  resp[0].each { |table|
    if table[0].string_value == config['table_name']
      has_table = true
    end
  }
  unless has_table
    sql = "CREATE TABLE #{config['table_name']} (work_item_id INT AUTO_INCREMENT PRIMARY KEY, description VARCHAR(400), guide VARCHAR(45), status VARCHAR(400), username VARCHAR(45), archived TINYINT(4));"
    client.execute_statement({
                                      resource_arn: config['resource_arn'],
                                      secret_arn: config['secret_arn'],
                                      sql: sql,
                                      database: config['database'],
                                    })
  end
rescue StandardError => e
  raise "Failed while checking for or creating existing database/tables:\n#{e}"
end