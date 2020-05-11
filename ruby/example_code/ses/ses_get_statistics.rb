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

