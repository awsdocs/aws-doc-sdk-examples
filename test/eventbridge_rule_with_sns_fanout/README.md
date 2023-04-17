![Stability: Stable](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

# Scheduled Event Producer Stack

The code in this directory deploys a CDK stack that produces events.

This stack can be deployed in isolation; however, it serves a purpose in this repository's [test automation architecture](../README.md).

Specifically, it deploys a scheduled EventBridge rule that publishes a message to an SNS topic to which many "consumer" SQS topics are subscribed.


![weathertop-comp-2.png](..%2Farchitecture_diagrams%2Fpng%2Fweathertop-comp-2.png)

---
## System requirements:
* npm (node.js)
* python 3.x
* AWS access key & secret for AWS user with permissions to create resources listed above.
* Successfully written [system parameters](#storing-system-parameters)
  
### Storing system parameters

Before you get started, execute [store_system_parameters.py](store_system_params.py) as described in the code comments:

```
python3 store_system_params.py
```
---
## CDK setup & deployment

First, you will need to install the AWS CDK:

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

At this point you can now synthesize the CloudFormation template for this code.

```
cdk synth
```

If everything looks good, go ahead and deploy!  This step will actually make
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
