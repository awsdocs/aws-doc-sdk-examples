# Java Metadata Generation

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
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/{service}
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - {service}.java2.create_resource.main
  services:
    {service}: {CreateResource}

{service}_GetResource:
  title: Get a &{ServiceAbbrev}; resource
  title_abbrev: Get a resource
  synopsis: get a &{ServiceAbbrev}; resource.
  category: Actions
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/{service}
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - {service}.java2.get_resource.main
  services:
    {service}: {GetResource}

{service}_Scenario:
  title: Get started with &{ServiceAbbrev}; resources
  title_abbrev: Get started with resources
  synopsis: learn the basics of &{ServiceAbbrev}; by creating resources and managing them.
  category: Scenarios
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/{service}
          sdkguide:
          excerpts:
            - description: Create a {Service} actions class to manage operations.
              snippet_tags:
                - {service}.java2.{service}_actions.main
            - description: Run an interactive scenario demonstrating {Service} basics.
              snippet_tags:
                - {service}.java2.{service}_scenario.main
  services:
    {service}: {CreateResource, GetResource, ListResources, DeleteResource}

{service}_Hello:
  title: Hello &{ServiceAbbrev};
  title_abbrev: Hello &{ServiceAbbrev};
  synopsis: get started using &{ServiceAbbrev};.
  category: Hello
  languages:
    Java:
      versions:
        - sdk_version: 2
          github: javav2/example_code/{service}
          sdkguide:
          excerpts:
            - description:
              snippet_tags:
                - {service}.java2.hello.main
  services:
    {service}: {ListResources}
```

## Snippet Tag Requirements

### Code Snippet Tags
All code must include proper snippet tags that match metadata:

```java
// snippet-start:[{service}.java2.{action_name}.main]
public static void {actionMethod}({Service}Client {service}Client) {
    // Action implementation
}
// snippet-end:[{service}.java2.{action_name}.main]
```

### Actions Class Tags
```java
// snippet-start:[{service}.java2.{service}_actions.main]
public class {Service}Actions {
    // Actions class implementation
}
// snippet-end:[{service}.java2.{service}_actions.main]
```

### Scenario Tags
```java
// snippet-start:[{service}.java2.{service}_scenario.main]
public class {Service}Scenario {
    // Scenario class implementation
}
// snippet-end:[{service}.java2.{service}_scenario.main]
```

### Hello Tags
```java
// snippet-start:[{service}.java2.hello.main]
public class Hello{Service} {
    // Hello implementation
}
// snippet-end:[{service}.java2.hello.main]
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

## Java-Specific Metadata Fields

### SDK Version
Always use `sdk_version: 2` for Java V2 SDK examples

### GitHub Path
Use `javav2/example_code/{service}` for Java V2 examples

### SDK Guide
Include `sdkguide:` field when documentation links are available

### Snippet Tag Format
Use format: `{service}.java2.{operation}.main`

## Metadata Validation

### Required Fields
- ✅ **title**: Descriptive title with service abbreviation
- ✅ **title_abbrev**: Shortened title
- ✅ **synopsis**: Brief description of what the example does
- ✅ **category**: Actions, Scenarios, Hello, or Cross-service
- ✅ **languages.Java.versions**: SDK version information
- ✅ **github**: Path to example code
- ✅ **snippet_tags**: Matching tags from code
- ✅ **services**: Service operations used

### Validation Commands
```bash
# Validate metadata with writeme tool
cd .tools/readmes
python -m writeme --languages Java:2 --services {service}
```

## Common Metadata Errors
- ❌ **Custom metadata keys** when specification exists
- ❌ **Mismatched snippet tags** between code and metadata
- ❌ **Missing service operations** in services section
- ❌ **Incorrect github paths** to example code
- ❌ **Wrong service abbreviations** in titles
- ❌ **Missing required fields** in metadata structure
- ❌ **Wrong SDK version** (should be 2 for Java V2)

## Metadata Generation Workflow
1. **Read specification** for exact metadata requirements
2. **Extract metadata table** from specification
3. **Create metadata file** using specification keys
4. **Add snippet tags** to all code files
5. **Validate metadata** with writeme tool
6. **Fix any validation errors** before completion

## Java-Specific Considerations

### Package Structure
Java examples are organized in packages:
```
javav2/example_code/{service}/src/main/java/com/example/{service}/
```

### Class Naming
- Hello examples: `Hello{Service}.java`
- Actions classes: `{Service}Actions.java`
- Scenarios: `{Service}Scenario.java`

### Snippet Tag Placement
Place snippet tags around entire classes or methods:
```java
// snippet-start:[{service}.java2.hello.main]
public class Hello{Service} {
    // Entire class content
}
// snippet-end:[{service}.java2.hello.main]
```

### Multiple Excerpts
For complex examples, use multiple excerpts:
```yaml
excerpts:
  - description: Create a {Service} actions class to manage operations.
    snippet_tags:
      - {service}.java2.{service}_actions.main
  - description: Run an interactive scenario demonstrating {Service} basics.
    snippet_tags:
      - {service}.java2.{service}_scenario.main
```