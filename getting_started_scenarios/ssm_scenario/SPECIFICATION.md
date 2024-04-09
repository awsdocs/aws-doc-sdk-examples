#  AWS Systems Manager Engineering Specification

## Overview
   This SDK getting started scenario demonstrates how to interact with AWS Systems Manager (SSM) using the AWS SDK. The provided code is a Java application that demonstrates the usage of the AWS SSM SDK to interact with various AWS SSM service operations.

## Scenario Program Flow
   - The application should allow the user to create an SSM maintenance window with a default name or a user-provided name.
   - The application should allow the user to modify the maintenance window schedule.
   - The application should allow the user to create an SSM document with a default name or a user-provided name.
   - The application should display a list of available SSM documents.
   - The application should send a command to a specified EC2 instance using the created SSM document and display the time when the command was invoked.
   - The application should allow the user to create an SSM OpsItem with a predefined title, source, category, and severity.
   - The application should allow the user to update and resolve the created OpsItem.
   - The application should allow the user to delete the created SSM maintenance window and document.

## Hello SSM
This program is intended for users not familiar with the AWS SSM SDK to easily get up an running. The logic is to show use of listDocumentsPaginator().

Program execution
The following shows the output of the program in the console.

 ``` java
Document Name: AWSMigration-ConvertCentOsToRockyLinuxDistribution
Document Name: AWSMigration-CreateLoadBalanceAutoScaleGroup
Document Name: AWSMigration-EnableInspector


 ```

## Scenario Program execution
The following shows the output of the program in the console. 
   - The application should provide clear and informative console output to guide the user through the various operations.
   - The application should handle any exceptions that may occur during the execution of the SSM operations and provide appropriate error messages.
   - The application should ensure that all required SSM resources (maintenance window, document, OpsItem) are properly created, modified, and deleted.

   ``` java

--------------------------------------------------------------------------------
Welcome to the AWS Systems Manager SDK Getting Started scenario.
This Java program demonstrates how to interact with AWS Systems Manager using the AWS SDK for Java (v2).
AWS Systems Manager is the operations hub for your AWS applications and resources and a secure end-to-end management solution.
The program's primary functionalities include creating a maintenance window, creating a document, sending a command to a document,
listing documents, listing commands, creating an OpsItem, modifying an OpsItem, and deleting AWS SSM resources.
Upon completion of the program, all AWS resources are cleaned up.
Let's get started...
Please hit Enter


--------------------------------------------------------------------------------
Create an SSM maintenance window.
Please enter the maintenance window name (default is ssm-maintenance-window):

The maintenance window id is mw-0a782f69416fa2d68
--------------------------------------------------------------------------------
Modify the maintenance window by changing the schedule
Please hit Enter

The SSM maintenance window was successfully updated
--------------------------------------------------------------------------------
Create an SSM document that defines the actions that Systems Manager performs on your managed nodes.
Please enter the document name (default is ssmdocument):

The status of the SSM document is Creating
Now we will list SSM documents. Please hit Enter

Document Name: AWSMigration-ConvertCentOsToRockyLinuxDistribution
Document Name: AWSMigration-CreateLoadBalanceAutoScaleGroup
Document Name: AWSMigration-EnableInspector
Document Name: AWSMigration-LinuxTimeSyncSetting
Document Name: AWSMigration-ReplaceSuseSubscriptionWithAwsSubscription
Document Name: AWSMigration-RunSourceServerAction
Document Name: AWSMigration-ValidateDiskSpace
Document Name: AWSMigration-ValidateHttpResponse
Document Name: AWSMigration-ValidateNetworkConnectivity
Document Name: AWSMigration-VerifyMountedVolumes
Document Name: AWSMigration-VerifyProcessIsRunning
Document Name: AWSMigration-VerifySqlAWSSubscription
Document Name: AWSMigration-VerifyTags
Document Name: AWSNVMe
Document Name: AmazonInspector-ManageAWSAgent
Document Name: AWSKinesisTap
Document Name: AWSApp2Container-ReplatformApplications
Document Name: AWSNitroEnclavesWindows
Document Name: AWSDistroOTel-Collector
Document Name: AmazonEFSUtils
Document Name: AWSEC2-ApplicationInsightsCloudwatchAgentInstallAndConfigure
Document Name: AWSEC2-CheckPerformanceCounterSets
Document Name: AWSEC2-CloneInstanceAndUpgradeSQLServer
Document Name: AWSEC2-CloneInstanceAndUpgradeWindows
Document Name: AWSEC2-CloneInstanceAndUpgradeWindows2019
Document Name: AWSEC2-ConfigureSTIG
Document Name: AWSEC2-CreateVssSnapshot
Document Name: AWSEC2-DetectWorkload
Document Name: AWSEC2-ManageVssIO
Document Name: AWSEC2-PatchLoadBalancerInstance
Document Name: AWSEC2-RunSysprep
Document Name: AWSEC2-SQLServerDBRestore
Document Name: AWSEC2-UpdateLaunchAgent
Document Name: AWSEC2-VssInstallAndSnapshot
Document Name: AWSSAPTools-DataProvider
Document Name: AWSResilienceHub-BacktrackRdsSOP_2020-04-01
Document Name: AWSResilienceHub-BlockSQSDeleteMessageTest_2021-03-09
Document Name: AWSResilienceHub-BreakEFSSecurityGroupTest_2020-09-21
Document Name: AWSResilienceHub-BreakLambdaSecurityGroupTest_2020-09-21
Document Name: AWSResilienceHub-BreakSNSSubscriptionDeliveryToSQSTest_2020-04-01
Document Name: AWSResilienceHub-BreakSNSSubscriptionRedrivePolicyTest_2020-04-01
Document Name: AWSResilienceHub-BreakSQSQueuePolicyTest_2020-11-27
Document Name: AWSResilienceHub-BreakTargetGroupsHealthCheckPortTest_2020-04-01
Document Name: AWSResilienceHub-ChangeEFSProvisionedThroughputSOP_2020-10-26
Document Name: AWSResilienceHub-ChangeHttpWsApiGwThrottlingSettingsSOP_2020-10-26
Document Name: AWSResilienceHub-ChangeLambdaConcurrencyLimitSOP_2020-10-26
Document Name: AWSResilienceHub-ChangeLambdaExecutionTimeLimitSOP_2020-10-26
Document Name: AWSResilienceHub-ChangeLambdaMemorySizeSOP_2020-10-26
Document Name: AWSResilienceHub-ChangeLambdaProvisionedConcurrencySOP_2020-10-26
Document Name: AWSResilienceHub-ChangeRestApiGwQuotaLimitSOP_2020-10-26
--------------------------------------------------------------------------------
Now we are going to run a command on an EC2 instance
Please hit Enter

The SSM document is active and ready to use.
Command ID: ac4aef03-7d60-4fba-8d71-051defea89e2
Wait 5 secs
Command execution successful
--------------------------------------------------------------------------------
Lets get the time when the specific command was sent to the specific managed node
Please hit Enter

The time of the command invocation is 2024-04-09 14:52:50
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
 Now we will create an SSM OpsItem.
 SSM OpsItem is a feature provided by Amazon's Systems Manager (SSM) service.
 It is a type of operational data item that allows you to manage and track various operational issues,
 events, or tasks within your AWS environment.

 You can create OpsItems to track and manage operational issues as they arise.
 For example, you could create an OpsItem whenever your application detects a critical error
 or an anomaly in your infrastructure.

Please hit Enter

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Now we will update SSM OpsItem oi-c1e5435471e9
Please hit Enter

Now we will resolve the SSM OpsItem oi-c1e5435471e9
Please hit Enter

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Would you like to delete the AWS Systems Manager resources? (y/n)

The AWS Systems Manager resources will not be deleted
--------------------------------------------------------------------------------
This concludes the AWS Systems Manager SDK Getting Started scenario.
--------------------------------------------------------------------------------

   ```

## SOS Tags

The following table describes the metadata used in this SDK Getting Started Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `deleteCluster`              | redshift_metadata.yaml       | redshift_DeleteCluster                  |
| `addrecord`                  | redshift_metadata.yaml       | redshift_Insert                         |
| `describeStatement`          | redshift_metadata.yaml       | redshift_DescribeStatement              |
| `modifyCluster `             | redshift_metadata.yaml       | redshift_ModifyCluster                  |
| `querymoviesd`               | redshift_metadata.yaml       | redshift_Query                          |
| `getStatementResult`         | redshift_metadata.yaml       | redshift_GetStatementResult             |
| `describeClusters`           | redshift_metadata.yaml       | redshift_DescribeClusters               |
| `createTable `               | redshift_metadata.yaml       | redshift_CreateTable                    |
| `createCluster `             | redshift_metadata.yaml       | redshift_CreateCluster                  |
| `describeClustersPaginator ` | redshift_metadata.yaml       | redshift_Hello                          |
| `scenario`                   | redshift_metadata.yaml       | redshift_Scenario                        |