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
# snippet-sourcedescription:[Enables a resource to publish to an SNS topic.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[Resource.topic method]
# snippet-keyword:[Topic.set_attributes method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sns]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]

      require 'aws-sdk-sns'  # v3: require 'aws-sdk'
      require 'rspec'
      sns = Aws::SNS::Resource.new(region: 'us-west-2')

      module Aws
        module SimpleNotificationService
          class EnableResource
            policy  = '{
      "Version":"2008-10-17",
      "Id":"__default_policy_ID",
      "Statement":[{
      "Sid":"__default_statement_ID",
      "Effect":"Allow",
      "Principal":{
      "AWS":"*"
      },
      "Action":["SNS:Publish"],
      "Resource":"' + my-topic-arn + '",
      "Condition":{
      "ArnEquals":{
      "AWS:SourceArn":"' + my-resource-arn + '"}
     }
  }]
}'
          end
module Aws
  module SimpleNotificationService
    class EnableResource
      def initialize(*args)
      @client = opts[:enableresource_client || Aws::EnableResource::Client.new]
    end

    def enable_resource()
      resp = @simplenotificationservice.enable_resource
      puts
      puts "Found #{resp.resource.count} resource(s)."
      puts


    resp.resources.each do |resource|
      show_resources(resources)
    end
    end
    end

    private

    def show_resources(resource)
    puts "Attribute Name: #{resource.attributename}"
    puts "Attribute Value: #{resource.attributevalue}"
    puts 'ARN:'

    if !resource.topic.nil?
      resource.each do |r|
        puts "  ARN:  #{r.topic_arn}"
      end
    end

    puts
  end
end




