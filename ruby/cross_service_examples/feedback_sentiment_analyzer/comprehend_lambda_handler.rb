require 'json'
require 'aws-sdk-comprehend'
require 'logger'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  rekognition = Aws::Comprehend::Client.new(region: 'us-west-2') # Replace with your desired AWS region

  response = rekognition.detect_sentiment({
                                            text: event['Payload'],
                                            language_code: 'fr' # Specify the language of the text
                                          })

  logger.info("Sentiment: #{response.sentiment}")
  logger.info("Sentiment Score: #{response.sentiment_score}")

  response.sentiment
end

# Usage
event = {"Payload": "CET HOTEL ÉTAIT RAVISSANT"}
context = {}
lambda_handler(event: event, context: context)
