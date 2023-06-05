# AWS CDK for FSA

Deploy the Feedback Sentiment analyzer with the AWS Cloud Development Kit (CDK).

## Prerequisites

- Install NodeJS 18+
- Install AWS CDK
  - https://docs.aws.amazon.com/cdk/v2/guide/cli.html
- AWS access key & secret access key for IAM user with permissions to create resources listed above
  - https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html#cli-configure-quickstart-config

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
2. Deploy the backend: `cdk deploy "fsa-${FSA_NAME}-${FSA_LANG}-be"`

## Useful commands

- `npm run build` compile typescript to js
- `npm run watch` watch for changes and compile
- `npm run test` perform the jest unit tests
- `cdk deploy` deploy this stack to your default AWS account/region
- `cdk diff` compare deployed stack with current state
- `cdk synth` emits the synthesized CloudFormation template
