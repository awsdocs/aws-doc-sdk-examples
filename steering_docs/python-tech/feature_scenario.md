# Python Feature Scenario Generation

## Purpose
Generate feature scenarios that demonstrate complete workflows using multiple service operations in a guided, educational manner. Implementation must be based on the service SPECIFICATION.md file.

## Target Directory
**IMPORTANT**: All new feature scenarios MUST be created in the `python/example_code/{service}/scenarios/` directory.

- **New scenarios**: `python/example_code/{service}/scenarios/{scenario_name}/`

## Requirements
- **Specification-Driven**: MUST read the `scenarios/features/{service_feature}/SPECIFICATION.md`
- **Interactive**: Use demo_tools.question for user input and guidance
- **Educational**: Break complex workflows into logical phases
- **Comprehensive**: Cover setup, demonstration, examination, and cleanup
- **Error Handling**: Graceful error handling with user-friendly messages
- **Wrapper Classes**: MUST use service wrapper classes for all operations
- **CloudFormation**: Deploy resources using CloudFormation helper when specified
- **Type Hints**: MUST use proper type annotations throughout
- **Documentation**: MUST include comprehensive docstrings

## User Interaction Patterns

### Standard demo_tools.question Functions
**MUST USE**: All user interactions should use the standard functions in `demo_tools.question`. These functions provide consistent validation and error handling.

#### Available Functions

```python
import demo_tools.question as q

# Basic question with no validation
answer = q.ask("Enter your name: ")

# Yes/No questions
use_existing = q.ask("Use existing resource? (y/n): ", q.is_yesno)

# Text input with validation
resource_name = q.ask("Enter resource name: ", q.non_empty)

# Integer input with validation
count = q.ask("How many items? ", q.is_int)

# Integer input with range validation
priority = q.ask("Enter priority (1-10): ", q.is_int, q.in_range(1, 10))

# Float input
price = q.ask("Enter price: ", q.is_float)

# Single letter input
option = q.ask("Select option (a-z): ", q.is_letter)

# Regular expression validation
email = q.ask("Enter email: ", q.re_match(r'^[^@]+@[^@]+\.[^@]+$'))

# Multiple choice selection
choices = ["Option A", "Option B", "Option C"]
selection = q.choose("Select an option: ", choices)
print(f"You selected: {choices[selection]}")
```

#### Validator Functions

- **`q.non_empty`** - Validates that the answer is not empty
- **`q.is_yesno`** - Validates yes/no answers (accepts 'y' or 'Y' as True)
- **`q.is_int`** - Validates integer input
- **`q.is_float`** - Validates floating-point input
- **`q.is_letter`** - Validates single letter input (converts to uppercase)
- **`q.in_range(lower, upper)`** - Validates numeric input within a range
- **`q.re_match(pattern)`** - Validates input against a regular expression pattern

#### Multiple Validators
You can chain multiple validators for complex validation:

```python
# Integer between 1 and 100
value = q.ask("Enter value (1-100): ", q.is_int, q.in_range(1, 100))

# Non-empty string matching pattern
code = q.ask("Enter code: ", q.non_empty, q.re_match(r'^[A-Z]{3}-\d{3}$'))
```

### Information Display Patterns

```python
# Progress indicators
print("✓ Operation completed successfully")
print("⚠ Warning: Resource may take time to initialize")
print("✗ Error occurred during operation")

# Section separators
print("-" * 80)
print("=" * 80)

# Formatted output
print(f"Found {len(items)} items:")
for i, item in enumerate(items, 1):
    print(f"  {i}. {item}")

# Wait for user acknowledgment
q.ask("\nPress Enter to continue...")
```

## Project Structure

Feature scenarios use a structured approach with separate modules for scenarios, wrappers, and CloudFormation helpers:

```
python/example_code/{service}/scenarios/{scenario_name}/
├── {scenario_name}_scenario.py                 # Main scenario file
├── {service}_wrapper.py                       # Wrapper class for service operations
├── cloudformation_helper.py                   # CloudFormation deployment helper (if needed)
├── README.md                                  # Scenario documentation
├── requirements.txt                           # Python dependencies
└── test/
    ├── conftest.py                            # Pytest configuration
    ├── test_{scenario_name}_scenario.py       # Integration tests
    └── test_requirements.txt                  # Test dependencies
```

## MANDATORY Pre-Implementation Steps

### Step 1: Read Scenario Specification
**CRITICAL**: Always read `scenarios/features/{servicefeature}/SPECIFICATION.md` first to understand:
- **API Actions Used**: Exact operations to implement
- **Proposed Example Structure**: Setup, demonstration, examination, cleanup phases
- **Error Handling**: Specific error codes and handling requirements
- **Scenario Flow**: Step-by-step scenario description

### Step 2: Extract Implementation Requirements
From the specification, identify:
- **Setup Phase**: What resources need to be created/configured
- **Demonstration Phase**: What operations to demonstrate
- **Examination Phase**: What data to display and how to filter/analyze
- **Cleanup Phase**: What resources to clean up and user options

## Scenario Class Pattern

### Implementation Pattern Based on SPECIFICATION.md

```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
{AWS Service} Feature Scenario

This scenario demonstrates how to use AWS {Service} to {scenario description}. 
The scenario includes the following steps:

1. Prepare the Application:
   - {Setup step 1 from specification}
   - {Setup step 2 from specification}
   - Deploy the CloudFormation template for resource creation.
   - Store the outputs of the stack into variables for use in the scenario.

2. {Phase 2 Name}:
   - {Phase 2 description from specification}

3. {Phase 3 Name}:
   - {Phase 3 description from specification}

4. Clean up:
   - Prompt the user for y/n answer if they want to destroy the stack and clean up all resources.
   - Delete resources created during the workflow.
   - Destroy the CloudFormation stack and wait until the stack has been removed.
"""

import time
import uuid
import sys
from typing import Tuple, Optional, Dict, Any, List
import logging

import boto3
from botocore.exceptions import ClientError

from cloudformation_helper import CloudFormationHelper
from {service}_wrapper import {Service}Wrapper

# Add relative path to include demo_tools
sys.path.append("../../../..")
import demo_tools.question as q

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.{service}.{Service}Scenario]
class {Service}Scenario:
    """Manages the {AWS Service Feature} scenario."""

    DASHES = "-" * 80
    STACK_NAME = "default-{service}-scenario-stack"
    STACK_RESOURCE_PATH = "../../../../../../scenarios/features/{service_feature}/resources/cfn_template.yaml"

    def __init__(self, {service}_wrapper: {Service}Wrapper, cfn_helper: CloudFormationHelper) -> None:
        """
        Initialize the {AWS Service} scenario.

        Args:
            {service}_wrapper: {Service}Wrapper instance for AWS service operations
            cfn_helper: CloudFormationHelper instance for CloudFormation operations
        """
        self.{service}_wrapper = {service}_wrapper
        self.cfn_helper = cfn_helper
        self._role_arn: Optional[str] = None
        self._target_arn: Optional[str] = None

    def wait_for_input(self) -> None:
        """Wait for user input to continue."""
        q.ask("\nPress Enter to continue...")
        print()

    def setup_resources(self) -> bool:
        """
        Set up initial resources for the scenario.

        Returns:
            bool: True if setup was successful, False otherwise
        """
        print("\nSetting up required resources...")
        try:
            # Prompt the user for required input (e.g., email, parameters)
            print("\nThis example creates resources in a CloudFormation stack.")
            
            user_input = self._prompt_user_for_input()

            # Prompt the user for a name for the CloudFormation stack
            stack_name = self._prompt_user_for_stack_name()

            # Deploy the CloudFormation stack
            deploy_success = self._deploy_cloudformation_stack(stack_name, user_input)

            if deploy_success:
                print("✓ Application preparation complete.")
                return True
            else:
                print("✗ Application preparation failed.")
                return False

        except Exception as e:
            logger.error(f"Error during setup: {e}")
            print(f"Error during setup: {e}")
            return False

    def _deploy_cloudformation_stack(self, stack_name: str, parameter: str) -> bool:
        """
        Deploy the CloudFormation stack with the necessary resources.

        Args:
            stack_name: The name of the CloudFormation stack
            parameter: Parameter value for the stack

        Returns:
            bool: True if the stack was deployed successfully
        """
        print(f"\nDeploying CloudFormation stack: {stack_name}")

        try:
            # Check if template file exists
            import os
            if not os.path.exists(self.STACK_RESOURCE_PATH):
                logger.error(f"CloudFormation template not found: {self.STACK_RESOURCE_PATH}")
                return False

            # Deploy the stack
            success = self.cfn_helper.deploy_cloudformation_stack(
                stack_name, 
                self.STACK_RESOURCE_PATH,
                parameters={"ParameterName": parameter} if parameter else None
            )

            if success:
                print(f"✓ CloudFormation stack creation completed: {stack_name}")
                
                # Retrieve the output values
                return self._get_stack_outputs(stack_name)
            else:
                logger.error(f"Failed to create CloudFormation stack: {stack_name}")
                return False

        except Exception as e:
            logger.error(f"Error deploying CloudFormation stack: {e}")
            return False

    def _get_stack_outputs(self, stack_name: str) -> bool:
        """
        Retrieve the output values from the CloudFormation stack.

        Args:
            stack_name: The name of the CloudFormation stack

        Returns:
            bool: True if outputs were retrieved successfully
        """
        try:
            outputs = self.cfn_helper.get_stack_outputs(stack_name)
            
            if outputs:
                self._role_arn = outputs.get('RoleARN')
                self._target_arn = outputs.get('TargetARN')
                
                if self._role_arn:
                    print(f"Stack output RoleARN: {self._role_arn}")
                if self._target_arn:
                    print(f"Stack output TargetARN: {self._target_arn}")
                
                return True
            else:
                logger.error("No stack outputs found")
                return False

        except Exception as e:
            logger.error(f"Failed to retrieve CloudFormation stack outputs: {e}")
            return False

    def run_scenario(self) -> None:
        """Run the {AWS Service Feature} scenario."""
        print(self.DASHES)
        print("Welcome to the {AWS Service Feature} Scenario.")
        print(self.DASHES)
        print("""
    This scenario demonstrates how to use {AWS Service} to {feature description}.
    
    The scenario will guide you through:
    1. Setting up the necessary AWS resources
    2. {Demonstration phase description}
    3. {Examination phase description}
    4. Cleaning up resources
        """)

        try:
            # Setup Phase
            print(self.DASHES)
            setup_success = self.setup_resources()
            print(self.DASHES)

            if setup_success:
                # Demonstration Phase
                print(self.DASHES)
                self._demonstration_phase()
                print(self.DASHES)

                # Examination Phase
                print(self.DASHES)
                self._examination_phase()
                print(self.DASHES)

            # Cleanup Phase
            print(self.DASHES)
            self._cleanup_phase()
            print(self.DASHES)

        except Exception as e:
            logger.error(f"Scenario failed: {e}")
            print(f"There was a problem with the scenario: {e}")
            print("\nInitiating cleanup...")
            try:
                self._cleanup_phase()
            except Exception as cleanup_error:
                logger.error(f"Error during cleanup: {cleanup_error}")

        print("✓ {AWS Service} feature scenario completed.")
        print(self.DASHES)

    def _demonstration_phase(self) -> None:
        """
        Demonstration phase: Implement based on specification.
        
        This phase demonstrates the core service operations as outlined
        in the specification's demonstration section.
        """
        print("Phase 2: {Demonstration Phase Name}")
        print("{Phase description from specification}")
        
        try:
            # Implement demonstration operations from specification
            # Example: Create resources, perform operations
            result = self.{service}_wrapper.perform_operation(self._role_arn, self._target_arn)
            
            if result:
                print("✓ Demonstration operations completed successfully")
            else:
                print("✗ Some demonstration operations encountered issues")
                
            self.wait_for_input()

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"AWS service error during demonstration: {error_code} - {e}")
            print(f"AWS service error: {e}")
        except Exception as e:
            logger.error(f"Error during demonstration phase: {e}")
            print(f"Error during demonstration: {e}")

    def _examination_phase(self) -> None:
        """
        Examination phase: Implement based on specification.
        
        This phase examines the results and demonstrates data analysis
        as outlined in the specification's examination section.
        """
        print("Phase 3: {Examination Phase Name}")
        print("{Phase description from specification}")
        
        try:
            # Implement examination operations from specification
            # Example: List resources, analyze data, show results
            data = self.{service}_wrapper.list_resources()
            
            if data:
                print(f"Found {len(data)} items:")
                for i, item in enumerate(data[:5]):  # Show first 5 items
                    print(f"  {i+1}. {item}")
                
                if len(data) > 5:
                    show_more = q.ask(f"Show all {len(data)} items? (y/n): ", q.is_yesno)
                    if show_more:
                        for i, item in enumerate(data[5:], 6):
                            print(f"  {i}. {item}")
            else:
                print("No data found.")
            
            self.wait_for_input()

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            logger.error(f"AWS service error during examination: {error_code} - {e}")
            print(f"AWS service error: {e}")
        except Exception as e:
            logger.error(f"Error during examination phase: {e}")
            print(f"Error during examination: {e}")

    def _cleanup_phase(self) -> None:
        """
        Clean up the resources created during the scenario.
        
        Prompts user for confirmation before deleting resources.
        """
        print("Cleanup Phase")
        
        # Prompt the user to confirm cleanup
        cleanup = q.ask(
            "Do you want to delete all resources created by this scenario? (y/n): ", q.is_yesno
        )
        
        if cleanup:
            try:
                # Delete scenario-specific resources first
                print("Deleting scenario resources...")
                cleanup_success = self.{service}_wrapper.cleanup_resources()
                
                if cleanup_success:
                    print("✓ Scenario resources deleted successfully")
                
                # Destroy the CloudFormation stack
                print("Deleting CloudFormation stack...")
                stack_delete_success = self.cfn_helper.destroy_cloudformation_stack(self.STACK_NAME)
                
                if stack_delete_success:
                    print("✓ CloudFormation stack deleted successfully")
                else:
                    print("✗ Failed to delete CloudFormation stack")
                
            except Exception as e:
                logger.error(f"Error during cleanup: {e}")
                print(f"Error during cleanup: {e}")
        else:
            print("Resources will be retained. You can delete them manually from the AWS Console.")
        
        print("Cleanup complete.")

    def _prompt_user_for_stack_name(self) -> str:
        """
        Prompt the user for a valid CloudFormation stack name.

        Returns:
            str: Valid stack name
        """
        while True:
            stack_name = q.ask("Enter a name for the AWS CloudFormation Stack: ")
            
            if not stack_name:
                print("Stack name cannot be empty.")
                continue
            
            # Basic validation for CloudFormation stack naming rules
            import re
            if not re.match(r'^[a-zA-Z][-a-zA-Z0-9]*$', stack_name):
                print("Invalid stack name. Use letters, numbers, and hyphens only. Must start with a letter.")
                continue
            
            if len(stack_name) > 128:
                print("Stack name must be 128 characters or fewer.")
                continue
                
            return stack_name

    def _prompt_user_for_input(self) -> str:
        """
        Prompt the user for required input parameters.

        Returns:
            str: User input value
        """
        # Customize based on scenario requirements
        user_input = q.ask("Enter required parameter value: ")
        return user_input

# snippet-end:[python.example_code.{service}.{Service}Scenario]


def main() -> None:
    """
    Main function to run the {AWS Service} feature scenario.
    
    This example uses the default settings specified in your shared credentials
    and config files.
    """
    logging.basicConfig(level=logging.WARNING, format="%(levelname)s: %(message)s")
    
    try:
        # Initialize AWS clients
        {service}_client = boto3.client('{service}')
        cloudformation_client = boto3.client('cloudformation')
        
        # Initialize wrappers
        {service}_wrapper = {Service}Wrapper({service}_client)
        cfn_helper = CloudFormationHelper(cloudformation_client)
        
        # Run the scenario
        scenario = {Service}Scenario({service}_wrapper, cfn_helper)
        scenario.run_scenario()
        
    except Exception as e:
        logger.error(f"Failed to run scenario: {e}")
        print(f"Failed to run the scenario: {e}")


if __name__ == "__main__":
    main()
```

## CloudFormation Helper Pattern

### CloudFormation Helper Class Structure
```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
CloudFormation helper for deploying and managing stacks in scenarios.
"""

import json
import time
from typing import Dict, Optional, Any
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class CloudFormationHelper:
    """Helper class for CloudFormation operations."""

    def __init__(self, cloudformation_client: Any) -> None:
        """
        Initialize the CloudFormation helper.

        Args:
            cloudformation_client: Boto3 CloudFormation client
        """
        self.cf_client = cloudformation_client

    def deploy_cloudformation_stack(
        self, 
        stack_name: str, 
        template_path: str, 
        parameters: Optional[Dict[str, str]] = None
    ) -> bool:
        """
        Deploy a CloudFormation stack.

        Args:
            stack_name: Name of the stack to create
            template_path: Path to the CloudFormation template file
            parameters: Optional parameters for the stack

        Returns:
            bool: True if deployment was successful
        """
        try:
            # Read template file
            with open(template_path, 'r', encoding='utf-8') as template_file:
                template_body = template_file.read()

            # Prepare create stack request
            create_request = {
                'StackName': stack_name,
                'TemplateBody': template_body,
                'Capabilities': ['CAPABILITY_NAMED_IAM']
            }

            # Add parameters if provided
            if parameters:
                create_request['Parameters'] = [
                    {'ParameterKey': key, 'ParameterValue': value}
                    for key, value in parameters.items()
                ]

            # Create the stack
            response = self.cf_client.create_stack(**create_request)

            if response['ResponseMetadata']['HTTPStatusCode'] == 200:
                print(f"CloudFormation stack creation started: {stack_name}")
                
                # Wait for stack creation to complete
                return self._wait_for_stack_completion(stack_name)
            else:
                logger.error(f"Failed to create CloudFormation stack: {stack_name}")
                return False

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            if error_code == 'AlreadyExistsException':
                print(f"CloudFormation stack '{stack_name}' already exists.")
                return True
            else:
                logger.error(f"Error creating CloudFormation stack: {e}")
                return False
        except Exception as e:
            logger.error(f"Error reading template file or creating stack: {e}")
            return False

    def _wait_for_stack_completion(self, stack_name: str) -> bool:
        """
        Wait for CloudFormation stack to complete creation.

        Args:
            stack_name: Name of the stack to monitor

        Returns:
            bool: True if stack creation was successful
        """
        max_attempts = 60
        attempt = 0
        delay = 30  # seconds

        print("Waiting for CloudFormation stack creation to complete...")

        while attempt < max_attempts:
            try:
                response = self.cf_client.describe_stacks(StackName=stack_name)
                
                if response['Stacks']:
                    stack_status = response['Stacks'][0]['StackStatus']
                    print(f"Stack status: {stack_status}")
                    
                    if stack_status == 'CREATE_COMPLETE':
                        print("✓ CloudFormation stack creation complete.")
                        return True
                    elif stack_status in ['CREATE_FAILED', 'ROLLBACK_COMPLETE']:
                        print("✗ CloudFormation stack creation failed.")
                        return False

                time.sleep(delay)
                attempt += 1

            except ClientError as e:
                logger.error(f"Error checking stack status: {e}")
                return False

        logger.error("Timed out waiting for CloudFormation stack creation to complete.")
        return False

    def get_stack_outputs(self, stack_name: str) -> Optional[Dict[str, str]]:
        """
        Get outputs from a CloudFormation stack.

        Args:
            stack_name: Name of the stack

        Returns:
            Dict containing stack outputs, or None if not found
        """
        try:
            response = self.cf_client.describe_stacks(StackName=stack_name)
            
            if response['Stacks']:
                stack = response['Stacks'][0]
                outputs = {}
                
                for output in stack.get('Outputs', []):
                    outputs[output['OutputKey']] = output['OutputValue']
                
                return outputs
            else:
                logger.error(f"No stack found: {stack_name}")
                return None

        except ClientError as e:
            logger.error(f"Error getting stack outputs: {e}")
            return None

    def destroy_cloudformation_stack(self, stack_name: str) -> bool:
        """
        Delete a CloudFormation stack.

        Args:
            stack_name: Name of the stack to delete

        Returns:
            bool: True if deletion was successful
        """
        try:
            self.cf_client.delete_stack(StackName=stack_name)
            print(f"CloudFormation stack '{stack_name}' deletion started...")
            
            return self._wait_for_stack_deletion(stack_name)

        except ClientError as e:
            logger.error(f"Error deleting CloudFormation stack: {e}")
            return False

    def _wait_for_stack_deletion(self, stack_name: str) -> bool:
        """
        Wait for CloudFormation stack deletion to complete.

        Args:
            stack_name: Name of the stack to monitor

        Returns:
            bool: True if deletion was successful
        """
        max_attempts = 60
        attempt = 0
        delay = 30  # seconds

        print("Waiting for CloudFormation stack deletion to complete...")

        while attempt < max_attempts:
            try:
                response = self.cf_client.describe_stacks(StackName=stack_name)
                
                if response['Stacks']:
                    stack_status = response['Stacks'][0]['StackStatus']
                    print(f"Stack status: {stack_status}")
                    
                    if stack_status == 'DELETE_COMPLETE':
                        print("✓ CloudFormation stack deletion complete.")
                        return True
                    elif stack_status == 'DELETE_FAILED':
                        print("✗ CloudFormation stack deletion failed.")
                        return False

                time.sleep(delay)
                attempt += 1

            except ClientError as e:
                error_code = e.response.get('Error', {}).get('Code', 'Unknown')
                if error_code == 'ValidationError':
                    # Stack no longer exists
                    print("✓ CloudFormation stack has been deleted.")
                    return True
                else:
                    logger.error(f"Error checking stack deletion status: {e}")
                    return False

        logger.error("Timed out waiting for CloudFormation stack deletion to complete.")
        return False
```

## Wrapper Class Pattern

### Snippet Tag Requirements

**CRITICAL**: Wrapper classes MUST use the correct snippet tag structure to ensure proper metadata integration and test compatibility.

#### Required Snippet Tag Structure

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

#### Cross-Service Scenario Requirements

**MANDATORY**: When wrapper classes are used in cross-service scenarios (e.g., topics_and_queues), the metadata files MUST reference the `.decl` tag pattern:

**Correct Metadata Reference:**
```yaml
python/cross_service/example_scenario:
  excerpts:
    - snippet_tags:
        - python.example_code.{service}.{Service}Wrapper.decl  # ✓ CORRECT
        - python.example_code.{service}.MethodName
```

**Incorrect Metadata Reference:**
```yaml
python/cross_service/example_scenario:
  excerpts:
    - snippet_tags:
        - python.example_code.{service}.{Service}Wrapper      # ✗ INCORRECT
        - python.example_code.{service}.MethodName
```

#### Why This Pattern Is Required

1. **Stub Test Compatibility**: The `.decl` pattern ensures stubbed tests can properly mock wrapper class declarations
2. **Metadata Consistency**: Follows established patterns from control-tower and other cross-service scenarios  
3. **Documentation Generation**: Enables proper extraction of class declarations for documentation
4. **Parameter Matching**: Prevents "Unexpected API Call" errors in stubbed tests

#### Implementation Example

**In Wrapper File (`{service}_wrapper.py`):**
```python
# snippet-start:[python.example_code.{servicename}.{Service}Wrapper.decl]
class {Service}Wrapper:
    """Wrapper class for managing {AWS Service} operations."""

    def __init__(self, {service}_client: Any) -> None:
        """Initialize the {Service}Wrapper."""
        self.{service}_client = {service}_client
# snippet-end:[python.example_code.{servicename}.{Service}Wrapper.decl]

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
          github: python/cross_service/example_scenario
          excerpts:
            - snippet_tags:
                - python.example_code.{service}.{Service}Wrapper.decl
                - python.example_code.{service}.CreateResource
```

**For Scenario Sections (CRITICAL - Must Include Both Scenario and Wrapper Classes):**
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


### Wrapper Class Structure
```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for AWS {Service} operations.
"""

import logging
from typing import List, Dict, Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.{serviceExample}.{Service}Wrapper.class]
# snippet-start:[python.example_code.{serviceExample}.{Service}Wrapper.decl]
class {Service}Wrapper:
    """Wrapper class for managing {AWS Service} operations."""

    def __init__(self, {service}_client: Any) -> None:
        """
        Initialize the {Service}Wrapper.
        
        Args:
            {service}_client: A Boto3 {AWS Service} client. This client provides low-level
                            access to AWS {Service} services.
        """
        self.{service}_client = {service}_client

    @classmethod
    def from_client(cls) -> '{Service}Wrapper':
        """
        Create a {Service}Wrapper instance using a default boto3 client.
        
        Returns:
            {Service}Wrapper: An instance of this class.
        """
        {service}_client = boto3.client('{service}')
        return cls({service}_client)
# snippet-end:[python.example_code.{serviceExample}.{Service}Wrapper.decl]

    # snippet-start:[python.example_code.{service}.operation_name]
    def operation_name(self, parameter: str) -> bool:
        """
        Description of what this operation does.

        Args:
            parameter: Description of parameter

        Returns:
            bool: True if successful, False otherwise

        Raises:
            ClientError: If the operation fails
        """
        try:
            response = self.{service}_client.operation_name(Parameter=parameter)
            
            print(f"✓ Operation completed successfully")
            logger.info(f"Operation result: {response}")
            return True

        except ClientError as e:
            error_code = e.response.get('Error', {}).get('Code', 'Unknown')
            
            # Handle specific errors based on specification
            if error_code == 'ResourceNotFoundException':
                logger.error(f"Resource not found: {e}")
                print(f"Resource not found: {parameter}")
                return False
            elif error_code == 'InvalidParameterException':
                logger.error(f"Invalid parameter: {e}")
                print(f"Invalid parameter provided: {parameter}")
                return False
            else:
                logger.error(f"Error in operation: {e}")
                print(f"An error occurred: {e}")
                raise

    # snippet-end:[python.example_code.{service}.operation_name]

    def list_resources(self) -> List[Dict[str, Any]]:
        """
        List resources from the service.

        Returns:
            List of resource dictionaries

        Raises:
            ClientError: If the operation fails
        """
        try:
            response = self.{service}_client.list_resources()
            resources = response.get('Resources', [])
            
            logger.info(f"Found {len(resources)} resources")
            return resources

        except ClientError as e:
            logger.error(f"Error listing resources: {e}")
            print(f"Error listing resources: {e}")
            return []

    def cleanup_resources(self) -> bool:
        """
        Clean up resources created during the scenario.

        Returns:
            bool: True if cleanup was successful

        Raises:
            ClientError: If cleanup fails
        """
        try:
            # Implement cleanup logic based on scenario requirements
            print("Cleaning up scenario resources...")
            
            # Example: Delete created resources
            # response = self.{service}_client.delete_resource(ResourceId=resource_id)
            
            print("✓ Resource cleanup completed")
            return True

        except ClientError as e:
            logger.error(f"Error during cleanup: {e}")
            print(f"Error during cleanup: {e}")
            return False

# snippet-end:[python.example_code.{serviceExample}.{Service}Wrapper.class]
```

### Wrapper Method Guidelines
- Return `bool` for success/failure operations
- Return specific types for data retrieval operations
- Log errors using the logger
- Display user-friendly messages to console
- Catch specific exceptions first, then general ClientError
- Include type hints for all parameters and returns
- Use docstrings following Google style
- Use snippet tags for documentation extraction

## Error Handling

### Specification-Based Error Handling
The specification includes an "Errors" section with specific error codes and handling:

```python
# Example error handling based on specification
try:
    response = self.{service}_wrapper.create_resource()
    return response
except ClientError as e:
    error_code = e.response.get('Error', {}).get('Code', 'Unknown')
    
    # Handle as specified: Resource already exists
    if error_code == 'ConflictException':
        logger.error(f"Resource conflict: {e}")
        print("Resource already exists. Please choose a different name.")
        return False
    # Handle as specified: Resource not found
    elif error_code == 'ResourceNotFoundException':
        logger.error(f"Resource not found: {e}")
        print("Required resource not found. Please check your configuration.")
        return True  # May return True if deletion was the goal
    else:
        logger.error(f"Unexpected error: {e}")
        print(f"An unexpected error occurred: {e}")
        raise
```

### Scenario Error Handling
- Wrap main scenario phases in try-catch blocks
- Log errors and initiate cleanup on failure
- Ensure cleanup runs in finally block or after error
- Provide helpful error messages to users
- Continue with scenario when possible

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
