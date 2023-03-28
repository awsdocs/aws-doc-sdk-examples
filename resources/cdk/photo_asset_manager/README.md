# AWS CDK for PAM

This project will create the following in your AWS Cloud environment:

- AWS Identity and Access Management (IAM) group
- IAM user (added to the IAM group)
- Amazon Simple Storage Service (Amazon S3) buckets
- Amazon DynamoDB tables
- AWS Lambda function that performs image classification with Amazon Rekognition when new images are uploaded to the S3 bucket
- Amazon API Gateway routing for Lambda functions
- Roles and policies allowing appropriate access to these resources

---

Requirements:

- git
- npm (node.js)
- docker
- AWS access key & secret access key for IAM user with permissions to create resources listed above
  - https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-config

---

First, you will need to install the dependencies in this project:

```
npm install
```

You can check the toolkit version with this command:

```
$ cdk --version
```

Export these variables:

| Name      | Usage                                                    |
| --------- | -------------------------------------------------------- |
| PAM_NAME  | Short one-word name to identify this stack.              |
| PAM_EMAIL | Email address for the pre-verified default user account. |
| PAM_LANG  | Programming language for the Lambdas in this deployment. |

Current languages: Java, Python

_bash/zsh_

```
export PAM_NAME=
export PAM_EMAIL=
export PAM_LANG=
```

_Windows cmd_

```
set PAM_NAME=
set PAM_EMAIL=
set PAM_LANG=
```

_Windows Powershell_

```
$Env:PAM_NAME =
$Env:PAM_EMAIL =
$Env:PAM_LANG =
```

There are three stacks that need to be deployed:

1. PamFrontEndInfraStack ({PAM_NAME}-FE-Infra-PAM) - Amazon Cloudfront distribution and S3 bucket host for static website files.
2. PamStack ({PAM_NAME}-{PAM_LANG}-PAM) - The backend resources (Amazon Rekognition, Lambda, Amazon S3, Amazon Cognito, API Gateway).
3. PamFrontEndAssetStack ({PAM_NAME}-FE-Assets-PAM) - The static website assets.

At this point you can now synthesize the AWS CloudFormation template for this code. Run `cdk ls` to see
a list of available stacks to synth/deploy.

```
cdk synth {PAM_NAME}-FE-Infra-PAM
```

If everything looks good, go ahead and deploy. This step will make
changes to your AWS Cloud environment.

```
$ cdk bootstrap # Only required once for the lifetime of your account.
$ cdk deploy {STACK_NAME} # Deploy each of the three preceding stacks in order.
```

## Test

After deploying the PamFrontEndInfraStack, your terminal should have a Cloudfront distribution URL.
Navigate to that URL to see the deployed app.

To clean up, run this command:

```
$ cdk destroy {STACK_NAME}
```

## Useful commands

- `cdk ls` List the stacks based on your name and programming language
- `cdk deploy` Deploy this stack to your default AWS account/region
- `cdk diff` Compare the deployed stack with the current state
- `cdk synth` Emits the synthesized CloudFormation template
