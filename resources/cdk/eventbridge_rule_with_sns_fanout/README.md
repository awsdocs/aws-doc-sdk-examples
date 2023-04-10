![Stability: Stable](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

# Scheduled Event Producer Stack

This project will create a stack for producing events from EventBridge and publishing them to a centralized SNS topic.

---

Requirements:
* git
* npm (node.js)
* python 3.x
* AWS access key & secret for AWS user with permissions to create resources listed above
  
---

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

 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

---
This code has been tested and verified to run with AWS CDK 1.100.0 (build d996c6d)
