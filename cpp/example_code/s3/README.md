# Amazon S3 code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to work with Amazon Simple Storage Service (Amazon S3).

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



Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon S3](hello_s3/CMakeLists.txt#L4) (`ListBuckets`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Add a policy to a bucket](put_bucket_policy.cpp#L38) (`PutBucketPolicy`)
- [Copy an object from one bucket to another](copy_object.cpp#L34) (`CopyObject`)
- [Create a bucket](create_bucket.cpp#L35) (`CreateBucket`)
- [Delete a policy from a bucket](delete_bucket_policy.cpp#L32) (`DeleteBucketPolicy`)
- [Delete an empty bucket](delete_bucket.cpp#L32) (`DeleteBucket`)
- [Delete an object](delete_object.cpp#L33) (`DeleteObject`)
- [Delete multiple objects](delete_objects.cpp#L35) (`DeleteObjects`)
- [Delete the website configuration from a bucket](delete_website_config.cpp#L32) (`DeleteBucketWebsite`)
- [Get an object from a bucket](get_object.cpp#L34) (`GetObject`)
- [Get the ACL of a bucket](get_bucket_acl.cpp#L39) (`GetBucketAcl`)
- [Get the ACL of an object](get_put_object_acl.cpp#L46) (`GetObjectAcl`)
- [Get the policy for a bucket](get_bucket_policy.cpp#L33) (`GetBucketPolicy`)
- [Get the website configuration for a bucket](get_website_config.cpp#L32) (`GetBucketWebsite`)
- [List buckets](list_buckets.cpp#L31) (`ListBuckets`)
- [List objects in a bucket](list_objects.cpp#L33) (`ListObjectsV2`)
- [Set a new ACL for a bucket](put_bucket_acl.cpp#L49) (`PutBucketAcl`)
- [Set the ACL of an object](get_put_object_acl.cpp#L173) (`PutObjectAcl`)
- [Set the website configuration for a bucket](put_website_config.cpp#L36) (`PutBucketWebsite`)
- [Upload an object to a bucket](put_object.cpp#L35) (`PutObject`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with buckets and objects](s3_getting_started_scenario.cpp)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a serverless application to manage photos](../../example_code/cross-service/photo_asset_manager)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.



#### Get started with buckets and objects

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.scenarios.s3_Scenario_GettingStarted.start-->
<!--custom.scenarios.s3_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.



```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest
```


<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for C++ Amazon S3 reference](https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-s3/html/annotated.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0