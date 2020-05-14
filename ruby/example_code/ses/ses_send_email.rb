# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-ses'  # v3: require 'aws-sdk
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
