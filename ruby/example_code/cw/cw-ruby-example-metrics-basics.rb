# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# Demonstrates how to:
# 1. Send custom metrics to Amazon CloudWatch.
# 2. Get information about custom metrics.

require 'aws-sdk-cloudwatch'  # v2: require 'aws-sdk'

# Uncomment for Windows.
# Aws.use_bundled_cert!

cw = Aws::CloudWatch::Client.new(region: 'us-east-1')

# Send custom metrics to Amazon CloudWatch.
# In this example, add metrics to the custom namespace "SITE/TRAFFIC":
# For the custom dimension named "SiteName", for the value named "example.com", add
# "UniqueVisitors" of 5885 and "UniqueVisits" of 8628.
# For the custom dimension named "PageURL", for the value named "my-page.html", add
# "PageViews" of 18057.
cw.put_metric_data({
  namespace: "SITE/TRAFFIC", 
  metric_data: [
    {
      metric_name: "UniqueVisitors", 
      dimensions: [
        {
          name: "SiteName", 
          value: "example.com" 
        }
      ],
      value: 5885.0,
      unit: "Count"
    },
    {
      metric_name: "UniqueVisits",
      dimensions: [
        {
          name: "SiteName",
          value: "example.com"
        }
      ],
      value: 8628.0,
      unit: "Count"  
    },
    {
      metric_name: "PageViews",
      dimensions: [
        {
          name: "PageURL",
          value: "my-page.html"
        }
      ],
      value: 18057.0,
      unit: "Count"
    }
  ]
})

# Get information about custom metrics.
list_metrics_output = cw.list_metrics({
  namespace: "SITE/TRAFFIC"
})

list_metrics_output.metrics.each do |metric|
  puts metric.metric_name
  metric.dimensions.each do |dimension|
    puts "#{dimension.name} = #{dimension.value}"
  end
  puts "\n"
end


