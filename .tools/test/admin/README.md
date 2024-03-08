![Stability: Stable](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

# Scheduled Event Producer stack

The code in this directory deploys an AWS Cloud Development Kit (AWS CDK) stack that produces events.

This stack can be deployed in isolation; however, it serves a purpose in this repository's [test automation architecture](../README.md).

Specifically, it deploys a scheduled Amazon EventBridge rule that publishes a message to an Amazon Simple Notification Service (Amazon SNS) topic. Many "consumer" Amazon Simple Queue Service (Amazon SQS) topics are subscribed to the SNS topic.


![weathertop-comp-2.png](..%2Farchitecture_diagrams%2Fpng%2Fweathertop-comp-2.png)

---
## System requirements
* npm (node.js)
* python 3.x
* AWS access key and secret for AWS user with permissions to create the preceding resources
* Successfully written [system parameters](#storing-system-parameters)
  
### Updating configuration data

Before you get started, update [config/resources.yaml](../config/resources.yaml) and [config/targets.yaml](../config/targets.yaml) to include logical names representing test targets and their corresponding AWS Account ID and enabled status. 

---
## AWS CDK setup and deployment

First, install the AWS CDK:

```
sudo npm install -g aws-cdk
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

If everything looks good, go ahead and deploy.  This step will actually make
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

 * `cdk ls`          List all stacks in the app
 * `cdk synth`       Emit the synthesized CloudFormation template
 * `cdk deploy`      Deploy this stack to your default AWS account/Region
 * `cdk diff`        Dompare deployed stack with current state
 * `cdk docs`        Open CDK documentation

---
This code has been tested and verified to run with AWS CDK 2.70.0 (build c13a0f1).
