# Amazon S3 code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Simple Storage Service (Amazon S3).

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon S3](hello.js#L6) (`ListBuckets`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/basic.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CopyObject](actions/copy-object.js#L4)
- [CreateBucket](actions/create-bucket.js#L4)
- [DeleteBucket](actions/delete-bucket.js#L4)
- [DeleteBucketPolicy](actions/delete-bucket-policy.js#L4)
- [DeleteBucketWebsite](actions/delete-bucket-website.js#L4)
- [DeleteObject](actions/delete-object.js#L4)
- [DeleteObjects](actions/delete-objects.js#L4)
- [GetBucketAcl](actions/get-bucket-acl.js#L4)
- [GetBucketCors](actions/get-bucket-cors.js#L4)
- [GetBucketPolicy](actions/get-bucket-policy.js#L4)
- [GetBucketWebsite](actions/get-bucket-website.js#L4)
- [GetObject](actions/get-object.js#L4)
- [GetObjectLegalHold](actions/get-object-legal-hold.js)
- [GetObjectLockConfiguration](actions/get-object-lock-configuration.js)
- [GetObjectRetention](actions/get-object-retention.js)
- [ListBuckets](actions/list-buckets.js#L6)
- [ListObjectsV2](actions/list-objects.js#L4)
- [PutBucketAcl](actions/put-bucket-acl.js#L4)
- [PutBucketCors](actions/put-bucket-cors.js#L4)
- [PutBucketPolicy](actions/put-bucket-policy.js#L4)
- [PutBucketWebsite](actions/put-bucket-website.js#L4)
- [PutObject](actions/put-object.js#L4)
- [PutObjectLegalHold](actions/put-object-legal-hold.js)
- [PutObjectLockConfiguration](actions/put-object-lock-configuration.js)
- [PutObjectRetention](actions/put-object-retention.js)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a presigned URL](scenarios/presigned-url-upload.js)
- [Create a web page that lists Amazon S3 objects](../web/s3/list-objects/src/App.tsx)
- [Delete all objects in a bucket](scenarios/delete-all-objects.js)
- [Lock Amazon S3 objects](scenarios/object-locking/index.js)
- [Make conditional requests](scenarios/conditional-requests/index.js)
- [Upload or download large files](scenarios/multipart-upload.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->

Some scenarios are web applications that must be bundled. These scenarios are in the `scenarios/web` folder.
Follow these steps to run a web scenario. Some scenarios might require extra steps to get them working.

1. Navigate to the web scenario you want to run.
1. Run `npm i`.
1. Run `npm run dev`.
<!--custom.instructions.end-->

#### Hello Amazon S3

This example shows you how to get started using Amazon S3.

```bash
node ./hello.js
```

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

#### Create a web page that lists Amazon S3 objects

This example shows you how to list Amazon S3 objects in a web page.


<!--custom.scenario_prereqs.s3_Scenario_ListObjectsWeb.start-->
<!--custom.scenario_prereqs.s3_Scenario_ListObjectsWeb.end-->


<!--custom.scenarios.s3_Scenario_ListObjectsWeb.start-->
<!--custom.scenarios.s3_Scenario_ListObjectsWeb.end-->

#### Delete all objects in a bucket

This example shows you how to delete all of the objects in an Amazon S3 bucket.


<!--custom.scenario_prereqs.s3_Scenario_DeleteAllObjects.start-->
<!--custom.scenario_prereqs.s3_Scenario_DeleteAllObjects.end-->


<!--custom.scenarios.s3_Scenario_DeleteAllObjects.start-->
<!--custom.scenarios.s3_Scenario_DeleteAllObjects.end-->

#### Lock Amazon S3 objects

This example shows you how to work with S3 object lock features.


<!--custom.scenario_prereqs.s3_Scenario_ObjectLock.start-->
<!--custom.scenario_prereqs.s3_Scenario_ObjectLock.end-->


<!--custom.scenarios.s3_Scenario_ObjectLock.start-->
<!--custom.scenarios.s3_Scenario_ObjectLock.end-->

#### Make conditional requests

This example shows you how to add preconditions to Amazon S3 requests.


<!--custom.scenario_prereqs.s3_Scenario_ConditionalRequests.start-->
<!--custom.scenario_prereqs.s3_Scenario_ConditionalRequests.end-->


<!--custom.scenarios.s3_Scenario_ConditionalRequests.start-->
<!--custom.scenarios.s3_Scenario_ConditionalRequests.end-->

#### Upload or download large files

This example shows you how to upload or download large files to and from Amazon S3.


<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenario_prereqs.s3_Scenario_UsingLargeFiles.end-->


<!--custom.scenarios.s3_Scenario_UsingLargeFiles.start-->
<!--custom.scenarios.s3_Scenario_UsingLargeFiles.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)
- [Amazon S3 API Reference](https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html)
- [SDK for JavaScript (v3) Amazon S3 reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/s3)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
