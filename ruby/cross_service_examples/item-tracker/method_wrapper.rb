# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Invoked by logic within Sinatra REST API routes (sinatra.rb)
# Invokes SQL logic within the RDS Wrapper (rds_wrapper.rb)

require 'logger'
require 'db_wrapper'
require 'multi_json'

logger = Logger.new(STDOUT)

# Encapsulates a REST resource that represents a list of work items.
class MethodWrapper

  def initialize(config)
    @rds_wrapper = DBWrapper.new(config, Aws::RDS::Client.new)
  end

  # Gets a list of zero or more work items
  # @param item_id: When specified, the ID of a single item to retrieve.
  # @param include_archived: Include archived results
  # @return: A list of work items and an HTTP result code.
  def get(item_id = nil, include_archived = nil)
    status = 200
    response = @rds_wrapper.get_work_items(item_id, include_archived)
    logger.info(response)
  rescue ClientError => err
    status = 400
    response = "There was an error on the RDS client:\n #{err}"
    logger.error(response)
  rescue StandardError => err
    status = 500
    response = "There was another error: \n #{err}"
    logger.error(response)
  ensure
    return MultiJson.dump(response), status
  end

  # Adds a work item to the database.
  # @param args: The request body data, validated and transformed by the work item schema.
  # @return: The generated ID of the newly added work item, and an HTTP result code.
  def post(item_json)
    status = 200
    logger.info("Attempting to add item:\n #{item_json}")
    response = @rds_wrapper.add_work_item(item_json)
    logger.info(": #{item_json}")
  rescue ClientError => err
    status = 503
    response = "There was an issue with the target RDS resource:\n #{err}"
    logger.error(response)
  rescue StandardError => err
    status = 500
    response = "There was another error: \n #{err}"
    logger.error(response)
  ensure
    return response.as_json, status
  end

  # Archives a work item
  # @param item_id: The ID of the work item to update.
  # @return: The ID of the archived item and an HTTP result code.
  def archive(item_id)
    response = @rds_wrapper.archive_work_item(item_id)
    status = 200
    logger.info("item_id: #{item_id}, action: #{action}")
  rescue ClientError => err
    status = 500
    response = "There was an issue on the RDS client:\n #{err}"
    logger.error(response)
  rescue StandardError => err
    status = 400
    response = "There was another error: \n #{err}"
    logger.error(response)
  ensure
    return MultiJson.dump(response), status
  end

end

