# S3 code examples for the AWS SDK for Ruby (v3)
## Overview
Shows how to create and manage Amazon Simple Storage Service (Amazon S3) buckets using the AWS SDK for Ruby (v3).
Amazon Simple Storage Service (Amazon S3) is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.
## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
## Code examples
### Single actions
Code excerpts that show you how to call individual service functions.

* [Add CORS rules to a bucket](bucket_cors.rb)

* [Add a policy to a bucket](bucket_policy.rb)

* [Copy an object from one bucket to another](object_copy.rb)

* [Copy an object from one bucket to another and add encryption](object_copy_encrypt.rb)

* [Create a bucket](bucket_create.rb)

* [Delete an empty bucket](scenario_getting_started.rb)

* [Delete CORS rules from a bucket](bucket_cors.rb)

* [Delete a policy from a bucket](bucket_policy.rb)

* [Delete multiple objects](scenario_getting_started.rb)

* [Determine the existence and content type of an object](object_exists.rb)

* [Get an object from a bucket](object_get.rb)

* [Get an object from a bucket and report its server-side encryption state](object_get_encryption.rb)

* [Get CORS rules for a bucket](bucket_cors.rb)

* [Get the policy for a bucket](bucket_policy.rb)

* [List buckets](bucket_list.rb)

* [List objects in a bucket](bucket_list_objects.rb)

* [Set the website configuration for a bucket](bucket_put_website.rb)

* [Set server-side encryption for a bucket](bucket_put_encryption.rb)

* [Upload an object to a bucket using Object.put](object_put.rb)

* [Upload an object to a bucket using Object.put and add server-side encryption](object_put_sse.rb)

* [Upload an object to a bucket using Object.upload_file](object_put.rb)


### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Create a presigned URL](object_presigned_url_upload.rb)

* [Getting started with buckets and objects](scenario_getting_started.rb)



## Run the examples

### Prerequisites

* An AWS account. To create an account, see [How do I create and activate a new AWS account](https://aws.amazon.com/premiumsupport/knowledge-center/create-and-activate-aws-account/) on the AWS Premium Support website.

* AWS credentials or an AWS Security Token Service (AWS STS) access token. For details, see [Configuring the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-config.html) in the *AWS SDK for Ruby Developer Guide*.

* To run the code examples, Ruby version 1.9 or later. For Ruby download and installation instructions, see [Download Ruby](https://www.ruby-lang.org/en/downloads/) on the Ruby Programming Language website.

* To test the code examples, RSpec 3.9 or later. For RSpec download and installation instructions, see the [rspec/rspec](https://github.com/rspec/rspec) repository in GitHub.

* The AWS SDK for Ruby. For AWS SDK for Ruby download and installation instructions, see [Install the AWS SDK for Ruby](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/setup-install.html) in the *AWS SDK for Ruby Developer Guide*.


### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your Command Line Interface (CLI). For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
## Contributing
Code examples thrive on community contribution!
To propose a new example, submit an [Enhancement Request](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fenhancement&template=enhancement.yaml&title=%5BEnhancement%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~2 min). To fix a bug, submit a [Bug Report](https://github.com/awsdocs/aws-doc-sdk-examples/issues/new?assignees=octocat&labels=type%2Fbug&template=bug.yaml&title=%5BBug%5D%3A+%3CDESCRIPTIVE+TITLE+HERE%3E) (~5 min).
### Testing
⚠️ Running these tests might result in charges to your AWS account.
This service is not currently tested.
## Additional resources
* [*Service Developer Guide*]()
* [*Service API Reference*]()
* [*SDK API reference guide*]()
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
