# CloudWatch code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin examples for Amazon CloudWatch.

Amazon CloudWatch enables you to monitor your complete stack (applications, infrastructure, network, and services) and use alarms, logs, and events data to take automated actions. 

## ⚠️ Important
* Running this code might result in charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

- [Hello Amazon CloudWatch](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/HelloService.kt) (listMetricsPaginator command)

### Single action

Code excerpts that show you how to call individual service functions.

- [Create anomaly detector](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (putAnomalyDetector command)
- [Create a dashboard](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (putDashboard command)
- [Create a metric alarm](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (putMetricAlarm command)
- [Delete alarms](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt) (createKeyspace command)
- [Delete an anomaly detector](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (deleteAnomalyDetectorRequest command)
- [Delete dashboards](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (deleteDashboards command)
- [Describe alarm history](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (describeAlarmHistory command)
- [Describe alarms](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (describeAlarms command)
- [Describe alarms for a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt) (describeAlarm command)
- [Describe anomaly detectors](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (describeAnomalyDetectors command)
- [Disable alarm actions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (disableAlarmActions command)
- [Enable alarm actions](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (enableAlarmActions command)
- [Get metric data](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (getMetricData command)
- [Get metric statistics](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (getMetricStatistics command)
- [Get a metric data image](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (getMetricWidgetImage command)
- [List metrics](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (ListMetrics command)
- [Put data into a metric](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (putMetricData command)


### Scenario 

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with billing, alarms, and metrics](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/cloudwatch/src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)  (multiple commands)

## Run the examples

### Prerequisites

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html). 

To run these examples, you must have the following three JSON files: jsonWidgets.json, CloudDashboard.json, and settings.json. Find these files in this GitHub repository. The CloudWatch scenario depends on these files. In addition, to enable billing metrics and statistics for the scenario example, make sure billing alerts are enabled for your account. For more information, see [Enabling billing alerts](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/monitor_estimated_charges_with_cloudwatch.html#turning_on_billing_metrics).

  **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

 ## Tests
 
 ⚠️ Running the tests might result in charges to your AWS account.

You can test the Kotlin code example for Amazon CloudWatch by running a test file named **CloudWatchTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

To successfully run the tests, define the following values in the test:

- **logGroup** - The name of the log group to use. For example, **testgroup**.
- **alarmName** – The name of the alarm to use. For example, **AlarmFeb**.
- **instanceId** – The ID of the instance to use. Get this value from the AWS Management Console. For example, **ami-04300000000**.
- **streamName** - The name of the stream to use. This value is used to retrieve log events.
- **ruleResource** – The Amazon Resource Name (ARN) of the user who owns the rule. Get this value from the AWS Management Console.  
-  **filterName**  - The name of the filter to use.
- **destinationArn** - The ARN of the destination. This value is used to create subscription filters.
- **roleArn** - The ARN of the user. This value is used to create subscription filters.
- **filterPattern** - The filter pattern. For example, **Error**.
- **myDateSc** - The start date to use to get metric statistics in the scenario test. (For example, 2023-01-11T18:35:24.00Z.) 
- **costDateWeekSc** - The start date to use to get AWS billing statistics. (For example, 2023-01-11T18:35:24.00Z.) 
- **dashboardNameSc** - The name of the dashboard to create in the scenario test. 
- **dashboardJsonSc** - The location of the jsonWidgets file to use to create a dashboard. 
- **dashboardAddSc** - The location of the CloudDashboard.json file to use to update a dashboard. (See Readme file.) 
- **settingsSc** - The location of the settings.json file from which various values are read. (See Readme file.) 
- **metricImageSc** - The location of a BMP file that is used to create a graph.  

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html).
* [User Guide - Amazon CloudWatch](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html).
* [Interface CloudWatchClient](https://sdk.amazonaws.com/kotlin/api/latest/cloudwatch/aws.sdk.kotlin.services.cloudwatch/index.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
