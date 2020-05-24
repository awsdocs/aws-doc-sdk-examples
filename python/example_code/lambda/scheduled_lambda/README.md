# AWS Lambda Sample: Scheduled Lambda

The Scheduled Lambda example demonstrates how an AWS Lambda function can be
invoked automatically based on a time schedule.

## Repository files

* `scheduled_lambda.py` : Main program source file
* `lambda_function.py` : AWS Lambda function that is invoked on a time schedule
* `lambda_util.py` : Utility functions to manage AWS Lambda functions

## AWS infrastructure resources

* AWS Lambda function
* AWS Identity and Access Management (IAM) role for the AWS Lambda function
* Amazon EventBridge/Amazon CloudWatch Events rule that invokes the Lambda function
based on a schedule

## Prerequisites

* Install Python 3.x.
* Install the AWS SDK for Python `boto3`. Instructions are at https://github.com/boto/boto3.
* Install the AWS CLI (Command Line Interface). Instructions are at 
  https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html.
* Configure the AWS CLI. Instructions are at 
  https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html.

## Instructions

To create and enable the Scheduled Lambda infrastructure:

    python scheduled_lambda.py

To toggle (enable/disable) the Scheduled Lambda event rule:

    python scheduled_lambda.py -t
    OR
    python scheduled_lambda.py --toggle

To delete the Scheduled Lambda infrastructure:

    python scheduled_lambda.py -d
    OR
    python scheduled_lambda.py --delete

By default, the defined EventBridge/CloudWatch Events rule is enabled and 
scheduled to invoke the Lambda function every minute.
 
To verify that the EventBridge/CloudWatch Events rule is invoking the Lambda
function, use the AWS console to check the CloudWatch log files. The Lambda
function writes event data to a log file each time it is executed. If the 
log files are not being created or updated, verify that the 
EventBridge/CloudWatch Events rule is enabled.
