# S3 code examples for the SDK for Ruby
## Overview
These examples show how to create and manage Amazon Simple Storage Service (Amazon S3) buckets using the SDK for Ruby.

S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Add CORS rules to a bucket](bucket_cors.rb) (`PutBucketCors`)

* [Add a policy to a bucket](bucket_policy.rb) (`PutBucketPolicy`)

* [Copy an object from one bucket to another](object_copy.rb) (`CopyObject`)

* [Copy an object from one bucket to another and add encryption](object_copy_encrypt.rb) (`CopyObject`)

* [Create a bucket](bucket_create.rb) (`CreateBucket`)

* [Delete an empty bucket](scenario_getting_started.rb) (`DeleteBucket`)

* [Delete CORS rules from a bucket](bucket_cors.rb) (`DeleteBucketCors`)

* [Delete a policy from a bucket](bucket_policy.rb) (`DeleteBucketPolicy`)

* [Delete multiple objects](scenario_getting_started.rb) (`DeleteObjects`)

* [Determine the existence and content type of an object](object_exists.rb) (`ListObjects`)

* [Get an object from a bucket](object_get.rb) (`GetObject`)

* [Get an object from a bucket and report its server-side encryption state](object_get_encryption.rb) (`GetObject`)

* [Get CORS rules for a bucket](bucket_cors.rb) (`GetBucketCors`)

* [Get the policy for a bucket](bucket_policy.rb) (`GetBucketPolicy`)

* [List buckets](bucket_list.rb) (`ListBuckets`)

* [List objects in a bucket](bucket_list_objects.rb) (`ListObjects`)

* [Set the website configuration for a bucket](bucket_put_website.rb) (`PutBucketWebsite`)

* [Set server-side encryption for a bucket](bucket_put_encryption.rb) (`PutBucketEncryption`)

* [Upload an object to a bucket using Object.put](object_put.rb) (`PutObject`)

* [Upload an object to a bucket using Object.put and add server-side encryption](object_put_sse.rb) (`PutObject`)

* [Upload an object to a bucket using Object.upload_file](object_put.rb) (`PutObject`)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Create a presigned URL](object_presigned_url_upload.rb)

* [Getting started with buckets and objects](scenario_getting_started.rb)





## Run the examples

### Prerequisites

See the [Ruby README.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/ruby/README.md) for prerequisites.

### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)

### Tests
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
