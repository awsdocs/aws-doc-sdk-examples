# Rust README/WRITEME and Documentation Generation

## Purpose
Generate and update README files and documentation using the writeme tool to ensure consistency and completeness.

## Requirements
- **Automated Generation**: Use writeme tool for README generation
- **Metadata Dependency**: Requires complete metadata files
- **Virtual Environment**: Run writeme in isolated environment
- **Validation**: Ensure all documentation is up-to-date

## File Structure
```
rustv1/examples/{service}/
├── README.md                   # Generated service README
├── Cargo.toml                  # Package configuration
└── {service}_metadata.yaml     # Metadata (in .doc_gen/metadata/)
```

## README Generation Process

### Step 1: Setup Writeme Environment
```bash
cd .tools/readmes

# Create virtual environment
python -m venv .venv

# Activate environment (Linux/macOS)
source .venv/bin/activate

# Activate environment (Windows)
.venv\Scripts\activate

# Install dependencies
python -m pip install -r requirements_freeze.txt
```

### Step 2: Generate README
```bash
# Generate README for specific service
python -m writeme --languages Rust:1 --services {service}
```

### Step 3: Validate Generation
- ✅ **README.md created/updated** in service directory
- ✅ **No generation errors** in writeme output
- ✅ **All examples listed** in README
- ✅ **Proper formatting** and structure
- ✅ **Working links** to code files

## README Content Structure

### Generated README Sections
1. **Service Overview**: Description of AWS service
2. **Code Examples**: List of available examples
3. **Prerequisites**: Setup requirements
4. **Installation**: Dependency installation
5. **Usage**: How to run examples
6. **Tests**: Testing instructions
7. **Additional Resources**: Links to documentation

### Example README Content
```markdown
# {AWS Service} code examples for the SDK for Rust

## Overview

This is a workspace where you can find the following AWS SDK for Rust 
{AWS Service} examples.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege.

## Code examples

### Actions

The following examples show you how to perform actions using the AWS SDK for Rust.

* [Create a resource](src/{service}.rs#L123) (`CreateResource`)
* [Get a resource](src/{service}.rs#L456) (`GetResource`)

### Scenarios

The following examples show you how to implement common scenarios.

* [Get started with resources](src/bin/{scenario-name}.rs) - Learn the basics by creating and managing resources.

### Hello

* [Hello {Service}](src/bin/hello.rs) - Get started with {AWS Service}.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured.
- Rust 1.70 or later
- Cargo (Rust package manager)

## Install

Install the prerequisites using Cargo:

```
cargo build
```

## Run the examples

### Instructions

All examples can be run individually. For example:

```
cargo run --bin hello
```

### Hello {Service}

This example shows you how to get started using {AWS Service}.

```
cargo run --bin hello
```

### Get started with {Service} resources

This interactive scenario runs at a command prompt and shows you how to use {AWS Service} to do the following:

1. Create a resource
2. Use the resource  
3. Clean up resources

```
cargo run --bin {scenario-name}
```

## Run the tests

Unit and integration tests in this module use the built-in Rust testing framework. To run all tests:

```
cargo test
```

To run tests with output:

```
cargo test -- --nocapture
```

## Additional resources

- [{AWS Service} User Guide](https://docs.aws.amazon.com/{service}/latest/ug/)
- [{AWS Service} API Reference](https://docs.aws.amazon.com/{service}/latest/APIReference/)
- [AWS SDK for Rust ({AWS Service})](https://docs.rs/aws-sdk-{service}/latest/aws_sdk_{service}/)
```

## Documentation Dependencies

### Required Files for README Generation
- ✅ **Metadata file**: `.doc_gen/metadata/{service}_metadata.yaml`
- ✅ **Code files**: All referenced Rust files must exist
- ✅ **Snippet tags**: All snippet tags in metadata must exist in code
- ✅ **Cargo.toml**: Package configuration with dependencies

### Metadata Integration
The writeme tool uses metadata to:
- Generate example lists and descriptions
- Create links to specific code sections
- Include proper service information
- Format documentation consistently

## Troubleshooting README Generation

### Common Issues
- **Missing metadata**: Ensure metadata file exists and is valid
- **Broken snippet tags**: Verify all snippet tags exist in code
- **File not found**: Check all file paths in metadata
- **Invalid YAML**: Validate metadata YAML syntax

### Error Resolution
```bash
# Check for metadata errors
python -m writeme --languages Rust:1 --services {service} --verbose

# Validate specific metadata file
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Check for missing snippet tags
grep -r "snippet-start" rustv1/examples/{service}/
```

## README Maintenance

### When to Regenerate README
- ✅ **After adding new examples**
- ✅ **After updating metadata**
- ✅ **After changing code structure**
- ✅ **Before committing changes**
- ✅ **During regular maintenance**

### README Quality Checklist
- ✅ **All examples listed** and properly linked
- ✅ **Prerequisites accurate** and complete
- ✅ **Installation instructions** work correctly
- ✅ **Usage examples** are clear and correct
- ✅ **Links functional** and point to right locations
- ✅ **Formatting consistent** with other services

## Integration with CI/CD

### Automated README Validation
```bash
# In CI/CD pipeline, validate README is up-to-date
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Rust:1 --services {service} --check

# Exit with error if README needs updates
if git diff --exit-code rustv1/examples/{service}/README.md; then
    echo "README is up-to-date"
else
    echo "README needs to be regenerated"
    exit 1
fi
```

## Cargo.toml Documentation

### Package Metadata
Ensure Cargo.toml includes proper metadata:

```toml
[package]
name = "{service}-examples"
version = "0.1.0"
edition = "2021"
authors = ["AWS SDK for Rust Team"]
description = "AWS SDK for Rust code examples for {Service}"
license = "Apache-2.0"
repository = "https://github.com/awsdocs/aws-doc-sdk-examples"

[dependencies]
aws-config = { version = "1.0", features = ["behavior-version-latest"] }
aws-sdk-{service} = "1.0"
tokio = { version = "1.0", features = ["full"] }
tracing-subscriber = { version = "0.3", features = ["env-filter"] }

[dev-dependencies]
mockall = "0.12"
```

## Documentation Comments

### Module-Level Documentation
```rust
//! # {AWS Service} Examples
//!
//! This module contains examples demonstrating how to use the AWS SDK for Rust
//! with {AWS Service}.
//!
//! ## Examples
//!
//! - `hello.rs` - Basic connectivity example
//! - `{scenario-name}.rs` - Interactive scenario demonstrating service features
```

### Function Documentation
```rust
/// Creates a new resource in {AWS Service}.
///
/// # Arguments
///
/// * `client` - The {Service} client
/// * `name` - The name for the new resource
///
/// # Returns
///
/// Returns the resource ID on success, or an error if the operation fails.
///
/// # Example
///
/// ```no_run
/// let resource_id = create_resource(&client, "my-resource").await?;
/// ```
pub async fn create_resource(
    client: &Client,
    name: &str,
) -> Result<String, Error> {
    // Implementation
}
```

## README Customization

### Service-Specific Sections
Add service-specific information to README:

```markdown
## Service-Specific Notes

### {Service} Quotas
Be aware of the following service quotas:
- Maximum resources per account: 100
- Maximum operations per second: 10

### Best Practices
- Always clean up resources after use
- Use pagination for large result sets
- Implement proper error handling
```

### Example Output
Include example output in README:

```markdown
## Example Output

When you run the hello example, you should see output similar to:

```
Hello, Amazon {Service}! Let's list available resources:

3 resource(s) retrieved.
	resource-1
	resource-2
	resource-3
```
```

## Linking to Code

### Direct File Links
Link to specific files:
```markdown
[Hello Example](src/bin/hello.rs)
[Scenario](src/bin/{scenario-name}.rs)
[Wrapper Module](src/{service}.rs)
```

### Line Number Links
Link to specific lines (GitHub):
```markdown
[Create Resource Function](src/{service}.rs#L123-L145)
```

## Multi-Language Documentation

### Cross-Language References
When documenting cross-language examples:

```markdown
## Related Examples

This example is also available in other languages:
- [Python](../../python/example_code/{service}/)
- [Java](../../javav2/{service}/)
- [.NET](../../dotnetv4/{Service}/)
```

## Documentation Best Practices
- ✅ **Keep README concise** but complete
- ✅ **Include working examples** that users can copy
- ✅ **Link to official docs** for detailed information
- ✅ **Show example output** to set expectations
- ✅ **Document prerequisites** clearly
- ✅ **Include troubleshooting** for common issues
- ✅ **Update regularly** as code changes
- ✅ **Test all commands** in README
- ✅ **Use consistent formatting** across services
- ✅ **Validate links** before committing

## Common Documentation Issues
- ❌ **Outdated examples** that don't match current code
- ❌ **Broken links** to moved or renamed files
- ❌ **Missing prerequisites** causing user confusion
- ❌ **Incorrect commands** that don't work
- ❌ **Inconsistent formatting** across services
- ❌ **Missing error handling** in examples
- ❌ **No example output** leaving users uncertain
- ❌ **Incomplete installation** instructions

This ensures documentation stays synchronized with code changes and provides a great user experience.
