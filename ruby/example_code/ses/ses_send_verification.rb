# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to verify an email address for Amazon Simple Email Service (Amazon SES).

# snippet-start:[s3.ruby.ses_send_verification.rb]

require 'aws-sdk-ses'  # v2: require 'aws-sdk'

# Replace recipient@example.com with a "To" address.
recipient = "z"

# Create a new SES resource in the us-west-2 region.
# Replace us-west-2 with the AWS Region you're using for Amazon SES.
ses = Aws::SES::Client.new(region: 'us-west-2')

# Try to verify email address.
begin
  ses.verify_email_identity({
    email_address: recipient
  })

  puts 'Email sent to ' + recipient

# If something goes wrong, display an error message.
rescue Aws::SES::Errors::ServiceError => error
  puts "Email not sent. Error message: #{error}"
end
# snippet-end:[s3.ruby.ses_send_verification.rb]
