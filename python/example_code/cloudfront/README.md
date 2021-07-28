# Amazon CloudFront distributions example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon CloudFront to
list and update distributions.

*CloudFront speeds up distribution of static and dynamic web content by
delivering it through a worldwide network of edge locations that provide low 
latency and high performance.* 

## Code examples

* [Listing CloudFront distributions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudfront/list_distributions.py) 
(`list_distributions`)
* [Updating a CloudFront distribution](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudfront/update_distribution.py) 
(`get_distribution_config`, `update_distribution`)

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
- Python 3.8 or later
- Boto3 1.14.47 or later

### Command

List distributions at a command prompt with the following command.

```
python list_distributions.py
``` 

Update the comment field in a distribution by running the following command
and answering the prompts.

```
python update_distribution.py
```

## Additional information

- [Boto3 Amazon CloudFront service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/cloudfront.html)
- [Amazon CloudFront documentation](https://docs.aws.amazon.com/cloudfront)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
