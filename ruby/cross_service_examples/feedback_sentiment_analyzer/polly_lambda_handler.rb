require 'json'
require 'aws-sdk-polly'
require 'aws-sdk-s3'
require 'logger'
# require 'pry'

def lambda_handler(event:, context:)

  logger = Logger.new($stdout)

  logger.info("event:\n #{event}")
  logger.info("context:\n #{context}")

  # Create an instance of the Textract client
  polly_client = Aws::Polly::Client.new(region: "us-east-1")

  resp = polly_client.synthesize_speech({
                                          output_format: "mp3",
                                          text: event['Payload']['translated_text'],
                                          voice_id: "Joanna",
                                        })

  logger.info(resp.to_s)

  # Define the bucket name and file name for the MP3 file in S3
  bucket_name = 'fordslittletest'
  file_name = 'recording.mp3'

  # Generate a unique key for the file in S3 (optional)
  # This ensures that each file has a unique key to avoid conflicts
  unique_key = SecureRandom.uuid

  s3_client = Aws::S3::Client.new(region: "us-east-1")

  # Put the MP3 file to S3
  s3_client.put_object(
    bucket: bucket_name,
    key: "#{unique_key}/#{file_name}", # Use a unique key for the file
    body: resp.audio_stream
  )

  # # Print the URL of the uploaded file
  # s3_file_url = "https://#{bucket_name}.s3.amazonaws.com/#{unique_key}/#{file_name}"
  # puts "Uploaded MP3 file URL: #{s3_file_url}"


end