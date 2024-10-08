![Stability: Stable](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

# Plugin Stack

The code in this directory deploys an AWS Cloud Development Kit (AWS CDK) stack that runs Docker images in AWS Batch.

This stack can be deployed in isolation; however, it serves a purpose in this repository's [test automation architecture](../README.md).

Specifically, it consumes images from a Simple Notification Service (SNS) topic, which trigger an AWS Lambda function that starts AWS Batch jobs.

![weathertop-comp-2.png](../../docs/architecture_diagrams/png/weathertop-comp-2.png)

---

## System requirements

- NodeJS 18+ (check with `node -v`)
- python 3.11 (check with `python --version`)
- AWS access key and secret for AWS user with permissions to create the preceding resources
- Successfully written [system parameters](#storing-system-parameters)

### Updating configuration data

Before you get started, update [config/resources.yaml](../config/resources.yaml) and [config/targets.yaml](../config/targets.yaml) to include logical names representing test targets and their corresponding AWS Account ID and enabled status.

If you need to disable a plugin stack, update [config/targets.yaml](../config/targets.yaml) accordingly and perform an [admin stack deployment](../../DEPLOYMENT.md#usage).

---

## AWS CDK setup and deployment

First, install the AWS CDK:

```
npm install -g aws-cdk
```

You can check the toolkit version with this command:

```
cdk --version
```

Now you are ready to create a virtualenv:

```
python3 -m venv .venv
```

Activate your virtualenv:

```
source .venv/bin/activate
```

Install the required dependencies:

```
pip install -r requirements.txt
```

At this point you can now synthesize the AWS CloudFormation template for this code.

```
cdk synth
```

If everything looks good, go ahead and deploy. This step will actually make
changes to your AWS cloud environment.

```
cdk bootstrap
cdk deploy
```

To clean up, issue this command:

```
cdk destroy
```

To exit the virtualenv python environment:

```
deactivate
```

# Useful commands

- `cdk ls` List all stacks in the app
- `cdk synth` Emit the synthesized CloudFormation template
- `cdk deploy` Deploy this stack to your default AWS account/Region
- `cdk diff` Dompare deployed stack with current state
- `cdk docs` Open CDK documentation

---

This code has been tested and verified to run with AWS CDK 2.70.0 (build c13a0f1).

# Additional scripts

This directory contains a [cleanup.py](cleanup.py) script which deletes all jobs that are queued in AWS Batch.

It is used in rare instances where a queue misconfiguration causes a backup of yet-to-be-run jobs.
