# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to use the Amazon Relational Database Service (Amazon RDS) Data Service to
# interact with an Amazon Aurora Serverless database.

require 'logger'
require_relative 'helpers/errors'
require 'sequel'
require 'multi_json'
# from botocore.exceptions import ClientError

@logger = Logger.new($stdout)

# Wraps issues commands directly to the Amazon RDS Data Service, including SQL statements.
class DBWrapper
  # @param config [List]
  # @param rds_client [AWS::RDS::Client] An Amazon RDS Data Service client.
  def initialize(config, rds_client)
    @cluster = config['cluster']
    @secret = config['secret']
    @db_name = config['db_name']
    @table_name = config['table_name']
    @rds_client = rds_client
    @model = Sequel::Database.new
  end

  # Runs a SQL statement and associated parameters using the Amazon RDS Data Service.
  # @param sql [String] The SQL statement to run.
  # @return The result of running the SQL statement.
  def _run_statement(sql)
    run_args = {
      'database': @db_name,
      'resourceArn': @cluster,
      'secretArn': @secret,
      'sql': sql
    }
    logger.info("Ran statement on #{@db_name}.")
    @rds_client.execute_statement(**run_args)
  rescue Aws::Errors::ServiceError => e
    if e.response['Error']['Code'] == 'BadRequestException' &&
       e.response['Error']['Message'].include?('Communications link failure')
      raise RDSResourceError(
        'The Aurora Data Service is not ready, probably because it entered '\
        'pause mode after a period of inactivity. Wait a minute for '\
        'your cluster to resume and try your request again.'
      )
    else
      logger.error("Run statement on #{@db_name} failed within AWS")
      raise RDSClientError(e)
    end
  rescue StandardError => e
    logger.error("There was an error outside of AWS:#{e}")
  end

  # Gets work items from the database.
  # @param include_archived [Boolean] If true, include archived items. Default: false
  # @return [] The result from RDS which is a list of retrieved work items.
  def get_work_items(item_id, include_archived: false)
    base_query = @model.select(:iditem, :description, :guide, :status, :username, :archived).from(:work_items)
    if include_archived
      final_query = base_query.where(archived: true)
    end
    if item_id
      final_query = base_query.where(item_id: item_id.to_i)
    end
    logger.info('Prepared query: ' + final_query.sql)
    results = _run_statement(final_query.sql)
    output = []
    results["records"].each do |record|
      output.append(record)
    end
    output
  end

  # Adds a work item to the database.
  # @param json [Hash]
  # @return: The generated ID of the new work item.
  def add_work_item(json)
    sql = @model.from(:work_items).insert_sql(
      description: json['description'],
      guide: json['guide'],
      status: json['status'],
      username: json['username']
    )
    logger.info('Prepared query: ' + sql)
    results = _run_statement(sql)
    results
  end

  # Archives a work item.
  #
  # @param iditem [] The ID of the work item to archive.
  def archive_work_item(iditem)
    sql = @model.from(:work_items).where(item_id: iditem.to_i).update_sql(archived: true)
    results = _run_statement(sql)
    results
  end

end
