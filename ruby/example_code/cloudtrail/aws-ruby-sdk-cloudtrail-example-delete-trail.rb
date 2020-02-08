# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[pccornel (AWS)]
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
  # Initialize an instance of DeleteTrailExample, creating a client for
  # AWS CloudTrail (unless already provided during initialization).
  #
  # (The following comments express documentation about this function in YARD 
  # format by using @ symbols.)
  #
  # @param [Hash] opts ({}) A hash of an API client for CloudTrail.
  # @option [Aws::CloudTrail::Client] :cloudtrail_client
  #  (Aws::CloudTrail::Client)
  def initialize(opts = {})
    # This CloudTrail API client is used to delete the CloudTrail resource.
    @cloudtrail = opts[:cloudtrail_client] || Aws::CloudTrail::Client.new
  end

  # Deletes the specified trail from AWS CloudTrail.
  # Prerequisites:
  #  An existing trail with the name specified in trail_name.
  # 
  # @param trail_name [String] The name of the trail to delete.
  def delete_trail(trail_name)
    @cloudtrail.delete_trail({ name: trail_name })
  end
end

# Tests the functionality in the preceding class by using RSpec.
RSpec.describe DeleteTrailExample do
  let(:trail_name)  { 'my-trail' }
  
  # Create a stubbed API client to use for testing.
  let(:cloudtrail_client) { Aws::CloudTrail::Client.new(stub_responses: true) }
  
  # Create a DeleteTrailExample object with our API client.
  let(:delete_trail_example) do
    DeleteTrailExample.new(
      cloudtrail_client: cloudtrail_client
    )
  end

  describe '#delete_trail' do
    it 'deletes an AWS CloudTrail trail' do
      expect_any_instance_of(Aws::CloudTrail::Client)
        .to receive(:delete_trail).with( name: trail_name )
      delete_trail_example.delete_trail(trail_name)
    end
  end  
end
