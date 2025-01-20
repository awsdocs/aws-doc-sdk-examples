
# aws-nuke for Weathertop

AWS Nuke is an open source tool maintained by [ekristen](https://github.com/ekristen/aws-nuke) that searches for deleteable resources in a provided AWS account and deletes those not considered "Default" or "AWS-Managed".

This tool is implemented using the Cloud Development Kit (CDK) scripts in this directory. These scripts deploy the official AWS Nuke image to a Lambda function, which runs on a schedule.

## ⚠ Important
This is a very destructive tool! It should not be deployed without fully understanding the impact it will have on your AWS accounts.
Please use caution and configure this tool to delete unused resources only in your lower test/sandbox environment accounts.

## Overview

The code in this repository deploys the following architecture to a peovided "Plugin" AWS account:

![infrastructure-overview](nuke-overview.png)

## Feature Outline

1. **Scheduled Trigger**: Amazon EventBridge invokes AWS Step Functions daily.
2. **Regional Scalability**: Runs AWS CodeBuild projects per region.
4. **Custom Config**: Pulls resource filters and region targets in [nuke_generic_config.yaml](nuke_generic_config.yaml).

## Prerequisites  
1. **Non-Prod AWS Account Alias**: A non-prod account alias must exist in target account. Set the alias by running `python create_account_alias.py demo` or following [these instructions](https://docs.aws.amazon.com/IAM/latest/UserGuide/account-alias-create.html).

## Setup and Installation
* Deploy the stack using the below command. You can run it in any desired region.
```sh
cdk bootstrap && cdk deploy
```

Note a successful stack creation, e.g.: 

```bash
 ✅  NukeStack

✨  Deployment time: 172.66s

Outputs:
NukeStack.NukeS3BucketValue = nuke-account-stack-config-616362312345-us-east-1-c043b470
Stack ARN:
arn:aws:cloudformation:us-east-1:123456788985:stack/NukeStack/cfhdkiott-acec-11ef-ba2e-4555c1356d07
```
