# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-ses'  # v3: require 'aws-sdk'
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



