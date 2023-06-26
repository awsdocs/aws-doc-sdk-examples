require 'json'
require 'aws-sdk-comprehend'
require 'logger'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event.to_s}\n")
  logger.info("context:\n #{context.to_s}\n")

  rekognition_client = Aws::Comprehend::Client.new(region: 'us-west-2') # Replace with your desired AWS region

  logger.info("paylaod:\n #{event['Payload']}")

  response = rekognition_client.batch_detect_dominant_language({
                                                                 text_list: [event['Payload']], # required
                                                               })

  language_code = response.result_list[0].languages[0].language_code

  logger.info("detected dominant language: #{language_code}")

  response = rekognition_client.detect_sentiment({
                                                   text: event['Payload'],
                                                   language_code: language_code
                                                 })

  logger.info("Sentiment: #{response.sentiment}")
  logger.info("Sentiment Score: #{response.sentiment_score}")

  response.sentiment
end