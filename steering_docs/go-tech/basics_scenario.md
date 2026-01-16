# Go Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Go-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Go-premium-KB", "Go implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: Use fmt.Print and fmt.Scan for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Actions Classes**: MUST use service actions structs for all operations
- **Context**: ALWAYS use context.Context for AWS operations

## File Structure
```
gov2/{service}/scenarios/
├── scenario_{name}.go              # Main scenario file
├── scenario_{name}_test.go         # Unit tests
└── scenario_{name}_integ_test.go   # Integration tests
```

## MANDATORY Pre-Implementation Steps

### Step 1: Read Service Specification
**CRITICAL**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-step workflow description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Scenario Implementation Pattern
### Implementation Pattern Based on SPECIFICATION.md
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package scenarios contains interactive scenarios that demonstrate AWS service capabilities.
package scenarios

import (
    "context"
    "errors"
    "fmt"
    "log"
    "strings"
    "time"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/{service}"
    "github.com/aws/smithy-go"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/{service}/actions"
)

// {Service}Scenario contains the state and methods for the interactive {AWS Service} scenario.
type {Service}Scenario struct {
    {service}Actions *actions.{Service}Actions
    questioner       demotools.IQuestioner
    resourceId       string
}

// New{Service}Scenario constructs a {Service}Scenario from the provided SDK configuration.
func New{Service}Scenario(sdkConfig aws.Config, questioner demotools.IQuestioner) {Service}Scenario {
    {service}Client := {service}.NewFromConfig(sdkConfig)
    return {Service}Scenario{
        {service}Actions: &actions.{Service}Actions{{Service}Client: {service}Client},
        questioner:       questioner,
    }
}

// Run runs the interactive scenario.
func (scenario *{Service}Scenario) Run(ctx context.Context) {
    defer func() {
        if r := recover(); r != nil {
            log.Printf("Something went wrong with the demo: %v", r)
        }
    }()

    log.Println(strings.Repeat("-", 88))
    log.Println("Welcome to the {AWS Service} basics scenario!")
    log.Println(strings.Repeat("-", 88))
    log.Println("{Service description and what users will learn}")
    log.Println()

    err := scenario.runScenario(ctx)
    if err != nil {
        log.Printf("Scenario failed: %v", err)
    }
}

// runScenario runs the main scenario workflow.
func (scenario *{Service}Scenario) runScenario(ctx context.Context) error {
    var err error
    
    defer func() {
        cleanupErr := scenario.cleanupPhase(ctx)
        if cleanupErr != nil {
            log.Printf("Cleanup failed: %v", cleanupErr)
        }
    }()

    err = scenario.setupPhase(ctx)
    if err != nil {
        return fmt.Errorf("setup phase failed: %w", err)
    }

    err = scenario.demonstrationPhase(ctx)
    if err != nil {
        return fmt.Errorf("demonstration phase failed: %w", err)
    }

    err = scenario.examinationPhase(ctx)
    if err != nil {
        return fmt.Errorf("examination phase failed: %w", err)
    }

    return nil
}

// setupPhase implements the setup phase based on specification.
func (scenario *{Service}Scenario) setupPhase(ctx context.Context) error {
    log.Println("Setting up {AWS Service}...")
    log.Println()

    // Example: Check for existing resources (from specification)
    existingResources, err := scenario.{service}Actions.ListResources(ctx)
    if err != nil {
        return fmt.Errorf("couldn't list existing resources: %w", err)
    }

    if len(existingResources) > 0 {
        log.Printf("Found %d existing resource(s):", len(existingResources))
        for _, resource := range existingResources {
            log.Printf("  - %s", *resource.{ResourceName})
        }

        useExisting := scenario.questioner.AskBool("Would you like to use an existing resource? (y/n)", "y")
        if useExisting {
            scenario.resourceId = *existingResources[0].{ResourceId}
            return nil
        }
    }

    // Create new resource as specified
    log.Println("Creating new resource...")
    resourceId, err := scenario.{service}Actions.CreateResource(ctx)
    if err != nil {
        return fmt.Errorf("couldn't create resource: %w", err)
    }
    
    scenario.resourceId = resourceId
    log.Printf("✓ Resource created successfully: %s", resourceId)
    return nil
}

// demonstrationPhase implements the demonstration phase based on specification.
func (scenario *{Service}Scenario) demonstrationPhase(ctx context.Context) error {
    log.Println("Demonstrating {AWS Service} capabilities...")
    log.Println()

    // Implement specific operations from specification
    // Example: Generate sample data if specified
    err := scenario.{service}Actions.CreateSampleData(ctx, scenario.resourceId)
    if err != nil {
        return fmt.Errorf("couldn't create sample data: %w", err)
    }
    log.Println("✓ Sample data created successfully")

    // Wait if specified in the specification
    log.Println("Waiting for data to be processed...")
    time.Sleep(5 * time.Second)

    return nil
}

// examinationPhase implements the examination phase based on specification.
func (scenario *{Service}Scenario) examinationPhase(ctx context.Context) error {
    log.Println("Examining {AWS Service} data...")
    log.Println()

    // List and examine data as specified
    dataItems, err := scenario.{service}Actions.ListData(ctx, scenario.resourceId)
    if err != nil {
        return fmt.Errorf("couldn't list data: %w", err)
    }

    if len(dataItems) == 0 {
        log.Println("No data found. Data may take a few minutes to appear.")
        return nil
    }

    log.Printf("Found %d data item(s)", len(dataItems))

    // Get detailed information as specified
    maxItems := 5
    if len(dataItems) < maxItems {
        maxItems = len(dataItems)
    }
    
    detailedData, err := scenario.{service}Actions.GetDataDetails(ctx, scenario.resourceId, dataItems[:maxItems])
    if err != nil {
        return fmt.Errorf("couldn't get data details: %w", err)
    }
    
    scenario.displayDataSummary(detailedData)

    // Show detailed view if specified
    if len(detailedData) > 0 {
        showDetails := scenario.questioner.AskBool("Would you like to see detailed information? (y/n)", "n")
        if showDetails {
            scenario.displayDataDetails(detailedData[0])
        }
    }

    // Filter data as specified
    scenario.filterDataByCriteria(dataItems)

    return nil
}

// cleanupPhase implements the cleanup phase based on specification.
func (scenario *{Service}Scenario) cleanupPhase(ctx context.Context) error {
    if scenario.resourceId == "" {
        return nil
    }

    log.Println("Cleanup options:")
    log.Println("Note: Deleting the resource will stop all monitoring/processing.")

    deleteResource := scenario.questioner.AskBool("Would you like to delete the resource? (y/n)", "n")

    if deleteResource {
        err := scenario.{service}Actions.DeleteResource(ctx, scenario.resourceId)
        if err != nil {
            return fmt.Errorf("couldn't delete resource: %w", err)
        }
        log.Printf("✓ Deleted resource: %s", scenario.resourceId)
    } else {
        log.Printf("Resource %s will continue running.", scenario.resourceId)
        log.Println("You can manage it through the AWS Console or delete it later.")
    }

    return nil
}

// displayDataSummary displays a summary of the data items.
func (scenario *{Service}Scenario) displayDataSummary(detailedData []{DetailedDataType}) {
    log.Println("\nData Summary:")
    for _, data := range detailedData {
        log.Printf("  • %s", *data.{SummaryField})
    }
}

// displayDataDetails displays detailed information about a data item.
func (scenario *{Service}Scenario) displayDataDetails(data {DetailedDataType}) {
    log.Println("\nDetailed Information:")
    log.Printf("  ID: %s", *data.{IdField})
    log.Printf("  Status: %s", *data.{StatusField})
    log.Printf("  Created: %s", *data.{CreatedField})
    // Add more fields as specified
}

// filterDataByCriteria filters data based on criteria from specification.
func (scenario *{Service}Scenario) filterDataByCriteria(dataItems []{DataItemType}) {
    log.Println("\nFiltering data by criteria...")
    // Implement filtering logic as specified in the specification
}
```

## Scenario Phase Structure (Based on Specification)

### Setup Phase
- **Read specification Setup section** for exact requirements
- Check for existing resources as specified
- Create necessary resources using actions methods
- Configure service settings per specification
- Verify setup completion as described

### Demonstration Phase  
- **Follow specification Demonstration section** exactly
- Perform core service operations using actions methods
- Generate sample data if specified in the specification
- Show service capabilities as outlined
- Provide educational context from specification

### Examination Phase
- **Implement specification Examination section** requirements
- List and examine results using actions methods
- Filter and analyze data as specified
- Display detailed information per specification format
- Allow user interaction as described in specification

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Offer cleanup options with warnings from specification
- Handle cleanup errors gracefully using actions methods
- Provide alternative management options as specified
- Confirm completion per specification

## User Interaction Patterns

### Question Types
```go
// Yes/No questions
useExisting := questioner.AskBool("Use existing resource? (y/n)", "y")

// Text input
resourceName := questioner.Ask("Enter resource name:")

// Choice selection
choice := questioner.AskChoice("Select an option:", []string{"Option 1", "Option 2"})
```

### Information Display
```go
// Progress indicators
log.Println("✓ Operation completed successfully")
log.Println("⚠ Warning message")
log.Println("✗ Error occurred")

// Formatted output
log.Println(strings.Repeat("-", 60))
log.Printf("Found %d items:", len(items))
for _, item := range items {
    log.Printf("  • %s", *item.Name)
}
```

## Specification-Based Error Handling

### Error Handling from Specification
The specification includes an "Errors" section with specific error codes and handling:

```go
// Example error handling based on specification
func (actions *{Service}Actions) CreateResource(ctx context.Context) (string, error) {
    response, err := actions.{Service}Client.CreateResource(ctx, &{service}.CreateResourceInput{
        // Parameters
    })
    if err != nil {
        var ae smithy.APIError
        if errors.As(err, &ae) {
            switch ae.ErrorCode() {
            case "BadRequestException":
                // Handle as specified: "Validate input parameters and notify user"
                return "", fmt.Errorf("invalid configuration, please check your parameters: %w", err)
            case "InternalServerErrorException":
                // Handle as specified: "Retry operation with exponential backoff"
                return "", fmt.Errorf("service temporarily unavailable, please retry: %w", err)
            default:
                return "", fmt.Errorf("unexpected error: %w", err)
            }
        }
        return "", fmt.Errorf("couldn't create resource: %w", err)
    }
    
    return *response.{ResourceId}, nil
}
```

## Scenario Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** include descriptive package and function comments explaining scenario steps from specification
- ✅ **ALWAYS** use demotools.IQuestioner for user interaction
- ✅ **ALWAYS** use service actions structs for all AWS operations
- ✅ **ALWAYS** implement proper cleanup in defer block
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification
- ✅ **ALWAYS** handle edge cases (no resources found, etc.) as specified
- ✅ **ALWAYS** use context.Context for all AWS operations

## Implementation Workflow

### Step-by-Step Implementation Process
1. **Read Specification**: Study `scenarios/basics/{service}/SPECIFICATION.md` thoroughly
2. **Extract API Actions**: Note all API actions listed in "API Actions Used" section
3. **Map to Actions Methods**: Ensure actions struct has methods for all required actions
4. **Implement Phases**: Follow the "Proposed example structure" section exactly
5. **Add Error Handling**: Implement error handling per the "Errors" section
6. **Test Against Specification**: Verify implementation matches specification requirements

### Specification Sections to Implement
- **API Actions Used**: All operations must be available in actions struct
- **Proposed example structure**: Direct mapping to scenario phases
- **Setup**: Exact setup steps and resource creation
- **Demonstration**: Specific operations to demonstrate
- **Examination**: Data analysis and filtering requirements
- **Cleanup**: Resource cleanup options and user choices
- **Errors**: Specific error codes and handling strategies

## Error Handling in Scenarios
- **Follow specification error table**: Implement exact error handling per specification
- Catch and display user-friendly error messages per specification guidance
- Continue scenario execution when possible as specified
- Provide guidance on resolving issues from specification
- Ensure cleanup runs even if errors occur

## Educational Elements
- **Use specification descriptions**: Explain operations using specification language
- Show before/after states as outlined in specification
- Provide context about service capabilities from specification
- Include tips and best practices mentioned in specification
- Follow the educational flow described in specification structure