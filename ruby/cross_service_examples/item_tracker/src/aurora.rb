# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "logger"
require "sequel"
require "multi_json"
require 'pry'
require_relative "report"

# Issues commands directly to the Amazon RDS Data Service, including SQL statements.
class AuroraActions

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

  # Gets work items from the database.
  # @param item_id [String] The Item ID to fetch. Returns all items if nil. Default: nil.
  # @param include_archived [Boolean] If true, include archived items. Default: true
  # @return [Array] The hashed records from RDS which represent work items.
  def get_work_items(item_id = nil, include_archived = nil)
    name = @table_name.to_s
    sql = @model.select(:work_item_id, :description, :guide, :status, :username, :archived).from(name.to_sym)
    sql = sql.where(archived: true?(include_archived)) if include_archived
    sql = sql.where(work_item_id: item_id.to_i) if item_id
    sql = _format_sql(sql.sql)
    @logger.info("Prepared GET query: #{sql}")
    results = run_statement(sql, "get")
    response = parse_work_items(results)
    @logger.info("Received GET response: #{response}")
    response
  end

  # Adds a work item to the database.
  # @param data [Hash] Data fields required for work item creation
  # @return: The generated ID of the new work item.
  def add_work_item(data)
    name = @table_name.to_s
    sql = @model.from(name.to_sym).insert_sql(
      description: data[:description],
      guide: data[:guide],
      status: data[:status],
      username: data[:name],
      archived: 0
    )
    sql = _format_sql(sql)
    @logger.info("Prepared POST query: #{sql}")
    response = run_statement(sql, "post")
    id = response[0][:long_value]
    @logger.info("Successfully created work_item_id: #{id}")
    id
  end

  # Archives a work item.
  # @param item_id [String] The ID of the work item to archive.
  # @returns [Boolean] If updated_records is 1, return true; else, return false.
  def archive_work_item(item_id)
    sql = @model.from(@table_name.to_sym).where(work_item_id: item_id).update_sql(archived: 1) # 1 is true, 0 is false
    sql = _format_sql(sql)
    @logger.info("Prepared PUT query: #{sql}")
    run_statement(sql, "put")
  end

  private

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

  # Helper method to centralize error formatting
  # @param msg [String] A custom error message
  def handle_error(msg)
    @logger.error(msg)
    raise msg
  end

  # Converts larger ExecuteStatementResponse into simplified Ruby object
  # @param results [Aws::RDSDataService::Types::ExecuteStatementResponse]
  # @return output [Array] A list of items, represented as hashes
  def parse_work_items(results)
    output = []
    results.each do |x|
      # name = x["username"]
      # id = x["work_item_id"]
      x["name"] = x["username"] # note: duplicative name/username field added due to front-end bug
      x["id"] = x["work_item_id"] # note: duplicative id/work_item_id field added due to front-end bug
      output.append(x)
    end
    output
  end

  # Validate response body from the API
  # @param response [Aws::RDSDataService::Types::ExecuteStatementResponse] The response body
  # @param method [String] The API Method used. Must be: post, put, get.
  # @return [RuntimeError, Boolean] If valid response, true; otherwise, RuntimeError.
  def validate_response(response, method)
    case method
    when "get"
      if response[:formatted_records].nil?
        raise "Expected formatted records returned from GET action."
      end
    when "post"
      if response[:number_of_records_updated] < 1
        raise "Expected at least 1 updated record from POST action."
      end
    end
    @logger.info("SQL call successful. Response body validated.")
  end

  # Transforms inconsistent return bodies into something API-friendly
  # @param response [Aws::RDSDataService::Types::ExecuteStatementResponse] The response body
  # @param method [String] The API Method used. Must be: post, put, get.
  # @return [Array] Containing zero or more hashes of response data
  def format_response(response, method)
    case method
    when "get"
      JSON.parse(response[:formatted_records])
      # response example:
      # [{"work_item_id"=>1,
      #   "description"=>"user research",
      #   "guide"=>"ruby",
      #   "status"=>"in-progress",
      #   "username"=>"wkhalifa",
      #   "archived"=>1}]
    when "post"
      [response[:generated_fields][0].to_h]
      # response example:
      # [{:long_value=>21}]
    when "put"
      []
    else
      raise "Configuration method. Must provide: get, post, or put."
    end
  end

  # Runs a SQL statement and associated parameters using the Amazon RDS Data Service.
  # @param sql [String] The SQL statement to run against the database.
  # @return [Array] Containing zero or more hashes of response data
  # @return [ErrorClass] Aws::Errors::ServiceError, StandardError
  def run_statement(sql, method)
    run_args = {
      'database': @db_name,
      'resource_arn': @cluster,
      'secret_arn': @secret,
      'sql': sql,
      'format_records_as': "JSON"
    }
    response = @rds_client.execute_statement(**run_args)
    validate_response(response, method)
    format_response(response, method)

  rescue Aws::RDS::Errors::ServiceError => e
    handle_error("SQL execution on #{@db_name} failed within RDS:\n#{e}")
  rescue StandardError => e
    handle_error("SQL execution on #{@db_name} failed outside of AWS:\n#{e}")
  end
end
