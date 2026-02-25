# CloudWatch Monitoring with SNS Alerts

## Overview

This scenario demonstrates how to use Amazon CloudWatch to monitor infrastructure metrics and send alerts via Amazon SNS when thresholds are breached. This is one of the most common real-world use cases for SNS in production environments.

## What You'll Learn

- Create SNS topics and email subscriptions for notifications
- Create CloudWatch alarms that monitor custom metrics
- Publish custom metric data to CloudWatch
- Trigger alarms and receive email notifications
- Create CloudWatch dashboards for visualization
- Retrieve alarm history and state changes

## Scenario Flow

1. **Setup**: Create SNS topic, subscribe email, create CloudWatch alarm and dashboard
2. **Monitoring**: Publish metric data and trigger alarm state changes
3. **Notifications**: Receive email alerts when thresholds are breached
4. **Cleanup**: Delete all created resources

## Prerequisites

- AWS credentials configured
- Valid email address for receiving notifications
- Permissions for CloudWatch, SNS, and IAM operations

## Services Used

- Amazon CloudWatch - Monitoring and alarms
- Amazon SNS - Notification delivery

## Running the Scenario

Follow the prompts to:
- Provide an email address for notifications
- Name your CloudWatch alarm
- Specify a custom metric namespace
- Publish metric data to trigger alarms
- View alarm state changes and history
- Clean up resources when complete

**Note**: You must confirm your email subscription to receive alarm notifications.
