# AWS Audit Manager code examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager to do the following:

* Create an assessment report that consists of evidence from one specific date.
* Create custom controls and a custom framework based on the managed rules in an AWS Config conformance pack.
* Create a custom framework with all standard controls using AWS Security Hub as their data source.

*AWS Audit Manager helps you continually audit your AWS usage to simplify how you manage 
risk and compliance with regulations and industry standards.*


## Code examples

### Scenario examples

* [Create an assessment report that consists of evidence from one specific date](create_assessment_report.py)
* [Create custom controls and a custom framework based on the managed rules in an AWS Config conformance pack](framework_from_conformance_pack.py) 
* [Create a custom framework with all standard controls using AWS Security Hub as their data source](security_hub_custom_framework.py) 

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
- You should be familiar with Audit Manager terminology and functionality. For a general 
overview, see [What is AWS Audit Manager?](https://docs.aws.amazon.com/audit-manager/latest/userguide/what-is.html) and [AWS Audit Manager concepts and terminology](https://docs.aws.amazon.com/audit-manager/latest/userguide/concepts.html).
- You must have completed all the prerequisites that are described in 
[Setting up AWS Audit Manager](https://docs.aws.amazon.com/audit-manager/latest/userguide/setting-up.html). 
- Your IAM identity must have the appropriate permissions to create resources in Audit 
Manager. Two suggested policies that grant these permissions are 
[Example 2: Allow full administrator access](https://docs.aws.amazon.com/audit-manager/latest/userguide/security_iam_id-based-policy-examples.html#example-1) 
and [Example 3: Allow management access](https://docs.aws.amazon.com/audit-manager/latest/userguide/security_iam_id-based-policy-examples.html#example-2).
- To create custom controls that use AWS Security Hub as a data source, you must first 
[enable AWS Security Hub](https://docs.aws.amazon.com/securityhub/latest/userguide/securityhub-settingup.html), then [enable all security standards](https://docs.aws.amazon.com/securityhub/latest/userguide/securityhub-standards-enable-disable.html#securityhub-standard-enable-console). 
- To create custom controls and frameworks from an AWS Config conformance pack, you 
must first [enable AWS Config](https://docs.aws.amazon.com/config/latest/developerguide/gs-console.html), 
then [deploy the conformance pack](https://docs.aws.amazon.com/config/latest/developerguide/conformance-pack-console.html) 
that you want to use.
- Python 3.8 or later
- Boto3 1.19.32 or later
- PyTest 6.0.2 or later (to run unit tests)

### Command

Each example can be at a command prompt with a command similar to the following.

```
python create_assessment_report.py
```

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/auditmanager 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 AWS Audit Manager service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/auditmanager.html)
- [AWS Audit Manager documentation](https://docs.aws.amazon.com/audit-manager/latest/userguide/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
