# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

# snippet-start:[ruby.example_code.lambda.handler.multiply]
require "logger"

# A function that multiplies two whole numbers and logs the result.
# Requires two whole numbers provided at runtime: 'first_number' and 'second_number'.
#
# @param event [Hash] Parameters sent when the function is invoked.
# @param context [Hash] Methods and properties that provide information.
# about the invocation, function, and execution environment.
# @ return product [String] The product of the two numbers.
def lambda_handler(event:, context:)
  logger = Logger.new($stdout)
  log_level = ENV["LOG_LEVEL"]
  logger.level = case log_level
                 when "debug"
                   Logger::DEBUG
                 when "info"
                   Logger::INFO
                 else
                   Logger::ERROR
                 end

  first_number = event["first_number"].to_f
  second_number = event["second_number"].to_f
  product = first_number.round * second_number.round
  logger.info("The product of #{first_number.round} and #{second_number.round} is #{product} ")
  product.to_s
end
# snippet-end:[ruby.example_code.lambda.handler.multiply]
