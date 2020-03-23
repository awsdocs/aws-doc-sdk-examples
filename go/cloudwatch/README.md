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

where all of the values are required:

- NAMESPACE is the namespace.
- METRIC-NAME is the name of the metric.
- UNITS are the units for the metric.
- VALUE is the value of the units.
  If omitted, defaults to 0.0.
- DIMENSION-NAME is the name of the dimension.
- DIMENSION-VALUE is the value of the dimension.

You must supply similar values in *config.json* for the unit test.

### CreateRole

This operation creates an IAM role that grants permission to CloudWatch Events as step one of the workflow of creating an event.

`go run CreateRole -p POLICY -r ROLE`

where all of these values are required:

- POLICY is the name of the policy.
- ROLE is the name of the role.

The unit test requires similar values in *config.json*.

### CreateRule

This operation creates a rule that watches for events on a schedule as step two of the workflow of creating an event.

`go run CreateRule `-r RULE -a ROLE-ARN -s SCHEDULE`

where all of the following values are required:

- RULE is the name of the rule.
- ROLE-ARN is the ARN of the role.
- SCHEDULE is the schedule expression,
  such as `rate(5 minutes)` to have the rule run every five minutes.

The unit test requires similar values in *config.json*.
If *config.json* does not have values for the following entries,
the unit test creates the associated resource using a random value:

- PolicyName is the name of a policy used by the role
- RoleARN is the ARN of the role
- RoleName is the name of the role
- RuleName is the name of the rule
- Schedule is the schedule expression

### CreateTarget

This operation creates an event target that is the resource that is invoked when the rule is triggered as step five of the workflow of creating an event.

`go run CreateTarget.go -r RULE -l LAMBDA-ARN -t TARGET`

Where all of the following are required:

- RULE is the of the rule.
- LAMBDA-ARN is the ARN of the Lambda function that is invoked.
- TARGET is th ID of the target.

You must supply similar values in *config.json* for the unit test.

### DeleteAlarm

This operation deletes an alarm.

`go run DeleteAlarm.go o-a ALARM`

Where the following is required:

- ALARM is the name of the alarm.

You can supply a similar value in *config.json* for the unit test.
If *config.json* does not include a value for ALARM,
it creates an alarm with a random name, enables the alarm, disables the alarm, and deletes the alarm.

In addition, the unit test accepts the following values so it can create and delete a randomly-named alarm:

- InstanceName is the name of an EC2 instance.
- InstanceID is the ID of an EC2 instance.
  If either value is not supplied in *config.json*,
  the unit test gets them from one of the existing EC2 instances.

### DescribeAlarms

This operation lists your Amazon CloudWatch alarms.

`go run DescribeAlarms.go`

### DisableAlarm

This operation disables an alarm.

`go run DisableAlarm -a ALARM`

The following argument is required:

- ALARM is the name of the alarm.

You can supply a similar value in *config.json* for the unit test.
If *config.json* does not include a value for ALARM,
it creates an alarm with a random name, enables the alarm, disables the alarm, and deletes the alarm.

### EnableAlarm

This operation creates and enables an alarm when the CPU utilization of an EC2 instance goes above 70%,
triggering a reboot of the instance.

`go run EnableAlarm -n INSTANCE-NAME -i INSTANCE-ID -a ALARM-NAME`

where all of these values are required:

- INSTANCE-NAME is the name of your EC2 instance.
- INSTANCE-ID is the ID of your EC2 instance.
- ALARM-NAME is the name of the alarm.

The unit test requires all of these values in *config.json*,
except for ALARM-NAME.
If ALARM-NAME is not provided,
it creates a random name starting with **Alarm70-**,
representing the threshold value.

### GetLogEvents

This operation lists 

### Lambda

This operation creates Lambda function as step three of the workflow of creating an event.


### ListMetrics

This operation lists up to 500 of your metrics.

`go run ListMetrics.go`

### SendEvent

This operation sends an event as the final step of the workflow of creating an event.


### UploadLambdaFunction

This operation uploads a Lambda function to an Amazon S3 bucket as step four of the workflow of creating an event.


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
