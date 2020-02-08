# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Lists your CloudTrail events.]
# snippet-keyword:[AWS CloudTrail]
# snippet-keyword:[lookup_events method]
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

# Lists selected information about existing events in CloudTrail.
class ListEventsExample
  # Initialize an instance of ListEventsExample, creating a client for
  # AWS CloudTrail (unless already provided during initialization).
  #
  # (The following comments express documentation about this function in YARD 
  # format by using @ symbols.)
  #
  # @param [Hash] opts ({}) A hash of an API client for CloudTrail.
  # @option [Aws::CloudTrail::Client] :cloudtrail_client
  #  (Aws::CloudTrail::Client)
  def initialize(opts = {})
    # This CloudTrail API client is used to list the CloudTrail resources.
    @cloudtrail = opts[:cloudtrail_client] || Aws::CloudTrail::Client.new
  end

  # Lists selected information about existing events in AWS CloudTrail.
  def list_events()
    resp = @cloudtrail.lookup_events

    puts
    puts "Found #{resp.events.count} event(s)."
    puts

    # Uncomment the following line to display available information.
    # puts resp

    resp.events.each do |event|
      show_event(event)
    end
  end 
  
  private 

  # @param event [Aws::CloudTrail::Types::Event]
  #  The event to list information about.
  def show_event(event)

    # Uncomment the following line to display available information.
    # puts event

    puts "Event name:   #{event.event_name}"
    puts "Event ID:     #{event.event_id}"
    puts "Event time:   #{event.event_time}"
    puts "User name:    #{event.username}"
    puts 'Resources:'

    if !event.resources.nil? 
      event.resources.each do |r|
        puts "  Name:       #{r.resource_name}"
        puts "  Type:       #{r.resource_type}"
        puts
      end
    end

    puts
  end
end

# Tests the functionality in the preceding class by using RSpec.
RSpec.describe ListEventsExample do
  # Create a stubbed API client to use for testing.
  let(:cloudtrail_client) { Aws::CloudTrail::Client.new(stub_responses: true) }
  
  # Create a ListEventsExample object with our API client.
  let(:list_events_example) do
    ListEventsExample.new(
      cloudtrail_client: cloudtrail_client
    )
  end
  
  describe '#list_events' do
    it 'lists information about available events' do
      # Stub the response data for our CloudTrail API client.
      cloudtrail_client.stub_responses(
        :lookup_events, :events => [ 
          { :event_id => "b94d139b-088e-4e76-9005-8d7d735320f3",
            :event_name => "UpdateInstanceInformation-1" },
          { :event_id => "b94d139b-088e-4e76-9005-8d7d735320f4",
            :event_name =>"UpdateInstanceInformation-2" }
        ]  
      )
      list_events_example.list_events()
    end
  end 
end
