# Rust Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Rust-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Rust-premium-KB", "Rust implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate test suites using Rust's built-in testing framework and mockall for mocking. Tests validate scenario workflows and individual operations.

## Requirements
- **Unit Tests**: Use `#[cfg(test)] mod test` or separate `test.rs` files
- **Integration Tests**: Place in `tests/` folder within scenario modules
- **Mocking**: Use mockall crate for mocking AWS service calls
- **Async Testing**: Use `#[tokio::test]` for async test functions
- **Coverage**: Use cargo-llvm-cov for code coverage

## File Structure
```
rustv1/examples/{service}/
├── src/
│   ├── {service}.rs              # Service wrapper with mockall
│   ├── {scenario_name}/
│   │   ├── mod.rs
│   │   ├── scenario.rs           # Scenario logic
│   │   └── tests/
│   │       └── mod.rs            # Integration tests
│   └── bin/
│       └── {action}.rs
└── Cargo.toml
```

## Test Dependencies in Cargo.toml
```toml
[dev-dependencies]
mockall = "0.12"
tokio-test = "0.4"

[dependencies]
tokio = { version = "1.0", features = ["full", "test-util"] }
```

## Unit Test Pattern (In Source File)
```rust
// In src/{scenario_name}/scenario.rs

#[cfg(test)]
mod tests {
    use super::*;
    use crate::{service}::Mock{Service}Impl;
    use mockall::predicate::*;

    #[tokio::test]
    async fn test_setup_phase_success() {
        let mut mock_{service} = Mock{Service}Impl::default();

        // Setup mock expectations
        mock_{service}
            .expect_list_resources()
            .return_once(|| Ok(vec![]));

        mock_{service}
            .expect_create_resource()
            .with(eq("test-resource"))
            .return_once(|_| Ok("resource-123".to_string()));

        // Test the function
        let result = setup_phase(&mock_{service}, "test-resource").await;
        
        assert!(result.is_ok());
        assert_eq!(result.unwrap(), "resource-123");
    }

    #[tokio::test]
    async fn test_setup_phase_error() {
        let mut mock_{service} = Mock{Service}Impl::default();

        mock_{service}
            .expect_list_resources()
            .return_once(|| Err(ScenarioError::with("Failed to list resources")));

        let result = setup_phase(&mock_{service}, "test-resource").await;
        
        assert!(result.is_err());
    }
}
```

## Integration Test Pattern (In tests/ folder)
```rust
// In src/{scenario_name}/tests/mod.rs

use crate::{service}::Mock{Service}Impl;
use crate::{scenario_name}::{run_scenario, ScenarioError};
use mockall::predicate::*;

#[tokio::test]
async fn test_scenario_complete_workflow() {
    let mut mock_{service} = Mock{Service}Impl::default();

    // Setup expectations for entire scenario
    mock_{service}
        .expect_list_resources()
        .return_once(|| Ok(vec![]));

    mock_{service}
        .expect_create_resource()
        .with(eq("test-resource"))
        .return_once(|_| Ok("resource-123".to_string()));

    mock_{service}
        .expect_generate_sample_data()
        .with(eq("resource-123"))
        .return_once(|_| Ok(()));

    mock_{service}
        .expect_list_data()
        .with(eq("resource-123"))
        .return_once(|_| Ok(vec!["data1".to_string(), "data2".to_string()]));

    mock_{service}
        .expect_delete_resource()
        .with(eq("resource-123"))
        .return_once(|_| Ok(()));

    // Run the scenario
    let result = run_scenario(mock_{service}).await;
    
    assert!(result.is_ok());
}

#[tokio::test]
async fn test_scenario_handles_errors() {
    let mut mock_{service} = Mock{Service}Impl::default();

    mock_{service}
        .expect_list_resources()
        .return_once(|| Err(ScenarioError::with("Service unavailable")));

    let result = run_scenario(mock_{service}).await;
    
    assert!(result.is_err());
}
```

## Mock Setup Patterns

### Basic Mock Expectation
```rust
mock_{service}
    .expect_operation()
    .return_once(|| Ok(expected_result));
```

### Mock with Parameter Matching
```rust
mock_{service}
    .expect_operation()
    .with(eq("expected-param"))
    .return_once(|_| Ok(expected_result));
```

### Mock with Multiple Parameters
```rust
mock_{service}
    .expect_operation()
    .with(eq("param1"), eq("param2"))
    .return_once(|_, _| Ok(expected_result));
```

### Mock Called Multiple Times
```rust
mock_{service}
    .expect_operation()
    .times(3)
    .returning(|| Ok(expected_result));
```

### Mock with Different Return Values
```rust
mock_{service}
    .expect_operation()
    .times(2)
    .returning(|| Ok(expected_result))
    .expect_operation()
    .times(1)
    .returning(|| Err(ScenarioError::with("Error on third call")));
```

## Service Wrapper with Mockall
```rust
// In src/{service}.rs

use aws_sdk_{service}::Client as {Service}Client;

#[cfg(test)]
use mockall::automock;

#[cfg(test)]
pub use Mock{Service}Impl as {Service};
#[cfg(not(test))]
pub use {Service}Impl as {Service};

pub struct {Service}Impl {
    pub inner: {Service}Client,
}

#[cfg_attr(test, automock)]
impl {Service}Impl {
    pub fn new(inner: {Service}Client) -> Self {
        {Service}Impl { inner }
    }

    pub async fn list_resources(&self) -> Result<Vec<String>, ScenarioError> {
        let response = self.inner
            .list_resources()
            .send()
            .await
            .map_err(|e| ScenarioError::new("Failed to list resources", &e))?;

        Ok(response
            .resources()
            .iter()
            .filter_map(|r| r.name())
            .map(String::from)
            .collect())
    }

    pub async fn create_resource(&self, name: &str) -> Result<String, ScenarioError> {
        let response = self.inner
            .create_resource()
            .name(name)
            .send()
            .await
            .map_err(|e| ScenarioError::new("Failed to create resource", &e))?;

        Ok(response.resource_id().unwrap_or("").to_string())
    }
}
```

## Testing Async Operations

### Basic Async Test
```rust
#[tokio::test]
async fn test_async_operation() {
    let result = async_function().await;
    assert!(result.is_ok());
}
```

### Async Test with Timeout
```rust
#[tokio::test(flavor = "multi_thread")]
async fn test_with_timeout() {
    let result = tokio::time::timeout(
        std::time::Duration::from_secs(5),
        async_function()
    ).await;
    
    assert!(result.is_ok());
}
```

### Testing Concurrent Operations
```rust
#[tokio::test]
async fn test_concurrent_operations() {
    let handle1 = tokio::spawn(async { operation1().await });
    let handle2 = tokio::spawn(async { operation2().await });
    
    let (result1, result2) = tokio::join!(handle1, handle2);
    
    assert!(result1.is_ok());
    assert!(result2.is_ok());
}
```

## Error Testing Patterns

### Testing Expected Errors
```rust
#[tokio::test]
async fn test_error_handling() {
    let mut mock_{service} = Mock{Service}Impl::default();

    mock_{service}
        .expect_operation()
        .return_once(|| Err(ScenarioError::with("Expected error")));

    let result = function_under_test(&mock_{service}).await;
    
    assert!(result.is_err());
    assert_eq!(
        result.unwrap_err().to_string(),
        "Expected error"
    );
}
```

### Testing Error Recovery
```rust
#[tokio::test]
async fn test_error_recovery() {
    let mut mock_{service} = Mock{Service}Impl::default();

    // First call fails
    mock_{service}
        .expect_operation()
        .times(1)
        .return_once(|| Err(ScenarioError::with("Temporary error")));

    // Second call succeeds
    mock_{service}
        .expect_operation()
        .times(1)
        .return_once(|| Ok(expected_result));

    let result = retry_function(&mock_{service}).await;
    
    assert!(result.is_ok());
}
```

## Test Organization

### Unit Tests in Source Files
```rust
// At the bottom of src/{scenario_name}/scenario.rs

#[cfg(test)]
mod tests {
    use super::*;

    #[tokio::test]
    async fn test_function_one() {
        // Test implementation
    }

    #[tokio::test]
    async fn test_function_two() {
        // Test implementation
    }
}
```

### Integration Tests in Separate Module
```rust
// In src/{scenario_name}/tests/mod.rs

use crate::{service}::Mock{Service}Impl;
use crate::{scenario_name}::*;

#[tokio::test]
async fn integration_test_one() {
    // Test implementation
}

#[tokio::test]
async fn integration_test_two() {
    // Test implementation
}
```

## Code Coverage

### Running Tests with Coverage
```bash
# Install cargo-llvm-cov
cargo install cargo-llvm-cov

# Run tests with coverage
cargo llvm-cov --all-features --workspace

# Generate HTML report
cargo llvm-cov --all-features --workspace --html

# Generate lcov report for CI
cargo llvm-cov --all-features --workspace --lcov --output-path lcov.info
```

### Coverage Configuration in Cargo.toml
```toml
[package.metadata.coverage]
exclude = [
    "src/bin/*",  # Exclude binary files from coverage
]
```

## Test Execution Commands

### Run All Tests
```bash
cargo test
```

### Run Tests for Specific Module
```bash
cargo test --lib {module_name}
```

### Run Integration Tests Only
```bash
cargo test --test '*'
```

### Run Tests with Output
```bash
cargo test -- --nocapture
```

### Run Tests in Parallel
```bash
cargo test -- --test-threads=4
```

### Run Specific Test
```bash
cargo test test_function_name
```

## Test Requirements Checklist
- ✅ **Unit tests** for individual functions in `#[cfg(test)] mod tests`
- ✅ **Integration tests** for complete scenarios in `tests/` folder
- ✅ **Mock setup** using mockall for AWS service calls
- ✅ **Async testing** with `#[tokio::test]` attribute
- ✅ **Error testing** for expected error conditions
- ✅ **Coverage** using cargo-llvm-cov
- ✅ **Documentation** for test purpose and setup

## Common Test Patterns

### Testing Pagination
```rust
#[tokio::test]
async fn test_pagination() {
    let mut mock_{service} = Mock{Service}Impl::default();

    mock_{service}
        .expect_list_items_paginated()
        .return_once(|| Ok(vec![
            "item1".to_string(),
            "item2".to_string(),
            "item3".to_string(),
        ]));

    let result = list_all_items(&mock_{service}).await;
    
    assert!(result.is_ok());
    assert_eq!(result.unwrap().len(), 3);
}
```

### Testing Waiters
```rust
#[tokio::test]
async fn test_waiter_success() {
    let mut mock_{service} = Mock{Service}Impl::default();

    mock_{service}
        .expect_wait_for_ready()
        .with(eq("resource-123"))
        .return_once(|_| Ok(()));

    let result = wait_for_resource(&mock_{service}, "resource-123").await;
    
    assert!(result.is_ok());
}
```

### Testing Cleanup
```rust
#[tokio::test]
async fn test_cleanup_always_runs() {
    let mut mock_{service} = Mock{Service}Impl::default();

    // Setup fails
    mock_{service}
        .expect_create_resource()
        .return_once(|_| Err(ScenarioError::with("Creation failed")));

    // Cleanup should still be called
    mock_{service}
        .expect_delete_resource()
        .times(0);  // Not called because resource wasn't created

    let result = run_scenario(mock_{service}).await;
    
    assert!(result.is_err());
}
```

## Best Practices
- ✅ **Test one thing per test**: Keep tests focused and simple
- ✅ **Use descriptive test names**: Clearly indicate what is being tested
- ✅ **Mock external dependencies**: Use mockall for AWS service calls
- ✅ **Test error paths**: Don't just test happy paths
- ✅ **Use async tests**: Always use `#[tokio::test]` for async functions
- ✅ **Verify mock expectations**: Ensure mocks are called as expected
- ✅ **Clean up resources**: Even in tests, practice good resource management
- ✅ **Document test purpose**: Add comments explaining complex test setups
- ✅ **Run tests regularly**: Integrate into development workflow
- ✅ **Measure coverage**: Use cargo-llvm-cov to track test coverage

## Common Testing Mistakes to Avoid
- ❌ **Not using #[tokio::test]** for async tests
- ❌ **Forgetting to set mock expectations** before calling functions
- ❌ **Not testing error conditions** and edge cases
- ❌ **Creating overly complex test setups** that are hard to maintain
- ❌ **Not cleaning up test resources** properly
- ❌ **Testing implementation details** instead of behavior
- ❌ **Ignoring test failures** or flaky tests
- ❌ **Not running tests before committing** code changes
