# Amazon S3 code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with Amazon Simple Storage Service (Amazon S3).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon S3 is storage for the internet. You can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `ruby` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Add CORS rules to a bucket](bucket_cors.rb#L34) (`PutBucketCors`)
- [Add a policy to a bucket](bucket_policy.rb#L36) (`PutBucketPolicy`)
- [Copy an object from one bucket to another](object_copy.rb#L8) (`CopyObject`)
- [Create a bucket](bucket_create.rb#L8) (`CreateBucket`)
- [Delete CORS rules from a bucket](bucket_cors.rb#L60) (`DeleteBucketCors`)
- [Delete a policy from a bucket](bucket_policy.rb#L48) (`DeleteBucketPolicy`)
- [Delete an empty bucket](scenario_getting_started.rb#L125) (`DeleteBucket`)
- [Delete multiple objects](scenario_getting_started.rb#L124) (`DeleteObjects`)
- [Determine the existence and content type of an object](object_exists.rb#L8) (`HeadObject`)
- [Get CORS rules for a bucket](bucket_cors.rb#L22) (`GetBucketCors`)
- [Get an object from a bucket](object_get.rb#L8) (`GetObject`)
- [Get the policy for a bucket](bucket_policy.rb#L23) (`GetBucketPolicy`)
- [List buckets](bucket_list.rb#L8) (`ListBuckets`)
- [List objects in a bucket](bucket_list_objects.rb#L8) (`ListObjectsV2`)
- [Set the website configuration for a bucket](bucket_put_website.rb#L8) (`PutBucketWebsite`)
- [Upload an object to a bucket](object_upload_file.rb#L8) (`PutObject`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](object_presigned_url_upload.rb)
- [Get started with buckets and objects](scenario_getting_started.rb)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->



#### Create a presigned URL

This example shows you how to create a presigned URL for Amazon S3 and upload an object.


<!--custom.scenario_prereqs.s3_Scenario_PresignedUrl.start-->
<!--custom.scenario_prereqs.s3_Scenario_PresignedUrl.end-->

Start the example by running the following at a command prompt:

```
ruby object_presigned_url_upload.rb
```

<!--custom.scenarios.s3_Scenario_PresignedUrl.start-->
<!--custom.scenarios.s3_Scenario_PresignedUrl.end-->

#### Get started with buckets and objects

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.end-->

Start the example by running the following at a command prompt:

```
ruby scenario_getting_started.rb
```

<!--custom.scenarios.s3_Scenario_GettingStarted.start-->
<!--custom.scenarios.s3_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `ruby` folder.



<!--custom.tests.start-->

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for Ruby Amazon S3 reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/S3.html)

<!--custom.resources.start-->
* [More Ruby Amazon S3 code examples](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/ruby_s3_code_examples.html)
* [SDK for Ruby Developer Guide](https://aws.amazon.com/developer/language/ruby/)
* [SDK for Ruby Amazon S3 Module](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/S3.html)
* [Amazon S3 Developer Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
* [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0