# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
# frozen_string_literal: true

require 'json'
require 'aws-sdk-textract'
require 'logger'

def lambda_handler(event:, context:)
  logger = Logger.new($stdout)

  logger.info("event:\n #{event}\n")
  logger.info("context:\n #{context}\n")

  # Create an instance of the Textract client
  client = Aws::Textract::Client.new(region: event['region'])

  params = {
    document: {
      s3_object: {
        bucket: event['bucket'],
        name: event['object']
      }
    }
  }
  logger.info("textract params: \n#{params}\n")
  response = client.detect_document_text(params)
  logger.info("#{response}\n")

  extracted_words = []

  response.blocks.each do |obj|
    next unless obj.block_type.include?('LINE')

    extracted_words.append(obj.text) if obj.respond_to?(:text) && obj.text
  end

  logger.info("extracted words: #{extracted_words}")

  extracted_words.join(' ')
end
