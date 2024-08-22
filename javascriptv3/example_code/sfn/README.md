# Step Functions code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with AWS Step Functions.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Step Functions makes it easy to coordinate the components of distributed applications as a series of steps in a visual workflow._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [StartExecution](actions/start-execution.js)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Use Step Functions to invoke Lambda functions](javascriptv3/example_code/cross-services/lambda-step-functions)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**
Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Use Step Functions to invoke Lambda functions

This example shows you how to create an AWS Step Functions state machine that invokes AWS Lambda functions in sequence.


<!--custom.scenario_prereqs.cross_ServerlessWorkflows.start-->
<!--custom.scenario_prereqs.cross_ServerlessWorkflows.end-->


<!--custom.scenarios.cross_ServerlessWorkflows.start-->
<!--custom.scenarios.cross_ServerlessWorkflows.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Step Functions Developer Guide](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html)
- [Step Functions API Reference](https://docs.aws.amazon.com/step-functions/latest/apireference/Welcome.html)
- [SDK for JavaScript (v3) Step Functions reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/sfn)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0