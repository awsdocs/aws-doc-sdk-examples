# AWS IoT Greengrass code snippets

## Purpose

Shows how to use the AWS IoT Greengrass Core SDK to create AWS Lambda functions
that publish MQTT messages, implement connectors, and retrieve secrets.

These code examples are primarily code snippets that are used in the 
[AWS Iot Greengrass developer guide](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html)
are not intended to be used out of context.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7
- GreengrassSdk 1.6.0 or later
- PyTest 5.3.5 or later (to run unit tests)

## Cautions

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

These examples are code snippets that are intended to be used in conjunction with the 
[AWS Iot Greengrass developer guide](https://docs.aws.amazon.com/greengrass/latest/developerguide/what-is-gg.html).
See the developer guide for setup and usage details.

### Example structure

The example contains the following files.

**connector_\*.py**

Shows how to use different kinds of AWS-provided Greengrass connectors. 
For more information, see 
[AWS-provided Greengrass connectors](https://docs.aws.amazon.com/greengrass/latest/developerguide/connectors-list.html).

**iot_data_client_\*.py**

Shows how to use various clients to publish MQTT messages.

**local_resource_access_volume.py**

Shows how to access local volume resources when running in a Greengrass core instance. 

**secret_resource_access_\*.py**

Shows how to retrieve secrets. For more information, see
[Deploy secrets to the AWS IoT Greengrass core](https://docs.aws.amazon.com/greengrass/latest/developerguide/secrets.html).

## Running the tests

To run the tests, run the following in your 
[GitHub root]/python/example_code/greengrass folder.

```    
python -m pytest
```

## Additional information

- [Greengrass Core Python SDK](https://github.com/aws/aws-greengrass-core-sdk-python)
- [AWS IoT Greengrass Documentation](https://docs.aws.amazon.com/greengrass/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
