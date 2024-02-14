# EventBridge code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with Amazon EventBridge.

<!--custom.overview.start-->
<!--custom.overview.end-->

_EventBridge is a serverless event bus service that makes it easy to connect your applications with data from a variety of sources._

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
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create and trigger a rule](cw-ruby-example-send-events-ec2.rb)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The quickest way to interact with this example code is to invoke a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.
<!--custom.instructions.end-->



#### Create and trigger a rule

This example shows you how to create and trigger a rule in Amazon EventBridge.


<!--custom.scenario_prereqs.eventbridge_Scenario_createAndTriggerARule.start-->
<!--custom.scenario_prereqs.eventbridge_Scenario_createAndTriggerARule.end-->

Start the example by running the following at a command prompt:

```
ruby cw-ruby-example-send-events-ec2.rb
```

<!--custom.scenarios.eventbridge_Scenario_createAndTriggerARule.start-->
<!--custom.scenarios.eventbridge_Scenario_createAndTriggerARule.end-->

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

- [EventBridge User Guide](https://docs.aws.amazon.com/eventbridge/latest/userguide/eb-what-is.html)
- [EventBridge API Reference](https://docs.aws.amazon.com/eventbridge/latest/APIReference/Welcome.html)
- [SDK for Ruby EventBridge reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Eventbridge.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0