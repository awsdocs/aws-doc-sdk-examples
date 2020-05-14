# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
sns = Aws::SES::Resource.new(region: 'us-west-2')

RSpec.describe ShowStatistics do
  let(:getstatistics_client) { Aws::GetStatistics::Client.new(stub_responses: true) }
  let(:getstatistics) do
    ShowStatistics.new(
        getstatistics_client: getstatistics_client
    )
  end

  describe '#showstatistics' do
    it 'get SES statistics to avoid damaging your reputation when emails are bounced or rejected' do
      getstatistics_client.stub_responses(
          :getstatistics, :statistics => [
          { :statistics_timestamp => "2020-03-13T22:22:00Z",
            :statistics_attempts => "4",
            :statistics_bounces => "0",
            :statistics_complaints => "0",
            :statistics_rejects => "0"   },
          { :statistics_timestamp => "2019-06-12T22:24:00Z",
            :statistics_attempts => "2",
            :statistics_bounces => "0",
            :statistics_complaints => "0",
            :statistics_rejects => "0"}
      ]
      )
    getstatistic.getstatistics()
  end




