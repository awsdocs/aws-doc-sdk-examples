# AWS Entity Resolution scenario resources

## Overview

This AWS CDK Java application generates a AWS CloudFormation template.
The CloudFormation template creates  the following resources for the AWS Entity Resolution scenario application: 
 
* An AWS IAM role that has permissions required to run this Scenario.
* An AWS Glue table that provides the input data for the entity resolution matching workflow.
* An Amazon S3 input bucket that is used by the AWS Glue table.
* An Amazon S3 output bucket that is used by the matching workflow to store results of the matching workflow.

## ⚠️ Important

* When the template is used by the AWS Entity Resolution scenario application,
  the resources it creates might result in charges to your account.
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Create a CloudFormation template

To output a template that creates the CloudFormation stack, execute the following CDK CLI command from the 
`resources/cdk/entityresolution_resources` working directory:
```
cdk synth --yaml >  ../../../javav2/example_code/entityresolution/src/main/resources/template.yaml
```
The result of running this command puts the `template.yaml` file into the directory where
the scenario application can use it.


## Outputs generated
When the template is used and the stack is created by the AWS Entity Resolution scenario application,
the following outputs are generated and used in the application:
```
EntityResolutionCdkStack.EntityResolutionArn = arn:aws:iam::XXXXX:role/EntityResolutionCdkStack-EntityResolutionRoleB51A51-TSzkkBfrkbfm
EntityResolutionCdkStack.GlueDataBucketName = glue-XXXXX3196d
EntityResolutionCdkStack.GlueTableArn = arn:aws:glue:us-east-1:XXXXX:table/entity_resolution_db/entity_resolution
```

## How stack-created resources are destroyed
AWS Entity Resolution scenario application destroys the resources created by the stack before it completes.


## Additional information

* [AWS CDK v2 Developer Guide](https://docs.aws.amazon.com/cdk/v2/guide/home.html)
* [AWS CLI User Guide for Version 2](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
* [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
