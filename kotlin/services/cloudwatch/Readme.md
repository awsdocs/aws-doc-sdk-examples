# Amazon CloudWatch Kotlin code examples

This README discusses how to run the Kotlin code examples for Amazon CloudWatch.

## Running the Amazon CloudWatch Kotlin examples

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a CloudWatch alarm. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateAlarm** - Demonstrates how to create an Amazon CloudWatch alarm.
- **CreateSubscriptionFilter** - Demonstrates how to create an Amazon CloudWatch log subscription filter.
- **DeleteAlarm** - Demonstrates how to delete an Amazon CloudWatch alarm.
- **DeleteSubscriptionFilter** - Demonstrates how to delete Amazon CloudWatch log subscription filters.
- **DescribeAlarms** - Demonstrates how to get information about Amazon CloudWatch alarms.
- **DescribeSubscriptionFilters** - Demonstrates how to get a list of Amazon CloudWatch subscription filters associated with a log group.
- **DisableAlarmActions** - Demonstrates how to disable actions on an Amazon CloudWatch alarm.
- **EnableAlarmActions** - Demonstrates how to enable actions on a CloudWatch alarm.
- **GetLogEvents** - Demonstrates how to get log events from Amazon CloudWatch.
- **GetMetricData** - Demonstrates how to get Amazon CloudWatch metric data.
- **PutEvents** - Demonstrates how to put a sample CloudWatch event.
- **PutMetricAlarm** - Demonstrates how to create a new Amazon CloudWatch alarm based on CPU utilization for an instance.
- **PutRule** - Demonstrates how to create an Amazon CloudWatch event-routing rule.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
