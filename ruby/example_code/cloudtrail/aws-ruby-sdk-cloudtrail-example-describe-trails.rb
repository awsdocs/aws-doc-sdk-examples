# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Lists your CloudTrail trails.]
# snippet-keyword:[AWS CloudTrail]
# snippet-keyword:[describe_trails method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[cloudtrail]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk-cloudtrail'

# Lists existing trails in AWS CloudTrail.
class ListTrailsExample
  # Lists information about existing trails in AWS CloudTrail.
  # Prerequisites:
  #  None.
  # Inputs:
  #  region_id: the ID of the AWS Region for the associated trails.
  # Outputs:
  #  Selected properties for any existing trails.
  def list_trails(region_id)
    client = Aws::CloudTrail::Client.new(region: region_id)
    resp = client.describe_trails({})
    
    puts
    puts "Found #{resp.trail_list.count} trail(s) in us-west-2:"
    puts
    
    resp.trail_list.each do |trail|
      puts 'Name:                  ' + trail.name
      puts 'Amazon S3 bucket name: ' + trail.s3_bucket_name
      puts
    end
  end
end

# Tests the functionality in the preceding class by using RSpec.
RSpec.describe ListTrailsExample do
  region_id = 'us-east-1'

  it 'lists trails in AWS CloudTrail' do
    expect(ListTrailsExample.new.list_trails(region_id)).to be
  end
end