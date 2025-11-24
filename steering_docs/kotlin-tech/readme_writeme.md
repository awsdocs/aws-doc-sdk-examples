# Kotlin README/WRITEME and Documentation Generation

## Purpose
Generate and update README files and documentation using the writeme tool to ensure consistency and completeness.

## Requirements
- **Automated Generation**: Use writeme tool for README generation
- **Metadata Dependency**: Requires complete metadata files
- **Virtual Environment**: Run writeme in isolated environment
- **Validation**: Ensure all documentation is up-to-date

## File Structure
```
kotlin/services/{service}/
├── README.md                   # Generated service README
├── build.gradle.kts            # Gradle dependencies
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
python -m writeme --languages Kotlin:1 --services {service}
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
# {AWS Service} code examples for the SDK for Kotlin

## Overview

This is a workspace where you can find the following AWS SDK for Kotlin 
{AWS Service} examples.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege.

## Code examples

### Actions

The following examples show you how to perform actions using the AWS SDK for Kotlin.

* [Create a resource](src/main/kotlin/com/kotlin/{service}/{Service}Actions.kt#L123) (`CreateResource`)
* [Get a resource](src/main/kotlin/com/kotlin/{service}/{Service}Actions.kt#L456) (`GetResource`)

### Scenarios

The following examples show you how to implement common scenarios.

* [Get started with resources](src/main/kotlin/com/kotlin/{service}/{Service}Basics.kt) - Learn the basics by creating and managing resources.

### Hello

* [Hello {Service}](src/main/kotlin/com/kotlin/{service}/Hello{Service}.kt) - Get started with {AWS Service}.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured.
- Kotlin 1.9 or later
- Gradle 8.0 or later

## Install

To build and run the examples, navigate to the directory that contains a `build.gradle.kts` file and run the following command:

```
./gradlew build
```

## Run the examples

### Instructions

All examples can be run individually. For example:

```
./gradlew run --args="us-east-1"
```

### Hello {Service}

This example shows you how to get started using {AWS Service}.

```
./gradlew run --args="us-east-1"
```

### Get started with {Service} resources

This interactive scenario runs at a command prompt and shows you how to use {AWS Service} to do the following:

1. Create a resource
2. Use the resource  
3. Clean up resources

```
./gradlew run --args="us-east-1"
```

## Run the tests

Unit tests in this module use JUnit 5 and MockK. To run all of the tests, 
run the following in your [GitHub root]/kotlin/services/{service} folder.

```
./gradlew test
```

## Additional resources

- [{AWS Service} User Guide](https://docs.aws.amazon.com/{service}/latest/ug/)
- [{AWS Service} API Reference](https://docs.aws.amazon.com/{service}/latest/APIReference/)
- [AWS SDK for Kotlin ({AWS Service})](https://sdk.amazonaws.com/kotlin/api/latest/{service}/index.html)
```

## Documentation Dependencies

### Required Files for README Generation
- ✅ **Metadata file**: `.doc_gen/metadata/{service}_metadata.yaml`
- ✅ **Code files**: All referenced Kotlin files must exist
- ✅ **Snippet tags**: All snippet tags in metadata must exist in code
- ✅ **Build file**: `build.gradle.kts` with dependencies

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
python -m writeme --languages Kotlin:1 --services {service} --verbose

# Validate specific metadata file
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Check for missing snippet tags
grep -r "snippet-start" kotlin/services/{service}/
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

## Kotlin-Specific README Elements

### Gradle Commands
README includes proper Gradle commands for:
- **Compilation**: `./gradlew build`
- **Execution**: `./gradlew run --args="..."`
- **Testing**: `./gradlew test`

### Prerequisites
Kotlin-specific prerequisites:
- Kotlin 1.9 or later
- Gradle 8.0 or later
- AWS credentials configured

### Code Links
Links point to specific Kotlin files:
- Actions: `src/main/kotlin/com/kotlin/{service}/{Service}Actions.kt`
- Scenarios: `src/main/kotlin/com/kotlin/{service}/{Service}Basics.kt`
- Hello: `src/main/kotlin/com/kotlin/{service}/Hello{Service}.kt`

### Coroutines Information
Include information about coroutines usage:
```markdown
## Coroutines

This project uses Kotlin coroutines for asynchronous operations. All AWS SDK calls are suspend functions that should be called from within a coroutine scope.

```kotlin
suspend fun main() {
    // Your async code here
}
```
```

## Integration with CI/CD

### Automated README Validation
```bash
# In CI/CD pipeline, validate README is up-to-date
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Kotlin:1 --services {service} --check

# Exit with error if README needs updates
if git diff --exit-code kotlin/services/{service}/README.md; then
    echo "README is up-to-date"
else
    echo "README needs to be regenerated"
    exit 1
fi
```

### Build Integration
Ensure README generation is part of the build process:

```kotlin
// In build.gradle.kts
tasks.register<Exec>("generateReadme") {
    group = "documentation"
    description = "Generate README using writeme tool"
    
    workingDir = file("../../.tools/readmes")
    commandLine = listOf(
        "python", "-m", "writeme",
        "--languages", "Kotlin:1",
        "--services", "{service}"
    )
}

tasks.named("build") {
    dependsOn("generateReadme")
}
```

## Kotlin-Specific Documentation Features

### Suspend Function Documentation
Document suspend functions properly:
```markdown
### Async Operations

All AWS operations in this example are implemented as suspend functions:

```kotlin
suspend fun createResource(client: {Service}Client): String {
    // Implementation
}
```

Make sure to call these functions from within a coroutine scope.
```

### Extension Function Documentation
Document extension functions when used:
```markdown
### Extension Functions

This example includes extension functions for common operations:

```kotlin
fun List<Resource>.filterActive(): List<Resource> {
    return filter { it.status == ResourceStatus.Active }
}
```
```

### Null Safety Documentation
Highlight Kotlin's null safety features:
```markdown
### Null Safety

This example leverages Kotlin's null safety features:

```kotlin
val resource: Resource? = getResource(id)
resource?.let { 
    println("Resource found: ${it.name}")
}
```
```

This ensures documentation stays synchronized with code changes and maintains consistency across all Kotlin examples while highlighting Kotlin-specific features and patterns.