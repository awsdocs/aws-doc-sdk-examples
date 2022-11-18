require 'aws-sdk-rdsdataservice'
re

# Pre-requisite: must have already run CDK script to create RDS database

config = File.read('config.yml')

sql = "create table #{config['table_name']} (work_item_id INT AUTO_INCREMENT PRIMARY KEY, created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, description TEXT, guide TEXT, status TEXT, username VARCHAR(45), archive BOOL DEFAULT 0);"

resp = client.execute_statement({
                                  resource_arn: config['resource_arn'],
                                  secret_arn: config['secret_arn'],
                                  sql: sql,
                                  database: config['database'],
                                })




# check if database exists

# delete database

# create database

# add 15 items