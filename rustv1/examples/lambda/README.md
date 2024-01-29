# Lambda code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with AWS Lambda.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Lambda allows you to run code without provisioning or managing servers._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->

Additionally, to compile Lambda functions written in the Rust programming language, use [Cargo Lambda](https://www.cargo-lambda.info/).

<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a function](src/actions.rs#L231) (`CreateFunction`)
- [Delete a function](src/actions.rs#L469) (`DeleteFunction`)
- [Get a function](src/actions.rs#L377) (`GetFunction`)
- [Invoke a function](src/actions.rs#L402) (`Invoke`)
- [List functions](src/actions.rs#L390) (`ListFunctions`)
- [Update function code](src/actions.rs#L418) (`UpdateFunctionCode`)
- [Update function configuration](src/actions.rs#L444) (`UpdateFunctionConfiguration`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with functions](Cargo.toml)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->

Before running the scenario, compile the Lambda functions.

```
cd calculator
cargo lambda build --output-format Zip
cd -
```

Then, run the complete scenario. Set `RUST_LOG` to capture appropriate tracing events during the execution of the scenario.

```
RUST_LOG=scenario=debug,lambda_code_examples=debug cargo run --bin scenario
```

Other single action examples write directly to stdout, and can be executed with `cargo run --bin [binary]`

<!--custom.instructions.end-->



#### Get started with functions

This example shows you how to do the following:

- Create an IAM role and Lambda function, then upload handler code.
- Invoke the function with a single parameter and get results.
- Update the function code and configure with an environment variable.
- Invoke the function with new parameters and get results. Display the returned execution log.
- List the functions for your account, then clean up resources.

<!--custom.scenario_prereqs.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.scenario_prereqs.lambda_Scenario_GettingStartedFunctions.end-->


<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Rust Lambda reference](https://docs.rs/aws-sdk-lambda/latest/aws_sdk_lambda/)

<!--custom.resources.start-->

- [Cargo Lambda](https://cargo-lambda.info)
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0