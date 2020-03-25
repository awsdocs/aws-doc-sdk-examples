# AWS SDK for Go Code Examples for Amazon CloudWatch

## Purpose

These examples demonstrate how to perform the following tasks in your default AWS Region
using your default credentials:

- Get a list of alarms (DescribeAlarms)
- Create and enable an alarm (EnableAlarm)
- Disable an alarm (DisableAlarm)
- Delete an alarm (DeleteAlarm)
- List resource metrics (ListMetrics)
- Create a custom metric (CreateCustomMetric)
- Create an event (CreateRole, CreateRule, Lambda, UploadLambdaFunction, CreateTarget, SendEvent)
- Display events (GetLogEvents)

## Prerequisites

You must have an AWS account, and have your default credentials and AWS Region
configured as described in
[Configuring the AWS SDK for Go](https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/configuring-sdk.html)
in the AWS SDK for Go Developer Guide.

## Running the Code

Each operation is in a sub-folder.
Some operations depend upon results from another operation.
For example, to create a target, you need the ARN of the Lambda function that is invoked.

Most unit tests for the operations require that you fill in some values
in *config.json".

### CreateCustomMetric

This operation creates a new metric in a namespace.

`go run CreateCustomMetric.go -n NAMESPACE -m METRIC-NAME -u UNITS [-v VALUE] -dn DIMENSION-NAME -dv DIMENSION-VALUE`

where:

- NAMESPACE is the namespace.
- METRIC-NAME is the name of the metric.
- UNITS are the units for the metric.
- VALUE is the value of the units.
  The default value is 0.0.
- DIMENSION-NAME is the name of the dimension.
- DIMENSION-VALUE is the value of the dimension.

You must supply similar values in *config.json* for the unit test.

The unit test:

1. Creates a custom metric for each metric in *config.json*.
2. Lists the custom metrics.

### CreateRole

This operation creates an IAM role that grants permission to CloudWatch Events as step one of the workflow of creating an event.

`go run CreateRole -p POLICY -r ROLE`

where:

- POLICY is the name of the policy.
- ROLE is the name of the role.

You must supply similar values in *config.json* for the unit test.

The unit test:

1. Creates a Role with a random name if one is not supplied in *config.json*.
2. Deletes the Role if it created it.

### CreateRule

This operation creates a rule that watches for events on a schedule as step two of the workflow of creating an event.

`go run CreateRule `-r RULE -a ROLE-ARN -s SCHEDULE`

where:

- RULE is the name of the rule.
- ROLE-ARN is the ARN of the role.
- SCHEDULE is the schedule expression,
  such as `rate(5 minutes)` to have the rule run every five minutes.

You must supply similar values in *config.json* for the unit test.

The unit test:

1. Creates a Rule using a random name if the name isn't supplied in *config.json*
2. Deletes the Rule if it created it

### CreateTarget

This operation creates an event target that is the resource that is invoked when the rule is triggered as step five of the workflow of creating an event.

`go run CreateTarget.go -r RULE -l LAMBDA-ARN -t TARGET`

where:

- RULE is the of the rule.
- LAMBDA-ARN is the ARN of the Lambda function that is invoked.
- TARGET is th ID of the target.

You must supply a Lambda ARN value in *config.json* for the unit test.

The unit test:

1. Creates a new Target with a random name if a name is not supplied in *config.json*.
2. Deletes the Target if it created it.

### DeleteAlarm

This operation deletes an alarm.

`go run DeleteAlarm.go -a ALARM`

where:

- ALARM is the name of the alarm.

You can supply a similar value in *config.json* for the unit test.

The unit test:

1. Creates an Alarm with a random name if the name is not supplied in *config.json*.
2. Deletes the Alarm if it created it.

### DescribeAlarms

This operation lists your Amazon CloudWatch alarms.

`go run DescribeAlarms.go`

### DisableAlarm

This operation disables an alarm.

`go run DisableAlarm -a ALARM`

where:

- ALARM is the name of the alarm.

You can supply a similar value in *config.json* for the unit test.

The unit test:

1. Creates and enables an Alarm with a random name if the name is not supplied in *config.json*.
2. Disables and deletes the Alarm if it created the Alarm.

### EnableAlarm

This operation creates and enables an alarm when the CPU utilization of an EC2 instance goes above 70%,
triggering a reboot of the instance.

`go run EnableAlarm -n INSTANCE-NAME -i INSTANCE-ID -a ALARM-NAME`

where:

- INSTANCE-NAME is the name of your EC2 instance.
- INSTANCE-ID is the ID of your EC2 instance.
- ALARM-NAME is the name of the alarm.

You must supply similar values in *config.json* for the unit test.

The unit test:

1. Creates an Alarm with a random name if the name is not supplied in *congig.json*.
2. Enables the Alarm.
3. Deletes the Alarm if it created it.

### GetLogEvents

This operation lists the log events for a log stream in a log group.

`go run GetLogEvents.go -l LOG-GROUP -s LOG-STREAM [-l LIMIT]`

where:

- LOG-GROUP is the name of the log group.
- LOG-STREAM is the name of the log stream.
- LIMIT is the maximum number of events to display.
  If omitted, defaults to 100.

You can supply similar values in *config.json* for the unit test.

The unit test:

1. Restricts the limit value to the range of 1 to 100.
2. Gets a log stream from the user's log groups if either name is not supplied in *config.json*.
3. Lists the events for the log stream in the log group.

### Lambda

The *main.go* file in this folder logs any CloudWatch event it receives.
Use the *packageLambda.bat* file for Windows or the *packageLambda.sh* file to create *main.zip* from *main.go*.
Creating the ZIP file is step three of the workflow of creating an event.
Note that you still must upload the Lambda function to a bucket before you can use it as a target.
See UploadLambdaFunction.

`packageLambda.bat` OR `packageLambda.sh`

### ListMetrics

This operation lists up to 500 of your metrics.

`go run ListMetrics.go`

### SendEvent

This operation sends an event as the final step of the workflow of creating an event.
It sends the event that is defined in *event.json*.

`go run SendEvent.go -l LAMBDA-ARN`

where:

- LAMBDA-ARN is the ARN of the Lambda function to which the event is sent.

You must supply a similar value in *config.json* for the unit test.

The unit test:

1. Creates an event.

### UploadLambdaFunction

This operation uploads a Lambda function to an Amazon S3 bucket as step four of the workflow of creating an event.

`go run UploadLambdaFunction.go -z ZIP-FILE -b BUCKET -f LAMBDA-FUNCTION [-h HANDLER] -a ROLE-ARN [-r RUNTIME]`

where:

- ZIP-FILE is the name of the ZIP file, without the *.zip* extension.
- BUCKET is the name of the Amazon S3 bucket.
- LAMBDA-FUNCTION is the name of the Lambda function.
- HANDLER is the name of the package class containing the function.
  The default is **main**.
- ROLE-ARN is the ARN of the role that enables calling the function
- RUNTIME is the runtime for the function.
  The default is **go1.x**.

You can supply similar values in *config.json* for the unit test.
If you do not, the unit test creates and deletes these resources.

The unit test:

1. Sets the runtime to **go1.x** if not set.
2. Creates a new role if the role ARN is not set.
3. Sets the handler to **main** if it is not set.
4. Creates a new bucket if it is not set.
5. Creates a new Lambda function if it is not set.
6. Sets the zip name to **main** if it is not set.
7. Calls **CreateFunction** to upload the ZIP file to the bucket and create the function.
8. If it created a new Lambda function, it deletes it.
9. If it created a new bucket, it deletes it.
10. If it created a new role, it deletes it.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum  permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all Regions.
  Some AWS services are available only in specific 
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the Unit Tests

Unit tests should delete any resources they create.
However, they might result in charges to your 
AWS account.

To run the unit tests, navigate to a sub-folder and enter:

`go test`

You should see something like the following,
where PATH is the path to folder containing the Go files:

```
PASS
ok      PATH 6.593s
```

If you want to see any log messages, enter:

`go test -test.v`

You should see some additional log messages.
The last two lines should be similar to the previous output shown.
