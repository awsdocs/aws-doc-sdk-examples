# Rust Steering Documentation

This directory contains modular steering documents for generating AWS SDK for Rust code examples. Each document focuses on a specific aspect of the code generation process.

## Document Overview

### Core Generation Documents

#### [basics.md](basics.md)
**Purpose**: Interactive scenario generation  
**Use When**: Creating complete workflow scenarios with user interaction  
**Key Topics**:
- Specification-driven development
- Scenario phase structure (setup, demonstration, examination, cleanup)
- User interaction patterns with inquire crate
- Custom ScenarioError type
- Async/await patterns with tokio

#### [hello.md](hello.md)
**Purpose**: Hello example generation  
**Use When**: Creating the simplest possible service introduction  
**Key Topics**:
- Mandatory hello examples for every service
- Pagination patterns for list operations
- Basic async/await with tokio
- Minimal example structure
- Direct client usage

#### [wrapper.md](wrapper.md)
**Purpose**: Service wrapper generation  
**Use When**: Mocking is needed for integration tests  
**Key Topics**:
- Optional wrapper creation (only when needed)
- Mockall integration for testing
- Pagination patterns
- Waiter patterns
- Error handling strategies
- When to use traits vs bare functions

### Testing and Quality

#### [tests.md](tests.md)
**Purpose**: Test generation and patterns  
**Use When**: Creating unit and integration tests  
**Key Topics**:
- Unit tests with #[cfg(test)]
- Integration tests in tests/ folders
- Mockall for mocking AWS services
- Async testing with #[tokio::test]
- Code coverage with cargo-llvm-cov
- Test organization patterns

### Documentation and Metadata

#### [metadata.md](metadata.md)
**Purpose**: Metadata generation for documentation pipeline  
**Use When**: Integrating examples with AWS documentation  
**Key Topics**:
- Specification-based metadata keys
- Snippet tag format and conventions
- Service abbreviations
- Metadata validation
- Cross-service examples

#### [readme_writeme.md](readme_writeme.md)
**Purpose**: README generation with writeme tool  
**Use When**: Creating or updating service documentation  
**Key Topics**:
- Writeme tool setup and usage
- README structure and content
- Documentation dependencies
- Cargo.toml documentation
- CI/CD integration

### Orchestration

#### [orchestration.md](orchestration.md)
**Purpose**: Component coordination and workflows  
**Use When**: Understanding the complete development process  
**Key Topics**:
- Full service implementation workflow
- Individual component updates
- Quality gates and validation
- Error recovery strategies
- CI/CD pipeline integration

## Quick Start Guide

### For New Service Implementation
1. **Read**: [orchestration.md](orchestration.md) for overall workflow
2. **Start with**: [hello.md](hello.md) to create basic connectivity example
3. **Add wrapper**: [wrapper.md](wrapper.md) if mocking is needed for tests
4. **Build scenario**: [basics.md](basics.md) following service specification
5. **Write tests**: [tests.md](tests.md) for coverage
6. **Add metadata**: [metadata.md](metadata.md) for documentation
7. **Generate docs**: [readme_writeme.md](readme_writeme.md) for README

### For Updating Existing Examples
1. **Identify component**: Determine which document applies
2. **Read guidance**: Review the specific document
3. **Make changes**: Follow the patterns and requirements
4. **Validate**: Use the validation commands in the document
5. **Update docs**: Regenerate README if needed

## Common Patterns

### File Structure
```
rustv1/examples/{service}/
├── Cargo.toml
├── README.md
├── src/
│   ├── lib.rs
│   ├── {service}.rs              # Optional wrapper
│   ├── bin/
│   │   ├── hello.rs              # MANDATORY
│   │   └── {scenario-name}.rs
│   └── {scenario_name}/
│       ├── mod.rs
│       ├── scenario.rs
│       └── tests/
│           └── mod.rs
```

### Snippet Tag Format
```rust
// snippet-start:[{service}.rust.{action_name}]
// Code here
// snippet-end:[{service}.rust.{action_name}]
```

### Error Handling
```rust
// Custom error type for scenarios
#[derive(Debug, PartialEq, Eq)]
pub struct ScenarioError {
    message: String,
    context: Option<String>,
}
```

### Async Pattern
```rust
#[tokio::main]
async fn main() -> Result<(), Error> {
    let sdk_config = aws_config::defaults(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&sdk_config);
    // Operations here
}
```

## Standards and Requirements

### General Code Standards
All Rust examples must follow:
- ✅ **Rust 2021 edition** or later
- ✅ **Async/await** with tokio runtime
- ✅ **Pagination** for list operations when available
- ✅ **Error handling** with Result types
- ✅ **Snake_case** naming conventions
- ✅ **Module documentation** with //! comments
- ✅ **Snippet tags** for all code examples

### Mandatory Components
Every service MUST include:
- ✅ **Hello example** in `src/bin/hello.rs`
- ✅ **Cargo.toml** with proper dependencies
- ✅ **README.md** (generated with writeme)
- ✅ **Metadata** in `.doc_gen/metadata/{service}_metadata.yaml`

### Optional Components
Create only when needed:
- Service wrapper in `src/{service}.rs` (for mocking)
- Integration tests in scenario tests/ folders
- Multiple scenario binaries

## Validation Commands

### Build and Test
```bash
cargo build                    # Build all examples
cargo test                     # Run all tests
cargo fmt --check             # Check formatting
cargo clippy -- -D warnings   # Run linter
```

### Documentation
```bash
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Rust:1 --services {service}
```

## Related Documents

### Parent Document
- [rust-tech.md](../rust-tech.md) - Main Rust technology overview

### General Standards
- [General-Code-Example-Standards.md](../General-Code-Example-Standards.md)
- [Rust-code-example-standards.md](../Rust-code-example-standards.md)

## Getting Help

### Common Issues
- **Compilation errors**: Check [wrapper.md](wrapper.md) for error handling patterns
- **Test failures**: See [tests.md](tests.md) for testing patterns
- **Metadata errors**: Review [metadata.md](metadata.md) for correct format
- **Documentation issues**: Check [readme_writeme.md](readme_writeme.md)

### Best Practices
- Always consult knowledge bases before starting
- Follow service specification exactly
- Use existing examples (like EC2) as reference
- Validate frequently during development
- Test both happy and error paths

## Contributing

When adding or updating steering documents:
1. Follow the established structure and format
2. Include practical examples and code snippets
3. Add validation commands and quality gates
4. Update this README with any new documents
5. Cross-reference related documents
