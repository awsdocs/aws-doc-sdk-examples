# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-ses'  # v3: require 'aws-sdk'
# Create a new SES resource in the us-west-2 region.
# Replace us-west-2 with the AWS Region you're using for Amazon SES.
ses = Aws::SES::Client.new(region: 'us-west-2')

module Aws
  module SimpleEmailService
    class GetStatistics
      def initialize(*args)
        @client = opts[:getstatistics_client || Aws::GetStatistics::Client.new]
      end

      def get_statistics()
        begin
          resp = @simpleemailservice.get_statistics

          puts
          puts "Found #{resp.email.dps} email(s)."
          puts

          resp.dps.each do |dp|
            show_dp(dp)
        end
       end

        private

        def show_statistic(statistic)
          puts 'Metrics:'
          if !statistic.metrics.nil?
            statistic.metrics.each do |m|
              puts "Timestamp:  #{dp.timestamp}" #=> Time
              puts "Attempts:   #{dp.delivery_attempts}" #=> Integer
              puts "Bounces:    #{dp.bounces}" #=> Integer
              puts "Complaints: #{dp.complaints}" #=> Integer
              puts "Rejects:    #{dp.rejects}"  #-> Integer
              puts
            end
          end

          puts
        end
      end

    rescue ServiceError => e
      puts "Service Error 'The request has failed due to a temporary failure of the server': #{e} (#{e.class})"
    end