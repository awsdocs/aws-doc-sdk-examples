# Rust Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Rust-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Rust-premium-KB", "Rust implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: Use inquire crate for complex input, stdin().read_line() for simple input
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Use custom ScenarioError type for graceful error handling
- **Async Operations**: All AWS operations must be async with tokio runtime
- **Module Organization**: Decompose scenario actions into functions in `lib/{scenario_name}.rs`

## File Structure
```
rustv1/examples/{service}/
├── Cargo.toml
├── README.md
├── src/
│   ├── lib.rs
│   ├── {service}.rs              # Service wrapper (if needed for mocking)
│   ├── bin/
│   │   ├── hello.rs              # MANDATORY: Hello scenario
│   │   └── {scenario-name}.rs    # Scenario entry point
│   └── {scenario_name}/
│       ├── mod.rs                # Module exports
│       ├── scenario.rs           # Main scenario logic
│       └── tests/
│           └── mod.rs            # Integration tests
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

## Scenario Binary Pattern (src/bin/{scenario-name}.rs)
```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//! Purpose
//! 
//! Shows how to use {AWS Service} to {scenario description}. This scenario demonstrates:
//! 
//! 1. {Phase 1 description}
//! 2. {Phase 2 description}
//! 3. {Phase 3 description}
//! 4. {Phase 4 description}

use aws_config::BehaviorVersion;
use {service}_code_examples::{run_scenario, ScenarioError};

#[tokio::main]
async fn main() -> Result<(), ScenarioError> {
    tracing_subscriber::fmt::init();

    // Initialize client once in main
    let sdk_config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = aws_sdk_{service}::Client::new(&sdk_config);

    // Pass client to scenario (clone when necessary)
    run_scenario(client).await
}
```

**Client Initialization Pattern:**
- ✅ **Initialize once**: Create the client once in main using `BehaviorVersion::latest()`
- ✅ **Pass by reference**: Pass `&Client` to functions when possible
- ✅ **Clone when needed**: Clone the client when passing to async tasks or storing in structs
- ✅ **Efficient cloning**: Client cloning is cheap (Arc internally), so don't worry about performance

```rust
// Passing client by reference
async fn operation_one(client: &Client) -> Result<(), Error> {
    client.list_resources().send().await?;
    Ok(())
}

// Cloning client for async tasks
let client_clone = client.clone();
tokio::spawn(async move {
    client_clone.list_resources().send().await
});
```

## Scenario Implementation Pattern (src/{scenario_name}/scenario.rs)
```rust
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_sdk_{service}::Client;
use std::fmt::Display;

/// Custom error type for scenario operations
#[derive(Debug, PartialEq, Eq)]
pub struct ScenarioError {
    message: String,
    context: Option<String>,
}

impl ScenarioError {
    pub fn with(message: impl Into<String>) -> Self {
        ScenarioError {
            message: message.into(),
            context: None,
        }
    }

    pub fn new(message: impl Into<String>, err: &dyn std::error::Error) -> Self {
        ScenarioError {
            message: message.into(),
            context: Some(err.to_string()),
        }
    }
}

impl std::error::Error for ScenarioError {}

impl Display for ScenarioError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match &self.context {
            Some(c) => write!(f, "{}: {}", self.message, c),
            None => write!(f, "{}", self.message),
        }
    }
}

/// Main scenario runner
pub async fn run_scenario(client: Client) -> Result<(), ScenarioError> {
    println!("{}", "-".repeat(88));
    println!("Welcome to the {AWS Service} basics scenario!");
    println!("{}", "-".repeat(88));
    println!("{Service description and what users will learn}");
    println!();

    let mut resource_id: Option<String> = None;

    let result = async {
        resource_id = Some(setup_phase(&client).await?);
        demonstration_phase(&client, resource_id.as_ref().unwrap()).await?;
        examination_phase(&client, resource_id.as_ref().unwrap()).await?;
        Ok::<(), ScenarioError>(())
    }
    .await;

    // Always attempt cleanup
    if let Some(id) = resource_id {
        cleanup_phase(&client, &id).await?;
    }

    result
}

/// Setup phase: Implement based on specification's Setup section
async fn setup_phase(client: &Client) -> Result<String, ScenarioError> {
    println!("Setting up {AWS Service}...");
    println!();

    // Check for existing resources
    let existing_resources = list_resources(client).await?;
    if !existing_resources.is_empty() {
        println!("Found {} existing resource(s):", existing_resources.len());
        for resource in &existing_resources {
            println!("  - {}", resource);
        }

        let use_existing = inquire::Confirm::new("Would you like to use an existing resource?")
            .with_default(false)
            .prompt()
            .map_err(|e| ScenarioError::with(format!("Failed to get user input: {}", e)))?;

        if use_existing {
            return Ok(existing_resources[0].clone());
        }
    }

    // Create new resource
    let resource_name = inquire::Text::new("Enter resource name:")
        .prompt()
        .map_err(|e| ScenarioError::with(format!("Failed to get resource name: {}", e)))?;

    create_resource(client, &resource_name).await
}

/// Demonstration phase: Implement operations from specification
async fn demonstration_phase(client: &Client, resource_id: &str) -> Result<(), ScenarioError> {
    println!("Demonstrating {AWS Service} capabilities...");
    println!();

    // Implement specific operations from specification
    generate_sample_data(client, resource_id).await?;
    println!("✓ Sample data created successfully");

    // Wait if specified in the specification
    println!("Waiting for data to be processed...");
    tokio::time::sleep(tokio::time::Duration::from_secs(5)).await;

    Ok(())
}

/// Examination phase: Implement data analysis from specification
async fn examination_phase(client: &Client, resource_id: &str) -> Result<(), ScenarioError> {
    println!("Examining {AWS Service} data...");
    println!();

    // List and examine data as specified
    let data_items = list_data(client, resource_id).await?;
    if data_items.is_empty() {
        println!("No data found. Data may take a few minutes to appear.");
        return Ok(());
    }

    println!("Found {} data item(s)", data_items.len());

    // Get detailed information as specified
    let detailed_data = get_data_details(client, resource_id, &data_items[..5.min(data_items.len())]).await?;
    display_data_summary(&detailed_data);

    // Show detailed view if specified
    if !detailed_data.is_empty() {
        let show_details = inquire::Confirm::new("Would you like to see detailed information?")
            .with_default(false)
            .prompt()
            .map_err(|e| ScenarioError::with(format!("Failed to get user input: {}", e)))?;

        if show_details {
            display_data_details(&detailed_data[0]);
        }
    }

    // Filter data as specified
    filter_data_by_criteria(&data_items);

    Ok(())
}

/// Cleanup phase: Implement cleanup options from specification
async fn cleanup_phase(client: &Client, resource_id: &str, skip_cleanup: bool) -> Result<(), ScenarioError> {
    if skip_cleanup {
        println!("Skipping cleanup (--no-cleanup flag set)");
        println!("Resource {} will continue running.", resource_id);
        return Ok(());
    }

    println!("Cleanup options:");
    println!("Note: Deleting the resource will stop all monitoring/processing.");

    let delete_resource = inquire::Confirm::new("Would you like to delete the resource?")
        .with_default(false)
        .prompt()
        .map_err(|e| ScenarioError::with(format!("Failed to get user input: {}", e)))?;

    if delete_resource {
        match delete_resource_impl(client, resource_id).await {
            Ok(_) => println!("✓ Deleted resource: {}", resource_id),
            Err(e) => println!("Error deleting resource: {}", e),
        }
    } else {
        println!("Resource {} will continue running.", resource_id);
        println!("You can manage it through the AWS Console or delete it later.");
    }

    Ok(())
}

// Helper functions for AWS operations
async fn list_resources(client: &Client) -> Result<Vec<String>, ScenarioError> {
    // Implementation
    Ok(vec![])
}

async fn create_resource(client: &Client, name: &str) -> Result<String, ScenarioError> {
    // Implementation
    Ok(name.to_string())
}

async fn generate_sample_data(client: &Client, resource_id: &str) -> Result<(), ScenarioError> {
    // Implementation
    Ok(())
}

async fn list_data(client: &Client, resource_id: &str) -> Result<Vec<String>, ScenarioError> {
    // Implementation
    Ok(vec![])
}

async fn get_data_details(client: &Client, resource_id: &str, items: &[String]) -> Result<Vec<String>, ScenarioError> {
    // Implementation
    Ok(items.to_vec())
}

async fn delete_resource_impl(client: &Client, resource_id: &str) -> Result<(), ScenarioError> {
    // Implementation
    Ok(())
}

fn display_data_summary(data: &[String]) {
    println!("Data Summary:");
    for item in data {
        println!("  • {}", item);
    }
}

fn display_data_details(item: &str) {
    println!("Detailed Information:");
    println!("  {}", item);
}

fn filter_data_by_criteria(items: &[String]) {
    println!("Filtering data...");
    // Implementation
}
```

## Scenario Phase Structure (Based on Specification)

### Setup Phase
- **Read specification Setup section** for exact requirements
- Check for existing resources as specified
- Create necessary resources using async operations
- Configure service settings per specification
- Verify setup completion as described

### Demonstration Phase  
- **Follow specification Demonstration section** exactly
- Perform core service operations using async operations
- Generate sample data if specified in the specification
- Show service capabilities as outlined
- Provide educational context from specification

### Examination Phase
- **Implement specification Examination section** requirements
- List and examine results using async operations
- Filter and analyze data as specified
- Display detailed information per specification format
- Allow user interaction as described in specification

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Offer cleanup options with warnings from specification
- Handle cleanup errors gracefully
- Provide alternative management options as specified
- Confirm completion per specification
- **Support --no-cleanup flag**: Scenarios may add a `--no-cleanup` or `--cleanup=false` flag to skip cleanup
- **Start with cleanup**: When implementing a scenario, try to start with the cleanup logic first

## User Interaction Patterns

### Using inquire Crate (Complex Input)
```rust
use inquire::{Confirm, Text, Select};

// Yes/No questions
let use_existing = Confirm::new("Use existing resource?")
    .with_default(false)
    .prompt()
    .map_err(|e| ScenarioError::with(format!("Failed to get input: {}", e)))?;

// Text input
let resource_name = Text::new("Enter resource name:")
    .prompt()
    .map_err(|e| ScenarioError::with(format!("Failed to get name: {}", e)))?;

// Selection from list
let choice = Select::new("Select an option:", vec!["Option 1", "Option 2"])
    .prompt()
    .map_err(|e| ScenarioError::with(format!("Failed to get selection: {}", e)))?;
```

### Using stdin (Simple Input)
```rust
use std::io::{stdin, stdout, Write};

let mut input = String::new();
print!("Enter value: ");
stdout().flush().unwrap();
stdin().read_line(&mut input)
    .map_err(|e| ScenarioError::with(format!("Failed to read input: {}", e)))?;
let value = input.trim();
```

### Information Display
```rust
// Progress indicators
println!("✓ Operation completed successfully");
println!("⚠ Warning message");
println!("✗ Error occurred");

// Formatted output
println!("{}", "-".repeat(60));
println!("Found {} items:", items.len());
for item in items {
    println!("  • {}", item);
}
```

## Error Handling Requirements

### Custom ScenarioError Type
```rust
#[derive(Debug, PartialEq, Eq)]
pub struct ScenarioError {
    message: String,
    context: Option<String>,
}

impl ScenarioError {
    pub fn with(message: impl Into<String>) -> Self {
        ScenarioError {
            message: message.into(),
            context: None,
        }
    }

    pub fn new(message: impl Into<String>, err: &dyn std::error::Error) -> Self {
        ScenarioError {
            message: message.into(),
            context: Some(err.to_string()),
        }
    }
}
```

### Error Handling from Specification
```rust
// Handle service-specific errors based on specification
match operation(client).await {
    Ok(result) => result,
    Err(e) => {
        if let Some(service_err) = e.as_service_error() {
            match service_err {
                // Handle specific errors per specification
                _ => return Err(ScenarioError::new("Operation failed", &e)),
            }
        } else {
            return Err(ScenarioError::new("Operation failed", &e));
        }
    }
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

[[bin]]
name = "{scenario-name}"
path = "src/bin/{scenario-name}.rs"

[dependencies]
aws-config = { version = "1.0", features = ["behavior-version-latest"] }
aws-sdk-{service} = "1.0"
tokio = { version = "1.0", features = ["full"] }
tracing-subscriber = { version = "0.3", features = ["env-filter"] }
inquire = "0.7"
clap = { version = "4.0", features = ["derive"] }  # For command line arguments
```

## Configuration and Command Line Arguments

### Using clap for Command Line Arguments
For command line examples, prefer using clap with derive macros:

```rust
use clap::Parser;

#[derive(Parser, Debug)]
#[command(author, version, about, long_about = None)]
struct Args {
    /// Name of the resource to create
    #[arg(short, long)]
    name: String,

    /// AWS region to use
    #[arg(short, long, default_value = "us-east-1")]
    region: String,

    /// Enable verbose output
    #[arg(short, long)]
    verbose: bool,
}

#[tokio::main]
async fn main() -> Result<(), ScenarioError> {
    let args = Args::parse();
    
    println!("Creating resource: {}", args.name);
    // Use args.name, args.region, etc.
}
```

### Configuration Files
For server and lambda examples, prefer loading configuration from environment or config files:

```rust
use std::env;

let region = env::var("AWS_REGION").unwrap_or_else(|_| "us-east-1".to_string());
let endpoint = env::var("SERVICE_ENDPOINT").ok();
```

### S3 Bucket Naming
When creating S3 buckets, follow these guidelines:

```rust
use std::env;
use uuid::Uuid;

// When creating a new bucket
fn generate_bucket_name() -> Result<String, ScenarioError> {
    let prefix = env::var("S3_BUCKET_NAME_PREFIX")
        .map_err(|_| ScenarioError::with(
            "S3_BUCKET_NAME_PREFIX environment variable not set. \
             Please set it to create buckets."
        ))?;
    
    let unique_id = Uuid::new_v4();
    Ok(format!("{}-{}", prefix, unique_id))
}

// When referencing an existing bucket
fn get_existing_bucket() -> Result<String, ScenarioError> {
    // Get from user input, config file, or command line args
    // Do NOT use S3_BUCKET_NAME_PREFIX for existing buckets
    let bucket_name = inquire::Text::new("Enter existing bucket name:")
        .prompt()
        .map_err(|e| ScenarioError::with(format!("Failed to get bucket name: {}", e)))?;
    
    Ok(bucket_name)
}
```

**Requirements:**
- ✅ **New buckets**: Require `S3_BUCKET_NAME_PREFIX` environment variable
- ✅ **Unique suffix**: Append UUID or timestamp to prefix
- ✅ **Exit if missing**: Don't create buckets without prefix
- ✅ **Existing buckets**: Don't use `S3_BUCKET_NAME_PREFIX` for existing buckets
- ✅ **Integration tests**: Same rules apply - tests should fail without prefix

## Code Style and Readability

### Loop vs Iterator
When to prefer Loop vs Iterator:
- **Use Iterator** if there is a clear transformation from `T` to `U`
- **Do not nest control flow** inside an iterator
  - Extract the logic to a dedicated function
  - Prefer a for loop if the function would be difficult to extract
  - Prefer an extracted function if the logic is nuanced and should be tested

```rust
// ✅ GOOD - Clear transformation
let names: Vec<String> = resources
    .iter()
    .map(|r| r.name().to_string())
    .collect();

// ✅ GOOD - Simple for loop for complex logic
for resource in resources {
    if resource.status() == "ACTIVE" {
        process_resource(&resource).await?;
    }
}

// ❌ BAD - Nested control flow in iterator
let results: Vec<_> = resources
    .iter()
    .map(|r| {
        if r.status() == "ACTIVE" {
            Some(process_resource(r))
        } else {
            None
        }
    })
    .collect();
```

### Function Nesting Depth
How deep to go in nesting vs when to extract a new function:
- **Two levels deep** is fine
- **Three levels deep** is pushing it
- **Four levels deep** is probably too much
- **Extract a function** if the logic is nuanced and should be tested

```rust
// ✅ GOOD - Extracted function for complex logic
async fn process_active_resources(resources: &[Resource]) -> Result<(), ScenarioError> {
    for resource in resources {
        if resource.status() == "ACTIVE" {
            validate_and_process(resource).await?;
        }
    }
    Ok(())
}

async fn validate_and_process(resource: &Resource) -> Result<(), ScenarioError> {
    // Complex validation and processing logic
    Ok(())
}
```

### Trait vs Bare Functions
When to use Trait vs bare functions:
- **Examples rarely need traits** - they add mental overhead
- **Prefer bare functions** or a struct to manage the Epic communication saga
- **Use traits only** when mocking is required for testing

```rust
// ✅ GOOD - Bare functions for simple cases
pub async fn create_resource(client: &Client, name: &str) -> Result<String, Error> {
    // Implementation
}

// ✅ GOOD - Struct for managing scenario state
pub struct ScenarioState {
    client: Client,
    resource_id: Option<String>,
}

impl ScenarioState {
    pub async fn setup(&mut self) -> Result<(), ScenarioError> {
        // Setup logic
    }
}
```

## Scenario Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** include module-level documentation explaining scenario steps from specification
- ✅ **ALWAYS** use inquire for complex input, stdin for simple input
- ✅ **ALWAYS** use async/await with tokio runtime
- ✅ **ALWAYS** implement proper cleanup that always runs
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification
- ✅ **ALWAYS** handle edge cases (no resources found, etc.) as specified
- ✅ **ALWAYS** use custom ScenarioError type for error handling
- ✅ **ALWAYS** prefer iterators for clear transformations, loops for complex logic
- ✅ **ALWAYS** extract functions when nesting exceeds 2-3 levels

## Implementation Workflow

### Step-by-Step Implementation Process
1. **Read Specification**: Study `scenarios/basics/{service}/SPECIFICATION.md` thoroughly
2. **Extract API Actions**: Note all API actions listed in "API Actions Used" section
3. **Map to Operations**: Ensure all required actions are available
4. **Implement Phases**: Follow the "Proposed example structure" section exactly
5. **Add Error Handling**: Implement error handling per the "Errors" section
6. **Test Against Specification**: Verify implementation matches specification requirements

## Runtime Resources and File Handling

### Compile-Time Data
Use `include_bytes!` or `include_str!` for compile-time data:

```rust
// Include text file at compile time
const CONFIG_TEMPLATE: &str = include_str!("../resources/config_template.json");

// Include binary file at compile time
const SAMPLE_DATA: &[u8] = include_bytes!("../resources/sample.dat");

fn use_embedded_data() {
    println!("Config template: {}", CONFIG_TEMPLATE);
    println!("Sample data size: {} bytes", SAMPLE_DATA.len());
}
```

### Runtime File Operations
For runtime file operations:

```rust
use std::fs;
use aws_sdk_s3::primitives::ByteStream;

// Read entire file as string
let content = fs::read_to_string("data.txt")?;

// Stream file for upload to S3
let body = ByteStream::from_path("large_file.dat").await?;
client.put_object()
    .bucket("my-bucket")
    .key("large_file.dat")
    .body(body)
    .send()
    .await?;
```

### Handling PII and Sensitive Data
When showing examples that handle PII, use the [secrets](https://crates.io/crates/secrets) crate:

```rust
use secrets::SecretString;

// Store sensitive data securely
let password = SecretString::new("sensitive_password".to_string());

// Use the secret (automatically zeroed on drop)
let result = authenticate_user(&password).await?;

// Password is automatically zeroed when dropped
```

### HTTP Servers
For HTTP server examples, use the actix-web crate:

```rust
use actix_web::{web, App, HttpResponse, HttpServer};

async fn health_check() -> HttpResponse {
    HttpResponse::Ok().body("OK")
}

#[tokio::main]
async fn main() -> std::io::Result<()> {
    HttpServer::new(|| {
        App::new()
            .route("/health", web::get().to(health_check))
    })
    .bind(("127.0.0.1", 8080))?
    .run()
    .await
}
```

## Educational Elements
- **Use specification descriptions**: Explain operations using specification language
- Show before/after states as outlined in specification
- Provide context about service capabilities from specification
- Include tips and best practices mentioned in specification
- Follow the educational flow described in specification structure
