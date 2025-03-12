#  AWS Systems Manager Engineering Specification

## Overview
    This SDK getting started scenario demonstrates how to interact with AWS Systems Manager using an AWS SDK. The provided code is an AWS application that demonstrates the usage of the AWS Systems Manager SDK to interact with various AWS Systems Manager service operations.

## Scenario Program Flow
   - Creates a Systems Manager maintenance window with a default name or a user-provided name.
   - Modifies the maintenance window schedule.
   - Creates a Systems Manager document with a default name or a user-provided name.
   - Sends a command to a specified EC2 instance using the created document and display the time when the command was invoked.
   - Creates a Systems Manager OpsItem with a predefined title, source, category, and severity.
   - Updates and resolves the created OpsItem.
   - Deletes the Systems Manager maintenance window, OpsItem, and document.

## Hello SSM
This program is intended for users not familiar with Systems Manager SDK to easily get up and running. The logic is to show use of listDocumentsPaginator().

### Program execution
The following shows the output of the program in the console.

```
   Document Name: AWSMigration-ConvertCentOsToRockyLinuxDistribution
   Document Name: AWSMigration-CreateLoadBalanceAutoScaleGroup
   Document Name: AWSMigration-EnableInspector
```


## Scenario Program execution
The following shows the output of the program in the console. 


```
--------------------------------------------------------------------------------
Welcome to the AWS Systems Manager SDK Getting Started scenario.
This Java program demonstrates how to interact with AWS Systems Manager using the AWS SDK.
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
 SSM OpsItem is a feature provided by the Systems Manager service.
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
```

## Metadata

The following table describes the metadata used in this SDK Getting Started Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `CreateOpsItem`              | ssm_metadata.yaml            | ssm_CreateOpsItem                       |
| `CreateMaintenanceWindow`    | ssm_metadata.yaml            | ssm_CreateMainWindow                    |
| `UpdateMaintenanceWindow`    | ssm_metadata.yaml            | ssm_UpdateMainWindow                    |
| `CreateDocument`             | ssm_metadata.yaml            | ssm_CreateDocument                      |
| `SendCommand `               | ssm_metadata.yaml            | ssm_SendCommand                         |
| `ListCommandInvocations`     | ssm_metadata.yaml            | ssm_ListCommandInfocations              |
| `UpdateOpsItem`              | ssm_metadata.yaml            | ssm_UpdateOpsItem                       |
| `DeleteMaintenanceWindow `   | ssm_metadata.yaml            | ssm_DeleteMainWindow                    |
| `DeleteDocument`             | ssm_metadata.yaml            | ssm_DeleteMainWindow                    |
| `DescribeOpsItems       `    | ssm_metadata.yaml            | ssm_DescribeOpsItems                    |
