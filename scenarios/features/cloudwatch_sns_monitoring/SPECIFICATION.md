# CloudWatch Monitoring with SNS Alerts - Technical Specification

This document contains the technical specifications for _CloudWatch Monitoring with SNS Alerts_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Sample reference output.
- Suggested error handling.
- Metadata information for the scenario.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Errors](#errors)
- [Metadata](#metadata)

## Resources and User Input

This scenario demonstrates a common real-world use case: using CloudWatch to monitor infrastructure metrics and send alerts via SNS when thresholds are breached.

### Phase 1: Setup Resources

The scenario creates the following resources:

- An Amazon SNS topic for receiving alarm notifications
- An email subscription to the SNS topic (user must confirm)
- A CloudWatch alarm that monitors a custom metric
- A CloudWatch dashboard to visualize the metric

**User Prompts**:

```
Enter an email address to receive alarm notifications:
```

The user provides an email address for SNS notifications. The scenario creates an SNS topic and subscribes the email.

```
Enter a name for the CloudWatch alarm:
```

Alarm names must be unique within the AWS account and region. Valid characters: ASCII characters only.

```
Enter a name for the custom metric namespace (default: CustomApp/Monitoring):
```

Namespace for the custom metric. Defaults to "CustomApp/Monitoring" if not provided.

**Example Output**:
```
--------------------------------------------------------------------------------
Welcome to the CloudWatch Monitoring with SNS Alerts Scenario.
--------------------------------------------------------------------------------
This scenario demonstrates how to:
1. Create an SNS topic for alarm notifications
2. Create a CloudWatch alarm that monitors a custom metric
3. Publish metric data to trigger the alarm
4. Receive email notifications when the alarm state changes

Enter an email address to receive alarm notifications:
user@example.com

Creating SNS topic...
SNS topic created: arn:aws:sns:us-east-1:123456789012:cloudwatch-alarms-topic
Email subscription created. Please check your email and confirm the subscription.

Enter a name for the CloudWatch alarm:
HighErrorRateAlarm

Enter a name for the custom metric namespace (default: CustomApp/Monitoring):
[Enter for default]

Creating CloudWatch alarm 'HighErrorRateAlarm'...
Alarm will trigger when ErrorCount >= 10 for 1 evaluation period (1 minute).
CloudWatch alarm created successfully.

Creating CloudWatch dashboard...
Dashboard 'monitoring-dashboard' created successfully.
View at: https://console.aws.amazon.com/cloudwatch/home?region=us-east-1#dashboards:name=monitoring-dashboard
--------------------------------------------------------------------------------
```

### Phase 2: Publish Metric Data

The scenario publishes custom metric data to CloudWatch to demonstrate alarm triggering.

**Steps**:
1. Publish normal metric values (below threshold)
2. Display current alarm state
3. Publish metric values that exceed the threshold
4. Wait for alarm state to change to ALARM
5. Display alarm state and notification details

**Example Output**:
```
--------------------------------------------------------------------------------
Publishing normal metric data (ErrorCount = 5)...
Metric data published successfully.

Checking alarm state...
Current alarm state: OK
Alarm reason: Threshold Crossed: 1 datapoint [5.0] was not greater than or equal to the threshold (10.0).

Publishing high metric data to trigger alarm (ErrorCount = 15)...
Metric data published successfully.

Waiting for alarm state to change (this may take up to 1 minute)...
Alarm state changed to: ALARM
Alarm reason: Threshold Crossed: 1 datapoint [15.0] was greater than or equal to the threshold (10.0).

An email notification has been sent to user@example.com
Check your email for the alarm notification.
--------------------------------------------------------------------------------
```

### Phase 3: Demonstrate Alarm Actions

The scenario retrieves and displays alarm history to show state transitions.

**Example Output**:
```
--------------------------------------------------------------------------------
Retrieving alarm history...

Recent alarm state changes:
1. 2026-02-25T13:45:00Z: State changed from OK to ALARM
   Summary: Threshold Crossed: 1 datapoint [15.0] was greater than or equal to the threshold (10.0).

2. 2026-02-25T13:40:00Z: State changed from INSUFFICIENT_DATA to OK
   Summary: Threshold Crossed: 1 datapoint [5.0] was not greater than or equal to the threshold (10.0).
--------------------------------------------------------------------------------
```

### Phase 4: Cleanup

The scenario prompts the user to delete all created resources.

**User Prompts**:
```
Delete all resources created by this scenario? (y/n)
```

**Example Output**:
```
Deleting CloudWatch dashboard...
Dashboard deleted successfully.

Deleting CloudWatch alarm...
Alarm deleted successfully.

Unsubscribing from SNS topic...
Subscription removed.

Deleting SNS topic...
SNS topic deleted successfully.

All resources cleaned up successfully.
--------------------------------------------------------------------------------
CloudWatch Monitoring with SNS Alerts scenario completed.
```

---

## Errors

| Action | Error | Handling |
|--------|-------|----------|
| `CreateTopic` | InvalidParameter | Notify user of invalid topic name format |
| `Subscribe` | InvalidParameter | Notify user of invalid email format |
| `PutMetricAlarm` | LimitExceededException | Notify user they've reached alarm limit |
| `PutMetricData` | InvalidParameterValue | Notify user of invalid metric data format |
| `DescribeAlarms` | ResourceNotFoundException | Notify user the alarm doesn't exist |

---

## Metadata

| action / scenario | metadata file | metadata key |
|-------------------|---------------|--------------|
| `CreateTopic` | sns_metadata.yaml | sns_CreateTopic |
| `Subscribe` | sns_metadata.yaml | sns_Subscribe |
| `Unsubscribe` | sns_metadata.yaml | sns_Unsubscribe |
| `DeleteTopic` | sns_metadata.yaml | sns_DeleteTopic |
| `PutMetricAlarm` | cloudwatch_metadata.yaml | cloudwatch_PutMetricAlarm |
| `PutMetricData` | cloudwatch_metadata.yaml | cloudwatch_PutMetricData |
| `DescribeAlarms` | cloudwatch_metadata.yaml | cloudwatch_DescribeAlarms |
| `DescribeAlarmHistory` | cloudwatch_metadata.yaml | cloudwatch_DescribeAlarmHistory |
| `DeleteAlarms` | cloudwatch_metadata.yaml | cloudwatch_DeleteAlarms |
| `PutDashboard` | cloudwatch_metadata.yaml | cloudwatch_PutDashboard |
| `DeleteDashboards` | cloudwatch_metadata.yaml | cloudwatch_DeleteDashboards |
| `CloudWatch SNS Monitoring Scenario` | cloudwatch_metadata.yaml | cloudwatch_Scenario_SnsMonitoring |
