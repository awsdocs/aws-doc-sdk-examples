require 'json'
require 'aws-sdk-polly'
require 'logger'
# require 'pry'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  # Create an instance of the Textract client
  client = Aws::Polly::Client.new(region: "us-east-1")

  client.synthesize_speech({
                                    output_format: "mp3",
                                    text: event['Payload'],
                                    voice_id: "Joanna",
                                  })

end
event = {"Payload": "CET HOTEL ÉTAIT RAVISSANT"}
context = {}
lambda_handler(event: event, context: context)