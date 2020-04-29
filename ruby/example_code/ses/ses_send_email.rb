# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Sends a message to an SES email address.]
# snippet-keyword:[Amazon Simple Email Service]
# snippet-keyword:[send_email method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[ses]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-ses'  # v2: require 'aws-sdk'
require 'rspec'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleNotificationService
    response = ses.verify_email_identity(
        EmailAddress = 'EMAIL_ADDRESS'
    )
    print(response)

    class SendEmail
      # @option options [required, String] :sender
      # @option options [required, String] :recipient
      # @option options [required, String] :configset
      # @option options [required, String] :awsregion
      # @option options [required, String] :subject
      # @option options [required, String] :htmlbody
      # @option options [required, String] :textbody
      # @option options [required, String] :encoding
      # @option options [required, String] :resource
      # @option options [required, String] :contents
      # @api private
      def initialize(options = {})
        @sender = options[:sender]
        @recipient = options[:recipient]
        @configset = options[:configset]
        @awsregion = options[:awsregion]
        @subject = options[:subject]
        @htmlbody = options[:htmlbody]
        @textbody = options[:textbody]
        @encoding = options[:encoding]
        @resource = options[:resource]
        @contents = options[:contents]
      end

      # @return [String] The sender's email address, which must be verified with SES, ie) 'peccy@amazon.com'
      attr_reader :sender

      # @return [String] Recipient's email address, which must also be verified with SES before you use it if your
      # account is still in the sandbox, ie) 'peccysfriend@amazon.com'
      attr_reader :recipient

      # @return [String] Specify a configuration set. If you do not want to use a configuration set, comment the
      # following variable and the configuration_set_name: configsetname argument below
      attr_reader :configset

      # @return [String] Replace us-west-2 with the AWS Region you're using for Amazon SES, ie) us-west-2
      attr_reader :awsregion

      # @return [String] Subject line for the email, ie) 'Re: Onboarding information'
      attr_reader :subject

      # @return [String] HTML body of the email,
      # ie) <h1>Amazon SES test (AWS SDK for Ruby)</h1>'\
      #   '<p>This email was sent with <a href="https://aws.amazon.com/ses/">'\
      #   'Amazon SES</a> using the <a href="https://aws.amazon.com/sdk-for-ruby/">'\
      #   'AWS SDK for Ruby</a>.'
      attr_reader :htmlbody

      # @return [String] Email body for recipients with non-HTML email clients, ie) 'This email was sent with Amazon
      # SES using the AWS SDK for Ruby.'
      attr_reader :textbody

      # @return [String] Specify the text encoding scheme, ie) 'UTF-8'
      attr_reader :encoding

      # @return [String] Create a new SES resource and specify a region
      # ie) ses = Aws::SES::Client.new(region: 'us-west-2')
      attr_reader :resource

      # @return [String] Provide the contents of the email
      attr_ reader :contents
      # ie) ses.send_email(
      #     destination: {
      #       to_addresses: [
      #         recipient
      #       ]
      #     },
      #     message: {
      #       body: {
      #         html: {
      #           charset: encoding,
      #           data: htmlbody
      #         },
      #         text: {
      #           charset: encoding,
      #           data: textbody
      #         }
      #       },
      #       subject: {
      #         charset: encoding,
      #         data: subject
      #       }
      #     },
      #     source: sender,
    end
  end
end

puts 'Email sent to ' + recipient

# If something goes wrong, display an error message.
rescue Aws::SES::Errors::ServiceError => error
  puts "Email not sent. Error message: #{error}"
end
