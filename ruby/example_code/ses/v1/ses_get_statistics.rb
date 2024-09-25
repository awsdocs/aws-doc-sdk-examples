# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-sesv2' # Ensure you're using the aws-sdk-sesv2 gem for AWS SDK for Ruby V3.

# This class encapsulates operations for fetching Amazon Simple Email Service (Amazon SES) sending statistics.
class SESStatisticsFetcher
  # Initialize the SES client.
  #
  # Utilizes the default region from the environment or AWS configuration.
  def initialize
    @ses = Aws::SESV2::Client.new
  end

  # Fetches and prints send statistics from the Amazon SES account.
  def fetch_and_print_statistics
    response = @ses.get_send_statistics
    data_points = response.send_data_points

    puts "Got #{data_points.count} data point(s):"
    data_points.each do |dp|
      print_data_point(dp)
    end
  rescue Aws::SESV2::Errors::ServiceError => e
    # Handles SES V2 service errors gracefully.
    puts "Error fetching SES statistics: #{e.message}"
  end

  private

  # Prints details of a single data point.
  #
  # @param dp [Aws::SESV2::Types::SendDataPoint] The data point to print.
  def print_data_point(dp)
    puts <<~DATA_POINT
      Timestamp:  #{dp.timestamp}
      Attempts:   #{dp.delivery_attempts}
      Bounces:    #{dp.bounces}
      Complaints: #{dp.complaints}
      Rejects:    #{dp.rejects}
    DATA_POINT
  end

# If something goes wrong, display an error message.
rescue Aws::SES::Errors::ServiceError => e
  puts "Error: #{e}"
end

# Example usage:
stats_fetcher = SESStatisticsFetcher.new
stats_fetcher.fetch_and_print_statistics
