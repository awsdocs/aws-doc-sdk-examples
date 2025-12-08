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

    let sdk_config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = aws_sdk_{service}::Client::new(&sdk_config);

    run_scenario(client).await
}
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
async fn cleanup_phase(client: &Client, resource_id: &str) -> Result<(), ScenarioError> {
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

## Implementation Workflow

### Step-by-Step Implementation Process
1. **Read Specification**: Study `scenarios/basics/{service}/SPECIFICATION.md` thoroughly
2. **Extract API Actions**: Note all API actions listed in "API Actions Used" section
3. **Map to Operations**: Ensure all required actions are available
4. **Implement Phases**: Follow the "Proposed example structure" section exactly
5. **Add Error Handling**: Implement error handling per the "Errors" section
6. **Test Against Specification**: Verify implementation matches specification requirements

## Educational Elements
- **Use specification descriptions**: Explain operations using specification language
- Show before/after states as outlined in specification
- Provide context about service capabilities from specification
- Include tips and best practices mentioned in specification
- Follow the educational flow described in specification structure
