require 'json'
require 'aws-sdk-textract'
require 'logger'
# require 'pry'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  # Define existing AWS resources required
  iam_role = 'arn:aws:iam::260778392212:role/service-role/StepFunctions-MyStateMachine-role-907d5af6'
  s3_bucket = 'fordslittletest'
  sns_topic = 'arn:aws:sns:us-east-1:260778392212:fsa-test'

  # Create an instance of the Textract client
  client = Aws::Textract::Client.new(region: "us-east-1")

  params = {
    document_location: {
      s3_object: {
        bucket: s3_bucket,
        name: 'pix/comment.png'
      }
    },
    notification_channel: {
      role_arn: iam_role,
      sns_topic_arn: sns_topic
    }
  }
  logger.info("textract params: \n#{params}")
  response = client.start_document_text_detection(params)
  logger.info(response)
  execution_arn = response[:job_id]

  extracted_words = []

  # Wait for a state machine execution to complete
  loop do
    # Get the status of a state machine execution
    response = client.get_document_text_detection({job_id: execution_arn})
    status = response[:job_status]
    logger.info(status)
    break if status == 'SUCCEEDED' || status == 'FAILED'
    sleep(5) # Wait for 5 seconds before checking again
  end

  logger.info("function either successful or failed")

  response.blocks.each do |obj|
    if obj.block_type.include?('LINE')
      if obj.respond_to?(:text) && obj.text
        extracted_words.append(obj.text)
        if obj.include?('RAVISSANT')
          puts obj.to_s
        end
      end
    end
  end

  logger.info("extracted words: #{extracted_words.to_s}")

  extracted_words.join(" ")
end

test = lambda_handler(event: nil, context: nil)
puts test