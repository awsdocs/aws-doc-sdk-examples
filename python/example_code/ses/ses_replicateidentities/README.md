# AWS SES Sample: Replicate Identities

This example allows migrating email address and domain identities from one region to another. It also allows you to setup SNS notifications and DKIM for the newly replicated identities. If using Route53, it also creates the required TXT (for domain verification) and CNAME records (for DKIM).

## Repository files
* `ses_replicateidentities.py` : Main program source file

## AWS infrastructure resources
* AWS SES

## Prerequisites
* Install Python 3.x.
* Install the AWS SDK for Python boto3. Instructions are at https://github.com/boto/boto3.
* Install the AWS CLI (Command Line Interface). Instructions are at https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html.
* Configure the AWS CLI. Instructions are at https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html.

## Instructions
To run the script:
`python3 ses_replicateidentities.py`
