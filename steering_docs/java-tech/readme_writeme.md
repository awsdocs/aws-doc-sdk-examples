# Java README/WRITEME and Documentation Generation

## Purpose
Generate and update README files and documentation using the writeme tool to ensure consistency and completeness.

## Requirements
- **Automated Generation**: Use writeme tool for README generation
- **Metadata Dependency**: Requires complete metadata files
- **Virtual Environment**: Run writeme in isolated environment
- **Validation**: Ensure all documentation is up-to-date

## File Structure
```
javav2/example_code/{service}/
├── README.md                   # Generated service README
├── pom.xml                     # Maven dependencies
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
python -m writeme --languages Java:2 --services {service}
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
# {AWS Service} code examples for the SDK for Java 2.x

## Overview

This is a workspace where you can find the following AWS SDK for Java 2.x 
{AWS Service} examples.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege.

## Code examples

### Actions

The following examples show you how to perform actions using the AWS SDK for Java 2.x.

* [Create a resource](src/main/java/com/example/{service}/{Service}Actions.java#L123) (`CreateResource`)
* [Get a resource](src/main/java/com/example/{service}/{Service}Actions.java#L456) (`GetResource`)

### Scenarios

The following examples show you how to implement common scenarios.

* [Get started with resources](src/main/java/com/example/{service}/{Service}Scenario.java) - Learn the basics by creating and managing resources.

### Hello

* [Hello {Service}](src/main/java/com/example/{service}/Hello{Service}.java) - Get started with {AWS Service}.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured.
- Java 17 or later
- Maven 3.6 or later

## Install

To build and run the examples, navigate to the directory that contains a `pom.xml` file and run the following command:

```
mvn compile
```

## Run the examples

### Instructions

All examples can be run individually. For example:

```
mvn exec:java -Dexec.mainClass="com.example.{service}.Hello{Service}" -Dexec.args="us-east-1"
```

### Hello {Service}

This example shows you how to get started using {AWS Service}.

```
mvn exec:java -Dexec.mainClass="com.example.{service}.Hello{Service}" -Dexec.args="us-east-1"
```

### Get started with {Service} resources

This interactive scenario runs at a command prompt and shows you how to use {AWS Service} to do the following:

1. Create a resource
2. Use the resource  
3. Clean up resources

```
mvn exec:java -Dexec.mainClass="com.example.{service}.{Service}Scenario" -Dexec.args="us-east-1"
```

## Run the tests

Unit tests in this module use JUnit 5. To run all of the tests, 
run the following in your [GitHub root]/javav2/example_code/{service} folder.

```
mvn test
```

## Additional resources

- [{AWS Service} User Guide](https://docs.aws.amazon.com/{service}/latest/ug/)
- [{AWS Service} API Reference](https://docs.aws.amazon.com/{service}/latest/APIReference/)
- [AWS SDK for Java 2.x ({AWS Service})](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/{service}/package-summary.html)
```

## Documentation Dependencies

### Required Files for README Generation
- ✅ **Metadata file**: `.doc_gen/metadata/{service}_metadata.yaml`
- ✅ **Code files**: All referenced Java files must exist
- ✅ **Snippet tags**: All snippet tags in metadata must exist in code
- ✅ **Build file**: `pom.xml` with dependencies

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
python -m writeme --languages Java:2 --services {service} --verbose

# Validate specific metadata file
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Check for missing snippet tags
grep -r "snippet-start" javav2/example_code/{service}/
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

## Java-Specific README Elements

### Maven Commands
README includes proper Maven commands for:
- **Compilation**: `mvn compile`
- **Execution**: `mvn exec:java -Dexec.mainClass="..." -Dexec.args="..."`
- **Testing**: `mvn test`

### Prerequisites
Java-specific prerequisites:
- Java 17 or later
- Maven 3.6 or later
- AWS credentials configured

### Code Links
Links point to specific Java files:
- Actions: `src/main/java/com/example/{service}/{Service}Actions.java`
- Scenarios: `src/main/java/com/example/{service}/{Service}Scenario.java`
- Hello: `src/main/java/com/example/{service}/Hello{Service}.java`

## Integration with CI/CD

### Automated README Validation
```bash
# In CI/CD pipeline, validate README is up-to-date
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Java:2 --services {service} --check

# Exit with error if README needs updates
if git diff --exit-code javav2/example_code/{service}/README.md; then
    echo "README is up-to-date"
else
    echo "README needs to be regenerated"
    exit 1
fi
```

### Build Integration
Ensure README generation is part of the build process:

```xml
<!-- In pom.xml -->
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>generate-readme</id>
            <phase>validate</phase>
            <goals>
                <goal>exec</goal>
            </goals>
            <configuration>
                <executable>python</executable>
                <workingDirectory>.tools/readmes</workingDirectory>
                <arguments>
                    <argument>-m</argument>
                    <argument>writeme</argument>
                    <argument>--languages</argument>
                    <argument>Java:2</argument>
                    <argument>--services</argument>
                    <argument>{service}</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This ensures documentation stays synchronized with code changes and maintains consistency across all Java examples.