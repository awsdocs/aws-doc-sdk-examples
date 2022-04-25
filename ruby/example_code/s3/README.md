# Amazon S3 examples using AWS SDK for Ruby

## Purpose

Shows how to use AWS SDK for Ruby to get started using bucket and
object operations in Amazon Simple Storage Service (Amazon S3).

*Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any
amount of data at any time, from anywhere on the web.*

## Code examples

### Scenarios

* [Create a presigned URL](object_presigned_url_upload.rb)
* [Getting started with buckets and objects](scenario_getting_started.rb)

### Actions

* [Add CORS rules to a bucket](bucket_cors.rb)
  (`PutBucketCors`)
* [Add a policy to a bucket](bucket_policy.rb)
  (`PutBucketPolicy`)
* [Copy an object from one bucket to another](object_copy.rb)
  (`CopyObject`)
* [Copy an object from one bucket to another and add encryption](object_copy_encrypt.rb)
  (`CopyObject`)
* [Create a bucket](bucket_create.rb)
  (`CreateBucket`)
* [Delete an empty bucket](scenario_getting_started.rb)
  (`DeleteBucket`)
* [Delete CORS rules from a bucket](bucket_cors.rb)
  (`DeleteBucketCors`)
* [Delete a policy from a bucket](bucket_policy.rb)
  (`DeleteBucketPolicy`)
* [Delete multiple objects](scenario_getting_started.rb)
  (`DeleteObjects`)
* [Determine the existence and content type of an object](object_exists.rb)
  (`HeadObject`)
* [Get an object from a bucket](object_get.rb)
  (`GetObject`)
* [Get an object from a bucket and report its server-side encryption state](object_get_encryption.rb)
  (`GetObject`)
* [Get CORS rules for a bucket](bucket_cors.rb)
  (`GetBucketCors`)
* [Get the policy for a bucket](bucket_policy.rb)
  (`GetBucketPolicy`)
* [List buckets](bucket_list.rb)
  (`ListBuckets`)
* [List objects in a bucket](bucket_list_objects.rb)
  (`ListObjects`)
* [Set the website configuration for a bucket](bucket_put_website.rb)
  (`PutBucketWebsite`)
* [Set server-side encryption for a bucket](bucket_put_encryption.rb)
  (`PutBucketEncryption`)
* [Upload an object to a bucket using Object.put](object_put.rb)
  (`PutObject`)
* [Upload an object to a bucket using Object.put and add server-side encryption](object_put_sse.rb)
  (`PutObject`)
* [Upload an object to a bucket using Object.upload_file](object_put.rb)
  (`PutObject`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the
  permissions required to perform a task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the *AWS Identity and Access Management
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are
  available only in specific Regions. For more information, see the
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Ruby 3.0.3 or later
- AWS SDK for Ruby 3.1.0 or later
- RSpec 3.10 or later (to run unit tests)

### Command

#### Scenarios

The getting started scenario can be run from the command prompt and interactively
asks for input as it runs. Start the scenario by running the following at a command 
prompt.

```
ruby scenario_getting_started.rb
```

#### Actions

Most of the action examples require that you replace specific values with your own values
before you run them. For example, to run the `object_upload_file.rb` example, you must 
modify the code to specify the name of a bucket that you own, the key for the 
uploaded object, and the path to a local file.

After you've specified the values, run the example at a command prompt. 

```
ruby object_upload_file.rb
```

## Running the tests

All tests use RSpec and can be found in the `spec` folder.

### Unit tests

The unit tests in this module use stubbed responses from AWS SDK for Ruby. When the
tests are run, requests are not sent to AWS, mocked responses are returned, and no
charges are incurred on your account.

Run unit tests at a command prompt by including the `~integ` tag.

```
rspec --tag ~integ
```

### Integration tests

The integration tests in this module do make requests to AWS, creating and destroying
resources in your account. Integration tests must be approached with caution and
may incur charges.

Run integration tests at a command prompt by including the `integ` tag.

```
rspec --tag integ
```

## Additional information

- [AWS SDK for Ruby Documentation](https://docs.aws.amazon.com/sdk-for-ruby)
- [Amazon S3 Documentation](https://docs.aws.amazon.com/s3)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
