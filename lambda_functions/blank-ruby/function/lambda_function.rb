# frozen_string_literal: true

# lambda_function.rb
require 'logger'
require 'json'
require 'aws-sdk-lambda'
$client = Aws::Lambda::Client.new
$client.get_account_settings

require 'aws-xray-sdk/lambda'

def lambda_handler(event:, context:)
  logger = Logger.new($stdout)
  logger.info('## ENVIRONMENT VARIABLES')
  vars = {}
  ENV.each do |variable|
    vars[variable[0]] = variable[1]
  end
  logger.info(vars.to_json)
  logger.info('## EVENT')
  logger.info(event.to_json)
  logger.info('## CONTEXT')
  logger.info(context)
  $client.get_account_settings.account_usage.to_h
end
