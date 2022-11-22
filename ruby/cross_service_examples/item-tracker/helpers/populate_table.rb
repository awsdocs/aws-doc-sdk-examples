# frozen_string_literal: true

require 'multi_json'
require 'yaml'
require 'json'
require 'rspec'
require 'aws-sdk-rdsdataservice'
require 'aws-sdk-rds'
require 'aws-sdk-ses'
require 'pry'
require 'faker'

require_relative('../db_wrapper')

client = Aws::RDSDataService::Client.new
rds_client = Aws::RDS::Client.new
config = YAML.load(File.read('../helpers/config.yml'))
wrapper = DBWrapper.new(config, client)

10.times do
  username = ['ltolstoy', 'jsteinbeck', 'jkerouac', 'wkhalifa'].sample
  item_data = {
    :description => ['New feature', 'Quick bugfix', 'User research', 'Tech debt'].sample,
    :guide => ['cpp', 'python', 'go', 'ruby', 'dotnet', 'js', 'php' ].sample,
    :status => ['backlog', 'icebox', 'unrefined', 'done', 'in-progress'].sample,
    :username => username,
    :name => username,
    :archived => [0, 1].sample
  }
  wrapper.add_work_item(item_data, config['table_name'])
end

begin
  identifier = config['resource_arn'].split(":cluster:")[1]
  rds_client.describe_db_clusters({db_cluster_identifier: identifier })
rescue Aws::RDS::Errors::DBClusterNotFoundFault => e
  raise "\n ############# No BD cluster exists #############\n Please run CDK script found in resources/cdk/aurora_serverless_app."
end

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
  sql = "CREATE TABLE #{config['table_name']} (id VARCHAR(45), description VARCHAR(400), guide VARCHAR(45), status VARCHAR(400), username VARCHAR(45), archived TINYINT(4));"
  client.execute_statement({
                                    resource_arn: config['resource_arn'],
                                    secret_arn: config['secret_arn'],
                                    sql: sql,
                                    database: config['database'],
                                  })
end