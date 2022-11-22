# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "logger"
require "sequel"
require "multi_json"
require_relative "report"

class RDSResourceError < Exception
end

class RDSClientError < Exception
end

# Wraps issues commands directly to the Amazon RDS Data Service, including SQL statements.
class DBWrapper

  # @param config [List]
  # @param rds_client [AWS::RDS::Client] An Amazon RDS Data Service client.
  def initialize(config, rds_client)
    @cluster = config["resource_arn"]
    @secret = config["secret_arn"]
    @db_name = config["database"]
    @table_name = config["table_name"]
    @rds_client = rds_client
    @model = Sequel::Database.new
    @logger = Logger.new($stdout)
  end

  # Helper method to prep SQL for execution
  # @param sql [String]
  # @return [String]
  def _format_sql(sql)
    sql = sql.delete '"'
    sql.downcase
  end

  # Helper method to convert Strings to Booleans
  # @param [String]
  # @return [Boolean]
  def true?(obj)
    obj.to_s.downcase == "true"
  end

  # Converts larger ExecuteStatementResponse into simplified Ruby object
  # @param results [Aws::RDSDataService::Types::ExecuteStatementResponse]
  # @return output [List] A list of items, represented as hashes
  def _parse_work_items(results)
    json = MultiJson.load(results)
    output = []
    json.each do |x|
      name = x["username"]
      id = x["work_item_id"]
      x["name"] = name
      x["id"] = id
      output.append(x)
    end
    output = MultiJson.dump(output)
    @logger.debug("Parsed response: #{output}")
    output
  end

  # Runs a SQL statement and associated parameters using the Amazon RDS Data Service.
  # @param sql [String] The SQL statement to run against the database.
  # @return [Aws::RDSDataService::Types::ExecuteStatementResponse,
  #   RDSResourceError, RDSClientError, StandardError] Output of the request to
  #   run a SQL statement against the database, or error class.
  def _run_statement(sql)
    run_args = {
      'database': @db_name,
      'resource_arn': @cluster,
      'secret_arn': @secret,
      'sql': sql,
      'format_records_as': "JSON"
    }
    @logger.info("Ran statement on #{@db_name}.")
    @rds_client.execute_statement(**run_args)
  rescue Aws::Errors::ServiceError => e
    if e.response["Error"]["Code"] == "BadRequestException" &&
       e.response["Error"]["Message"].include?("Communications link failure")
      raise RDSResourceError(
        "The Aurora Data Service is not ready, probably because it entered "\
        "pause mode after a period of inactivity. Wait a minute for "\
        "your cluster to resume and try your request again."
      )
    else
      @logger.error("Run statement on #{@db_name} failed within AWS")
      raise RDSClientError(e)
    end
  rescue StandardError => e
    @logger.error("There was an error outside of AWS:#{e}")
    raise
  end

  # Gets database table name
  def get_table_name
    sql = "SHOW TABLES"
    sql = _format_sql(sql)
    @logger.info("Prepared GET query: #{sql}")
    response = _run_statement(sql)
    response.formatted_records.delete_prefix('"').delete_suffix('"')
  end

  # Gets work items from the database.
  # @param item_id [String] The Item ID to fetch. Returns all items if nil. Default: nil.
  # @param include_archived [Boolean] If true, include archived items. Default: true
  # @return [Array] The hashed records from RDS which represent work items.
  def get_work_items(item_id = nil, include_archived = nil, table_name = @table_name)
    sql = @model.select(:work_item_id, :description, :guide, :status, :username, :archived).from(table_name.to_sym)
    sql = sql.where(archived: true?(include_archived)) if include_archived
    sql = sql.where(work_item_id: item_id.to_i) if item_id
    sql = _format_sql(sql.sql)
    @logger.info("Prepared GET query: #{sql}")
    results = _run_statement(sql)
    body = results.formatted_records.delete_prefix('"').delete_suffix('"')
    response = _parse_work_items(body)
    json = MultiJson.load(response)
    @logger.info("Received GET response: #{json}")
    json
  end

  # Adds a work item to the database.
  # @param data [Hash] Data fields required for work item creation
  # @param table_name [String] The name of the table. Must use snake_case.
  # @return: The generated ID of the new work item.
  def add_work_item(data, table_name = @table_name)
    sql = @model.from(table_name.to_sym).insert_sql(
      description: data[:description],
      guide: data[:guide],
      status: data[:status],
      username: data[:name],
      archived: data[:archived]
    )
    sql = _format_sql(sql)
    @logger.info("Prepared POST query: #{sql}")
    response = _run_statement(sql)
    id = response["generated_fields"][0]["long_value"]
    @logger.info("Successfully created work_item_id: #{id}")
    id
  end

  # Archives a work item.
  # @param item_id [String] The ID of the work item to archive.
  # @returns [Boolean] If updated_records is 1, return true; else, return false.
  def archive_work_item(item_id, table_name = @table_name)
    sql = @model.from(table_name.to_sym).where(work_item_id: item_id).update_sql(archived: 1) # 1 is true, 0 is false
    sql = _format_sql(sql)
    @logger.info("Prepared PUT query: #{sql}")
    response = _run_statement(sql)
    if response.number_of_records_updated == 1
      @logger.info("Successfully archived item_id: #{item_id}")
      true
    else
      false
    end
  end
end
