# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require "aws-sdk-elasticbeanstalk"
require "logger"

# snippet-start:[eb.Ruby.listStacks]
# Manages listing of AWS Elastic Beanstalk solution stacks
class StackLister
  # Initialize with AWS Elastic Beanstalk client
  def initialize(eb_client, filter)
    @eb_client = eb_client
    @logger = Logger.new($stdout)
    @filter = filter.downcase
  end

  # Lists and logs Elastic Beanstalk solution stacks
  def list_stacks
    stacks = @eb_client.list_available_solution_stacks.solution_stacks
    orig_length = stacks.length
    filtered_length = 0

    stacks.each do |stack|
      if @filter.empty? || stack.downcase.include?(@filter)
        @logger.info(stack)
        filtered_length += 1
      end
    end

    log_summary(filtered_length, orig_length)
  rescue Aws::Errors::ServiceError => e
    @logger.error("Error listing solution stacks: #{e.message}")
  end

  private

  # Logs summary of listed stacks
  def log_summary(filtered_length, orig_length)
    if @filter.empty?
      @logger.info("Showed #{orig_length} stack(s)")
    else
      @logger.info("Showed #{filtered_length} stack(s) of #{orig_length}")
    end
  end
end
# snippet-end:[eb.Ruby.listStacks]

# Example usage:
if $PROGRAM_NAME == __FILE__
  # Hardcoded AWS Region; adjust as needed
  eb_client = Aws::ElasticBeanstalk::Client.new(region: region)
  stack_lister = StackLister.new(eb_client, "java")
  stack_lister.list_stacks
end
