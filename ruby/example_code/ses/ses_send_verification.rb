# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-ses'  # v3: require 'aws-sdk'
# Replace recipient@example.com with a "To" address.
recipient = "recipient@example.com"
# Create a new SES resource in the us-west-2 region.
# Replace us-west-2 with the AWS Region you're using for Amazon SES.
ses = Aws::SES::Client.new(region: 'us-west-2')

module Aws
  module SimpleEmailService
    class SendVerification
      def initialize(*args)
        @client = opts[:sendverification_client || Aws::SendVerification::Client.new]
      end

      def send_email()
        begin
          resp = @simpleemailservice.send_verification

          puts
          puts "Found #{resp.verfication.count} email(s)."
          puts


          resp.verfication.each do |verification|
            show_verfication(verification)
          end
        end

        private

        def show_verfication(verification)
          puts "Email: #{verification_email}"
          puts 'All Identities:'


          if !email.allidentities.nil?
            email.allidentities.each do |a|
              puts "Email Address Identities: #{a.allidentities_emailaddressidentities}"
              puts "Verification Status:  #{a.allidentities.verificationstatus}"
              puts
            end
          end

          puts
        end
      end

      rescue ServiceError => e
        puts "Service Error 'The request has failed due to a temporary failure of the server': #{e} (#{e.class})"
      end


