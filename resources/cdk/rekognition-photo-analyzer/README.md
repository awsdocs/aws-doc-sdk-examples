
<!--BEGIN STABILITY BANNER-->
---

![Stability: Stable](https://img.shields.io/badge/stability-Stable-success.svg?style=for-the-badge)

> **This is a stable example. It should successfully build out of the box**
>
> This example is built on Construct Libraries marked "Stable" and does not have any infrastructure prerequisites to build.
---
<!--END STABILITY BANNER-->

This project is intended to be sample code only. Not for use in production.

This project will create the following in your AWS cloud environment:
* IAM group
* IAM user (added to the IAM group)
* S3 bucket
* DynamoDB table
* Lambda function that performs image classification via AWS Rekognition when new images are uploaded to the S3 bucket
* Roles and policies allowing appropriate access to these resources

Rekognition labels will be written to CloudWatch logs, a results folder in the S3 bucket, as well as the DynamoDB table.  
  
This project was inspired by the AWS CDK workshop (https://cdkworkshop.com) and I highly recommend you go through that as well.
  
---

Requirements:
* git
* npm (node.js)
* python 3.x
* AWS access key & secret for AWS user with permissions to create resources listed above
  
---

First, you will need to install the AWS CDK:

```
$ sudo npm install -g aws-cdk
```

You can check the toolkit version with this command:

```
$ cdk --version
```

Next, you will want to create a project directory:

```
$ mkdir ~/cdk-samples
```

Now you are ready to create a virtualenv:

```
$ cd ~/cdk-samples
$ python3 -m venv .venv
```

Activate your virtualenv:

```
$ source .venv/bin/activate
```

Now you're ready to clone this repo and change to this sample directory:

```
$ git clone https://github.com/aws-samples/aws-cdk-examples.git
$ cd python/s3-trigger-lambda-rekognition-dynamodb
```

Install the required dependencies:

```
$ pip install -r requirements.txt
```

At this point you can now synthesize the CloudFormation template for this code.

```
$ cdk synth
```

If everything looks good, go ahead and deploy!  This step will actually make
changes to your AWS cloud environment.  

```
$ cdk bootstrap
$ cdk deploy
```

## Testing the app
Upload an image fie to the S3 bucket that was created by CloudFormation.
The image will be automatically classified.
Results can be found in DynamoDB, S3 bucket "results" folder, and CloudWatch logs for the Lambda function
  
To clean up, issue this command (this will NOT remove the DynamoDB
table, CloudWatch logs, or S3 bucket -- you will need to do those manually) :

```
$ cdk destroy
```

To exit the virtualenv python environment:

```
$ deactivate
```

# Useful commands

 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

---
This code has been tested and verified to run with AWS CDK 1.100.0 (build d996c6d)
