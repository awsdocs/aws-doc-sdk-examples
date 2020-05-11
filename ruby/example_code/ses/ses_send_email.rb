/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
    CONDITIONS OF ANY KIND, either express or implied. See the License for the
    specific language governing permissions and limitations under the License.
*/

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

require 'aws-sdk-ses'  # v3: require 'aws-sdk'
require 'rspec'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleEmailService
    class SendEmail
      def initialize(*args)
         @client = opts[:sendemail_client || Aws::SendEmail::Client.new]
      end

      def send_email()
        begin
          resp = @simpleemailservice.send_email
          puts
          puts "Found #{resp.email.status} email(s)."
          puts


          resp.emails.each do |email|
            show_email(email)
          end
        end

        private
        def show_email(email)
          puts "Format: #{email.format}"
          puts "From: #{email.from}"
          puts "To: #{email.to}"
          puts "Subject: #{email.subject}"
          puts "Encoding: #{email.encdoing}"
          puts "Body: #{email.body}"
          puts 'Message:


          if !email.message.nil?
            email.message.each do |m|
              puts "  Message ID: #{m.message_messageid}"
              puts 'Email sent to ' #{resp.email.to}
            end
          end

        rescue ServiceError => e
          puts "Service Error 'The request has failed due to a temporary failure of the server': #{e} (#{e.class})"
        end

