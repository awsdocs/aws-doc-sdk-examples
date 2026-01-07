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

### Cross-Service Scenario Requirements

**MANDATORY**: When wrapper classes are used in cross-service scenarios (e.g., topics_and_queues), the metadata files MUST reference the `.decl` tag pattern:

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
2. **Metadata Consistency**: Follows established patterns from control-tower and other cross-service scenarios  
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

**For Cross-Service Scenario Sections:**
```yaml
{service}_Scenario_CrossService:
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/cross_service/example_scenario
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

**For Cross-Service Scenario Sections:**
```yaml
{service}_Scenario_CrossService:
  languages:
    Python:
      versions:
        - sdk_version: 3
          github: python/cross_service/example_scenario
          excerpts:
            - description: Run an interactive scenario at a command prompt.
              snippet_tags:
                - python.example_code.cross_service.example_scenario.ExampleScenario
            - description: Create classes that wrap service operations.
              snippet_tags:
                - python.example_code.{service}.{Service}Wrapper.decl
                - python.example_code.{other_service}.{OtherService}Wrapper.decl
```

**Why Both Are Required in Scenario Sections:**
1. **Complete Documentation**: Scenarios demonstrate both the orchestration logic and the underlying service operations
2. **Pattern Consistency**: Follows established patterns from controltower and other cross-service scenarios
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
