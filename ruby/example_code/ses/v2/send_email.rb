# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to send a message using the Amazon Simple Email Service (Amazon SES) v2 client.

# snippet-start:[ruby.example_code.ses.v2.send_email]
require "aws-sdk-sesv2" # v2: require 'aws-sdk'.
require_relative "config" # Recipient and sender email addresses.

# Set up the SESv2 client.
client = Aws::SESV2::Client.new(region: AWS_REGION)

def send_email(client, sender_email, recipient_email)
  response = client.send_email(
    {
       from_email_address: sender_email,
       destination: {
         to_addresses: [recipient_email]
       },
       content: {
         simple: {
           subject: {
             data: "Test email subject"
           },
           body: {
             text: {
               data: "Test email body"
             }
           }
         }
       }
    }
  )
  puts "Email sent from #{SENDER_EMAIL} to #{RECIPIENT_EMAIL} with message ID: #{response.message_id}"
end

send_email(client, SENDER_EMAIL, RECIPIENT_EMAIL)
# snippet-end:[ruby.example_code.ses.v2.send_email]
