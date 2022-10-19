# CloudWatch code examples for the SDK for Ruby
## Overview
These examples show how to create and manage Amazon CloudWatch metrics, dashboards, and alarms using the SDK for Ruby.

CloudWatch provides a reliable, scalable, and flexible monitoring solution that you can start using within minutes.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create an alarm](./cw-ruby-example-create-alarm.rb) (`CreateAlarm`)

* [Show alarms](./cw-ruby-example-show-alarms.rb) (`DescribeAlarms`)

* [Disable alarm actions](./cw-ruby-example-alarm-actions.rb) (`DisableAlarmActions`)



### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Manage alarms](./cw-ruby-example-alarm-basics.rb)

* [Disable alarm actions](./cw-ruby-example-alarm-actions.rb)

* [Add metrics](./cw-ruby-example-metrics-basics.rb)





## Run the examples

### Prerequisites

See the [Ruby README.md](../../../ruby/README.md) for prerequisites.

### Instructions
The easiest way to interact with this example code is by invoking a [Scenario](#Scenarios) from your command line. For example, `ruby some_scenario.rb` will invoke `some_scenario.rb`.

## Contributing
Code examples thrive on community contribution!
* To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md)

### Tests
⚠️ Running tests might result in charges to your AWS account.

This service is not currently tested.

## Additional resources
* [Service Developer Guide](https://docs.aws.amazon.com/sdk-for-ruby/v3/developer-guide/welcome.html)
* [Service API Reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/)
* [SDK API reference guide](https://aws.amazon.com/developer/language/ruby/)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
