# Amazon Inspector Specification

## Overview
This SDK Basics scenario demonstrates how to interact with Amazon Inspector using an AWS SDK. 
It demonstrates various tasks such as checking the status of an Inspector account, enabling Inspector for resource types, showing coverage information, and so on.  

## Resources
This Basics scenario does not require any additional AWS resources. 

### Relevant documentation

* [Getting started with Amazon Inspector](https://docs.aws.amazon.com/inspector/latest/user/getting_started.html)
* [What is Amazon Inspector?](https://docs.aws.amazon.com/inspector/latest/user/what-is-inspector.html)
* [Amazon Inspector API Reference](https://docs.aws.amazon.com/inspector/v2/APIReference/Welcome.html)
* [Amazon Inspector Pricing](https://aws.amazon.com/inspector/pricing/)

### API Actions Used

* [Enable](https://docs.aws.amazon.com/inspector/v2/APIReference/API_Enable.html)
* [BatchGetAccountStatus](https://docs.aws.amazon.com/inspector/v2/APIReference/API_BatchGetAccountStatus.html)
* [ListFindings](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListFindings.html)
* [BatchGetFindingDetails](https://docs.aws.amazon.com/inspector/v2/APIReference/API_BatchGetFindingDetails.html)
* [ListCoverage](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListCoverage.html)
* [Disable](https://docs.aws.amazon.com/inspector/v2/APIReference/API_Disable.html)

## Hello Amazon Inspector

The Hello example is a separate runnable example. - Set up the Inspector service client, checks the current account status for Inspector and displays available scan types.

## Scenario

#### Setup

* Enable Amazon Inspector for the account
* Verify Inspector is successfully activated
* Display account status and enabled scan types

#### Coverage Assessment

* List coverage statistics for EC2 instances, ECR repositories, and Lambda functions
* Display resource coverage details
* Show scanning status for different resource types

#### Findings Management

* List security findings across all resource types
* Filter findings by severity level (CRITICAL, HIGH, MEDIUM, LOW)
* Retrieve detailed information for specific findings

#### Vulnerability Analysis

* Display vulnerability details including CVE information
* Show affected resources and remediation guidance
* Filter findings by resource type (EC2, ECR, Lambda)

#### Cleanup

* Optionally disable Inspector scanning (with user confirmation)
* Display final account status

## Errors

SDK Code examples include basic exception handling for each action used. The table below describes an appropriate exception which will be handled in the code for each service action.

|Action	|Error	|Handling	|
|---	|---	|---	|
|`Enable`	|ValidationException	|Validate resource types and account permissions.	|
|`Enable`	|AccessDeniedException	|Notify user of insufficient permissions and exit.	|
|`BatchGetAccountStatus`	|ValidationException	|Validate account IDs format.	|
|`BatchGetAccountStatus`	|AccessDeniedException	|Handle permission errors gracefully.	|
|`ListFindings`	|ValidationException	|Validate filter criteria and pagination parameters.	|
|`ListFindings`	|InternalServerException	|Retry operation with exponential backoff.	|
|`BatchGetFindingDetails`	|ValidationException	|Validate finding ARNs format.	|
|`BatchGetFindingDetails`	|AccessDeniedException	|Handle access denied for specific findings.	|
|`ListCoverage`	|ValidationException	|Validate filter and pagination parameters.	|
|`Disable`	|ValidationException	|Validate resource types for disabling.	|
|`Disable`	|ConflictException	|Handle cases where Inspector cannot be disabled.	|

## Metadata

|action / scenario	|metadata file	|metadata key	|
|---	|---	|---	|
|`Enable`	|inspector_metadata.yaml	|inspector_Enable	|
|`BatchGetAccountStatus`	|inspector_metadata.yaml	|inspector_BatchGetAccountStatus	|
|`ListFindings`	|inspector_metadata.yaml	|inspector_ListFindings	|
|`BatchGetFindingDetails`	|inspector_metadata.yaml	|inspector_BatchGetFindingDetails	|
|`ListCoverage`	|inspector_metadata.yaml	|inspector_ListCoverage	|
|`Disable`	|inspector_metadata.yaml	|inspector_Disable	|
|`Amazon Inspector Basics Scenario`	|inspector_metadata.yaml	|inspector_Scenario	|

