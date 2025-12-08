# Rust Hello Examples Generation

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
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct AWS SDK for Rust client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use AWS SDK for Rust client directly
- **Async**: Use tokio runtime for async operations
- **Pagination**: Use paginators when available for list operations

## File Structure
```
rustv1/examples/{service}/
├── Cargo.toml
├── src/
│   ├── lib.rs                    # Module exports (if needed)
│   └── bin/
│       └── hello.rs              # MANDATORY: Hello scenario
```

**CRITICAL:** 
- ✅ Hello example MUST be in `src/bin/hello.rs`
- ✅ MUST be the simplest possible example
- ✅ MUST use async/await with tokio runtime
- ✅ MUST use pagination for list operations when available

## Hello Example Pattern
```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! Purpose
//!
//! Shows how to list {resources} using the AWS SDK for Rust.

use aws_config::BehaviorVersion;
use aws_sdk_{service}::Client;

// snippet-start:[{service}.rust.hello]
#[tokio::main]
async fn main() -> Result<(), aws_sdk_{service}::Error> {
    tracing_subscriber::fmt::init();

    let sdk_config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&sdk_config);

    println!("Hello, {AWS Service}! Let's list available {resources}:");
    println!();

    // Use pagination to retrieve all {resources}
    let mut {resources}_paginator = client
        .list_{resources}()
        .into_paginator()
        .send();

    let mut {resources} = Vec::new();
    while let Some(page) = {resources}_paginator.next().await {
        let page = page?;
        {resources}.extend(page.{resources}().to_vec());
    }

    println!("{} {resource}(s) retrieved.", {resources}.len());

    for {resource} in {resources} {
        println!("\t{}", {resource}.name().unwrap_or("Unknown"));
    }

    Ok(())
}
// snippet-end:[{service}.rust.hello]
```

## Hello Example with Simple List (No Pagination)
For services where pagination is not available or not needed:

```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! Purpose
//!
//! Shows how to list {resources} using the AWS SDK for Rust.

use aws_config::BehaviorVersion;
use aws_sdk_{service}::Client;

// snippet-start:[{service}.rust.hello]
#[tokio::main]
async fn main() -> Result<(), aws_sdk_{service}::Error> {
    tracing_subscriber::fmt::init();

    let sdk_config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&sdk_config);

    println!("Hello, {AWS Service}! Let's list available {resources}:");
    println!();

    let response = client.list_{resources}().send().await?;
    let {resources} = response.{resources}();

    println!("{} {resource}(s) retrieved.", {resources}.len());

    for {resource} in {resources} {
        println!("\t{}", {resource}.name().unwrap_or("Unknown"));
    }

    Ok(())
}
// snippet-end:[{service}.rust.hello]
```

## Hello Examples by Service Type

### List-Based Services (S3, DynamoDB, etc.)
- **Operation**: List primary resources (buckets, tables, etc.)
- **Message**: Show count and names of resources
- **Pagination**: Use `.into_paginator()` when available

### Status-Based Services (GuardDuty, Config, etc.)  
- **Operation**: Check service status or list detectors/configurations
- **Message**: Show service availability and basic status
- **Pagination**: Use if available for list operations

### Compute Services (EC2, Lambda, etc.)
- **Operation**: List instances/functions or describe regions
- **Message**: Show available resources or regions
- **Pagination**: Essential for potentially large result sets

## Pagination Pattern
```rust
// Use pagination to retrieve all results with collect
let page_size = page_size.unwrap_or(10);
let items: Result<Vec<_>, _> = client
    .scan()
    .table_name(table)
    .limit(page_size)
    .into_paginator()
    .items()
    .send()
    .collect()
    .await;

println!("Items in table (up to {page_size}):");
for item in items? {
    println!("   {:?}", item);
}
```

## Alternative Pagination Pattern (Using while let)
```rust
// Use while let to iterate through pages
let mut items_paginator = client
    .list_items()
    .into_paginator()
    .send();

let mut items = Vec::new();
while let Some(page) = items_paginator.next().await {
    let page = page?;
    items.extend(page.items().to_vec());
}

println!("Retrieved {} items", items.len());
```

## Error Handling Pattern
```rust
#[tokio::main]
async fn main() -> Result<(), aws_sdk_{service}::Error> {
    // All AWS operations return Result types
    // Use ? operator for simple error propagation
    let response = client.list_resources().send().await?;
    
    // Or handle errors explicitly
    match client.list_resources().send().await {
        Ok(response) => {
            println!("Success: {} resources", response.resources().len());
        }
        Err(e) => {
            eprintln!("Error listing resources: {}", e);
            return Err(e);
        }
    }
    
    Ok(())
}
```

## Cargo.toml Configuration
```toml
[package]
name = "{service}-examples"
version = "0.1.0"
edition = "2021"

[[bin]]
name = "hello"
path = "src/bin/hello.rs"

[dependencies]
aws-config = { version = "1.0", features = ["behavior-version-latest"] }
aws-sdk-{service} = "1.0"
tokio = { version = "1.0", features = ["full"] }
tracing-subscriber = { version = "0.3", features = ["env-filter"] }
```

## Validation Requirements
- ✅ **Must run without errors** (with proper credentials)
- ✅ **Must handle credential issues gracefully**
- ✅ **Must display meaningful output**
- ✅ **Must use tokio runtime with #[tokio::main]**
- ✅ **Must use pagination for list operations when available**
- ✅ **Must include proper module-level documentation**
- ✅ **Must use correct snippet tag format**: `[{service}.rust.hello]`

## Common Patterns
- Use `#[tokio::main]` for async main function
- Initialize tracing with `tracing_subscriber::fmt::init()`
- Load AWS config with `aws_config::defaults(BehaviorVersion::latest()).load().await`
- Create client with `Client::new(&sdk_config)`
- Use pagination to retrieve all results when available
- Return `Result<(), aws_sdk_{service}::Error>` from main
- Keep it as simple as possible - all logic in main function
- Use `?` operator for error propagation

## Snippet Tag Format
**CRITICAL**: Use the correct snippet tag format:

```rust
// ✅ CORRECT
// snippet-start:[{service}.rust.hello]
#[tokio::main]
async fn main() -> Result<(), aws_sdk_{service}::Error> {
    // Implementation
}
// snippet-end:[{service}.rust.hello]

// ❌ WRONG - Old format
// snippet-start:[rust.example_code.{service}.hello]
// snippet-end:[rust.example_code.{service}.hello]
```

**Format**: `[{service}.rust.hello]`
- Service name in snake_case (e.g., s3, dynamodb, ec2)
- Always use `.rust.hello` for Hello examples

## Module Documentation
```rust
//! Purpose
//!
//! Shows how to {operation description} using the AWS SDK for Rust.
//!
//! # Arguments
//!
//! * None - uses default AWS configuration
//!
//! # Example
//!
//! ```no_run
//! cargo run --bin hello
//! ```
```

## Running the Hello Example
```bash
# From the service directory
cargo run --bin hello

# With specific AWS profile
AWS_PROFILE=myprofile cargo run --bin hello

# With specific region
AWS_REGION=us-west-2 cargo run --bin hello

# With debug logging
RUST_LOG=debug cargo run --bin hello
```

## Common Hello Operations by Service

### S3
- **Operation**: `list_buckets()`
- **Display**: Bucket names and creation dates
- **Pagination**: Not needed (typically small result set)

### DynamoDB
- **Operation**: `list_tables()`
- **Display**: Table names
- **Pagination**: Use `.into_paginator()` for many tables

### EC2
- **Operation**: `describe_regions()` or `describe_instances()`
- **Display**: Region names or instance IDs
- **Pagination**: Use for `describe_instances()`

### Lambda
- **Operation**: `list_functions()`
- **Display**: Function names and runtimes
- **Pagination**: Use `.into_paginator()` for many functions

### IAM
- **Operation**: `list_users()` or `list_roles()`
- **Display**: User/role names
- **Pagination**: Use `.into_paginator()` for large organizations

## Best Practices
- ✅ **Keep it simple**: Hello examples should be minimal
- ✅ **Use pagination**: Always use paginators when available
- ✅ **Show meaningful output**: Display useful information, not raw dumps
- ✅ **Handle errors gracefully**: Use Result types and ? operator
- ✅ **Include documentation**: Module-level docs explaining the example
- ✅ **Follow naming conventions**: Use snake_case for Rust identifiers
- ✅ **Initialize tracing**: Include tracing_subscriber for debugging
- ✅ **Use latest SDK patterns**: BehaviorVersion::latest() for config
