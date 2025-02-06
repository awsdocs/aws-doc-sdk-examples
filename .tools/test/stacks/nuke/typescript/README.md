# aws-nuke for Weathertop

[aws-nuke](https://github.com/ekristen/aws-nuke) is an open-source tool that deletes non-default resources in a provided AWS account. It's implemented here in this directory using Cloud Development Kit (CDK) code that deploys the [official aws-nuke image](https://github.com/ekristen/aws-nuke/pkgs/container/aws-nuke) to an AWS Lambda function.

## ⚠ Important

This is a very destructive tool! It should not be deployed without fully understanding the impact it will have on your AWS accounts.
Please use caution and configure this tool to delete unused resources only in your lower test/sandbox environment accounts.

## Overview

This CDK stack is defined in [account_nuker.ts](account_nuker.ts). It includes:

- A Docker-based Lambda function with ARM64 architecture and 1GB memory
- An IAM role with administrative permissions for the Lambda's nuking function
- An EventBridge rule that triggers the function every Sunday at midnight

More specifically, this Lambda function is built from a [Dockerfile](Dockerfile) and runs with a 15-minute timeout. It contains a [nuke_generic_config.yml](nuke_generic_config.yaml) config and executes a [run.sh](run.sh) when invoked every Sunday at midnight UTC.

![infrastructure-overview](nuke-overview.png)

## Prerequisites

1. **Non-Prod AWS Account Alias**: A non-prod account alias must exist in target account. Set the alias by running `python create_account_alias.py weathertop-test` or following [these instructions](https://docs.aws.amazon.com/IAM/latest/UserGuide/account-alias-create.html).

## Setup and Installation

For multi-account deployments, please use the [deploy.py](../../../DEPLOYMENT.md#option-1-using-deploypy) script.

For single-account deployment, you can just run:

```sh
cdk bootstrap && cdk deploy
```

Note a successful stack creation, e.g.:

```bash
NukeStack: success: Published 956fbd116734e79edb987e767fe7f45d0b97e2123456789109103f80ba4c1:123456789101-us-east-1
Stack undefined
NukeStack: deploying... [1/1]
NukeStack: creating CloudFormation changeset...

 ✅  NukeStack

✨  Deployment time: 27.93s

Stack ARN:
arn:aws:cloudformation:us-east-1:123456789101:stack/NukeStack/9835cc20-d358-11ef-bccf-123407dc82dd

✨  Total time: 33.24s
```
