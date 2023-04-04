![Stability: Stable](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

# Batch Fargate Consumer Stack with Cross-Account Trigger

This project will create a CDK stack for running integration tests within a personal account based on a SNS fanout topic triggering within a centralized account.

Test results will be written to CloudWatch logs.  
    
---

Requirements:
* git
* npm (node.js)
* python 3.7
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

Next, you will want to create a project directory:

```
mkdir ~/cdk-samples
```

Now you are ready to create a virtualenv:

```
cd ~/cdk-samples
python3 -m venv .venv
```

Activate your virtualenv:

```
source .venv/bin/activate
```

Now you're ready to clone this repo and change to this sample directory:

```
git clone https://github.com/aws-samples/aws-cdk-examples.git
cd python/rekognition-lambda-s3-trigger
```

Install the required dependencies:

```
pip install -r requirements.txt
```
Before going any further, save your language name as an environment variable called `LANGUAGE_NAME`.

If your language is Java, you would use:
```
export LANGUAGE_NAME=javav2
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

## Testing the app
Upload an image fie to the S3 bucket that was created by CloudFormation.
The image will be automatically classified.
Results can be found in DynamoDB, S3 bucket "results" folder, and CloudWatch logs for the Lambda function
  
To clean up, issue this command (this will NOT remove the DynamoDB
table, CloudWatch logs, or S3 bucket -- you will need to do those manually) :

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
