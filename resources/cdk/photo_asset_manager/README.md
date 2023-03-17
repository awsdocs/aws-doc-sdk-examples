# PAM CDK

This project will create the following in your AWS cloud environment:

- IAM group
- IAM user (added to the IAM group)
- S3 buckets
- DynamoDB tables
- Lambda function that performs image classification via AWS Rekognition when new images are uploaded to the S3 bucket
- API Gateway routing for lambda functions
- Roles and policies allowing appropriate access to these resources

---

Requirements:

- git
- npm (node.js)
- docker
- AWS access key & secret for AWS user with permissions to create resources listed above
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
| PAM_LANG  | Programming language for the lambdas in this deployment. |

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

1. PamFrontEndInfraStack ({PAM_NAME}-FE-Infra-PAM) - Cloudfront distribution and S3 bucket host for static website files.
2. PamStack ({PAM_NAME}-{PAM_LANG}-PAM) - The backend resources (Rekognition, Lambda, S3, Cognito, ApiGateway).
3. PamFrontEndAssetStack ({PAM_NAME}-FE-Assets-PAM) - The static website assets.

At this point you can now synthesize the CloudFormation template for this code. Run `cdk ls` to see
a list of available stacks to synth/deploy.

```
cdk synth {PAM_NAME}-FE-Infra-PAM
```

If everything looks good, go ahead and deploy! This step will actually make
changes to your AWS cloud environment.

```
$ cdk bootstrap # Only required once for the lifetime of your account.
$ cdk deploy {STACK_NAME} # Deploy each of the three preceding stacks in order.
```

## Testing

After deploying the PamFrontEndInfraStack, your terminal should have a Cloudfront distribution URL.
Navigate to that URL to see the deployed app.

To clean up, run this command:

```
$ cdk destroy {STACK_NAME}
```

## Useful commands

- `cdk ls` list the stacks based on your name & programming language
- `cdk deploy` deploy this stack to your default AWS account/region
- `cdk diff` compare deployed stack with current state
- `cdk synth` emits the synthesized CloudFormation template
