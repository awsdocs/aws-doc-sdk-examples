# Python Metadata Generation

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
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/{service}
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.{service}.CreateResource
  services:
    {service}: {CreateResource}

{service}_GetResource:
  title: Get a &{ServiceAbbrev}; resource
  title_abbrev: Get a resource
  synopsis: get a &{ServiceAbbrev}; resource.
  category: Actions
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/{service}
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.{service}.GetResource
  services:
    {service}: {GetResource}

{service}_Scenario:
  title: Get started with &{ServiceAbbrev}; resources
  title_abbrev: Get started with resources
  synopsis: learn the basics of &{ServiceAbbrev}; by creating resources and managing them.
  category: Scenarios
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/{service}
          excerpts:
            - description: Create a {Service} wrapper class to manage operations.
              snippet_tags:
                - python.example_code.{service}.{Service}Wrapper
            - description: Run an interactive scenario demonstrating {Service} basics.
              snippet_tags:
                - python.example_code.{service}.{Service}Scenario
  services:
    {service}: {CreateResource, GetResource, ListResources, DeleteResource}

{service}_Hello:
  title: Hello &{ServiceAbbrev};
  title_abbrev: Hello &{ServiceAbbrev};
  synopsis: get started using &{ServiceAbbrev};.
  category: Hello
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/{service}
          excerpts:
            - description:
              snippet_tags:
                - python.example_code.{service}.Hello
  services:
    {service}: {ListResources}
```

## Snippet Tag Requirements

### Code Snippet Tags
All code must include proper snippet tags that match metadata:

```python
# snippet-start:[python.example_code.{service}.{ActionName}]
def action_method(self):
    """Action implementation"""
    pass
# snippet-end:[python.example_code.{service}.{ActionName}]
```

### Wrapper Class Tags
```python
# snippet-start:[python.example_code.{service}.{Service}Wrapper]
class {Service}Wrapper:
    """Wrapper class implementation"""
    pass
# snippet-end:[python.example_code.{service}.{Service}Wrapper]
```

### Scenario Tags
```python
# snippet-start:[python.example_code.{service}.{Service}Scenario]
class {Service}Scenario:
    """Scenario class implementation"""
    pass
# snippet-end:[python.example_code.{service}.{Service}Scenario]
```

### Hello Tags
```python
# snippet-start:[python.example_code.{service}.Hello]
def hello_{service}():
    """Hello implementation"""
    pass
# snippet-end:[python.example_code.{service}.Hello]
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
- ✅ **languages.Python.versions**: SDK version information
- ✅ **github**: Path to example code
- ✅ **snippet_tags**: Matching tags from code
- ✅ **services**: Service operations used

### Validation Commands
```bash
# Validate metadata with writeme tool
cd .tools/readmes
python -m writeme --languages Python:3 --services {service}
```

## Common Metadata Errors
- ❌ **Custom metadata keys** when specification exists
- ❌ **Mismatched snippet tags** between code and metadata
- ❌ **Missing service operations** in services section
- ❌ **Incorrect github paths** to example code
- ❌ **Wrong service abbreviations** in titles
- ❌ **Missing required fields** in metadata structure

## Metadata Generation Workflow
1. **Read specification** for exact metadata requirements
2. **Extract metadata table** from specification
3. **Create metadata file** using specification keys
4. **Add snippet tags** to all code files
5. **Validate metadata** with writeme tool
6. **Fix any validation errors** before completion