# AWS CDK for FSA

Deploy the Feedback Sentiment Analyzer (FSA) with the AWS Cloud Development Kit (AWS CDK).

## Prerequisites

- Install NodeJS 18+
- Install the AWS CDK
  - https://docs.aws.amazon.com/cdk/v2/guide/cli.html
- AWS access key and secret access key for an AWS Identity and Access Management (IAM) user with permissions to create the preceding resources
  - Configure the AWS Command Line Interface (AWS CLI) https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-config

## Set variables

Export these variables:

| Name      | Usage                                                    |
| --------- | -------------------------------------------------------- |
| FSA_NAME  | Short one-word name to identify this stack.              |
| FSA_EMAIL | Email address for the pre-verified default user account. |
| FSA_LANG  | Programming language for the Lambdas in this deployment. |

_bash/zsh_

```
export FSA_NAME=
export FSA_EMAIL=
export FSA_LANG=
```

_Windows cmd_

```
set FSA_NAME=
set FSA_EMAIL=
set FSA_LANG=
```

_Windows Powershell_

```
$Env:FSA_NAME =
$Env:FSA_EMAIL =
$Env:FSA_LANG =
```

## Deploy

1. `npm install`
2. Deploy the backend: `cdk deploy "fsa-${FSA_NAME}-${FSA_LANG}"`

## Useful commands

- `npm run build` - Compile typescript to js
- `npm run watch` - Watch for changes and compile
- `npm run test` - Perform the jest unit tests
- `cdk deploy` - Deploy this stack to your default AWS account/Region
- `cdk diff` - Compare deployed stack with current state
- `cdk synth` - Emits the synthesized AWS CloudFormation template
