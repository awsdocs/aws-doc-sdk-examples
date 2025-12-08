# Rust Service Wrapper Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Rust-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Rust-premium-KB", "Rust implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate service wrapper modules that encapsulate AWS service functionality for integration test mocking when necessary. Wrappers are optional and only needed when mocking is required for testing.

## Requirements
- **Optional**: Only create wrappers when integration test mocking is needed
- **Mockable**: Use mockall crate for test mocking support
- **Async Operations**: All AWS operations must be async
- **Error Handling**: Return Result types with appropriate error types
- **Trait-Based**: Use traits for mockability (only when needed)

## When to Create a Wrapper

### Create Wrapper When:
- ✅ **Integration tests need mocking** for complex scenarios
- ✅ **Multiple operations need coordination** in a testable way
- ✅ **Scenario has complex state management** requiring testing

### Skip Wrapper When:
- ❌ **Simple one-off scripts** or single-action examples
- ❌ **Hello examples** (use client directly)
- ❌ **No testing requirements** for the example

## File Structure
```
rustv1/examples/{service}/
├── src/
│   ├── lib.rs                    # Module exports
│   ├── {service}.rs              # Service wrapper (if needed)
│   └── bin/
│       └── {action}.rs           # Action examples
```

## Wrapper Module Pattern (src/{service}.rs)
```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_sdk_{service}::{
    Client as {Service}Client,
    operation::{
        operation_one::OperationOneOutput,
        operation_two::OperationTwoOutput,
    },
    error::SdkError,
};

#[cfg(test)]
use mockall::automock;

// Use the mock in tests, real implementation in production
#[cfg(test)]
pub use Mock{Service}Impl as {Service};
#[cfg(not(test))]
pub use {Service}Impl as {Service};

/// Wrapper for {Service} operations to enable mocking in tests
pub struct {Service}Impl {
    pub inner: {Service}Client,
}

#[cfg_attr(test, automock)]
impl {Service}Impl {
    /// Create a new {Service} wrapper
    pub fn new(inner: {Service}Client) -> Self {
        {Service}Impl { inner }
    }

    /// Perform operation one
    pub async fn operation_one(
        &self,
        param: &str,
    ) -> Result<OperationOneOutput, SdkError<OperationOneError>> {
        self.inner
            .operation_one()
            .parameter(param)
            .send()
            .await
    }

    /// Perform operation two with pagination
    pub async fn operation_two_paginated(
        &self,
        param: &str,
    ) -> Result<Vec<Item>, SdkError<OperationTwoError>> {
        let mut items = Vec::new();
        let mut paginator = self.inner
            .operation_two()
            .parameter(param)
            .into_paginator()
            .send();

        while let Some(page) = paginator.next().await {
            let page = page?;
            items.extend(page.items().to_vec());
        }

        Ok(items)
    }
}
```

## Wrapper Without Mocking (Simple Cases)
For simpler cases where mocking isn't needed, use bare functions:

```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_sdk_{service}::Client;

/// List all resources
pub async fn list_resources(
    client: &Client,
) -> Result<Vec<String>, aws_sdk_{service}::Error> {
    let mut resources = Vec::new();
    let mut paginator = client
        .list_resources()
        .into_paginator()
        .send();

    while let Some(page) = paginator.next().await {
        let page = page?;
        resources.extend(
            page.resources()
                .iter()
                .filter_map(|r| r.name())
                .map(String::from)
        );
    }

    Ok(resources)
}

/// Create a resource
pub async fn create_resource(
    client: &Client,
    name: &str,
) -> Result<String, aws_sdk_{service}::Error> {
    let response = client
        .create_resource()
        .name(name)
        .send()
        .await?;

    Ok(response.resource_id().unwrap_or("").to_string())
}
```

## Pagination Patterns

### Using Paginator with while let
```rust
pub async fn list_all_items(
    &self,
) -> Result<Vec<Item>, SdkError<ListItemsError>> {
    let mut items = Vec::new();
    let mut paginator = self.inner
        .list_items()
        .into_paginator()
        .send();

    while let Some(page) = paginator.next().await {
        let page = page?;
        items.extend(page.items().to_vec());
    }

    Ok(items)
}
```

### Using Paginator with collect
```rust
pub async fn list_all_items(
    &self,
) -> Result<Vec<Item>, SdkError<ListItemsError>> {
    let items: Result<Vec<_>, _> = self.inner
        .list_items()
        .into_paginator()
        .items()
        .send()
        .collect()
        .await;

    items
}
```

### Paginator with Limit
```rust
pub async fn list_items_limited(
    &self,
    page_size: i32,
) -> Result<Vec<Item>, SdkError<ListItemsError>> {
    let items: Result<Vec<_>, _> = self.inner
        .list_items()
        .limit(page_size)
        .into_paginator()
        .items()
        .send()
        .collect()
        .await;

    items
}
```

## Error Handling Patterns

### Using Client::Error for Simple Cases
```rust
use aws_sdk_{service}::{Client, Error};

pub async fn simple_operation(client: &Client) -> Result<(), Error> {
    let response = client.operation().send().await?;
    println!("Operation successful");
    Ok(())
}
```

### Using SdkError for Detailed Error Handling
```rust
use aws_sdk_{service}::error::SdkError;
use aws_sdk_{service}::operation::operation_name::OperationNameError;

pub async fn detailed_operation(
    &self,
) -> Result<Output, SdkError<OperationNameError>> {
    match self.inner.operation().send().await {
        Ok(response) => Ok(response),
        Err(e) => {
            if let Some(service_err) = e.as_service_error() {
                // Handle specific service errors
                eprintln!("Service error: {:?}", service_err);
            }
            Err(e)
        }
    }
}
```

### Using into_service_error for Error Matching
```rust
use aws_sdk_{service}::operation::operation_name::OperationNameError;

pub async fn handle_specific_errors(
    &self,
) -> Result<Output, Box<dyn std::error::Error>> {
    match self.inner.operation().send().await {
        Ok(response) => Ok(response),
        Err(e) => {
            match e.into_service_error() {
                OperationNameError::ResourceNotFoundException(_) => {
                    eprintln!("Resource not found");
                }
                OperationNameError::ValidationException(_) => {
                    eprintln!("Invalid parameters");
                }
                e => {
                    eprintln!("Other error: {:?}", e);
                }
            }
            Err(Box::new(e))
        }
    }
}
```

## Waiter Patterns

### Using Built-in Waiters
```rust
use std::time::Duration;

pub async fn wait_for_resource_ready(
    &self,
    resource_id: &str,
) -> Result<(), Box<dyn std::error::Error>> {
    let wait_result = self.inner
        .wait_until_resource_ready()
        .resource_id(resource_id)
        .wait(Duration::from_secs(60))
        .await;

    match wait_result {
        Ok(_) => {
            println!("Resource {} is ready", resource_id);
            Ok(())
        }
        Err(err) => {
            eprintln!("Error waiting for resource: {:?}", err);
            Err(Box::new(err))
        }
    }
}
```

### Custom Polling When No Waiter Available
```rust
use tokio::time::{sleep, Duration};

pub async fn poll_until_ready(
    &self,
    resource_id: &str,
    max_attempts: u32,
) -> Result<(), Box<dyn std::error::Error>> {
    for attempt in 1..=max_attempts {
        let status = self.get_resource_status(resource_id).await?;
        
        if status == "READY" {
            println!("Resource ready after {} attempts", attempt);
            return Ok(());
        }
        
        println!("Attempt {}/{}: Status is {}, waiting...", attempt, max_attempts, status);
        sleep(Duration::from_secs(5)).await;
    }
    
    Err("Resource did not become ready in time".into())
}
```

## Module Organization in lib.rs
```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! AWS SDK for Rust code examples for {Service}

// Export wrapper module if it exists
pub mod {service};

// Export scenario modules
pub mod scenario_name;

// Re-export commonly used types
pub use scenario_name::{run_scenario, ScenarioError};
```

## When to Use Traits vs Bare Functions

### Use Traits When:
- ✅ **Mocking is required** for integration tests
- ✅ **Multiple implementations** are needed
- ✅ **Complex state management** requires abstraction

### Use Bare Functions When:
- ✅ **Simple operations** without state
- ✅ **No mocking needed** for tests
- ✅ **One-off scripts** or examples
- ✅ **Minimal complexity** is desired

## Testing with Mocks

### Mock Setup in Tests
```rust
#[cfg(test)]
mod tests {
    use super::*;
    use mockall::predicate::*;

    #[tokio::test]
    async fn test_operation_success() {
        let mut mock_{service} = Mock{Service}Impl::default();

        mock_{service}
            .expect_operation_one()
            .with(eq("test-param"))
            .return_once(|_| {
                Ok(OperationOneOutput::builder().build())
            });

        let result = mock_{service}.operation_one("test-param").await;
        assert!(result.is_ok());
    }

    #[tokio::test]
    async fn test_operation_error() {
        let mut mock_{service} = Mock{Service}Impl::default();

        mock_{service}
            .expect_operation_one()
            .with(eq("invalid"))
            .return_once(|_| {
                Err(SdkError::service_error(
                    OperationOneError::ValidationException(
                        ValidationException::builder().build()
                    ),
                    Response::new(StatusCode::try_from(400).unwrap(), SdkBody::empty())
                ))
            });

        let result = mock_{service}.operation_one("invalid").await;
        assert!(result.is_err());
    }
}
```

## Wrapper Requirements
- ✅ **Only create when needed** for mocking or complex coordination
- ✅ **Use mockall** for test mocking when wrapper is needed
- ✅ **Include comprehensive documentation** for all public functions
- ✅ **Use async/await** pattern for all AWS operations
- ✅ **Return appropriate Result types** with proper error types
- ✅ **Use pagination** for list operations when available
- ✅ **Implement waiters** when available or custom polling when needed
- ✅ **Keep it simple** - prefer bare functions over traits when possible

## Function Naming Conventions
- Use snake_case for all function names
- Match AWS API operation names in snake_case (e.g., `list_buckets` for `ListBuckets`)
- Use descriptive parameter names
- Return appropriate Result types

## Documentation Requirements
- Include module-level documentation explaining wrapper purpose
- Document all public functions with `///` doc comments
- Document parameters and return values
- Include usage examples in documentation where helpful
- Document error conditions and handling

## Cargo.toml Dependencies
```toml
[dependencies]
aws-config = { version = "1.0", features = ["behavior-version-latest"] }
aws-sdk-{service} = "1.0"
tokio = { version = "1.0", features = ["full"] }

[dev-dependencies]
mockall = "0.12"
```

## Best Practices
- ✅ **Prefer simplicity**: Use bare functions unless mocking is needed
- ✅ **Use pagination**: Always use paginators for list operations
- ✅ **Handle errors appropriately**: Choose error type based on use case
- ✅ **Use waiters**: Prefer built-in waiters over custom polling
- ✅ **Document thoroughly**: Explain purpose and usage
- ✅ **Keep functions focused**: One operation per function
- ✅ **Avoid unnecessary abstraction**: Don't create traits unless needed
