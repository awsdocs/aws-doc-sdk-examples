# Python Technology Stack & Build System

## Python 3.6+ Development Environment

### Build Tools & Dependencies
- **Package Manager**: pip
- **Virtual Environment**: venv
- **Testing Framework**: pytest
- **Code Formatting**: black
- **Linting**: pylint, flake8
- **Type Checking**: mypy (where applicable)

### Common Build Commands

```bash
# Environment Setup
python -m venv .venv
source .venv/bin/activate  # Linux/macOS
.venv\Scripts\activate     # Windows

# Dependencies
pip install -r requirements.txt

# Testing
python -m pytest -m "not integ"  # Unit tests
python -m pytest -m "integ"      # Integration tests

# Code Quality
black .                           # Format code
pylint --rcfile=.github/linters/.python-lint .
```

### Python-Specific Pattern Requirements

#### File Naming Conventions
- Use snake_case for all Python files
- Service prefix pattern: `{service}_action.py` (e.g., `s3_list_buckets.py`)
- Scenario files: `{service}_basics.py` or `{service}_scenario.py`
- Test files: `test_{service}_action.py`

#### Hello Scenario Structure
- **File naming**: `{service}_hello.py` or hello function in main module
- **Function naming**: `hello_{service}()` or `main()`
- **Documentation**: Include docstrings explaining the hello example purpose

#### Code Structure Standards
- **Imports**: Follow PEP 8 import ordering (standard library, third-party, local)
- **Functions**: Use descriptive names with snake_case
- **Classes**: Use PascalCase for class names
- **Constants**: Use UPPER_CASE for constants
- **Type Hints**: Include type annotations where beneficial

#### Error Handling Patterns
```python
import boto3
from botocore.exceptions import ClientError, NoCredentialsError

def example_function():
    try:
        # AWS service call
        response = client.operation()
        return response
    except ClientError as e:
        error_code = e.response['Error']['Code']
        if error_code == 'SpecificError':
            # Handle specific error
            pass
        else:
            # Handle general client errors
            raise
    except NoCredentialsError:
        # Handle credential issues
        raise
```

#### Testing Standards
- **Test markers**: Use `@pytest.mark.integ` for integration tests
- **Fixtures**: Create reusable fixtures for AWS resources
- **Cleanup**: Ensure proper resource cleanup in tests
- **Mocking**: Use `boto3` stubber for unit tests when appropriate

#### Documentation Requirements
- **Module docstrings**: Include purpose and usage examples
- **Function docstrings**: Follow Google or NumPy docstring format
- **Inline comments**: Explain complex AWS service interactions
- **README sections**: Include setup instructions and prerequisites

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER create scenarios without checking existing patterns**
- ❌ **NEVER use camelCase for Python variables or functions**
- ❌ **NEVER ignore proper exception handling for AWS operations**
- ❌ **NEVER skip virtual environment setup**

### Best Practices
- ✅ **ALWAYS follow the established `{service}_basics.py` or scenario patterns**
- ✅ **ALWAYS use snake_case naming conventions**
- ✅ **ALWAYS include proper error handling for AWS service calls**
- ✅ **ALWAYS use virtual environments for dependency management**
- ✅ **ALWAYS include type hints where they improve code clarity**

### Integration with Knowledge Base
Before creating Python code examples:
1. Query `coding-standards-KB` for "Python-code-example-standards"
2. Query `Python-premium-KB` for "Python implementation patterns"
3. Follow KB-documented patterns for file structure and naming
4. Validate against existing Python examples only after KB consultation