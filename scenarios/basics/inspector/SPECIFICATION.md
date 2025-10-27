# Amazon Inspector Specification

This document contains the specification for the *Amazon Inspector Basics Scenario*, based on the implemented Java code examples in `javav2/example_code/Inspector/`. The specification describes the actual code example scenario that demonstrates Amazon Inspector V2 vulnerability management capabilities using the AWS SDK for Java V2.

### Relevant documentation

* [Getting started with Amazon Inspector](https://docs.aws.amazon.com/inspector/latest/user/getting_started.html)
* [What is Amazon Inspector?](https://docs.aws.amazon.com/inspector/latest/user/what-is-inspector.html)
* [Amazon Inspector API Reference](https://docs.aws.amazon.com/inspector/v2/APIReference/Welcome.html)
* [Amazon Inspector Pricing](https://aws.amazon.com/inspector/pricing/)

### API Actions Used

**Core Operations (Implemented in InspectorActions.java):**
* [Enable](https://docs.aws.amazon.com/inspector/v2/APIReference/API_Enable.html) - Enable Inspector scanning for resource types
* [BatchGetAccountStatus](https://docs.aws.amazon.com/inspector/v2/APIReference/API_BatchGetAccountStatus.html) - Check account status and configuration
* [ListFindings](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListFindings.html) - List security findings with filtering
* [ListCoverage](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListCoverage.html) - Monitor resource scan coverage
* [ListCoverageStatistics](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListCoverageStatistics.html) - Get coverage statistics
* [ListUsageTotals](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListUsageTotals.html) - Track usage and costs
* [CreateFilter](https://docs.aws.amazon.com/inspector/v2/APIReference/API_CreateFilter.html) - Create filters to suppress findings
* [ListFilters](https://docs.aws.amazon.com/inspector/v2/APIReference/API_ListFilters.html) - List existing filters

**Additional Operations (Available but not used in main scenario):**
* [BatchGetFindingDetails](https://docs.aws.amazon.com/inspector/v2/APIReference/API_BatchGetFindingDetails.html) - Get detailed finding information
* [Disable](https://docs.aws.amazon.com/inspector/v2/APIReference/API_Disable.html) - Disable Inspector scanning

## Implemented Example Structure

The implementation includes three main Java classes that demonstrate Amazon Inspector V2 capabilities:

### HelloInspector.java

**Purpose**: Basic connectivity verification and service introduction
**Location**: `javav2/example_code/Inspector/src/main/java/com/java/inspector/HelloInspector.java`

**Operations Demonstrated**:
- Set up the Inspector2Client with proper region configuration
- Check current account status using BatchGetAccountStatus
- List recent security findings with basic information
- Display usage totals and cost information
- Provide comprehensive error handling and troubleshooting guidance

**Key Features**:
- Uses BatchGetAccountStatus (not deprecated GetAccountStatus)
- Displays resource states for EC2, ECR, Lambda, and Lambda Code scanning
- Shows findings with severity, type, status, and affected resources
- Includes usage and cost tracking for the last 30 days
- Comprehensive error handling with user-friendly troubleshooting tips

### InspectorActions.java

**Purpose**: Comprehensive service operations library
**Location**: `javav2/example_code/Inspector/src/main/java/com/java/inspector/InspectorActions.java`

**Implemented Methods**:
- `enableInspector()` - Enable Inspector for specified resource types
- `getAccountStatus()` - Get account status using BatchGetAccountStatus
- `listFindings()` - List findings with optional filtering
- `listCoverage()` - Get resource coverage information
- `listCoverageStatistics()` - Get coverage statistics
- `listUsageTotals()` - Get usage and cost information
- `createFilter()` - Create filters to suppress low-severity findings
- `listFilters()` - List existing filters

### InspectorScenario.java

**Purpose**: Complete 8-step workflow demonstration
**Location**: `javav2/example_code/Inspector/src/main/java/com/java/inspector/InspectorScenario.java`

**Scenario Steps**:
1. Check Inspector account status and configuration
2. Enable Inspector for EC2, ECR, Lambda, and Lambda Code
3. Analyze security findings with severity grouping
4. Monitor scan coverage across resources
5. Create example filter to suppress low-severity findings
6. List and display existing filters
7. Track usage and costs
8. Display coverage statistics

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

