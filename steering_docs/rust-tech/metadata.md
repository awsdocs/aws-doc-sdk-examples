# Rust Metadata Generation

## Purpose
Generate documentation metadata files that integrate with AWS Documentation pipeline for snippet extraction and cross-referencing.

## Requirements
- **Specification First**: Always check service specification for exact metadata keys
- **Snippet Tags**: Match snippet tags in code exactly
- **Complete Coverage**: Include all actions and scenarios from specification

## File Structure
```
.doc_gen/metadata/
├── {service}_metadata.yaml     # Service metadata file
```

## Metadata Discovery Process

### Step 1: Check Service Specification
**CRITICAL**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first for metadata requirements.

Look for the metadata table:
```markdown
## Metadata

|action / scenario	|metadata file	|metadata key	|
|---	|---	|---	|
|`CreateDetector`	|{service}_metadata.yaml	|{service}_CreateDetector	|
|`GetDetector`	|{service}_metadata.yaml	|{service}_GetDetector	|
|`Service Basics Scenario`	|{service}_metadata.yaml	|{service}_Scenario	|
```

### Step 2: Use Exact Metadata Keys
**NEVER** create custom metadata keys when specification defines them. Use the exact keys from the specification table.

## Metadata File Pattern
```yaml
# .doc_gen/metadata/{service}_metadata.yaml

{service}_CreateResource:
  title: Create a &{ServiceAbbrev}; resource
  title_abbrev: Create a resource
  synopsis: create a &{ServiceAbbrev}; resource.
  category: Actions
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/{service}
          excerpts:
            - description:
              snippet_tags:
                - {service}.rust.create_resource
  services:
    {service}: {CreateResource}

{service}_GetResource:
  title: Get a &{ServiceAbbrev}; resource
  title_abbrev: Get a resource
  synopsis: get a &{ServiceAbbrev}; resource.
  category: Actions
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/{service}
          excerpts:
            - description:
              snippet_tags:
                - {service}.rust.get_resource
  services:
    {service}: {GetResource}

{service}_Scenario:
  title: Get started with &{ServiceAbbrev}; resources
  title_abbrev: Get started with resources
  synopsis: learn the basics of &{ServiceAbbrev}; by creating resources and managing them.
  category: Scenarios
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/{service}
          excerpts:
            - description: Run an interactive scenario demonstrating {Service} basics.
              snippet_tags:
                - {service}.rust.scenario
  services:
    {service}: {CreateResource, GetResource, ListResources, DeleteResource}

{service}_Hello:
  title: Hello &{ServiceAbbrev};
  title_abbrev: Hello &{ServiceAbbrev};
  synopsis: get started using &{ServiceAbbrev};.
  category: Hello
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/{service}
          excerpts:
            - description:
              snippet_tags:
                - {service}.rust.hello
  services:
    {service}: {ListResources}
```

## Snippet Tag Requirements

### Code Snippet Tags
All code must include proper snippet tags that match metadata:

```rust
// snippet-start:[{service}.rust.{action_name}]
pub async fn action_function(client: &Client) -> Result<(), Error> {
    // Action implementation
}
// snippet-end:[{service}.rust.{action_name}]
```

### Hello Tags
```rust
// snippet-start:[{service}.rust.hello]
#[tokio::main]
async fn main() -> Result<(), aws_sdk_{service}::Error> {
    // Hello implementation
}
// snippet-end:[{service}.rust.hello]
```

### Scenario Tags
```rust
// snippet-start:[{service}.rust.scenario]
pub async fn run_scenario(client: Client) -> Result<(), ScenarioError> {
    // Scenario implementation
}
// snippet-end:[{service}.rust.scenario]
```

## Snippet Tag Naming Conventions

### Format
`[{service}.rust.{action_name}]`

- **Service name**: Use snake_case (e.g., `s3`, `dynamodb`, `ec2`)
- **Language**: Always `rust` for Rust examples
- **Action name**: Use snake_case (e.g., `create_bucket`, `list_tables`, `hello`)

### Examples
```rust
// ✅ CORRECT
// snippet-start:[s3.rust.create_bucket]
// snippet-start:[dynamodb.rust.list_tables]
// snippet-start:[ec2.rust.describe_instances]
// snippet-start:[guardduty.rust.hello]

// ❌ WRONG - Don't use these formats
// snippet-start:[rust.example_code.s3.create_bucket]
// snippet-start:[S3.Rust.CreateBucket]
// snippet-start:[s3-rust-create-bucket]
```

## Service Abbreviations

Common service abbreviations for metadata:
- **GuardDuty**: GD
- **DynamoDB**: DDB  
- **Simple Storage Service**: S3
- **Elastic Compute Cloud**: EC2
- **Identity and Access Management**: IAM
- **Key Management Service**: KMS
- **Simple Notification Service**: SNS
- **Simple Queue Service**: SQS
- **Relational Database Service**: RDS
- **Lambda**: Lambda
- **CloudWatch**: CW

## Metadata Categories

### Actions
Individual service operations (CreateResource, GetResource, etc.)

### Scenarios  
Multi-step workflows demonstrating service usage

### Hello
Simple introduction examples

### Cross-service
Examples spanning multiple AWS services

## Metadata Validation

### Required Fields
- ✅ **title**: Descriptive title with service abbreviation
- ✅ **title_abbrev**: Shortened title
- ✅ **synopsis**: Brief description of what the example does
- ✅ **category**: Actions, Scenarios, Hello, or Cross-service
- ✅ **languages.Rust.versions**: SDK version information (sdk_version: 1)
- ✅ **github**: Path to example code (rustv1/examples/{service})
- ✅ **snippet_tags**: Matching tags from code
- ✅ **services**: Service operations used

### Validation Commands
```bash
# Validate metadata with writeme tool
cd .tools/readmes
python -m writeme --languages Rust:1 --services {service}
```

## Multiple Excerpts Pattern
For scenarios with multiple code sections:

```yaml
{service}_Scenario:
  title: Get started with &{ServiceAbbrev}; resources
  title_abbrev: Get started with resources
  synopsis: learn the basics of &{ServiceAbbrev}; by creating resources and managing them.
  category: Scenarios
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/{service}
          excerpts:
            - description: Define the scenario error type.
              snippet_tags:
                - {service}.rust.scenario_error
            - description: Run the main scenario workflow.
              snippet_tags:
                - {service}.rust.scenario
            - description: Implement the setup phase.
              snippet_tags:
                - {service}.rust.setup_phase
            - description: Implement the cleanup phase.
              snippet_tags:
                - {service}.rust.cleanup_phase
  services:
    {service}: {CreateResource, GetResource, ListResources, DeleteResource}
```

## Cross-Service Examples
For examples using multiple AWS services:

```yaml
{service1}_{service2}_CrossService:
  title: Use &{Service1Abbrev}; with &{Service2Abbrev};
  title_abbrev: Cross-service example
  synopsis: use &{Service1Abbrev}; with &{Service2Abbrev}; to accomplish a task.
  category: Cross-service
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/cross_service/{example_name}
          excerpts:
            - description:
              snippet_tags:
                - {service1}.{service2}.rust.cross_service
  services:
    {service1}: {Operation1, Operation2}
    {service2}: {Operation3, Operation4}
```

## Common Metadata Errors
- ❌ **Custom metadata keys** when specification exists
- ❌ **Mismatched snippet tags** between code and metadata
- ❌ **Missing service operations** in services section
- ❌ **Incorrect github paths** to example code (should be rustv1/examples/{service})
- ❌ **Wrong service abbreviations** in titles
- ❌ **Missing required fields** in metadata structure
- ❌ **Wrong SDK version** (should be sdk_version: 1 for Rust)
- ❌ **Incorrect snippet tag format** (should be {service}.rust.{action})

## Metadata Generation Workflow
1. **Read specification** for exact metadata requirements
2. **Extract metadata table** from specification
3. **Create metadata file** using specification keys
4. **Add snippet tags** to all code files
5. **Validate metadata** with writeme tool
6. **Fix any validation errors** before completion

## Example: Complete Metadata Entry
```yaml
s3_ListBuckets:
  title: List &S3; buckets using an &AWS; SDK
  title_abbrev: List buckets
  synopsis: list &S3; buckets.
  category: Actions
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/s3
          excerpts:
            - description: List all buckets in your account.
              snippet_tags:
                - s3.rust.list_buckets
  services:
    s3: {ListBuckets}

s3_Hello:
  title: Hello &S3;
  title_abbrev: Hello &S3;
  synopsis: get started using &S3;.
  category: Hello
  languages:
    Rust:
      versions:
        - sdk_version: 1
          github: rustv1/examples/s3
          excerpts:
            - description: List buckets to demonstrate basic &S3; connectivity.
              snippet_tags:
                - s3.rust.hello
  services:
    s3: {ListBuckets}
```

## Best Practices
- ✅ **Follow specification exactly** for metadata keys
- ✅ **Use consistent snippet tag format** across all Rust examples
- ✅ **Include all service operations** in services section
- ✅ **Provide clear descriptions** for each excerpt
- ✅ **Validate before committing** using writeme tool
- ✅ **Use correct SDK version** (1 for Rust)
- ✅ **Match github paths** to actual file locations
- ✅ **Use snake_case** for service names in snippet tags
