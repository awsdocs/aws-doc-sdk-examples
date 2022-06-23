# AWS Step Functions Kotlin code examples

This README discusses how to run the Kotlin code examples for AWS Step Functions.

## Running the AWS Step Functions Kotlin files

**IMPORTANT**

The Kotlin code examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources,  such as deleting a state machine. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these examples: 

- **CreateStateMachine** - Demonstrates how to create a state machine for Step Functions.
- **DeleteStateMachine** - Demonstrates how to delete a state machine for Step Functions.
- **GetExecutionHistory** - Demonstrates how to retrieve the history of the specified execution as a list of events.
- **ListActivities** - Demonstrates how to list existing activities for Step Functions.

To run these examples, you can setup your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 
