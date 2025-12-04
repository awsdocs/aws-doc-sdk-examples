# Python Test Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Python-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Python-premium-KB", "Python implementation patterns testing")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate comprehensive test suites including unit tests, integration tests, and scenario tests with proper stubbing and AWS data structures.

## Requirements
- **MANDATORY**: Create service stubber BEFORE conftest.py
- **Complete Data**: Use complete AWS data structures in tests
- **Proper Markers**: Use pytest markers for test categorization
- **Error Coverage**: Test all error conditions from specification

## File Structure
```
python/example_code/{service}/test/
├── conftest.py                     # Test configuration and fixtures
├── test_{service}_wrapper.py       # Unit tests for wrapper
└── test_{service}_integration.py   # Integration test that runs entire scenario
```

## MANDATORY Pre-Test Setup

### Step 1: Create Service Stubber (REQUIRED FIRST)
```python
# python/test_tools/{service}_stubber.py
from test_tools.example_stubber import ExampleStubber

class {Service}Stubber(ExampleStubber):
    """Stub functions for {AWS Service} unit tests."""
    
    def __init__(self, {service}_client: client, use_stubs=True) -> None:
        super().__init__({service}_client, use_stubs)

    def stub_{action_name}(self, param: str, response_data: dict, error_code: str = None) -> None:
        """Stub the {action_name} function."""
        expected_params = {"Parameter": param}
        response = response_data
        self._stub_bifurcator(
            "{action_name}", expected_params, response, error_code=error_code
        )
```

### Step 2: Add to Stubber Factory
```python
# python/test_tools/stubber_factory.py
from test_tools.{service}_stubber import {Service}Stubber

# Add to imports and factory function
elif service_name == "{service}":
    return {Service}Stubber
```

### Step 3: Create conftest.py with ScenarioData
```python
# python/example_code/{service}/test/conftest.py
import sys
import boto3
import pytest

sys.path.append("../..")
from test_tools.fixtures.common import *
from {service}_wrapper import {Service}Wrapper

class ScenarioData:
    """Encapsulates data for {Service} scenario tests."""
    
    def __init__(self, {service}_client, {service}_stubber):
        self.{service}_client = {service}_client
        self.{service}_stubber = {service}_stubber
        self.wrapper = {Service}Wrapper({service}_client)

@pytest.fixture
def scenario_data(make_stubber):
    """Creates a ScenarioData object for {Service} scenario tests."""
    {service}_client = boto3.client("{service}", region_name="us-east-1")
    {service}_stubber = make_stubber({service}_client)
    return ScenarioData({service}_client, {service}_stubber)
```

## Unit Test Pattern
```python
# test_{service}_wrapper.py
import pytest
import boto3
from botocore.exceptions import ClientError
from {service}_wrapper import {Service}Wrapper

@pytest.mark.parametrize("error_code", [None, "BadRequestException", "InternalServerErrorException"])
def test_{action_name}(make_stubber, error_code):
    {service}_client = boto3.client("{service}", region_name="us-east-1")
    {service}_stubber = make_stubber({service}_client)
    {service}_wrapper = {Service}Wrapper({service}_client)
    
    # Test parameters
    param_value = "test-value"
    expected_response = {"ResponseKey": "response-value"}
    
    # Setup stub
    {service}_stubber.stub_{action_name}(param_value, expected_response, error_code)
    
    if error_code is None:
        result = {service}_wrapper.{action_name}(param_value)
        assert result["ResponseKey"] == "response-value"
    else:
        with pytest.raises(ClientError) as exc_info:
            {service}_wrapper.{action_name}(param_value)
        assert exc_info.value.response["Error"]["Code"] == error_code
```

## Complete AWS Data Structures

### CRITICAL: Use Complete AWS Response Data
```python
# ❌ WRONG - Minimal data that fails validation
findings = [{"Id": "finding-1", "Type": "SomeType", "Severity": 8.0}]

# ✅ CORRECT - Complete AWS data structure
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

## Integration Test Pattern
The integration test should run the entire scenario end-to-end against real AWS services:

```python
# test_{service}_integration.py
import pytest
import time
from unittest.mock import patch
from {service}_wrapper import {Service}Wrapper
from scenario_{service}_basics import {Service}Scenario

@pytest.mark.integ
def test_scenario_complete_integration():
    """Test the complete scenario flow against real AWS services."""
    {service}_wrapper = {Service}Wrapper.from_client()
    scenario = {Service}Scenario({service}_wrapper)
    
    # Mock user inputs for automated testing
    with patch('demo_tools.question.ask') as mock_ask:
        # Configure responses for the scenario questions
        mock_ask.side_effect = [
            False,  # Don't use existing resource
            False,  # Don't show detailed info  
            True,   # Perform cleanup
        ]
        
        # Mock time.sleep to speed up test
        with patch('time.sleep'):
            # Run the complete scenario
            scenario.run_scenario()
    
    # Verify scenario completed successfully
    # (cleanup should have been performed, so resource_id should be handled)
    assert True  # Scenario completed without exceptions

@pytest.mark.integ
def test_scenario_with_existing_resources():
    """Test scenario when existing resources are present."""
    {service}_wrapper = {Service}Wrapper.from_client()
    scenario = {Service}Scenario({service}_wrapper)
    
    # First create a resource to test "existing resource" path
    resource_id = None
    try:
        resource_id = {service}_wrapper.create_resource(enable=True)
        
        with patch('demo_tools.question.ask') as mock_ask:
            mock_ask.side_effect = [
                True,   # Use existing resource
                False,  # Don't show detailed info
                False,  # Don't delete resource (we'll clean up manually)
            ]
            
            with patch('time.sleep'):
                scenario.run_scenario()
        
        # Verify scenario used existing resource
        assert scenario.resource_id == resource_id
        
    finally:
        # Manual cleanup
        if resource_id:
            try:
                {service}_wrapper.delete_resource(resource_id)
            except Exception:
                pass  # Ignore cleanup errors
```

## Additional Integration Test Patterns (Optional)
If needed, you can add specific wrapper method tests:

```python
@pytest.mark.integ
def test_{action_name}_integration():
    """Test {action} against real AWS."""
    {service}_wrapper = {Service}Wrapper.from_client()
    
    # This should not raise an exception
    result = {service}_wrapper.{action_name}()
    
    # Verify result structure
    assert isinstance(result, expected_type)

@pytest.mark.integ  
def test_resource_lifecycle_integration():
    """Test creating, using, and deleting a resource."""
    {service}_wrapper = {Service}Wrapper.from_client()
    resource_id = None
    
    try:
        # Create resource
        resource_id = {service}_wrapper.create_resource()
        assert resource_id is not None
        
        # Use resource
        result = {service}_wrapper.get_resource(resource_id)
        assert result is not None
        
    finally:
        # Clean up
        if resource_id:
            try:
                {service}_wrapper.delete_resource(resource_id)
            except ClientError:
                pass  # Ignore cleanup errors
```

## Test Execution Commands

### Unit Tests
```bash
PYTHONPATH=python:python/example_code/{service} python -m pytest python/example_code/{service}/test/ -m "not integ" -v
```

### Integration Tests (Runs Complete Scenario)
```bash
PYTHONPATH=python:python/example_code/{service} python -m pytest python/example_code/{service}/test/ -m "integ" -v
```

## Test Requirements Checklist
- ✅ **Service stubber created FIRST** (before conftest.py)
- ✅ **Stubber added to factory** (stubber_factory.py)
- ✅ **ScenarioData class** for complex services
- ✅ **Complete AWS data structures** in all tests
- ✅ **Proper pytest markers** (`@pytest.mark.integ`)
- ✅ **Error condition coverage** per specification
- ✅ **Integration test runs complete scenario** end-to-end
- ✅ **Integration test cleanup** (try/finally blocks)
- ✅ **Region specification** (region_name="us-east-1")
- ✅ **Mock user inputs** for automated scenario testing

## Integration Test Focus

### Primary Test: Complete Scenario Integration
The main integration test should:
- ✅ **Run the entire scenario** from start to finish
- ✅ **Mock user inputs** to automate the interactive parts
- ✅ **Test against real AWS** services (not stubs)
- ✅ **Handle cleanup properly** to avoid resource leaks
- ✅ **Test multiple paths** (new resources, existing resources)

### Test Structure Priority
1. **Integration Test** - Most important, tests complete workflow
2. **Unit Tests** - Test individual wrapper methods
3. **Additional Integration Tests** - Optional, for specific edge cases

## Common Test Failures to Avoid
- ❌ Creating conftest.py before service stubber
- ❌ Using incomplete AWS data structures
- ❌ Missing pytest markers for integration tests
- ❌ Not handling cleanup in integration tests
- ❌ Forgetting to set AWS region in test clients
- ❌ Not testing all error conditions from specification
- ❌ Not mocking user inputs in scenario integration tests
- ❌ Not testing the complete scenario end-to-end