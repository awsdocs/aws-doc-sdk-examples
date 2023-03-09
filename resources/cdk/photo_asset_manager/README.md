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

At this point you can now synthesize the CloudFormation template for this code.
{PAM_LANG} is one of "Java" or "Python", with more coming soon!

```
cdk synth {PAM_NAME}-{PAM_LANG}-PAM
```

If everything looks good, go ahead and deploy! This step will actually make
changes to your AWS cloud environment.

```
$ cdk bootstrap
$ cdk deploy {STACK_NAME} # {PAM_NAME}-{PAM_LANG}-PAM from above
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

## Useful commands

- `cdk ls` list the stacks based on your name & progrmming language
- `cdk deploy` deploy this stack to your default AWS account/region
- `cdk diff` compare deployed stack with current state
- `cdk synth` emits the synthesized CloudFormation template
