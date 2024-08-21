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


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](s3_getting_started_scenario.cpp)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AbortMultipartUpload](s3_object_integrity_workflow/s3_object_integrity_workflow.cpp#L1097)
- [CompleteMultipartUpload](s3_object_integrity_workflow/s3_object_integrity_workflow.cpp#L1129)
- [CopyObject](copy_object.cpp#L32)
- [CreateBucket](create_bucket.cpp#L30)
- [CreateMultipartUpload](s3_object_integrity_workflow/s3_object_integrity_workflow.cpp#L1006)
- [DeleteBucket](delete_bucket.cpp#L30)
- [DeleteBucketPolicy](delete_bucket_policy.cpp#L30)
- [DeleteBucketWebsite](delete_website_config.cpp#L30)
- [DeleteObject](delete_object.cpp#L31)
- [DeleteObjects](delete_objects.cpp#L33)
- [GetBucketAcl](get_bucket_acl.cpp#L36)
- [GetBucketPolicy](get_bucket_policy.cpp#L32)
- [GetBucketWebsite](get_website_config.cpp#L29)
- [GetObject](get_object.cpp#L33)
- [GetObjectAcl](get_put_object_acl.cpp#L43)
- [GetObjectAttributes](s3_object_integrity_workflow/s3_object_integrity_workflow.cpp#L707)
- [ListBuckets](list_buckets.cpp#L29)
- [ListObjectsV2](list_objects.cpp#L32)
- [PutBucketAcl](put_bucket_acl.cpp#L47)
- [PutBucketPolicy](put_bucket_policy.cpp#L37)
- [PutBucketWebsite](put_website_config.cpp#L33)
- [PutObject](put_object.cpp#L33)
- [PutObjectAcl](get_put_object_acl.cpp#L165)
- [UploadPart](s3_object_integrity_workflow/s3_object_integrity_workflow.cpp#L1040)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](presigned_get_object.cpp)
- [Create a serverless application to manage photos](cpp/example_code/cross-service/photo_asset_manager)
- [Work with Amazon S3 object integrity](s3_object_integrity_workflow/s3_object_integrity_workflow.cpp)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

An executable is built for each source file in this folder. These executables are located in the build folder and have
"run_" prepended to the source file name, minus the suffix. See the "main" function in the source file for further instructions.

For example, to run the action in the source file "my_action.cpp", execute the following command from within the build folder. The command
will display any required arguments.

```
./run_my_action
```

If the source file is in a different folder, instructions can be found in the README in that
folder.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.


#### Learn the basics

This example shows you how to do the following:

- Create a bucket and upload a file to it.
- Download an object from a bucket.
- Copy an object to a subfolder in a bucket.
- List the objects in a bucket.
- Delete the bucket objects and the bucket.

<!--custom.basic_prereqs.s3_Scenario_GettingStarted.start-->
<!--custom.basic_prereqs.s3_Scenario_GettingStarted.end-->


<!--custom.basics.s3_Scenario_GettingStarted.start-->
<!--custom.basics.s3_Scenario_GettingStarted.end-->


#### Create a presigned URL

This example shows you how to create a presigned URL for Amazon S3 and upload an object.


<!--custom.scenario_prereqs.s3_Scenario_PresignedUrl.start-->
<!--custom.scenario_prereqs.s3_Scenario_PresignedUrl.end-->


<!--custom.scenarios.s3_Scenario_PresignedUrl.start-->
<!--custom.scenarios.s3_Scenario_PresignedUrl.end-->

#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Work with Amazon S3 object integrity

This example shows you how to work with S3 object integrity features.


<!--custom.scenario_prereqs.s3_Scenario_ObjectIntegrity.start-->
<!--custom.scenario_prereqs.s3_Scenario_ObjectIntegrity.end-->


<!--custom.scenarios.s3_Scenario_ObjectIntegrity.start-->
<!--custom.scenarios.s3_Scenario_ObjectIntegrity.end-->

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