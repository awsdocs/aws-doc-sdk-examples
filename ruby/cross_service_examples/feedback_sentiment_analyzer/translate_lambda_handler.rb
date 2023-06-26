require 'json'
require 'aws-sdk-translate'
require 'logger'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  # Create an instance of the Textract client
  client = Aws::Translate::Client.new(region: "us-east-1")

  client.translate_text({
                          text: event['Payload'], # required
                          source_language_code: "fr", # required
                          target_language_code: "en", # required
                        })
end
