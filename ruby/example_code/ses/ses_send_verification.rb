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
# snippet-sourcedescription:[Verifies an email address for SES.]
# snippet-keyword:[Amazon Simple Email Service]
# snippet-keyword:[verify_email_identity method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[ses]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]

require 'aws-sdk-ses'  # v3: require 'aws-sdk'
require 'rspec'
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

        rescue ServiceError => e
          puts "Service Error 'The request has failed due to a temporary failure of the server': #{e} (#{e.class})"
        end
    end




