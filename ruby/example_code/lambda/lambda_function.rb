# frozen_string_literal: true

# snippet-start:[ruby.example_code.lambda.handler.increment]
require 'logger'

# A function that increments a whole number by one (1) and logs the result.
# Requires a manually-provided runtime parameter, 'number', which must be Int
#
# @param event [Hash] contains parameters sent when the function is invoked
# @param context [Hash] contains methods and properties that provide information
# about the invocation, function, and execution environment.
def lambda_handler(event:, context:)
  logger = Logger.new($stdout)
  log_level = ENV['LOG_LEVEL']
  logger.level = case log_level
                 when 'debug'
                   Logger::DEBUG
                 when 'info'
                   Logger::INFO
                 else
                   Logger::ERROR
                 end
  logger.debug('This is a debug log message.')
  logger.info('This is an info log message. Code executed successfully!')
  number = event["number"].to_i
  incremented_number = number + 1
  logger.info("You provided #{number.round} and it was incremented to #{incremented_number.round}")
end
# snippet-end:[ruby.example_code.lambda.handler.increment]
