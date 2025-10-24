# Python README/WRITEME and Documentation Generation

## Purpose
Generate and update README files and documentation using the writeme tool to ensure consistency and completeness.

## Requirements
- **Automated Generation**: Use writeme tool for README generation
- **Metadata Dependency**: Requires complete metadata files
- **Virtual Environment**: Run writeme in isolated environment
- **Validation**: Ensure all documentation is up-to-date

## File Structure
```
python/example_code/{service}/
├── README.md                   # Generated service README
├── requirements.txt            # Dependencies
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
python -m writeme --languages Python:3 --services {service}
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
# {AWS Service} code examples for the SDK for Python

## Overview

This is a workspace where you can find the following AWS SDK for Python (Boto3) 
{AWS Service} examples.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege.

## Code examples

### Actions

The following examples show you how to perform actions using the AWS SDK for Python (Boto3).

* [Create a resource](action_file.py#L123) (`CreateResource`)
* [Get a resource](action_file.py#L456) (`GetResource`)

### Scenarios

The following examples show you how to implement common scenarios.

* [Get started with resources](scenario_file.py) - Learn the basics by creating and managing resources.

### Hello

* [Hello {Service}](hello_file.py) - Get started with {AWS Service}.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region configured.
- Python 3.6 or later
- Boto3 1.26.137 or later
- PyTest 5.3.5 or later (to run unit tests)

## Install

Install the prerequisites using pip:

```
pip install -r requirements.txt
```

## Run the examples

### Instructions

All examples can be run individually. For example:

```
python hello_{service}.py
```

### Hello {Service}

This example shows you how to get started using {AWS Service}.

```
python hello_{service}.py
```

### Get started with {Service} resources

This interactive scenario runs at a command prompt and shows you how to use {AWS Service} to do the following:

1. Create a resource
2. Use the resource  
3. Clean up resources

```
python scenario_{service}_basics.py
```

## Run the tests

Unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/{service} folder.

```
python -m pytest
```

## Additional resources

- [{AWS Service} User Guide](https://docs.aws.amazon.com/{service}/latest/ug/)
- [{AWS Service} API Reference](https://docs.aws.amazon.com/{service}/latest/APIReference/)
- [AWS SDK for Python ({AWS Service})](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/{service}.html)
```

## Documentation Dependencies

### Required Files for README Generation
- ✅ **Metadata file**: `.doc_gen/metadata/{service}_metadata.yaml`
- ✅ **Code files**: All referenced Python files must exist
- ✅ **Snippet tags**: All snippet tags in metadata must exist in code
- ✅ **Requirements file**: `requirements.txt` with dependencies

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
python -m writeme --languages Python:3 --services {service} --verbose

# Validate specific metadata file
python -c "import yaml; yaml.safe_load(open('.doc_gen/metadata/{service}_metadata.yaml'))"

# Check for missing snippet tags
grep -r "snippet-start" python/example_code/{service}/
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
python -m writeme --languages Python:3 --services {service} --check

# Exit with error if README needs updates
if git diff --exit-code python/example_code/{service}/README.md; then
    echo "README is up-to-date"
else
    echo "README needs to be regenerated"
    exit 1
fi
```

This ensures documentation stays synchronized with code changes.