# PAM CDK

This project will create the following in your AWS cloud environment:

- IAM group
- IAM user (added to the IAM group)
- S3 buckets
- DynamoDB tables
- Lambda function that performs image classification via AWS Rekognition when new images are uploaded to the S3 bucket
- API Gateway routing for lambda functions
- Roles and policies allowing appropriate access to these resources

### Prerequisites

- git
- npm (node.js)
- python 3.x
- docker
- AWS access key & secret for AWS user with permissions to create resources listed above
  - https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-config

---

First, you will need to install the AWS CDK:

```
$ npm install -g aws-cdk
```

You can check the toolkit version with this command:

```
$ cdk --version
```

Bootstrap the CDK:

```
$ cdk bootstrap
```

## Deploy steps

### Backend

Now you are ready to create a virtualenv. Run the following in the backend directory

```
$ python3 -m venv .venv
```

Activate your virtualenv:

```
$ source .venv/bin/activate
```

(or on Windows)

```
C:\> .venv\Scripts\activate.bat
```

Install the required dependencies:

```
$ pip install -r requirements.txt
```

Configure the stack for your account:

```
$ export PAM_NAME=$(whoami) # Or whatever name you want
$ export PAM_EMAIL={yourrmail@domain}
$ aws configure # Or otherwise set AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
```

(or on Windows)

```
$ set PAM_NAME={your name}
$ set PAM_EMAIL=youremail@domain
$ aws configure # Or otherwise set AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY
```

At this point you can now synthesize the CloudFormation template for this code.
{Lang} is one of "Java" or "Python", with more coming soon!

```
$ cdk synth ${PAM_NAME}-{Lang}-PAM
```

If everything looks good, go ahead and deploy! This step will actually make
changes to your AWS cloud environment.

```
$ cdk bootstrap
$ cdk deploy {STACK_NAME} # {PAM_NAME}-{Lang}-PAM from above
```

### Frontend

Run the following in the backend directory:

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

Configure the stack for your account:

```
$ export PAM_NAME=$(whoami) # Or whatever name you want
$ export BACKEND_STACK_ID= # The stack id output from [the backend steps](#backend)
$ export AWS_ACCOUNT= # configure your AWS account environment as necessary
```

Run synth.

```
$ cdk synth ${PAM_NAME}-FrontEnd-PAM
```

If everything looks good, go ahead and deploy! This step will actually make
changes to your AWS cloud environment.

```
$ cdk deploy
```

## Testing

Upload an image fie to the S3 bucket that was created by CloudFormation.
The image will be automatically classified.
Results can be found in DynamoDB, S3 bucket "results" folder, and CloudWatch logs for the Lambda function

To clean up, issue this command (this will NOT remove the DynamoDB
table, CloudWatch logs, or S3 bucket -- you will need to do those manually) :

```
$ cdk destroy {STACK_NAME}
```
