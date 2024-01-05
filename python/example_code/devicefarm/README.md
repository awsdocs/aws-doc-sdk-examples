# Device Farm code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Device Farm.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Device Farm is an app testing service that enables you to test your iOS, Android and Fire OS apps on real, physical phones and tablets that are hosted by AWS._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Run browser tests and take screenshots](browser_testing/test_suite.py)
- [Upload and test device packages](device_testing/run_tests.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Run browser tests and take screenshots

This example shows you how to run browser tests with Device Farm and take screenshots.


<!--custom.scenario_prereqs.device-farm_Scenario_BrowserTesting.start-->
<!--custom.scenario_prereqs.device-farm_Scenario_BrowserTesting.end-->

Start the example by running the following at a command prompt:

```
python browser_testing/test_suite.py
```


<!--custom.scenarios.device-farm_Scenario_BrowserTesting.start-->
<!--custom.scenarios.device-farm_Scenario_BrowserTesting.end-->

#### Upload and test device packages

This example shows you how to upload and test mobile device packages with Device Farm.


<!--custom.scenario_prereqs.device-farm_Scenario_DeviceTesting.start-->
<!--custom.scenario_prereqs.device-farm_Scenario_DeviceTesting.end-->

Start the example by running the following at a command prompt:

```
python device_testing/run_tests.py
```


<!--custom.scenarios.device-farm_Scenario_DeviceTesting.start-->
<!--custom.scenarios.device-farm_Scenario_DeviceTesting.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Device Farm Developer Guide](https://docs.aws.amazon.com/devicefarm/latest/developerguide/welcome.html)
- [Device Farm API Reference](https://docs.aws.amazon.com/devicefarm/latest/APIReference/Welcome.html)
- [SDK for Python Device Farm reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/devicefarm.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0