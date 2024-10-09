# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
# frozen_string_literal: true

require 'json'
require 'aws-sdk-comprehend'
require 'logger'

def lambda_handler(event:, context:)
  logger = Logger.new($stdout)

  logger.info("event:\n #{event}\n")
  logger.info("context:\n #{context}\n")

  comprehend_client = Aws::Comprehend::Client.new(region: event['region'])

  source_text = event['source_text']

  logger.info("payload:\n #{source_text}")

  response = comprehend_client.detect_dominant_language({ text: source_text })

  language_code = response.languages[0].language_code

  logger.info("detected dominant language: #{language_code}")

  response = comprehend_client.detect_sentiment({
                                                   text: source_text,
                                                   language_code:
                                                 })

  logger.info("Sentiment: #{response.sentiment}")
  logger.info("Sentiment Score: #{response.sentiment_score}")

  { 'sentiment' => response.sentiment, 'language_code' => language_code }
end
