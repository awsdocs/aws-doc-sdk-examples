# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
# frozen_string_literal: true

require 'json'
require 'aws-sdk-translate'
require 'logger'

def lambda_handler(event:, context:)
  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  # Create an instance of the Translate client
  client = Aws::Translate::Client.new(region: event['region'])

  client.translate_text({
                          text: event['extracted_text'], # required
                          source_language_code: event['source_language_code'], # required
                          target_language_code: 'en'
                        })
end
