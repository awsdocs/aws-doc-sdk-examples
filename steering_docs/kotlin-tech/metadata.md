# Kotlin Metadata Generation

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
    Kotlin:
      versions:
        - sdk_version: 1
          github: kotlin/services/{service}
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - {service}.kotlin.create_resource.main
  services:
    {service}: {CreateResource}

{service}_GetResource:
  title: Get a &{ServiceAbbrev}; resource
  title_abbrev: Get a resource
  synopsis: get a &{ServiceAbbrev}; resource.
  category: Actions
  languages:
    Kotlin:
      versions:
        - sdk_version: 1
          github: kotlin/services/{service}
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - {service}.kotlin.get_resource.main
  services:
    {service}: {GetResource}

{service}_Scenario:
  title: Get started with &{ServiceAbbrev}; resources
  title_abbrev: Get started with resources
  synopsis: learn the basics of &{ServiceAbbrev}; by creating resources and managing them.
  category: Scenarios
  languages:
    Kotlin:
      versions:
        - sdk_version: 1
          github: kotlin/services/{service}
          sdkguide:
          excerpts:
            - description: Create a {Service} actions class to manage operations.
              snippet_tags:
                - {service}.kotlin.{service}_actions.main
            - description: Run an interactive scenario demonstrating {Service} basics.
              snippet_tags:
                - {service}.kotlin.{service}_scenario.main
  services:
    {service}: {CreateResource, GetResource, ListResources, DeleteResource}

{service}_Hello:
  title: Hello &{ServiceAbbrev};
  title_abbrev: Hello &{ServiceAbbrev};
  synopsis: get started using &{ServiceAbbrev};.
  category: Hello
  languages:
    Kotlin:
      versions:
        - sdk_version: 1
          github: kotlin/services/{service}
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - {service}.kotlin.hello.main
  services:
    {service}: {ListResources}
```

## Snippet Tag Requirements

### Code Snippet Tags
All code must include proper snippet tags that match metadata:

```kotlin
suspend fun {actionMethod}({service}Client: {Service}Client, param: String): {ActionName}Response {
    // Action implementation
}
```

### Actions Class Tags
```kotlin
class {Service}Actions {
    // Actions class implementation
}
```

### Scenario Tags
```kotlin
class {Service}Scenario {
    // Scenario class implementation
}
```

### Hello Tags
```kotlin
suspend fun main() {
    // Hello implementation
}
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
- **Inspector**: Inspector

## Metadata Categories

### Actions
Individual service operations (CreateResource, GetResource, etc.)

### Scenarios  
Multi-step workflows demonstrating service usage

### Hello
Simple introduction examples

### Cross-service
Examples spanning multiple AWS services

## Kotlin-Specific Metadata Fields

### SDK Version
Always use `sdk_version: 1` for Kotlin SDK examples

### GitHub Path
Use `kotlin/services/{service}` for Kotlin examples

### SDK Guide
Include `sdkguide:` field when documentation links are available

### Snippet Tag Format
Use format: `{service}.kotlin.{operation}.main`

## Metadata Validation

### Required Fields
- ✅ **title**: Descriptive title with service abbreviation
- ✅ **title_abbrev**: Shortened title
- ✅ **synopsis**: Brief description of what the example does
- ✅ **category**: Actions, Scenarios, Hello, or Cross-service
- ✅ **languages.Kotlin.versions**: SDK version information
- ✅ **github**: Path to example code
- ✅ **snippet_tags**: Matching tags from code
- ✅ **services**: Service operations used

### Validation Commands
```bash
# Validate metadata with writeme tool
cd .tools/readmes
python -m writeme --languages Kotlin:1 --services {service}
```

## Common Metadata Errors
- ❌ **Custom metadata keys** when specification exists
- ❌ **Mismatched snippet tags** between code and metadata
- ❌ **Missing service operations** in services section
- ❌ **Incorrect github paths** to example code
- ❌ **Wrong service abbreviations** in titles
- ❌ **Missing required fields** in metadata structure
- ❌ **Wrong SDK version** (should be 1 for Kotlin)

## Metadata Generation Workflow
1. **Read specification** for exact metadata requirements
2. **Extract metadata table** from specification
3. **Create metadata file** using specification keys
4. **Add snippet tags** to all code files
5. **Validate metadata** with writeme tool
6. **Fix any validation errors** before completion

## Kotlin-Specific Considerations

### Package Structure
Kotlin examples are organized in packages:
```
kotlin/services/{service}/src/main/kotlin/com/kotlin/{service}/
```

### File Naming
- Hello examples: `Hello{Service}.kt`
- Actions classes: `{Service}Actions.kt`
- Scenarios: `{Service}Basics.kt` or `{Service}Scenario.kt`

### Multiple Excerpts
For complex examples, use multiple excerpts:
```yaml
excerpts:
  - description: Create a {Service} actions class to manage operations.
    snippet_tags:
      - {service}.kotlin.{service}_actions.main
  - description: Run an interactive scenario demonstrating {Service} basics.
    snippet_tags:
      - {service}.kotlin.{service}_scenario.main
```
