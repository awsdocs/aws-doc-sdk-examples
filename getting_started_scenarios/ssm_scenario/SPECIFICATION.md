#  AWS Systems Manager Engineering Specification

## Overview
   This SDK getting started scenario demonstrates how to interact with AWS Systems Manager (SSM) using the AWS SDK. The provided code is a Java application that demonstrates the usage of the AWS SSM SDK to interact with various AWS SSM service operations.

## Scenario Program Flow
   - The application should allow the user to create an SSM maintenance window with a default name or a user-provided name.
   - The application should allow the user to modify the maintenance window schedule.
   - The application should allow the user to create an SSM document with a default name or a user-provided name.
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
## CDK Usage

This example requires an EC2 instance in order to run the program. A CDK example that stands up an EC2 instance will be part of this solution. 


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