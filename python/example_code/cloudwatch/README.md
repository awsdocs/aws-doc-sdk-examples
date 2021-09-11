# Amazon CloudWatch custom metrics and alarms example

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon CloudWatch to manage custom
metrics and alarms.

*CloudWatch provides a reliable, scalable, and flexible monitoring solution that you 
can start using within minutes.*

## Code examples

### Usage examples

* [Managing alarms and metrics](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`usage_demo`)

### API examples

* [Creating an alarm that watches a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`put_metric_alarm`)
* [Deleting alarms that are watching a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`delete_alarms`)
* [Enabling and disabling actions on an alarm](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`enable_alarm_actions`, `disable_alarm_actions`)
* [Getting statistics for a metric within a time span](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`get_metric_statistics`)
* [Getting the alarms that are watching a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`describe_alarms_for_metric`)
* [Listing metrics](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`list_metrics`)
* [Sending a set of data to a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`put_metric_data_data_set`)
* [Sending a single data value to a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/cloudwatch/cloudwatch_basics.py)
(`put_metric_data`)

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
- PyTest 5.3.5 or later (to run unit tests)

### Command

Run this example at a command prompt with the following command.

```
python cloudwatch_basics.py
``` 

### Example structure

The example contains the following file.

**cloudwatch_basics.py**

Shows how to use Amazon CloudWatch APIs to create and manage custom metrics and 
alarms. The `CloudWatchWrapper` class encapsulates CloudWatch functions. The 
`usage_demo` script uses the CloudWatchWrapper class to:
1. Create a custom metric and alarm
1. Send data to the metric
1. Get statistics for the metric
1. Check the state of the alarm  

## Running the tests

The unit tests in this module use the botocore Stubber. The Stubber captures requests 
before they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following command in your [GitHub root]/python/example_code/cloudwatch 
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
