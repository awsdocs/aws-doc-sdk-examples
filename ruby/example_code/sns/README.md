# AWS SDK for Ruby code examples for Amazon SNS

## Purpose

These examples demonstrate how to perform several Amazon SNS operations. Learn how to
create subscriptions, create topics, enable resources, send messages, show subscriptions,
and show topics.

## Prerequisites 

- You must have an AWS account, and have your default credentials and AWS Regions configured
as described in [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html).
in the AWS SDK for Ruby Developer Guide. 
- RubyGems 3.1.2 or later.
- AWS SDK for Ruby. For download and installation instructions, see [Installing the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html).
- Ruby 2.6 or later. After you install Ruby, add the path to Ruby in your environment
 variables so that you can run Ruby from any command prompt. 
- RSpec 4.0 or later (to run unit tests).
- Create an IAM administrator user 
   - Follow the instructions in the IAM User Guide to create your first IAM administrator user and group in the 
   *AWS Identity and Access Management User Guide*
    For more information, see [Creating Your First IAM Admin User and Group ](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html).
- To avoid using your IAM administrator user for Amazon SNS operations, it is a best 
practice to create an IAM user for each person who needs administrative access to 
Amazon SNS. To work with Amazon SNS, you need the ``AmazonSNSFullAccess`` policy and AWS 
credentials that are associated with your IAM user. These credentials are comprised of
an access key ID and a secret access key. For more information, see 
[AWS Identity and Access Management console ](https://signin.aws.amazon.com/signin?redirect_uri=https%3A%2F%2Fconsole.aws.amazon.com%2Fiam%2F%3Fstate%3DhashArgs%2523%26isauthcode%3Dtrue&client_id=arn%3Aaws%3Aiam%3A%3A015428540659%3Auser%2Fiam&forceMobileApp=0).
##  Running the code 

### sns-ruby-example-create-subscription/sns-ruby-example-create-subscription.rb

This example shows how to use the AWS SDK for Ruby to subscribe to an endpoint
by sending the endpoint a confirmation message.

####Response elements

`SubscriptionArn`
- *ARN* - The ARN of the subscription if it is confirmed, or the string 
"pending confirmation" if the subscription requires confirmation. However, if the 
API request parameter `ReturnSubscriptionArn` is true, the value is always the 
subscription ARN, even if the subscription requires confirmation.
- *Type* - String


### sns-ruby-example-create-topic/sns-ruby-example-create-topic.rb

This example explains how to use the AWS SDK for Ruby to create a topic to which 
notifications can be published. Users can create at most 100,000 topics.

####Response elements
`TopicArn`
- The Amazon Resource Name (ARN) assigned to the created topic.
- *Type* String



### sns-ruby-example-enable-resource/sns-ruby-example-enable-resource.rb

This example shows how to use the AWS SDK for Ruby to enable the resource with the 
ARN to publish to the desired topic ARN.

####Response Elements
Constructor details: `#initialize(options = {}) ⇒ Resource`
- Returns a new instance of Resource.
- *Parameters* options ({}) (defaults to: {})
- *Options Hash* :client (Client)


### sns-ruby-example-send-message/sns-ruby-example-send-message.rb

This example describes how to use the AWS SDK for Ruby to deliver the
message to each endpoint that is subscribed to the topic, such as sending a text
message (SMS message) directly to a phone number, or a message to a mobile platform 
endpoint (when you specify the TargetArn).

####Response elements
`MessageId`
- Unique identifier assigned to the published message.
- Length constraint - Maximum 100 characters
- *Type* - String


### sns-ruby-example-show-subscriptions/sns-ruby-example-show-subscriptions.rb

This example describes how to use the AWS SDK for Ruby to return all
subscriptions and their associated properties.

####Response elements
`SubscriptionArn`
- The ARN of the subscription whose properties you want to get.
- *Type* String
- Required: Yes

### sns-ruby-example-show-topics/sns-ruby-example-show-topics.rb

This example describes how to use the AWS SDK for Ruby to return all
topics and their associated properties. Topic properties returned might differ based 
on the authorization of the user.

####Response elements
`TopicArn`
- The ARN of the topic whose properties you want to get.
- *Type* String
- Required: Yes


## Sample request 
The following example Query request subscribes an Amazon SQS queue to an Amazon SNS topic. The queries
for create topic, enable resource, send message, show subscriptions, and show topics 
follow similar formatting.

    https://sns.us-west-2.amazonaws.com/?Action=Subscribe
    &TopicArn=arn%3Aaws%3Asns%3Aus-west-2%3A123456789012%3AMyTopic
    &Endpoint=arn%3Aaws%3Asqs%3Aus-west-2%3A123456789012%3AMyQueue
    &Protocol=sqs
    &Version=2010-03-31
    &AUTHPARAMS

## Sample response

    <SubscribeResponse xmlns="https://sns.amazonaws.com/doc/2010-03-31/">
        <SubscribeResult>
            <SubscriptionArn>arn:aws:sns:us-west-2:123456789012:MyTopic:6b0e71bd-7e97-4d97-80ce-4a0994e55286</SubscriptionArn>
        </SubscribeResult>
        <ResponseMetadata>
            <RequestId>c4407779-24a4-56fa-982c-3d927f93a775</RequestId>
        </ResponseMetadata>
    </SubscribeResponse>


## Testing the Amazon SNS Ruby files
You can test the Amazon SNS ruby code examples by running RSpec 4.0, which is located in the ses/spec directory.

You can execute the RSpec tests from a Ruby IDE, such as RubyMine, or from the command
line using a command line console. There is a green dot for every passing spec, and one
red `F` for every failing test. For example, the following message informs you that a 
test has passed.

    Finished in 0.00217 seconds (files took 0.13631 seconds to load)
    3 examples, 0 failures
    $ echo $?
    0
    $ 
    
### Command line 
To execute RSpec tests from the command line, you can save the example files into a 
directory on your local machine, `cd` into the directory in which it is saved, and 
type `rspec` followed by the name of the spec file:

     $ rspec ses_get_statistics_spec.rb
     .
        
### Additional information
- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  "AWS Regional Table" on the AWS website.
- Running this code might result in charges to your AWS account.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0