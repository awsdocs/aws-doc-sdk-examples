require 'json'
require 'aws-sdk-textract'
require 'logger'
# require 'pry'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event.to_s}\n")
  logger.info("context:\n #{context.to_s}\n")

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
  logger.info("textract params: \n#{params.to_s}\n")
  response = client.detect_document_text(params)
  logger.info("#{response.to_s}\n")

  extracted_words = []

  response.blocks.each do |obj|
    if obj.block_type.include?('LINE')
      if obj.respond_to?(:text) && obj.text
        extracted_words.append(obj.text)
      end
    end
  end

  logger.info("extracted words: #{extracted_words.to_s}")

  extracted_words.join(" ")
end