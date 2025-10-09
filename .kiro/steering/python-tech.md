# Python Technology Stack & Build System

## 🚨 READ THIS FIRST - MANDATORY WORKFLOW 🚨

**BEFORE CREATING ANY PYTHON CODE FOR AWS SERVICES:**

1. **FIRST**: Run knowledge base consultation (ListKnowledgeBases + QueryKnowledgeBases)
2. **SECOND**: Create service stubber in `python/test_tools/{service}_stubber.py`
3. **THIRD**: Add stubber to `python/test_tools/stubber_factory.py`
4. **FOURTH**: Create conftest.py with ScenarioData class
5. **FIFTH**: Create implementation files with complete AWS data structures
6. **SIXTH**: Run ALL mandatory commands (pytest, black, pylint, writeme)

**❌ SKIPPING ANY STEP = REJECTED CODE**
**❌ WRONG ORDER = REJECTED CODE**
**❌ INCOMPLETE DATA STRUCTURES = REJECTED CODE**

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

## 🚨 CRITICAL: MANDATORY WORKFLOW BEFORE ANY CODE CREATION 🚨

### STEP-BY-STEP MANDATORY SEQUENCE (MUST BE FOLLOWED EXACTLY)

**❌ COMMON MISTAKES THAT LEAD TO REJECTED CODE:**
- Creating any files before knowledge base consultation
- Creating conftest.py before service stubber
- Skipping ScenarioData class for complex services
- Using incomplete AWS data structures in tests
- Not running proper pytest markers
- Skipping code formatting and linting

**✅ CORRECT MANDATORY SEQUENCE:**

**STEP 1: KNOWLEDGE BASE CONSULTATION (REQUIRED FIRST)**
```bash
# MUST be done before creating ANY files
ListKnowledgeBases()
QueryKnowledgeBases("coding-standards-KB", "Python-code-example-standards")
QueryKnowledgeBases("Python-premium-KB", "Python implementation patterns")
```

**STEP 2: CREATE SERVICE STUBBER (REQUIRED BEFORE CONFTEST)**
```bash
# Check if python/test_tools/{service}_stubber.py exists
# If missing, create it following existing patterns
# Add to python/test_tools/stubber_factory.py
```

**STEP 3: CREATE CONFTEST.PY WITH SCENARIODATA**
```bash
# Create python/example_code/{service}/test/conftest.py
# MUST include ScenarioData class for complex services
# Import from test_tools.fixtures.common import *
```

**STEP 4: CREATE IMPLEMENTATION FILES**
```bash
# Create wrapper, hello, scenario files
# Use complete AWS data structures (not minimal ones)
```

**STEP 5: MANDATORY TESTING AND QUALITY**
```bash
# MUST run these exact commands:
python -m pytest -m "not integ"  # Unit tests
python -m pytest -m "integ"      # Integration tests
black .                          # Format code
pylint --rcfile=.github/linters/.python-lint .
python -m writeme --languages Python:3 --services {service}
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

#### Scenario Pattern Structure
**MANDATORY for all scenario files:**

```python
# scenario_{service}_basics.py structure
import logging
import os
import sys
from typing import Optional

import boto3
from botocore.exceptions import ClientError

from {service}_wrapper import {Service}Wrapper

# Add relative path to include demo_tools
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "../.."))
from demo_tools import question as q

logger = logging.getLogger(__name__)

class {Service}Scenario:
    """Runs an interactive scenario that shows how to use {AWS Service}."""
    
    def __init__(self, {service}_wrapper: {Service}Wrapper):
        """
        :param {service}_wrapper: An instance of the {Service}Wrapper class.
        """
        self.{service}_wrapper = {service}_wrapper
    
    def run_scenario(self):
        """Runs the {AWS Service} basics scenario."""
        print("-" * 88)
        print("Welcome to the {AWS Service} basics scenario!")
        print("-" * 88)
        
        try:
            self._setup_phase()
            self._demonstration_phase()
            self._examination_phase()
        except Exception as e:
            logger.error(f"Scenario failed: {e}")
        finally:
            self._cleanup_phase()
    
    def _setup_phase(self):
        """Setup phase implementation."""
        pass
    
    def _demonstration_phase(self):
        """Demonstration phase implementation."""
        pass
    
    def _examination_phase(self):
        """Examination phase implementation."""
        pass
    
    def _cleanup_phase(self):
        """Cleanup phase implementation."""
        pass

def main():
    """Runs the {AWS Service} basics scenario."""
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")
    
    try:
        {service}_wrapper = {Service}Wrapper.from_client()
        scenario = {Service}Scenario({service}_wrapper)
        scenario.run_scenario()
    except Exception as e:
        logger.error(f"Failed to run scenario: {e}")

if __name__ == "__main__":
    main()
```

**Scenario Requirements:**
- ✅ **ALWAYS** include descriptive comment block at top explaining scenario steps
- ✅ **ALWAYS** use demo_tools for user interaction
- ✅ **ALWAYS** implement proper cleanup in finally block
- ✅ **ALWAYS** break scenario into logical phases
- ✅ **ALWAYS** include comprehensive error handling

#### Code Structure Standards
- **Imports**: Follow PEP 8 import ordering (standard library, third-party, local)
- **Functions**: Use descriptive names with snake_case
- **Classes**: Use PascalCase for class names
- **Constants**: Use UPPER_CASE for constants
- **Type Hints**: Include type annotations where beneficial

#### Wrapper Class Pattern
**MANDATORY for all services:**

```python
# {service}_wrapper.py structure
import logging
import boto3
from botocore.exceptions import ClientError
from typing import Dict, List, Optional, Any

logger = logging.getLogger(__name__)

class {Service}Wrapper:
    """Encapsulates {AWS Service} functionality."""
    
    def __init__(self, {service}_client: boto3.client):
        """
        :param {service}_client: A Boto3 {AWS Service} client.
        """
        self.{service}_client = {service}_client
    
    @classmethod
    def from_client(cls):
        {service}_client = boto3.client("{service}")
        return cls({service}_client)
    
    # Individual action methods with proper error handling
    def action_method(self, param: str) -> Dict[str, Any]:
        """
        Action description.
        
        :param param: Parameter description.
        :return: Response description.
        """
        try:
            response = self.{service}_client.action_name(Parameter=param)
            logger.info(f"Action completed successfully")
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "SpecificError":
                logger.error("Specific error handling")
            else:
                logger.error(f"Error in action: {e}")
            raise
```

**Wrapper Class Requirements:**
- ✅ **ALWAYS** include proper logging
- ✅ **ALWAYS** provide `from_client()` class method
- ✅ **ALWAYS** handle service-specific errors
- ✅ **ALWAYS** include comprehensive docstrings
- ✅ **ALWAYS** use type hints for parameters and returns

#### Testing Convention and Structure

**🚨 MANDATORY Testing Infrastructure Setup (EXACT ORDER REQUIRED):**

**❌ CRITICAL ERROR: Creating conftest.py before service stubber will cause failures**

**✅ CORRECT ORDER:**

1. **FIRST: Create Service Stubber (MANDATORY BEFORE CONFTEST):**
   - **CRITICAL**: Check if `python/test_tools/{service}_stubber.py` exists
   - **CRITICAL**: If missing, create it FIRST following existing patterns (e.g., `controltower_stubber.py`)
   - **CRITICAL**: Inherit from `ExampleStubber` and implement ALL service-specific stub methods
   - **CRITICAL**: Add to `python/test_tools/stubber_factory.py` import and factory function
   - **CRITICAL**: Each stub method MUST handle both stubbing and AWS passthrough modes

2. **SECOND: Create Service conftest.py (ONLY AFTER STUBBER EXISTS):**
   - **CRITICAL**: Create `python/example_code/{service}/test/conftest.py`
   - **CRITICAL**: Import common fixtures: `from test_tools.fixtures.common import *`
   - **CRITICAL**: Add path configuration: `sys.path.append("../..")`
   - **CRITICAL**: For complex services, MUST create ScenarioData class (not optional)
   - **CRITICAL**: Use complete AWS data structures in tests (not minimal ones)

3. **Test File Structure:**
   - **Unit Tests**: `test_{service}_wrapper.py` - Test wrapper class methods with mocked responses
   - **Integration Tests**: `test_{service}_integration.py` - Test against real AWS services (marked with `@pytest.mark.integ`)
   - **Scenario Tests**: `test_{service}_scenario.py` - Test complete scenarios end-to-end

**Testing Pattern Examples:**

```python
# Simple conftest.py pattern
import sys
sys.path.append("../..")
from test_tools.fixtures.common import *

# Complex conftest.py with ScenarioData class
class ScenarioData:
    def __init__(self, service_client, service_stubber):
        self.service_client = service_client
        self.service_stubber = service_stubber
        self.wrapper = ServiceWrapper(service_client)

@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client("service")
    stubber = make_stubber(client)
    return ScenarioData(client, stubber)
```

**🚨 CRITICAL Testing Requirements:**
- ✅ **ALWAYS** use the centralized `test_tools` infrastructure
- ✅ **ALWAYS** support both stubbed and real AWS testing modes
- ✅ **ALWAYS** mark integration tests with `@pytest.mark.integ`
- ✅ **ALWAYS** ensure proper cleanup in integration tests
- ✅ **ALWAYS** use the `make_stubber` fixture for consistent stubbing
- ✅ **CRITICAL**: Use COMPLETE AWS data structures in tests (see below)

**🚨 CRITICAL: Complete AWS Data Structures Required**

**❌ COMMON MISTAKE: Using minimal test data that fails validation**
```python
# WRONG - Missing required AWS fields
findings = [{"Id": "finding-1", "Type": "SomeType", "Severity": 8.0}]
```

**✅ CORRECT: Complete AWS data structures**
```python
# RIGHT - All required AWS fields included
findings = [{
    "Id": "finding-1",
    "AccountId": "123456789012",
    "Arn": "arn:aws:service:region:account:resource/id",
    "Type": "SomeType", 
    "Severity": 8.0,
    "CreatedAt": "2023-01-01T00:00:00.000Z",
    "UpdatedAt": "2023-01-01T00:00:00.000Z",
    "Region": "us-east-1",
    "SchemaVersion": "2.0",
    "Resource": {"ResourceType": "Instance"}
}]
```

**CRITICAL**: Always check AWS API documentation for required fields before creating test data.

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

#### Requirements File Pattern
**MANDATORY for every service directory:**

```txt
# requirements.txt - minimum versions
boto3>=1.26.137
botocore>=1.29.137
```

**Requirements Guidelines:**
- ✅ **ALWAYS** specify minimum compatible versions
- ✅ **ALWAYS** include both boto3 and botocore
- ✅ **ALWAYS** test with specified minimum versions
- ✅ **NEVER** pin to exact versions unless absolutely necessary

#### Documentation Requirements
- **Module docstrings**: Include purpose and usage examples
- **Function docstrings**: Follow Google or NumPy docstring format
- **Inline comments**: Explain complex AWS service interactions
- **README sections**: Include setup instructions and prerequisites
- **Snippet tags**: Include proper snippet tags for documentation generation

### Language-Specific Pattern Errors to Avoid
- ❌ **NEVER create scenarios without checking existing patterns**
- ❌ **NEVER use camelCase for Python variables or functions**
- ❌ **NEVER ignore proper exception handling for AWS operations**
- ❌ **NEVER skip virtual environment setup**

### 🚨 MANDATORY COMPLETION CHECKLIST

**❌ WORK IS NOT COMPLETE UNTIL ALL THESE COMMANDS PASS:**

```bash
# 1. MANDATORY: Unit tests must pass
python -m pytest -m "not integ" -v

# 2. MANDATORY: Integration tests must pass  
python -m pytest -m "integ" -v

# 3. MANDATORY: Code formatting must be applied
black .

# 4. MANDATORY: Linting must pass
pylint --rcfile=.github/linters/.python-lint python/example_code/{service}/

# 5. MANDATORY: Documentation must be updated
cd .tools/readmes
source .venv/bin/activate
python -m writeme --languages Python:3 --services {service}

# 6. MANDATORY: ALL EXAMPLE FILES MUST BE EXECUTED TO VALIDATE CREATION
PYTHONPATH=python:python/example_code/{service} python python/example_code/{service}/{service}_hello.py
PYTHONPATH=python:python/example_code/{service} python python/example_code/{service}/scenario_{service}_basics.py
# Test wrapper functions directly
PYTHONPATH=python:python/example_code/{service} python -c "from {service}_wrapper import {Service}Wrapper; wrapper = {Service}Wrapper.from_client(); print('✅ Wrapper functions working')"
```

**🚨 CRITICAL**: If ANY of these commands fail, the work is INCOMPLETE and must be fixed.

**🚨 MANDATORY VALIDATION REQUIREMENT**: 
- **ALL generated example files MUST be executed successfully to validate their creation**
- **Hello examples MUST run without errors and display expected output**
- **Scenario examples MUST run interactively and complete all phases**
- **Wrapper classes MUST be importable and instantiable**
- **Any runtime errors or import failures indicate incomplete implementation**

### Best Practices
- ✅ **ALWAYS follow the established `{service}_basics.py` or scenario patterns**
- ✅ **ALWAYS use snake_case naming conventions**
- ✅ **ALWAYS include proper error handling for AWS service calls**
- ✅ **ALWAYS use virtual environments for dependency management**
- ✅ **ALWAYS include type hints where they improve code clarity**
- ✅ **CRITICAL**: Follow the mandatory workflow sequence exactly
- ✅ **CRITICAL**: Use complete AWS data structures in all tests
- ✅ **CRITICAL**: Create service stubber before conftest.py
- ✅ **CRITICAL**: Include ScenarioData class for complex services

#### Metadata File Pattern
**MANDATORY for documentation generation:**

**CRITICAL**: Always check the specification file first for metadata requirements:
- **Specification Location**: `scenarios/basics/{service}/SPECIFICATION.md`
- **Metadata Section**: Contains exact metadata keys and structure to use
- **Use Spec Metadata As-Is**: Copy the metadata table from the specification exactly

**Specification Metadata Table Format:**
```
## Metadata

|action / scenario	|metadata file	|metadata key	|
|---	|---	|---	|
|`ActionName`	|{service}_metadata.yaml	|{service}_ActionName	|
|`Service Basics Scenario`	|{service}_metadata.yaml	|{service}_Scenario	|
```

**Implementation Steps:**
1. **Read Specification**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first
2. **Extract Metadata Table**: Use the exact metadata keys from the specification
3. **Create Metadata File**: Create `.doc_gen/metadata/{service}_metadata.yaml`
4. **Follow Spec Structure**: Use the metadata keys exactly as specified in the table

**Standard Metadata Structure (when no spec exists):**
```yaml
# .doc_gen/metadata/{service}_metadata.yaml
{service}_Hello:
  title: Hello &{Service};
  title_abbrev: Hello &{Service};
  synopsis: get started using &{Service};.
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
    {service}: {ListOperation}
```

**Metadata Requirements:**
- ✅ **ALWAYS** check specification file for metadata requirements FIRST
- ✅ **ALWAYS** use exact metadata keys from specification table
- ✅ **ALWAYS** include Hello scenario metadata
- ✅ **ALWAYS** include all action and scenario metadata as specified
- ✅ **ALWAYS** use proper snippet tags matching code
- ✅ **ALWAYS** validate metadata with writeme tool

### Integration with Knowledge Base and Specifications

**MANDATORY Pre-Implementation Workflow:**

1. **Check Specification File FIRST:**
   - **Location**: `scenarios/basics/{service}/SPECIFICATION.md`
   - **Extract**: API actions, error handling requirements, metadata table
   - **Use**: Exact metadata keys and structure from specification

2. **Knowledge Base Consultation:**
   - Query `coding-standards-KB` for "Python-code-example-standards"
   - Query `Python-premium-KB` for "Python implementation patterns"
   - Follow KB-documented patterns for file structure and naming

3. **Implementation Priority:**
   - **Specification requirements**: Use exact API actions and metadata from spec
   - **KB patterns**: Follow established coding patterns and structure
   - **Existing examples**: Validate against existing Python examples only after KB consultation

**Critical Rule**: When specification exists, always use its metadata table exactly as provided. Never create custom metadata keys when specification defines them.
## 🚨 
COMMON FAILURE PATTERNS TO AVOID 🚨

### ❌ Pattern 1: Skipping Knowledge Base Consultation
**Mistake**: Starting to code immediately without KB research
**Result**: Wrong file structure, missing patterns, rejected code
**Fix**: ALWAYS do ListKnowledgeBases + QueryKnowledgeBases FIRST

### ❌ Pattern 2: Wrong Testing Infrastructure Order  
**Mistake**: Creating conftest.py before service stubber
**Result**: Import errors, test failures, broken infrastructure
**Fix**: Create stubber FIRST, then conftest.py

### ❌ Pattern 3: Missing ScenarioData Class
**Mistake**: Using simple conftest.py for complex services
**Result**: Difficult test setup, inconsistent patterns
**Fix**: Always use ScenarioData class for services with scenarios

### ❌ Pattern 4: Incomplete AWS Data Structures
**Mistake**: Using minimal test data missing required AWS fields
**Result**: Parameter validation errors, test failures
**Fix**: Use complete AWS data structures with all required fields

### ❌ Pattern 5: Wrong Test Commands
**Mistake**: Using `pytest test/` instead of proper markers
**Result**: Not following established patterns, inconsistent testing
**Fix**: Use `python -m pytest -m "not integ"` and `python -m pytest -m "integ"`

### ❌ Pattern 6: Skipping Code Quality
**Mistake**: Not running black, pylint, or writeme
**Result**: Inconsistent formatting, linting errors, outdated docs
**Fix**: ALWAYS run all mandatory commands before considering work complete

### ✅ SUCCESS PATTERN: Follow the Exact Sequence
1. Knowledge base consultation
2. Service stubber creation
3. Stubber factory integration  
4. Conftest.py with ScenarioData
5. Implementation with complete data structures
6. All mandatory commands execution

**REMEMBER**: These patterns exist because they prevent the exact mistakes made in this session. Follow them exactly.