# Amazon CloudWatch Logs Syslog Ingestion Specification

This specification describes a scenario that demonstrates the new CloudWatch Logs syslog ingestion feature. CloudWatch Logs now supports managed syslog ingestion, enabling customers to route syslog data from firewalls, routers, switches, and Linux servers directly into CloudWatch Logs log groups through a VPC endpoint — without running a separate collection tier. This scenario walks through creating a syslog configuration for a log group, listing syslog configurations, and cleaning up by deleting the configuration. The scenario uses only the new syslog configuration management APIs.

### Resources

The syslog VPC endpoint is a prerequisite that the scenario provisions automatically with an AWS CloudFormation stack, so the example is self-contained and requires no manual setup. The scenario deploys the stack at startup and deletes it during cleanup.

- **CloudFormation stack (provisioned by the scenario).** A CloudFormation template, included with the example, creates the network prerequisites for syslog ingestion:
  - A VPC with at least one subnet.
  - A security group allowing inbound syslog traffic (TCP 6514 for TLS, TCP 1514 for plaintext, UDP 514).
  - An interface VPC endpoint for the CloudWatch Logs syslog service (service name `com.amazonaws.<region>.syslog-logs`) in that subnet and security group.
  - Stack **output** `VpcEndpointId` — the id of the created syslog VPC endpoint, which the scenario reads and passes to `PutSyslogConfiguration`.
- **Log group (created by the scenario, not the stack).** The scenario creates and deletes the log group that receives syslog data.
- **Resource policy (created by the scenario).** A resource policy on the log group that grants the `syslog.logs.amazonaws.com` service principal permission to call `logs:PutLogEvents` and `logs:CreateLogStream`, scoped to the VPC endpoint.

> The scenario deploys the CloudFormation stack, reads the `VpcEndpointId` output, and uses it throughout — the user is NOT prompted for a VPC endpoint id. The stack is deleted during cleanup so no resources are left behind. Deploying the VPC endpoint can take several minutes; the scenario waits for stack creation to complete before continuing.

### Relevant documentation

- [What is Amazon CloudWatch Logs?](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)
- [Amazon CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html)
- [PutSyslogConfiguration API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutSyslogConfiguration.html)
- [ListSyslogConfigurations API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_ListSyslogConfigurations.html)
- [DeleteSyslogConfiguration API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_DeleteSyslogConfiguration.html)
- [Use CloudWatch syslog and Log Alarms to give AWS DevOps Agent on-premises visibility](https://aws.amazon.com/blogs/mt/use-cloudwatch-syslog-and-log-alarms-to-give-aws-devops-agent-on-premises-visibility/)

### API Actions Used

- [CreateLogGroup](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_CreateLogGroup.html) — Creates a log group to receive syslog data.
- [DescribeLogGroups](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_DescribeLogGroups.html) — Lists log groups to verify creation and for the Hello example.
- [PutSyslogConfiguration](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_PutSyslogConfiguration.html) — Creates or updates a syslog configuration for a log group, enabling syslog ingestion through a VPC endpoint.
- [ListSyslogConfigurations](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_ListSyslogConfigurations.html) — Returns a list of syslog configurations, optionally filtered by log group or VPC endpoint.
- [DeleteSyslogConfiguration](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_DeleteSyslogConfiguration.html) — Deletes a syslog configuration for a log group, stopping syslog ingestion through the specified VPC endpoint.
- [DeleteLogGroup](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/API_DeleteLogGroup.html) — Deletes the log group created for the scenario.

## Hello CloudWatch Logs

The Hello example is a separate, standalone runnable example that verifies connectivity to the CloudWatch Logs service.

- Set up the CloudWatch Logs service client.
- Call `DescribeLogGroups` with no filter to retrieve a page of log groups.
- Display the name, ARN, and creation time of each returned log group.
- If no log groups exist, display a message indicating the account has no log groups in this Region.

## Scenario

This scenario demonstrates the full lifecycle of the new CloudWatch Logs syslog configuration feature: creating a log group, associating a syslog configuration with it, listing configurations, and cleaning up.

### Setup

1. **Deploy the CloudFormation prerequisite stack.** Deploy the included CloudFormation template (using the SDK's CloudFormation client), which provisions the VPC, subnet, security group, and the CloudWatch Logs syslog interface VPC endpoint. Wait for the stack to reach `CREATE_COMPLETE` (this can take several minutes), then read the `VpcEndpointId` stack output. Use this endpoint id for the rest of the scenario. Do NOT prompt the user for a VPC endpoint id.
2. **Prompt for a log group name.** Ask the user for a name for the log group that will receive syslog data (e.g., `/syslog/demo`). Validate that the name meets CloudWatch Logs naming requirements (1–512 characters; a-z, A-Z, 0-9, underscore, hyphen, forward slash, period, number sign).
3. **Create the log group.** Call `CreateLogGroup` with the user-provided name. If the log group already exists (ResourceAlreadyExistsException), notify the user and proceed with the existing log group.
4. **Verify the log group.** Call `DescribeLogGroups` with a log group name prefix matching the new log group name. Display the log group name, ARN, and creation timestamp to confirm it was created successfully.
5. **Add the log group resource policy.** Create a resource policy on the log group that authorizes the `syslog.logs.amazonaws.com` service principal to call `logs:PutLogEvents` and `logs:CreateLogStream`, scoped to the deployed VPC endpoint, so the syslog service can write to the log group.

### Create Syslog Configuration

6. **Create the syslog configuration.** Call `PutSyslogConfiguration` with:
   - `logGroupIdentifier`: The name or ARN of the log group created in Setup Step 3.
   - `vpcEndpointId`: The `VpcEndpointId` read from the CloudFormation stack output in Setup Step 1.

   Explain that this associates the VPC endpoint with the log group so that syslog messages arriving at the endpoint are stored in the log group. The service parses common syslog formats (RFC 5424, RFC 3164, Cisco FTD/ASA) and extracts fields such as `facility`, `severity`, `hostname`, and `appName`.

6. **Confirm the configuration.** Display a success message indicating that syslog ingestion is now enabled for the log group through the specified VPC endpoint. Note the three supported transport options:
   - TCP with TLS on port 6514 (recommended)
   - Plaintext TCP on port 1514
   - UDP on port 514

### List Syslog Configurations

7. **List all syslog configurations.** Call `ListSyslogConfigurations` with no filters to retrieve all syslog configurations in the account. Handle pagination by following `nextToken` if present. For each configuration, display:
   - Log group ARN (`logGroupArn`)
   - VPC endpoint ID (`vpcEndpointId`)
   - Source type (`sourceType`)
   - Creation timestamp (`createdAt`)

8. **List configurations filtered by log group.** Call `ListSyslogConfigurations` with `logGroupIdentifier` set to the log group created in Setup. Display the filtered results and confirm the configuration created in Step 5 appears.

9. **List configurations filtered by VPC endpoint.** Call `ListSyslogConfigurations` with `vpcEndpointId` set to the VPC endpoint from Step 4. Display the results to show all log groups associated with that endpoint.

### Cleanup

10. **Prompt user for cleanup.** Ask the user whether they want to delete the resources created during the scenario.
11. **Delete the syslog configuration.** If the user confirms, call `DeleteSyslogConfiguration` with:
    - `logGroupIdentifier`: The log group name or ARN.
    - `vpcEndpointId`: The VPC endpoint ID.

    Explain that after deletion, syslog data is no longer ingested through the specified VPC endpoint into this log group.

12. **Verify deletion.** Call `ListSyslogConfigurations` filtered by the log group to confirm the configuration has been removed. The result should be an empty list.

13. **Delete the log group.** Call `DeleteLogGroup` with the log group name. Notify the user that the log group and all archived log events have been permanently deleted.
14. **Delete the CloudFormation prerequisite stack.** Delete the stack deployed in Setup Step 1 (which removes the VPC endpoint, security group, subnet, and VPC), and wait for the stack to reach `DELETE_COMPLETE` so no resources are left behind. Cleanup should run even if an earlier step failed (use a `finally`-style guarantee), and should tolerate resources that were never created or are already gone.

### Outcome

At the end of this scenario, the user will have:

- Deployed a CloudFormation stack that provisions the VPC endpoint prerequisite for syslog ingestion, and read its `VpcEndpointId` output.
- Created a CloudWatch Logs log group for syslog ingestion and authorized the syslog service with a resource policy.
- Associated a syslog configuration with the log group and the VPC endpoint using `PutSyslogConfiguration`.
- Listed syslog configurations with and without filters using `ListSyslogConfigurations`.
- Cleaned up by deleting the syslog configuration with `DeleteSyslogConfiguration`, the log group, and the CloudFormation stack.
- Understood how CloudWatch managed syslog ingestion eliminates the need for a collection tier by accepting syslog directly through a VPC endpoint.

## Errors

SDK code examples include basic exception handling for each action used. The table below describes the single most relevant exception for each action in the context of this scenario.

| Action | Exception | Handling |
| - | - | - |
| `CreateLogGroup` | ResourceAlreadyExistsException | Notify the user that the log group already exists and proceed with the existing log group. |
| `DescribeLogGroups` | ServiceUnavailableException | Notify the user that the service is temporarily unavailable and suggest retrying. |
| `PutSyslogConfiguration` | ResourceNotFoundException | Notify the user that the specified log group or VPC endpoint does not exist. Prompt for valid values. |
| `ListSyslogConfigurations` | InvalidParameterException | Notify the user that a filter parameter is invalid. Display the expected format and prompt for correction. |
| `DeleteSyslogConfiguration` | ResourceNotFoundException | Notify the user that the syslog configuration does not exist or was already deleted. Continue with cleanup. |
| `DeleteLogGroup` | ResourceNotFoundException | Notify the user that the log group does not exist or was already deleted. Continue gracefully. |

## Metadata

| action / scenario | metadata file | metadata key |
| - | - | - |
| `DescribeLogGroups` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_DescribeLogGroups |
| `CreateLogGroup` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_CreateLogGroup |
| `PutSyslogConfiguration` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_PutSyslogConfiguration |
| `ListSyslogConfigurations` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_ListSyslogConfigurations |
| `DeleteSyslogConfiguration` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_DeleteSyslogConfiguration |
| `DeleteLogGroup` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_DeleteLogGroup |
| `CloudWatch Logs Hello` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_Hello |
| `CloudWatch Logs Syslog Basics Scenario` | cloudwatchlogs_metadata.yaml | cloudwatchlogs_Scenario |
