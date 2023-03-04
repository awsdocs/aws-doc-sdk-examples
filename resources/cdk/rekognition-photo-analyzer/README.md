
# PAM CDK

This project will create the following in your AWS cloud environment:
* IAM group
* IAM user (added to the IAM group)
* S3 bucket
* DynamoDB table
* Lambda function that performs image classification via AWS Rekognition when new images are uploaded to the S3 bucket
* Roles and policies allowing appropriate access to these resources

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

Now you are ready to create a virtualenv from within this directory:

```
$ python3 -m venv .venv
```

Activate your virtualenv:

```
$ source .venv/bin/activate
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

## Testing
Upload an image fie to the S3 bucket that was created by CloudFormation.
The image will be automatically classified.
Results can be found in DynamoDB, S3 bucket "results" folder, and CloudWatch logs for the Lambda function
  
To clean up, issue this command (this will NOT remove the DynamoDB
table, CloudWatch logs, or S3 bucket -- you will need to do those manually) :

```
$ cdk destroy
```
