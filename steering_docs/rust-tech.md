# Rust Technology Stack & Build System

## Overview
This document provides an overview of the Rust technology stack and build system for AWS SDK code examples. For detailed guidance on specific components, see the modular steering documents in the `rust-tech/` directory:

- **[basics.md](rust-tech/basics.md)** - Interactive scenario generation
- **[hello.md](rust-tech/hello.md)** - Hello example generation
- **[wrapper.md](rust-tech/wrapper.md)** - Service wrapper generation
- **[tests.md](rust-tech/tests.md)** - Test generation and patterns
- **[metadata.md](rust-tech/metadata.md)** - Metadata generation
- **[orchestration.md](rust-tech/orchestration.md)** - Component orchestration
- **[readme_writeme.md](rust-tech/readme_writeme.md)** - README generation

## Rust SDK v1 Development Environment

### Build Tools & Dependencies
- **Build System**: Cargo
- **Testing Framework**: Built-in Rust testing
- **Package Manager**: Cargo with crates.io
- **SDK Version**: AWS SDK for Rust v1
- **Rust Version**: Latest stable Rust

### Common Build Commands

```bash
# Development
cargo check                       # Check compilation without building
cargo build                       # Build project
cargo build --release            # Build optimized release version

# Testing
cargo test                        # Run all tests
cargo test --test integration     # Run integration tests
cargo test -- --nocapture        # Run tests with output

# Execution
cargo run --bin hello             # Run hello scenario
cargo run --bin getting-started  # Run getting started scenario
cargo run --bin {scenario-name}  # Run specific scenario
```

### Rust-Specific Pattern Requirements

**CRITICAL**: Rust examples follow a specific directory structure pattern. Always examine existing Rust examples (like EC2) before creating new ones.

#### Correct Structure for Rust Scenarios
```
rustv1/examples/{service}/
├── Cargo.toml
├── README.md
├── src/
│   ├── lib.rs
│   ├── {service}.rs              # Service wrapper
│   ├── bin/
│   │   ├── hello.rs              # MANDATORY: Hello scenario
│   │   ├── {action-one}.rs       # Individual action file
│   │   ├── {action-two}.rs       # Individual action file, etc.
│   │   └── {scenario-name}.rs    # Other scenario entry points
│   └── {scenario_name}/
│       ├── mod.rs
│       ├── scenario.rs           # Main scenario logic
│       └── tests/
│           └── mod.rs            # Integration tests
```

#### Key Structural Points
- **MANDATORY**: Every service must include `src/bin/hello.rs` as the simplest example
- **Follow the EC2 example structure exactly** - it's the canonical pattern
- **Service wrapper goes in `src/{service}.rs`** (e.g., `src/comprehend.rs`)
- **Tests go in `{scenario_name}/tests/mod.rs`** for integration testing
- **Hello scenario**: Should demonstrate the most basic service operation

#### File Naming Conventions
- Use snake_case for all Rust files and directories
- Binary files: `{action}.rs` in `src/bin/` directory
- Service modules: `{service}.rs` in `src/` directory
- Scenario modules: `{scenario_name}/mod.rs` and `scenario.rs`

#### Hello Scenario Structure
- **File location**: `src/bin/hello.rs`
- **Function structure**: `main()` function as entry point with `tokio::main` attribute
- **Documentation**: Include module-level documentation explaining the hello example

#### Code Structure Standards
- **Modules**: Use `mod.rs` files for module organization
- **Functions**: Use snake_case for function names
- **Structs/Enums**: Use PascalCase for type names
- **Constants**: Use UPPER_SNAKE_CASE for constants
- **Async/Await**: Use `tokio` runtime for async operations

#### Error Handling Patterns
```rust
use aws_sdk_s3::{Client, Error};
use aws_config::meta::region::RegionProviderChain;

#[tokio::main]
async fn main() -> Result<(), Error> {
    let region_provider = RegionProviderChain::default_provider().or_else("us-east-1");
    let config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&config);

    match client.list_buckets().send().await {
        Ok(response) => {
            // Handle successful response
            println!("Buckets: {:?}", response.buckets());
            Ok(())
        }
        Err(e) => {
            // Handle error
            eprintln!("Error: {}", e);
            Err(e)
        }
    }
}
```

#### Testing Standards
- **Integration tests**: Place in `{scenario_name}/tests/mod.rs`
- **Unit tests**: Include `#[cfg(test)]` modules in source files
- **Async testing**: Use `#[tokio::test]` for async test functions
- **Test naming**: Use descriptive function names with snake_case

#### Cargo.toml Configuration
```toml
[package]
name = "{service}-examples"
version = "0.1.0"
edition = "2021"

[[bin]]
name = "hello"
path = "src/bin/hello.rs"

[[bin]]
name = "{action-name}"
path = "src/bin/{action-name}.rs"

[dependencies]
aws-config = "1.0"
aws-sdk-{service} = "1.0"
tokio = { version = "1.0", features = ["full"] }
tracing-subscriber = "0.3"
```

#### Documentation Requirements
- **Module documentation**: Use `//!` for module-level docs
- **Function documentation**: Use `///` for function documentation
- **Inline comments**: Explain complex AWS service interactions
- **README sections**: Include Cargo setup and execution instructions

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER assume file naming patterns** without checking existing examples
- ❌ **NEVER skip the mandatory `src/bin/hello.rs` file**
- ❌ **NEVER use camelCase for Rust identifiers**
- ❌ **NEVER ignore proper error handling with Result types**
- ❌ **NEVER forget to use async/await for AWS operations**

### Best Practices
- ✅ **ALWAYS examine `rustv1/examples/ec2/` structure first**
- ✅ **ALWAYS include the mandatory hello scenario**
- ✅ **ALWAYS use snake_case naming conventions**
- ✅ **ALWAYS handle errors with Result types and proper error propagation**
- ✅ **ALWAYS use tokio runtime for async AWS operations**
- ✅ **ALWAYS follow the established directory structure exactly**

### Cargo Workspace Integration
- **Workspace member**: Each service example is a workspace member
- **Shared dependencies**: Common dependencies managed at workspace level
- **Build optimization**: Shared target directory for faster builds

### Integration with Knowledge Base
Before creating Rust code examples:
1. Query `coding-standards-KB` for "Rust-code-example-standards"
2. Query `Rust-premium-KB` for "Rust implementation patterns"
3. **CRITICAL**: Always examine existing EC2 example structure as canonical pattern
4. Follow KB-documented patterns for Cargo project structure and module organization
5. Validate against existing Rust examples only after KB consultation