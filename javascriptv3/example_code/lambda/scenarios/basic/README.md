# Scenario - AWS Lambda for AWS SDK for JavaScript (v3)

This is an interactive command prompt that showcases how the JavaScript SDK can be
used for interacting with Lambda.

For the purpose of this example `root` refers to the same directory as this readme.

## Prerequisites

- create an AWS account
- setup a [credentials file](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html)
- configure your account (or IAM user) with access to Lambda and IAM
- install `nodejs` version 18.7.0
- install `yarn`

## Running the example

### Install dependencies for this example

The included setup script installs dependencies for this example,
the included Lambda functions, and a few IAM commands.

1. Run `yarn`

### Service usage warning

- Running this code might result in charges to your AWS account.
- Make sure you understand the pricing model for the services used here. See https://aws.amazon.com/pricing

### Run the interactive command prompt

1. From a command prompt, change directories to `root`.
1. Run `node index.js`.
1. Type `help` and press `Enter` to see a list of commands.
