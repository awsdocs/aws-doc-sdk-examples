# AWS Entity Resolution resources

## Overview

Creates the following AWS resources for Amazon DynamoDB item tracker sample applications: 
 
* An AWS IAM role that has permissions required to run this Scenario.
* An AWS Glue table that provides the input data for the entity resolution matching workflow.
* An Amazon S3 input bucket that is used by the AWS Glue table.
* An Amazon S3 output bucket that is used by the matching workflow to store results of the matching workflow.

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Deploy resources

You can use the AWS Cloud Development Kit (AWS CDK) or the AWS Command Line Interface
(AWS CLI) to deploy and destroy the resources for this example.

### Deploy with the AWS CDK

To deploy with the AWS CDK, you must install [Java JDK](https://www.oracle.com/ca-en/java/technologies/downloads/) and the 
[AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/getting_started.html).

This example was built and tested with AWS CDK 2.135.0.

Deploy AWS resources by running the following at a command prompt in this README's folder:

```
cdk deploy
```

The stack takes a few minutes to deploy. When it completes, it prints output like 
the following:

```
Outputs:
EntityResolutionCdkStack.EntityResolutionArn = arn:aws:iam::XXXXX:role/EntityResolutionCdkStack-EntityResolutionRoleB51A51-TSzkkBfrkbfm
EntityResolutionCdkStack.GlueDataBucketName = glue-XXXXX3196d
EntityResolutionCdkStack.GlueTableArn = arn:aws:glue:us-east-1:XXXXX:table/entity_resolution_db/entity_resolution
```

Note - Copy these AWS resources into your AWS Entity Resolution scenario. These values are required for the program to successfully run. 

## Destroy resources

### Destroy with the AWS CDK

You can use the AWS CDK to destroy the resources by running the following:

```
cdk destroy
```

## Additional resources

* [AWS CDK v2 Developer Guide](https://docs.aws.amazon.com/cdk/v2/guide/home.html)
* [AWS CLI User Guide for Version 2](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
* [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
