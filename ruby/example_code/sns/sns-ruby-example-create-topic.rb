# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Creates an SNS topic.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[create_topic method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sns]
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

require 'aws-sdk-sns'  # v2: require 'aws-sdk'
require 'rspec'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  module SimpleNotificationService
    class CreateTopic
      # @option options [required, String] :topicname
      # @api private
      def initialize(options = {})
        @topicname = options[:topicname]
      end

      # @return [String] The topic name of the topic you created earlier
      # e.g. "arn:aws:sns:us-west-2:123456789012:MyTopic".
      attr_reader :topicname
    end
  end
end

# Validate the topic name if it meets the SNS requirements for a topic name, including strictly alphanumeric characters,
# hyphens(-), and underscores(_)
  def validate_topic(topicname)?
      return false unless (topicname =~ ^ [A - Za - z0 - 9)_ -] + $)
    end

# The topic is created and the topic's Name, ARN (optional), Display name, and Topic owner's AWS account ID are
# displayed
  topic = sns.describe_topics({topicname: [args[0]]})

# The topic's Name, ARN (optional), Display name, and Topic owner's AWS account ID are displayed in the Details section
# of the MyTopic page
  if topic.exists?
    puts format("%12s | %s", "MyTopic", "Details")
    puts "-" * 30
    puts format ("%12s | %2i", "Topic Name:", #{topic.topicname}")
    puts format ("%12s | %2i", "ARN (optional):", #{topic.arn}")
    puts format ("%12s | %2i", "Display name:", #{topic.displayname}")
    puts format ("%12s | %2i", "AWS account ID:", #{topic.accountid}")
  end


