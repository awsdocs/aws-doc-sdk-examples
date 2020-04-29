# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Publishes a message to an SNS topic.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[Resource.topic method]
# snippet-keyword:[Topic.publish method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sns]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
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
    class SendMessage
      # @option options [optional, String] :subject
      # @option options [optional, String] :timetolive
      # @option options [required, String] :messagebody
      # @option options [required, String] :type
      # @option options [required, String] :name
      # @option options [required, String] :value
      # @option options [required, String] :addanotherattribute
      # @api private
      def initialize(options = {})
        @subject = options[:subject]
        @timetolive = options[:timetolive]
        @messagebody = options[:messagebody]
        @type = options[:type]
        @name = options[:name]
        @value = options[:value]
        @addanotherattribute = options[:addanotherattribute]
      end

      # @return [String] Optional Subject field in the Message details section. 100 printable ASCII characters
      # are permissible.
      attr_reader :subject

      # @return [String] Optional Time to Live field applies only to mobile application endpoints. The number of seconds
      # that the push notification service has to deliver the message to the endpoint.
      attr_reader :timetolive

      # @return [String] Select the message structure as either an Identical Payload for all delivery protocols, with
      # the same payload sent to the endpoints subscribed to the topic, regardless of their delivery protocol and the
      # message body sent to the endpoint.
      # Alternatively, choose a Custom Payload for each delivery protocol, with different payloads going to endpoints
      # subscribed to the topic, based on their delivery protocol.
      attr_reader :messagebody

      # Amazon SNS supports delivery of message attributes which let you provide structured metadata items
      # (such as timestamps, geospatial data, signatures, and identifiers) for a message. Attributes are sent
      # along with the message body but are optional and separate from it. The receiver of the message can use this
      # information to decide how to handle the message without having to first process the message body.
      # Each message can have up to 10 attributes.
      # @return [String] Attribute type e.g. "String.Array"
      attr_reader :type

      # @return [String] Attribute name e.g. "customer_interests"
      attr_reader :name

      # @return [String] Value for attribute e.g. "["soccer", "rugby", "hockey"]"
      attr_reader :value

      # @return [String] Option to add additional attributes
      attr_reader :addanotherattribute

      end
    end
    end

# Validate the subject if it meets the requirements for using strictly ASCII characters with a maximum allowable string
# length of 100 characters
def validate_topic(subject)
  if subject =~ str.ascii_only? && str.length <= 100
  end

# The topic is created and the topic's Name, ARN (optional), Display name, and Topic owner's AWS account ID
# are displayed
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
