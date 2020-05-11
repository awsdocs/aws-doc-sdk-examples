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
# snippet-sourcedescription:[Lists the verified email addresses for SES.]
# snippet-keyword:[Amazon Simple Email Service]
# snippet-keyword:[get_identity_verification_attributes method]
# snippet-keyword:[list_identities method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[ses]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]

require 'aws-sdk-ses'  # v3: require 'aws-sdk'
require 'rspec'
# Create client in us-west-2 region
client = Aws::SES::Client.new(region: 'us-west-2')
      module Aws
        module SimpleEmailService
          class ListEmails
            def initialize(*args)
              @client = opts[:listemails_client || Aws::ListEmails::Client.new]
            end

            def list_emails()
                resp = @simpleemailservice.list_emails
                puts
                puts "Found #{resp.email.list} email(s)."
                puts

                resp.emails.each do |email|
                  show_email(email)
                end
              end

              private
              def show_email(email)
                puts 'Identities:'
                if !email.identities.nil?
                  email.identities.each do |i|
                # Display email addresses that have been verified
                    if identities.success
                      render json: {message: ['Verification Status: Verified']}, status: 200
                    elsif identities.pending
                      render json: {message: ['Verification Status: Pending']}, status: 202
                    else identities.failed
                    render json: {message: ['The email address you entered does not appear to be valid.
                    Please try entering the email address again']}, status: 422
                    end
                  end
                end

                puts
              end
            end





