# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Deletes a CloudTrail trail.]
# snippet-keyword:[AWS CloudTrail]
# snippet-keyword:[delete_trail method]
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

# Deletes a trail from AWS CloudTrail.
class DeleteTrailExample
  # Deletes the specified trail from AWS CloudTrail.
  # Prerequisites:
  #  An existing trail with the name specified in trail_name.
  # Inputs: 
  #  cloudtrail_client: an instance of an AWS CloudTrail API client.
  #  trail_name: the name of the trail to delete.
  # Outputs:
  #   Nothing.
  def delete_trail(cloudtrail_client, trail_name)
    cloudtrail_client.delete_trail({ name: trail_name })
  end
end

# Tests the functionality in the preceding class by using RSpec.
RSpec.describe DeleteTrailExample do
  trail_name = 'my-trail'
  region_id = 'us-east-1'
  cloudtrail_client = ''
  mock_cloudtrail_client = ''

  before(:all) do
    cloudtrail_client = Aws::CloudTrail::Client.new(region: region_id)
    mock_cloudtrail_client = Aws::CloudTrail::Client.new(stub_responses: true)    
  end

  it 'actually deletes an AWS CloudTrail trail' do
    expect(DeleteTrailExample.new.delete_trail(cloudtrail_client, trail_name)).to be
  end

  it 'mocks deleting an AWS CloudTrail trail' do
    expect(DeleteTrailExample.new.delete_trail(mock_cloudtrail_client, trail_name)).to be
  end

end
