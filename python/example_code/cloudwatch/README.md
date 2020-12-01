# Amazon CloudWatch custom metrics and alarms example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon CloudWatch to
create a custom metric and alarm, send data for the metric, get statistics for the 
metric, and check the alarm state.

* Create a custom metric.
* Create an alarm that watches a metric.
* Send data to CloudWatch for a metric.
* Get statistics for a metric.
* Trigger an alarm and get its state.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.8 or later
- Boto3 1.14.47 or later
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

Run this example at a command prompt with the following command.

```
python cloudwatch_basics.py
``` 

### Example structure

The example contains the following file.

**cloudwatch_basics.py**

Shows how to use Amazon CloudWatch APIs to create and manage custom metrics and 
alarms. The `CloudWatchWrapper` class encapsulates CloudWatch functions. The 
`usage_demo` script uses the CloudWatchWrapper class to create a custom metric and 
alarm, send data to the metric, get statistics for the metric, and check the state of 
the alarm.  

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/cloudwatch 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon CloudWatch service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/cloudwatch.html)
- [Amazon CloudWatch documentation](https://docs.aws.amazon.com/cloudwatch/index.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
