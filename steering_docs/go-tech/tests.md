# Go Testing Standards

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Go-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Go-premium-KB", "Go testing patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive unit and integration tests for Go AWS SDK examples using the built-in Go testing framework and testtools for mocking.

## Requirements
- **Unit Tests**: Use `_test.go` suffix, test with mocked/stubbed responses
- **Integration Tests**: Use `_integ_test.go` suffix and `//go:build integration` tag
- **Test Framework**: Use built-in Go testing with `testing` package
- **Mocking**: Use `gov2/testtools` for stubbing AWS calls
- **Context**: ALWAYS use context.Context in tests
- **Table-Driven**: Use subtests with `t.Run()` for multiple test cases

## File Structure
```
gov2/{service}/actions/
├── {service}_basics_test.go        # Unit tests
└── {service}_basics_integ_test.go  # Integration tests

gov2/{service}/scenarios/
├── scenario_{name}_test.go         # Unit tests for scenarios
└── scenario_{name}_integ_test.go   # Integration tests for scenarios

gov2/{service}/stubs/
└── {service}_basics_stubs.go       # Test stubs for mocking
```

## Unit Test Pattern
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
    "context"
    "errors"
    "testing"

    "github.com/aws/aws-sdk-go-v2/service/{service}"
    "github.com/aws/aws-sdk-go-v2/service/{service}/types"
    "github.com/aws/smithy-go"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func TestListResources(t *testing.T) {
    tests := []struct {
        name           string
        stubFunc       func(*testtools.AwsmStubber)
        expectError    bool
        expectedCount  int
        expectedErrMsg string
    }{
        {
            name: "Success",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "List{Resources}",
                    Input: &{service}.List{Resources}Input{},
                    Output: &{service}.List{Resources}Output{
                        {Resources}: []types.{ResourceType}{
                            {
                                {ResourceName}: testtools.GetPtr("test-resource-1"),
                                {ResourceId}:   testtools.GetPtr("resource-1"),
                            },
                            {
                                {ResourceName}: testtools.GetPtr("test-resource-2"),
                                {ResourceId}:   testtools.GetPtr("resource-2"),
                            },
                        },
                    },
                })
            },
            expectError:   false,
            expectedCount: 2,
        },
        {
            name: "AccessDenied",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "List{Resources}",
                    Input: &{service}.List{Resources}Input{},
                    Error: &smithy.GenericAPIError{
                        Code:    "AccessDenied",
                        Message: "Access denied",
                    },
                })
            },
            expectError:    true,
            expectedErrMsg: "you don't have permission to list",
        },
        {
            name: "InternalServerError",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "List{Resources}",
                    Input: &{service}.List{Resources}Input{},
                    Error: &smithy.GenericAPIError{
                        Code:    "InternalServerError",
                        Message: "Internal server error",
                    },
                })
            },
            expectError:    true,
            expectedErrMsg: "service temporarily unavailable",
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            ctx := context.Background()
            stubber := testtools.NewStubber()
            
            tt.stubFunc(stubber)
            
            actions := &{Service}Actions{
                {Service}Client: {service}.NewFromConfig(aws.Config{}, func(o *{service}.Options) {
                    o.HTTPClient = stubber
                }),
            }
            
            resources, err := actions.ListResources(ctx)
            
            if tt.expectError {
                if err == nil {
                    t.Errorf("Expected error but got none")
                }
                if tt.expectedErrMsg != "" && !strings.Contains(err.Error(), tt.expectedErrMsg) {
                    t.Errorf("Expected error message to contain '%s', got '%s'", tt.expectedErrMsg, err.Error())
                }
            } else {
                if err != nil {
                    t.Errorf("Unexpected error: %v", err)
                }
                if len(resources) != tt.expectedCount {
                    t.Errorf("Expected %d resources, got %d", tt.expectedCount, len(resources))
                }
            }
            
            testtools.VerifyStubsCalled(t, stubber)
        })
    }
}

func TestCreateResource(t *testing.T) {
    tests := []struct {
        name           string
        resourceName   string
        stubFunc       func(*testtools.AwsmStubber)
        expectError    bool
        expectedId     string
        expectedErrMsg string
    }{
        {
            name:         "Success",
            resourceName: "test-resource",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "Create{Resource}",
                    Input: &{service}.Create{Resource}Input{
                        {ResourceName}: testtools.GetPtr("test-resource"),
                    },
                    Output: &{service}.Create{Resource}Output{
                        {ResourceId}: testtools.GetPtr("resource-123"),
                    },
                })
            },
            expectError: false,
            expectedId:  "resource-123",
        },
        {
            name:         "ResourceAlreadyExists",
            resourceName: "existing-resource",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "Create{Resource}",
                    Input: &{service}.Create{Resource}Input{
                        {ResourceName}: testtools.GetPtr("existing-resource"),
                    },
                    Error: &smithy.GenericAPIError{
                        Code:    "ResourceAlreadyExistsException",
                        Message: "Resource already exists",
                    },
                })
            },
            expectError:    true,
            expectedErrMsg: "already exists",
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            ctx := context.Background()
            stubber := testtools.NewStubber()
            
            tt.stubFunc(stubber)
            
            actions := &{Service}Actions{
                {Service}Client: {service}.NewFromConfig(aws.Config{}, func(o *{service}.Options) {
                    o.HTTPClient = stubber
                }),
            }
            
            resourceId, err := actions.CreateResource(ctx, tt.resourceName)
            
            if tt.expectError {
                if err == nil {
                    t.Errorf("Expected error but got none")
                }
                if tt.expectedErrMsg != "" && !strings.Contains(err.Error(), tt.expectedErrMsg) {
                    t.Errorf("Expected error message to contain '%s', got '%s'", tt.expectedErrMsg, err.Error())
                }
            } else {
                if err != nil {
                    t.Errorf("Unexpected error: %v", err)
                }
                if resourceId != tt.expectedId {
                    t.Errorf("Expected resource ID '%s', got '%s'", tt.expectedId, resourceId)
                }
            }
            
            testtools.VerifyStubsCalled(t, stubber)
        })
    }
}

func TestDeleteResource(t *testing.T) {
    tests := []struct {
        name           string
        resourceId     string
        stubFunc       func(*testtools.AwsmStubber)
        expectError    bool
        expectedErrMsg string
    }{
        {
            name:       "Success",
            resourceId: "resource-123",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "Delete{Resource}",
                    Input: &{service}.Delete{Resource}Input{
                        {ResourceId}: testtools.GetPtr("resource-123"),
                    },
                    Output: &{service}.Delete{Resource}Output{},
                })
            },
            expectError: false,
        },
        {
            name:       "ResourceNotFound",
            resourceId: "nonexistent-resource",
            stubFunc: func(stubber *testtools.AwsmStubber) {
                stubber.Add(testtools.Stub{
                    OperationName: "Delete{Resource}",
                    Input: &{service}.Delete{Resource}Input{
                        {ResourceId}: testtools.GetPtr("nonexistent-resource"),
                    },
                    Error: &smithy.GenericAPIError{
                        Code:    "ResourceNotFoundException",
                        Message: "Resource not found",
                    },
                })
            },
            expectError:    true,
            expectedErrMsg: "not found",
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            ctx := context.Background()
            stubber := testtools.NewStubber()
            
            tt.stubFunc(stubber)
            
            actions := &{Service}Actions{
                {Service}Client: {service}.NewFromConfig(aws.Config{}, func(o *{service}.Options) {
                    o.HTTPClient = stubber
                }),
            }
            
            err := actions.DeleteResource(ctx, tt.resourceId)
            
            if tt.expectError {
                if err == nil {
                    t.Errorf("Expected error but got none")
                }
                if tt.expectedErrMsg != "" && !strings.Contains(err.Error(), tt.expectedErrMsg) {
                    t.Errorf("Expected error message to contain '%s', got '%s'", tt.expectedErrMsg, err.Error())
                }
            } else {
                if err != nil {
                    t.Errorf("Unexpected error: %v", err)
                }
            }
            
            testtools.VerifyStubsCalled(t, stubber)
        })
    }
}
```

## Integration Test Pattern
```go
//go:build integration

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
    "context"
    "testing"
    "time"

    "github.com/aws/aws-sdk-go-v2/config"
    "github.com/aws/aws-sdk-go-v2/service/{service}"
)

func TestListResourcesInteg(t *testing.T) {
    ctx := context.Background()
    
    sdkConfig, err := config.LoadDefaultConfig(ctx)
    if err != nil {
        t.Fatalf("Couldn't load configuration: %v", err)
    }
    
    {service}Client := {service}.NewFromConfig(sdkConfig)
    actions := &{Service}Actions{
        {Service}Client: {service}Client,
    }
    
    resources, err := actions.ListResources(ctx)
    if err != nil {
        t.Errorf("Couldn't list resources: %v", err)
    }
    
    // Verify we can list resources without error
    // Don't assert specific counts as they may vary
    t.Logf("Found %d resource(s)", len(resources))
}

func TestCreateAndDeleteResourceInteg(t *testing.T) {
    ctx := context.Background()
    
    sdkConfig, err := config.LoadDefaultConfig(ctx)
    if err != nil {
        t.Fatalf("Couldn't load configuration: %v", err)
    }
    
    {service}Client := {service}.NewFromConfig(sdkConfig)
    actions := &{Service}Actions{
        {Service}Client: {service}Client,
    }
    
    // Create a test resource
    resourceName := fmt.Sprintf("test-resource-%d", time.Now().Unix())
    resourceId, err := actions.CreateResource(ctx, resourceName)
    if err != nil {
        t.Fatalf("Couldn't create resource: %v", err)
    }
    
    // Ensure cleanup
    defer func() {
        err := actions.DeleteResource(ctx, resourceId)
        if err != nil {
            t.Logf("Couldn't delete resource %s: %v", resourceId, err)
        }
    }()
    
    // Verify resource was created
    resource, err := actions.GetResource(ctx, resourceId)
    if err != nil {
        t.Errorf("Couldn't get created resource: %v", err)
    }
    
    if *resource.{ResourceName} != resourceName {
        t.Errorf("Expected resource name '%s', got '%s'", resourceName, *resource.{ResourceName})
    }
    
    // Test update if applicable
    updates := map[string]interface{}{
        "description": "Updated description",
    }
    err = actions.UpdateResource(ctx, resourceId, updates)
    if err != nil {
        t.Errorf("Couldn't update resource: %v", err)
    }
    
    // Verify update
    updatedResource, err := actions.GetResource(ctx, resourceId)
    if err != nil {
        t.Errorf("Couldn't get updated resource: %v", err)
    }
    
    // Verify the update took effect (adjust based on actual service)
    if *updatedResource.{DescriptionField} != "Updated description" {
        t.Errorf("Resource update didn't take effect")
    }
}
```

## Scenario Test Pattern
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package scenarios

import (
    "context"
    "strings"
    "testing"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/{service}"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func TestRunScenario(t *testing.T) {
    ctx := context.Background()
    stubber := testtools.NewStubber()
    
    // Set up stubs for the entire scenario
    setupScenarioStubs(stubber)
    
    // Create mock questioner with predefined answers
    mockQuestioner := &demotools.MockQuestioner{
        Answers: []string{"n", "y", "n"}, // Responses to questions in order
    }
    
    scenario := {Service}Scenario{
        {service}Actions: &actions.{Service}Actions{
            {Service}Client: {service}.NewFromConfig(aws.Config{}, func(o *{service}.Options) {
                o.HTTPClient = stubber
            }),
        },
        questioner: mockQuestioner,
    }
    
    // Run the scenario
    scenario.Run(ctx)
    
    // Verify all stubs were called
    testtools.VerifyStubsCalled(t, stubber)
}

func setupScenarioStubs(stubber *testtools.AwsmStubber) {
    // Add stubs for all operations in the scenario
    stubber.Add(testtools.Stub{
        OperationName: "List{Resources}",
        Input: &{service}.List{Resources}Input{},
        Output: &{service}.List{Resources}Output{
            {Resources}: []types.{ResourceType}{},
        },
    })
    
    stubber.Add(testtools.Stub{
        OperationName: "Create{Resource}",
        Input: &{service}.Create{Resource}Input{
            {ResourceName}: testtools.GetPtr("test-resource"),
        },
        Output: &{service}.Create{Resource}Output{
            {ResourceId}: testtools.GetPtr("test-resource-id"),
        },
    })
    
    // Add more stubs as needed for the complete scenario
}
```

## Test Stubs Pattern
```go
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package stubs defines service operation stubs that are used by unit tests to
// mock service operations.
package stubs

import (
    "github.com/aws/aws-sdk-go-v2/service/{service}"
    "github.com/aws/aws-sdk-go-v2/service/{service}/types"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

// StubList{Resources} builds a stub for the List{Resources} operation.
func StubList{Resources}(resources []types.{ResourceType}) testtools.Stub {
    return testtools.Stub{
        OperationName: "List{Resources}",
        Input: &{service}.List{Resources}Input{},
        Output: &{service}.List{Resources}Output{
            {Resources}: resources,
        },
    }
}

// StubList{Resources}Error builds a stub for the List{Resources} operation that returns an error.
func StubList{Resources}Error(errorCode string, errorMessage string) testtools.Stub {
    return testtools.Stub{
        OperationName: "List{Resources}",
        Input: &{service}.List{Resources}Input{},
        Error: &smithy.GenericAPIError{
            Code:    errorCode,
            Message: errorMessage,
        },
    }
}

// StubCreate{Resource} builds a stub for the Create{Resource} operation.
func StubCreate{Resource}(resourceName string, resourceId string) testtools.Stub {
    return testtools.Stub{
        OperationName: "Create{Resource}",
        Input: &{service}.Create{Resource}Input{
            {ResourceName}: testtools.GetPtr(resourceName),
        },
        Output: &{service}.Create{Resource}Output{
            {ResourceId}: testtools.GetPtr(resourceId),
        },
    }
}

// StubCreate{Resource}Error builds a stub for the Create{Resource} operation that returns an error.
func StubCreate{Resource}Error(resourceName string, errorCode string, errorMessage string) testtools.Stub {
    return testtools.Stub{
        OperationName: "Create{Resource}",
        Input: &{service}.Create{Resource}Input{
            {ResourceName}: testtools.GetPtr(resourceName),
        },
        Error: &smithy.GenericAPIError{
            Code:    errorCode,
            Message: errorMessage,
        },
    }
}

// StubDelete{Resource} builds a stub for the Delete{Resource} operation.
func StubDelete{Resource}(resourceId string) testtools.Stub {
    return testtools.Stub{
        OperationName: "Delete{Resource}",
        Input: &{service}.Delete{Resource}Input{
            {ResourceId}: testtools.GetPtr(resourceId),
        },
        Output: &{service}.Delete{Resource}Output{},
    }
}

// StubDelete{Resource}Error builds a stub for the Delete{Resource} operation that returns an error.
func StubDelete{Resource}Error(resourceId string, errorCode string, errorMessage string) testtools.Stub {
    return testtools.Stub{
        OperationName: "Delete{Resource}",
        Input: &{service}.Delete{Resource}Input{
            {ResourceId}: testtools.GetPtr(resourceId),
        },
        Error: &smithy.GenericAPIError{
            Code:    errorCode,
            Message: errorMessage,
        },
    }
}
```

## Testing Requirements
- ✅ **ALWAYS** use table-driven tests with `t.Run()` for multiple test cases
- ✅ **ALWAYS** test both success and error scenarios
- ✅ **ALWAYS** use `testtools.NewStubber()` for mocking AWS calls in unit tests
- ✅ **ALWAYS** verify stubs were called with `testtools.VerifyStubsCalled()`
- ✅ **ALWAYS** use context.Context in all test functions
- ✅ **ALWAYS** include integration tests with `//go:build integration` tag
- ✅ **ALWAYS** clean up resources in integration tests using defer
- ✅ **ALWAYS** test error handling paths
- ✅ **ALWAYS** use meaningful test names that describe the scenario

## Common Test Patterns
- Use `testtools.GetPtr()` for creating pointers to values in stubs
- Test all error codes that your actions handle
- Use mock questioner for scenario tests
- Include cleanup in integration tests
- Test pagination in list operations
- Verify error messages contain expected text
- Use subtests for organizing related test cases
- Test context cancellation where applicable