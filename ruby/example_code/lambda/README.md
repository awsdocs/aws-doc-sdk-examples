# Lambda code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with AWS Lambda.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `ruby` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a function](lambda_basics.rb#L127) (`CreateFunction`)
- [Delete a function](lambda_basics.rb#L277) (`DeleteFunction`)
- [Get a function](lambda_basics.rb#L110) (`GetFunction`)
- [Invoke a function](lambda_basics.rb#L164) (`Invoke`)
- [List functions](lambda_basics.rb#L262) (`ListFunctions`)
- [Update function code](lambda_basics.rb#L237) (`UpdateFunctionCode`)
- [Update function configuration](lambda_basics.rb#L212) (`UpdateFunctionConfiguration`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with functions](lambda_basics.rb)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
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

Start the example by running the following at a command prompt:

```
ruby lambda_basics.rb
```

<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.start-->
<!--custom.scenarios.lambda_Scenario_GettingStartedFunctions.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `ruby` folder.



<!--custom.tests.start-->

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources

- [Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [SDK for Ruby Lambda reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Lambda.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0