# Step Functions code examples for the SDK for Swift

## Overview

Shows how to use the AWS SDK for Swift to work with AWS Step Functions.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `swift` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario/Sources/entry.swift)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateActivity](scenario/Sources/Activity.swift#L42)
- [CreateStateMachine](scenario/Sources/StateMachine.swift#L133)
- [DeleteActivity](scenario/Sources/Activity.swift#L102)
- [DeleteStateMachine](scenario/Sources/StateMachine.swift#L308)
- [DescribeExecution](scenario/Sources/StateMachine.swift#L263)
- [DescribeStateMachine](scenario/Sources/StateMachine.swift#L180)
- [GetActivityTask](scenario/Sources/StateMachine.swift#L228)
- [ListActivities](scenario/Sources/Activity.swift#L42)
- [ListStateMachines](scenario/Sources/StateMachine.swift#L105)
- [SendTaskSuccess](scenario/Sources/Activity.swift#L115)
- [StartExecution](scenario/Sources/StateMachine.swift#L205)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

To build any of these examples from a terminal window, navigate into its
directory, then use the following command:

```
$ swift build
```

To build one of these examples in Xcode, navigate to the example's directory
(such as the `ListUsers` directory, to build that example). Then type `xed.`
to open the example directory in Xcode. You can then use standard Xcode build
and run commands.

<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create an activity.
- Create a state machine from an Amazon States Language definition that contains the previously created activity as a step.
- Run the state machine and respond to the activity with user input.
- Get the final status and output after the run completes, then clean up resources.

<!--custom.basic_prereqs.sfn_Scenario_GetStartedStateMachines.start-->
<!--custom.basic_prereqs.sfn_Scenario_GetStartedStateMachines.end-->


<!--custom.basics.sfn_Scenario_GetStartedStateMachines.start-->
<!--custom.basics.sfn_Scenario_GetStartedStateMachines.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `swift` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Step Functions Developer Guide](https://docs.aws.amazon.com/step-functions/latest/dg/welcome.html)
- [Step Functions API Reference](https://docs.aws.amazon.com/step-functions/latest/apireference/Welcome.html)
- [SDK for Swift Step Functions reference](https://sdk.amazonaws.com/swift/api/awssfn/latest/documentation/awssfn)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
