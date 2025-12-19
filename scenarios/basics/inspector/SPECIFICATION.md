# Amazon Inspector Specification

This SDK Basics scenario demonstrates how to interact with Amazon Inspector, a basics scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code examples team to use while developing this example in additional languages.

## Resources
This Basics scenario does not require any additional AWS resources. 

### Relevant documentation

* [Getting started with Amazon Inspector](https://docs.aws.amazon.com/inspector/latest/user/getting_started.html)
* [What is Amazon Inspector?](https://docs.aws.amazon.com/inspector/latest/user/what-is-inspector.html)
* [Amazon Inspector API Reference](https://docs.aws.amazon.com/inspector/v2/APIReference/Welcome.html)
* [Amazon Inspector Pricing](https://aws.amazon.com/inspector/pricing/)

### API Actions Used

* [CreateFilter](https://docs.aws.amazon.com/inspector/v2/APIReference/API_CreateFilter.html)

* [Enable](https://docs.aws.amazon.com/inspector/v2/APIReference/API_Enable.html)

* [ListCoverageStatistics](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListCoverageStatistics.html)

* [ListUsageTotals](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListUsageTotals.html)

* [BatchGetAccountStatus](https://docs.aws.amazon.com/inspector/v2/APIReference/API_BatchGetAccountStatus.html)

* [ListFilters](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListFilters.html)

* [ListFindings](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListFindings.html)

* [BatchGetFindingDetails](https://docs.aws.amazon.com/inspector/v2/APIReference/API_BatchGetFindingDetails.html)

* [ListCoverage](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListCoverage.html)

* [DeleteFilter](https://docs.aws.amazon.com/inspector/v2/APIReference/API_DeleteFilter.html)

- [Disable](https://docs.aws.amazon.com/inspector/v2/APIReference/API_Disable.html)


## Hello Amazon Inspector

The Hello example is designed for users who are new to Amazon Inspector. It demonstrates how to set up the Inspector service client and retrieve a list of all member accounts associated with the current Inspector administrator account.

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

### Cleanup

* Delete the filter
* Diable inspector

---

### Outcome

By following this scenario, users learn how to:

* Check Inspector account status and configuration
* Enable Inspector for different resource types
* List and analyze security findings
* Monitor scan coverage
* Create and manage filters
* Track usage and coverage statistics
* Cleanup the resources

## Errors

The table below describes the exceptions handled in the program for each action.

| Action                       | Exception                  | Handling                                                                 |
|-------------------------------|---------------------------|--------------------------------------------------------------------------|
| `Enable`                      | `ValidationException`      | Prints a message indicating Inspector may already be enabled.           |
| `Disable`                      | `ValidationException`      | Prints a message indicating Inspector may already be disabled.           |
| `listUsageTotals`                      | `ValidationException`      | Validation error listing usage totals. 
| `BatchGetAccountStatus`       | `AccessDeniedException`      | Prints AWS service error details and rethrows the exception.            |
| `ListFindings`                | `ValidationException`      | Prints validation error details.                                         |
| `ListCoverage`                | `ValidationException`      | Prints validation error details.                                         |
| `ListCoverageStatistics`      | `ValidationException`      | Prints validation error details.                                         |
| `createFilter`             | `ValidationException`      | Prints validation error details.                                         |
| `ListFilters`                 | `ValidationException`      | Prints AWS service error details and rethrows the exception.            |
| `deleteFilter`            | `ResourceNotFoundException`      | Prints AWS service error details and rethrows the exception.         |
| `batchGetFindingDetails`            | `ResourceNotFoundException`      | Prints AWS service error details and rethrows the exception.         |


## Metadata

| Action / Scenario                        | Metadata File           | Metadata Key                  |
|-----------------------------------------|------------------------|-------------------------------|
| `Enable`                                 | inspector_metadata.yaml | inspector_Enable     |
| `Disable`                                 | inspector_metadata.yaml | inspector_Disable     |
| `BatchGetAccountStatus`                  | inspector_metadata.yaml | inspector_GetAccountStatus    |
| `ListFindings`                           | inspector_metadata.yaml | inspector_ListFindings        |
| `ListCoverage`                           | inspector_metadata.yaml | inspector_ListCoverage        |
| `ListCoverageStatistics`                 | inspector_metadata.yaml | inspector_ListCoverageStatistics |
| `ListUsageTotals`                         | inspector_metadata.yaml | inspector_ListUsageTotals     |
| `CreateFilter`                           | inspector_metadata.yaml | inspector_CreateFilter        |
| `ListFilters`                            | inspector_metadata.yaml | inspector_ListFilters         |
| `DeleteFilter`       | inspector_metadata.yaml | inspector_DeleteFilter           |
| `BatchGetFindingDetails`       | inspector_metadata.yaml | inspector_BatchGetFindingDetails           |
| `Amazon Inspector Hello`       | inspector_metadata.yaml | inspector_Hello            |
| `Amazon Inspector Basics Scenario`       | inspector_metadata.yaml | inspector_Scenario     