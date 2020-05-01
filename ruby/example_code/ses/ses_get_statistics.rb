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
# snippet-sourcedescription:[Gets your SES send statistics.]
# snippet-keyword:[Amazon Simple Email Service]
# snippet-keyword:[get_send_statistics method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[ses]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]

require 'aws-sdk-ses'  # v2: require 'aws-sdk'

# Create a new SES resource in the us-west-2 region.
# Replace us-west-2 with the AWS Region you're using for Amazon SES.
ses = Aws::SES::Client.new(region: 'us-west-2')

module Aws
  module SimpleEmailService
    class GetStatistics
      def initialize
        @dp = input.to_i
      end

#valid data points fall somewhere in the range of 0-100%
      def valid_dp?
        dp.between?(0.0, 1.0)
      end
      end

begin
# Get send statistics so we don't ruin our reputation
  resp = ses.get_send_statistics({})
  dps = resp.send_data_points
  puts "Got #{dps.count} data point(s):"

# Iterate over the list of data points and display the data for timestamps, attempts, bounces, complaints, and rejects.
# Each contains statistics for a 15-minute period of sending activity.
  dps.each do |dp|
    puts "Timestamp:  #{dp.timestamp}" #=> Time of the data point
    puts "Attempts:   #{dp.delivery_attempts}" #=> Integer, number of emails that have been sent
    puts "Bounces:    #{dp.bounces}" #=> Integer, number of emails that have bounced
    puts "Complaints: #{dp.complaints}" #=> Integer, number of unwanted emails that were rejected by recipients
    puts "Rejects:    #{dp.rejects}"  #=> Integer, number of emails rejected by Amazon SES
  end

# If something goes wrong, display an error message
rescue Aws::SES::Errors::ServiceError => error
  puts "Error: #{error}"
  end
