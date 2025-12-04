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

The Hello example is intended for users not familiar with this service to easily get up and running. It sets up the Inspector service client, checks the current account status for Inspector and displays available scan types.

## Scenario

## Scenario

This scenario demonstrates the basic usage of **Amazon Inspector** using a Java program. It focuses on checking account status, enabling Inspector, listing findings, reviewing coverage, and managing filters.

---

### Setup

* Check Amazon Inspector account status
* Enable Inspector for available resource types (if not already enabled)
* Display account status summary

---

### Coverage Assessment

* List coverage details for scanned resources
* Display overall coverage statistics
* Review scan status for resources (general overview)

---

### Findings Management

* List security findings across all resource types
* Create an example filter to suppress low-severity findings
* List existing filters

---

### Usage and Costs

* Check usage totals and metrics for Inspector
* Review coverage statistics

---

### Notes

* The program **does not retrieve detailed vulnerability (CVE) information**.
* Resource-specific filtering (e.g., EC2, ECR, Lambda) is **not implemented**.
* Cleanup (disabling Inspector) is **not performed**.

---

### Outcome

By following this scenario, users learn how to:

* Check Inspector account status and configuration
* Enable Inspector for different resource types
* List and analyze security findings
* Monitor scan coverage
* Create and manage filters
* Track usage and coverage statistics

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

