# Python Interactive Scenario Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Python-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Python-premium-KB", "Python implementation patterns structure")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate interactive scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Requirements
- **Specification-Driven**: MUST read the `scenarios/basics/{service}/SPECIFICATION.md`
- **Interactive**: USE demo_tools for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Wrapper Classes**: MUST use service wrapper classes for all operations

## File Structure
```
python/example_code/{service}/
├── scenario_{service}_basics.py    # Main scenario file
```

## MANDATORY Pre-Implementation Steps

### Step 1: Read Service Specification
**CRITICAL**: Always read `scenarios/basics/{service}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-step workflow description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Wrapper Class Snippet Tag Requirements

**CRITICAL**: Wrapper classes MUST use the correct snippet tag structure to ensure proper metadata integration and test compatibility.

### Required Snippet Tag Structure

**For Wrapper Class Declaration:**
```python
# snippet-start:[python.example_code.{service}.{Service}Wrapper.decl]
class {Service}Wrapper:
    """Wrapper class for managing {Service} operations."""
    # Class implementation...
# snippet-end:[python.example_code.{service}.{Service}Wrapper.decl]
```

**For Individual Methods:**
```python
# snippet-start:[python.example_code.{service}.MethodName]
def method_name(self, parameter: str) -> bool:
    """Method implementation."""
    # Method code...
# snippet-end:[python.example_code.{service}.MethodName]
```

**MANDATORY**: When wrapper classes are used in scenarios, the metadata files MUST reference the `.decl` tag pattern:

**Correct Metadata Reference:**
```yaml
python/cross_service/example_scenario:
  excerpts:
    - snippet_tags:
        - python.example_code.{service}.{ServiceName}Wrapper.decl  # ✓ CORRECT
        - python.example_code.{service}.MethodName
```

**Incorrect Metadata Reference:**
```yaml
python/cross_service/example_scenario:
  excerpts:
    - snippet_tags:
        - python.example_code.{service}.{ServiceName}Wrapper      # ✗ INCORRECT
        - python.example_code.{service}.MethodName
```
### Why This Pattern Is Required

1. **Stub Test Compatibility**: The `.decl` pattern ensures stubbed tests can properly mock wrapper class declarations
2. **Metadata Consistency**: Follows established patterns from control-tower and other scenarios  
3. **Documentation Generation**: Enables proper extraction of class declarations for documentation
4. **Parameter Matching**: Prevents "Unexpected API Call" errors in stubbed tests

### Implementation Example

**In Wrapper File (`{service}_wrapper.py`):**
```python
# snippet-start:[python.example_code.{service_name}.{Service}Wrapper.decl]
class {Service}Wrapper:
    """Wrapper class for managing {AWS Service} operations."""

    def __init__(self, {service}_client: Any) -> None:
        """Initialize the {Service}Wrapper."""
        self.{service}_client = {service}_client
# snippet-end:[python.example_code.{service_name}.{Service}Wrapper.decl]

    # snippet-start:[python.example_code.{service}.CreateResource]
    def create_resource(self, name: str) -> str:
        """Create a resource."""
        # Method implementation...
    # snippet-end:[python.example_code.{service}.CreateResource]
```

**In Metadata File (`.doc_gen/metadata/{service}_metadata.yaml`):**

**For Individual Service Operations:**
```yaml
{service}_CreateResource:
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/{service}
          excerpts:
            - snippet_tags:
                - python.example_code.{serviceName}.{ServiceName}Wrapper.decl
                - python.example_code.{serviceName}.CreateResource
```

**For Scenario Sections (CRITICAL - Must Include Both Scenario and Wrapper Classes):**
```yaml
{service}_Scenario_Basics:
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/example_code/{service}
          excerpts:
            - description: Run an interactive scenario at a command prompt.
              snippet_tags:
                - python.example_code.{service}.{ServiceName}Scenario
            - description: Create a class that wraps service operations.
              snippet_tags:
                - python.example_code.{service}.{ServiceName}Wrapper.decl
```


**Why Both Are Required in Scenario Sections:**
1. **Complete Documentation**: Scenarios demonstrate both the orchestration logic and the underlying service operations
2. **Pattern Consistency**: Follows established patterns from controltower and other scenarios
3. **User Understanding**: Helps readers understand both the high-level workflow and individual service operations
4. **Documentation Generation**: Ensures complete code examples are extracted for documentation

**Example Based on Topics and Queues:**
```yaml
sqs_Scenario_TopicsAndQueues:
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/cross_service/scenario_name
          excerpts:
            - description: Run an interactive scenario at a command prompt.
              snippet_tags:
                - python.example_code.cross_service.scenario_name.FeatureScenario
            - description: Create classes that wrap service operations.
              snippet_tags:
                - python.example_code.service.ServiceWrapper.decl
                - python.example_code.service.ServiceWrapper.Action
```

## Scenario Class Pattern
### Implementation Pattern Based on SPECIFICATION.md
```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use {AWS Service} to {scenario description}. This scenario demonstrates:

1. {Phase 1 description}
2. {Phase 2 description}
3. {Phase 3 description}
4. {Phase 4 description}

This example uses the AWS SDK for Python (Boto3) to interact with {AWS Service}.
"""

import logging
import sys
import time
from typing import List, Dict, Any

import boto3
from botocore.exceptions import ClientError

from {service}_wrapper import {Service}Wrapper

# Add relative path to include demo_tools
sys.path.insert(0, "../..")
from demo_tools import question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.{service}.{Service}Scenario]
class {Service}Scenario:
    """Runs an interactive scenario that shows how to use {AWS Service}."""

    def __init__(self, {service}_wrapper: {Service}Wrapper):
        """
        :param {service}_wrapper: An instance of the {Service}Wrapper class.
        """
        self.{service}_wrapper = {service}_wrapper
        self.{resource_id} = None

    def run_scenario(self):
        """Runs the {AWS Service} basics scenario."""
        print("-" * 88)
        print("Welcome to the {AWS Service} basics scenario!")
        print("-" * 88)
        print(
            "{Service description and what users will learn}"
        )
        print()

        try:
            self._setup_phase()
            self._demonstration_phase()
            self._examination_phase()
        except Exception as e:
            logger.error(f"Scenario failed: {e}")
            print(f"The scenario encountered an error: {e}")
        finally:
            self._cleanup_phase()

    def _setup_phase(self):
        """Setup phase: Implement based on specification's Setup section."""
        print("Setting up {AWS Service}...")
        print()
        
        # Example: Check for existing resources (from specification)
        existing_resources = self.{service}_wrapper.list_resources()
        if existing_resources:
            print(f"Found {len(existing_resources)} existing resource(s):")
            for resource in existing_resources:
                print(f"  - {resource}")
            
            use_existing = q.ask(
                "Would you like to use an existing resource? (y/n): ", q.is_yesno
            )
            if use_existing:
                self.resource_id = existing_resources[0]
                return

    def _demonstration_phase(self):
        """Demonstration phase: Implement operations from specification."""
        print("Demonstrating {AWS Service} capabilities...")
        print()
        
        # Implement specific operations from specification
        # Example: Generate sample data if specified
        self.{service}_wrapper.create_sample_data(self.resource_id)
        print("✓ Sample data created successfully")
        
        # Wait if specified in the specification
        print("Waiting for data to be processed...")
        time.sleep(5)

    def _examination_phase(self):
        """Examination phase: Implement data analysis from specification."""
        print("Examining {AWS Service} data...")
        print()
        
        # List and examine data as specified
        data_items = self.{service}_wrapper.list_data(self.resource_id)
        if not data_items:
            print("No data found. Data may take a few minutes to appear.")
            return
        
        print(f"Found {len(data_items)} data item(s)")
        
        # Get detailed information as specified
        detailed_data = self.{service}_wrapper.get_data_details(self.resource_id, data_items[:5])
        self._display_data_summary(detailed_data)
        
        # Show detailed view if specified
        if detailed_data:
            show_details = q.ask(
                "Would you like to see detailed information? (y/n): ", q.is_yesno
            )
            if show_details:
                self._display_data_details(detailed_data[0])
        
        # Filter data as specified
        self._filter_data_by_criteria(data_items)

    def _cleanup_phase(self):
        """Cleanup phase: Implement cleanup options from specification."""
        if not self.resource_id:
            return
            
        print("Cleanup options:")
        print("Note: Deleting the resource will stop all monitoring/processing.")
        
        delete_resource = q.ask(
            "Would you like to delete the resource? (y/n): ", q.is_yesno
        )
        
        if delete_resource:
            try:
                self.{service}_wrapper.delete_resource(self.resource_id)
                print(f"✓ Deleted resource: {self.resource_id}")
            except Exception as e:
                print(f"Error deleting resource: {e}")
        else:
            print(f"Resource {self.resource_id} will continue running.")
            print("You can manage it through the AWS Console or delete it later.")
# snippet-end:[python.example_code.{service}.{Service}Scenario]


def main():
    """Runs the {AWS Service} basics scenario."""
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")

    try:
        {service}_wrapper = {Service}Wrapper.from_client()
        scenario = {Service}Scenario({service}_wrapper)
        scenario.run_scenario()
    except Exception as e:
        logger.error(f"Failed to run scenario: {e}")
        print(f"Failed to run the scenario: {e}")


if __name__ == "__main__":
    main()
```

## Scenario Phase Structure (Based on Specification)

### Setup Phase
- **Read specification Setup section** for exact requirements
- Check for existing resources as specified
- Create necessary resources using wrapper methods
- Configure service settings per specification
- Verify setup completion as described

### Demonstration Phase  
- **Follow specification Demonstration section** exactly
- Perform core service operations using wrapper methods
- Generate sample data if specified in the specification
- Show service capabilities as outlined
- Provide educational context from specification

### Examination Phase
- **Implement specification Examination section** requirements
- List and examine results using wrapper methods
- Filter and analyze data as specified
- Display detailed information per specification format
- Allow user interaction as described in specification

### Cleanup Phase
- **Follow specification Cleanup section** guidance
- Offer cleanup options with warnings from specification
- Handle cleanup errors gracefully using wrapper methods
- Provide alternative management options as specified
- Confirm completion per specification

## User Interaction Patterns

### Question Types
```python
# Yes/No questions
use_existing = q.ask("Use existing resource? (y/n): ", q.is_yesno)

# Text input
resource_name = q.ask("Enter resource name: ")

# Numeric input  
count = q.ask("How many items? ", q.is_int)
```

### Information Display
```python
# Progress indicators
print("✓ Operation completed successfully")
print("⚠ Warning message")
print("✗ Error occurred")

# Formatted output
print("-" * 60)
print(f"Found {len(items)} items:")
for item in items:
    print(f"  • {item['name']}")
```

## Specification-Based Error Handling

### Error Handling from Specification
The specification includes an "Errors" section with specific error codes and handling:

```python
# Example error handling based on specification
try:
    response = self.{service}_wrapper.create_resource()
    return response
except ClientError as e:
    error_code = e.response["Error"]["Code"]
    if error_code == "BadRequestException":
        # Handle as specified: "Validate input parameters and notify user"
        print("Invalid configuration. Please check your parameters.")
    elif error_code == "InternalServerErrorException":
        # Handle as specified: "Retry operation with exponential backoff"
        print("Service temporarily unavailable. Retrying...")
        # Implement retry logic
    else:
        print(f"Unexpected error: {e}")
    raise
```

## Dependencies

### Requirements Files

#### requirements.txt (Main Dependencies)
```txt
boto3>=1.26.137
botocore>=1.29.137
```

#### test/test_requirements.txt (Test Dependencies)
```txt
pytest>=7.0.0
pytest-mock>=3.10.0
moto>=4.0.0
```

## Integration Tests

### Requirements

**MANDATORY**: All feature scenarios MUST include both stubbed unit tests and live integration tests:

1. **Stubbed Tests** - Use the established stubbing pattern from `test_tools` for fast, reliable unit testing
2. **Live Integration Tests** - Use `@pytest.mark.integ` decorator to test against real AWS services

### Stubber Method Verification

**CRITICAL**: Before implementing tests, verify that all wrapper methods have corresponding stubber methods available in `python/test_tools/{service}_stubber.py`. 

#### Available Service Stubbers
Common service stubbers available in `python/test_tools/`:
- `sns_stubber.py` - Amazon SNS operations
- `sqs_stubber.py` - Amazon SQS operations  
- `s3_stubber.py` - Amazon S3 operations
- `dynamodb_stubber.py` - Amazon DynamoDB operations
- `lambda_stubber.py` - AWS Lambda operations
- `iam_stubber.py` - AWS IAM operations
- `cloudformation_stubber.py` - AWS CloudFormation operations
- And many more...

#### Verification Process

1. **Check Stubber Availability**: Verify `{service}_stubber.py` exists in `python/test_tools/`
2. **Review Available Methods**: Examine the stubber class to see what methods are available
3. **Map Wrapper to Stubber Methods**: Ensure each wrapper method has a corresponding stub method

#### Example Method Mapping

**SNS Wrapper → SNS Stubber Mapping:**
```python
# SNS Wrapper Methods → Available Stubber Methods
create_topic()              → stub_create_topic()
subscribe_queue_to_topic()  → stub_subscribe() 
publish_message()           → stub_publish()
unsubscribe()              → stub_unsubscribe()
delete_topic()             → stub_delete_topic()
list_topics()              → stub_list_topics()
```

**SQS Wrapper → SQS Stubber Mapping:**
```python
# SQS Wrapper Methods → Available Stubber Methods  
create_queue()             → stub_create_queue()
get_queue_arn()            → stub_get_queue_attributes()
set_queue_policy_for_topic() → stub_set_queue_attributes()
receive_messages()         → stub_receive_messages()
delete_messages()          → stub_delete_message_batch()
delete_queue()             → stub_delete_queue()
list_queues()              → stub_list_queues()
send_message()             → stub_send_message()
```

#### Missing Stubber Methods

If a wrapper method doesn't have a corresponding stubber method:

1. **Check Other Services**: Some operations may be available in related service stubbers
2. **Create Custom Stub**: Add the missing stub method to the appropriate stubber class
3. **Use Generic Stubs**: For simple operations, use `_stub_bifurcator` directly
4. **Update Test Strategy**: Consider alternative testing approaches for complex operations

#### Stubber Method Parameters

**CRITICAL**: Always verify that stubber method parameters match the actual wrapper implementation calls. Mismatched parameters will cause "Unexpected API Call" errors.

##### Common Parameter Matching Issues

**Problem**: `Error getting response stub for operation CreateTopic: Unexpected API Call: A call was made but no additional calls expected.`

**Root Cause**: The wrapper method passes different parameters than what the stubber expects.

##### Parameter Verification Process

1. **Check Wrapper Implementation**: Examine how the wrapper method calls the AWS client
2. **Check Stubber Signature**: Review the stubber method parameter requirements
3. **Match Parameters Exactly**: Ensure stub parameters match wrapper call parameters

##### Example: SNS CreateTopic Parameter Matching

**Wrapper Implementation Analysis:**
```python
# From sns_wrapper.py create_topic method:
attributes = {}
if is_fifo:
    attributes['FifoTopic'] = 'true'
    if content_based_deduplication:
        attributes['ContentBasedDeduplication'] = 'true'

response = self.sns_client.create_topic(
    Name=topic_name,
    Attributes=attributes  # ALWAYS passed, even if empty for standard topics
)
```

**Stubber Method Signature:**
```python
# From sns_stubber.py:
def stub_create_topic(self, topic_name, topic_arn, topic_attributes=None, error_code=None):
    expected_params = {"Name": topic_name}
    if topic_attributes is not None:
        expected_params["Attributes"] = topic_attributes
```

**Correct Test Stub Usage:**
```python
# For standard (non-FIFO) topic - MUST include empty attributes
runner.add(
    mock_mgr.sns_stubber.stub_create_topic,
    "test-topic",                                    # topic_name
    "arn:aws:sns:us-east-1:123456789012:test-topic", # topic_arn
    {},                                             # topic_attributes (empty dict for standard)
)

# For FIFO topic - include FIFO attributes
runner.add(
    mock_mgr.sns_stubber.stub_create_topic,
    "test-topic.fifo",                               # topic_name  
    "arn:aws:sns:us-east-1:123456789012:test-topic.fifo", # topic_arn
    {"FifoTopic": "true"},                          # topic_attributes (FIFO settings)
)
```

##### Debugging Parameter Mismatches

**Step 1**: Enable detailed logging to see actual vs expected parameters:
```python
import logging
logging.basicConfig(level=logging.DEBUG)
```

**Step 2**: Compare wrapper client call with stubber expected_params:
- Print wrapper parameters before AWS client call
- Check stubber `expected_params` dictionary construction
- Ensure all parameters match exactly (including optional ones)

**Step 3**: Common parameter patterns:
```python
# SNS Operations
create_topic()    → Always passes Attributes (even if empty dict)
subscribe()       → May include Attributes for filter policies  
publish()         → May include MessageAttributes, MessageGroupId, etc.

# SQS Operations  
create_queue()    → Always passes Attributes (even if empty dict)
receive_message() → Always passes MessageAttributeNames, WaitTimeSeconds
send_message()    → May include MessageAttributes, DelaySeconds, etc.
```

### Pytest Integration Test Pattern with Stubbing

Integration tests should use the established stubbing pattern from `test_tools` and verify no errors are logged:

```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for {Service} scenario.
"""

import pytest
from unittest.mock import patch
import logging
import boto3

import sys
import os

# Add parent directory to path to import scenario modules
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
# Add test_tools to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "..", "..", "test_tools"))

from {scenario_name}_scenario import {Service}Scenario
from {service}_wrapper import {Service}Wrapper


class MockManager:
    """Mock manager for the {Service} scenario tests."""
    
    def __init__(self, {service}_client, {service}_stubber, stub_runner):
        """{Service} test setup manager."""
        self.{service}_client = {service}_client
        self.{service}_stubber = {service}_stubber
        self.stub_runner = stub_runner
        self.{service}_wrapper = {Service}Wrapper({service}_client)
        self.scenario = {Service}Scenario(self.{service}_wrapper)


@pytest.fixture
def mock_mgr(make_stubber, stub_runner):
    """Create a mock manager with AWS service stubbers."""
    {service}_client = boto3.client('{service}')
    {service}_stubber = make_stubber({service}_client)
    
    return MockManager({service}_client, {service}_stubber, stub_runner)


class TestScenarioIntegration:
    """Integration tests for the {Service} scenario."""

    @patch('demo_tools.question.ask')
    def test_scenario_integration_no_errors_logged(self, mock_ask, mock_mgr, caplog):
        """
        Verify the scenario runs without logging any errors.
        
        Args:
            mock_ask: Mock for user input
            mock_mgr: Mock manager with stubbed AWS clients
            caplog: Pytest log capture fixture
        """
        # Arrange user inputs
        mock_ask.side_effect = [
            "test-resource-name",  # Resource name input
            "test-parameter",      # Parameter input
            True,                  # Cleanup confirmation
        ]

        # Set up stubs for AWS operations
        with mock_mgr.stub_runner(None, None) as runner:
            # Add stubs for each AWS operation the scenario will perform
            # Example for SNS operations:
            runner.add(
                mock_mgr.sns_stubber.stub_create_topic,
                "test-topic",
                "arn:aws:sns:us-east-1:123456789012:test-topic",
            )
            runner.add(
                mock_mgr.sns_stubber.stub_subscribe,
                "arn:aws:sns:us-east-1:123456789012:test-topic",
                "sqs",
                "arn:aws:sqs:us-east-1:123456789012:test-queue",
                "arn:aws:sns:us-east-1:123456789012:test-topic:subscription-id",
            )
            runner.add(
                mock_mgr.sns_stubber.stub_publish,
                "Test message",
                "message-id-123",
                topic_arn="arn:aws:sns:us-east-1:123456789012:test-topic",
            )
            runner.add(
                mock_mgr.sns_stubber.stub_delete_topic,
                "arn:aws:sns:us-east-1:123456789012:test-topic",
            )
            
            # Example for SQS operations:
            runner.add(
                mock_mgr.sqs_stubber.stub_create_queue,
                "test-queue",
                {},
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue",
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_get_queue_attributes,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue",
                "arn:aws:sqs:us-east-1:123456789012:test-queue",
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_receive_messages,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue",
                [{"body": "Test message"}],
                1,
            )
            runner.add(
                mock_mgr.sqs_stubber.stub_delete_queue,
                "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue",
            )

            # Act - Run the scenario
            with caplog.at_level(logging.ERROR):
                mock_mgr.scenario.run_scenario()

            # Assert no errors logged
            error_logs = [record for record in caplog.records if record.levelno >= logging.ERROR]
            assert len(error_logs) == 0, f"Expected no error logs, but found: {error_logs}"

    @patch('demo_tools.question.ask')
    def test_scenario_handles_aws_errors_gracefully(self, mock_ask, mock_mgr):
        """Test that the scenario handles AWS errors gracefully."""
        # Arrange
        mock_ask.side_effect = [
            "test-resource-name",  # Resource name
        ]

        # Set up stub to simulate AWS error
        with mock_mgr.stub_runner("TestException", 0) as runner:
            runner.add(
                mock_mgr.{service}_stubber.stub_create_resource,
                "test-resource-name", 
                "arn:aws:{service}:us-east-1:123456789012:resource/test-resource",
            )

            # Act & Assert - Should handle error gracefully
            mock_mgr.scenario.run_scenario()
            # Verify error was logged appropriately (scenario should continue or cleanup)


### Live Integration Test Pattern

**MANDATORY**: Always include a live integration test that uses real AWS services:

```python
@pytest.mark.integ
def test_run_scenario_integ(input_mocker, capsys):
    """Test the scenario with an integration test using live AWS services."""
    # Mock user inputs for automated testing
    answers = [
        "test-resource-name-integ",  # Resource name
        "test-parameter",            # Parameter input
        True,                        # Cleanup confirmation
    ]

    input_mocker.mock_answers(answers)
    
    # Create real AWS clients (no stubs)
    {service}_client = boto3.client('{service}')
    
    # Initialize wrappers and scenario with real clients
    {service}_wrapper = {Service}Wrapper({service}_client)
    scenario = {Service}Scenario({service}_wrapper)

    # Run the scenario with live AWS services
    scenario.run_scenario()

    # Verify the scenario completed successfully
    captured = capsys.readouterr()
    assert "scenario completed successfully" in captured.out
```

**Key Features of Live Integration Tests:**
- ✅ **`@pytest.mark.integ` decorator** - Allows selective test execution
- ✅ **Real AWS clients** - Tests against actual AWS services
- ✅ **User input automation** - Uses `input_mocker` to simulate user interactions  
- ✅ **Output verification** - Checks console output for completion messages
- ✅ **Resource cleanup** - Ensures all resources are cleaned up after testing

**Running Integration Tests:**
```bash
# Run only unit tests (stubbed)
pytest test/test_scenario.py

# Run only integration tests (live AWS)
pytest test/test_scenario.py -m integ

# Run all tests
pytest test/test_scenario.py -m "not integ" && pytest test/test_scenario.py -m integ
```


## Scenario Requirements
- ✅ **ALWAYS** read and implement based on `scenarios/basics/{service}/SPECIFICATION.md`
- ✅ **ALWAYS** include descriptive comment block at top explaining scenario steps from specification
- ✅ **ALWAYS** use demo_tools for user interaction
- ✅ **ALWAYS** use service wrapper classes for all AWS operations
- ✅ **ALWAYS** implement proper cleanup in finally block
- ✅ **ALWAYS** break scenario into logical phases per specification
- ✅ **ALWAYS** include error handling per specification's Errors section
- ✅ **ALWAYS** provide educational context and explanations from specification
- ✅ **ALWAYS** handle edge cases (no resources found, etc.) as specified

## Implementation Workflow

### Step-by-Step Implementation Process
1. **Read Specification**: Study `scenarios/basics/{service}/SPECIFICATION.md` thoroughly
2. **Extract API Actions**: Note all API actions listed in "API Actions Used" section
3. **Map to Wrapper Methods**: Ensure wrapper class has methods for all required actions
4. **Implement Phases**: Follow the "Proposed example structure" section exactly
5. **Add Error Handling**: Implement error handling per the "Errors" section
6. **Test Against Specification**: Verify implementation matches specification requirements

### Specification Sections to Implement
- **API Actions Used**: All operations must be available in wrapper class
- **Proposed example structure**: Direct mapping to scenario phases
- **Setup**: Exact setup steps and resource creation
- **Demonstration**: Specific operations to demonstrate
- **Examination**: Data analysis and filtering requirements
- **Cleanup**: Resource cleanup options and user choices
- **Errors**: Specific error codes and handling strategies

## Error Handling in Scenarios
- **Follow specification error table**: Implement exact error handling per specification
- Catch and display user-friendly error messages per specification guidance
- Continue scenario execution when possible as specified
- Provide guidance on resolving issues from specification
- Ensure cleanup runs even if errors occur

## Educational Elements
- **Use specification descriptions**: Explain operations using specification language
- Show before/after states as outlined in specification
- Provide context about service capabilities from specification
- Include tips and best practices mentioned in specification
- Follow the educational flow described in specification structure
