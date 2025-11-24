# Python Service Wrapper Generation

## MANDATORY: Knowledge Base Consultation (FIRST STEP)
**🚨 CRITICAL - Must be completed BEFORE any code generation**

```bash
# Step 1: List available knowledge bases
ListKnowledgeBases()

# Step 2: Query coding standards (REQUIRED)
QueryKnowledgeBases("coding-standards-KB", "Python-code-example-standards")

# Step 3: Query implementation patterns (REQUIRED)  
QueryKnowledgeBases("Python-premium-KB", "Python implementation patterns")

# Step 4: AWS service research (REQUIRED)
search_documentation("What is [AWS Service] and what are its key API operations?")
read_documentation("https://docs.aws.amazon.com/[service]/latest/[relevant-page]")
```

**FAILURE TO COMPLETE KNOWLEDGE BASE CONSULTATION WILL RESULT IN INCORRECT CODE STRUCTURE**

## Purpose
Generate service wrapper classes that encapsulate AWS service functionality with proper error handling and logging.

## Requirements
- **MANDATORY**: Every service MUST have a wrapper class
- **Error Handling**: Handle service-specific errors appropriately
- **Logging**: Include comprehensive logging for all operations
- **Type Hints**: Use type annotations for better code clarity

## File Structure
```
python/example_code/{service}/
├── {service}_wrapper.py        # Service wrapper class
```

## Wrapper Class Pattern
```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with {AWS Service} to
{service description and main use cases}.
"""

import logging
import boto3
from botocore.exceptions import ClientError
from typing import Dict, List, Optional, Any

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.{service}.{Service}Wrapper]
class {Service}Wrapper:
    """Encapsulates {AWS Service} functionality."""

    def __init__(self, {service}_client: boto3.client):
        """
        :param {service}_client: A Boto3 {AWS Service} client.
        """
        self.{service}_client = {service}_client

    @classmethod
    def from_client(cls):
        """
        Creates a {Service}Wrapper instance with a default {AWS Service} client.
        
        :return: An instance of {Service}Wrapper.
        """
        {service}_client = boto3.client("{service}")
        return cls({service}_client)
# snippet-end:[python.example_code.{service}.{Service}Wrapper]

    # Individual action methods follow...
```

## Action Method Pattern
```python
    # snippet-start:[python.example_code.{service}.{ActionName}]
    def {action_method}(self, param: str) -> Dict[str, Any]:
        """
        {Action description}.

        :param param: Parameter description.
        :return: Response description.
        """
        try:
            response = self.{service}_client.{action_name}(Parameter=param)
            logger.info(f"{Action} completed successfully")
            return response
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "SpecificError":
                logger.error("Specific error handling message")
            elif error_code == "AnotherSpecificError":
                logger.error("Another specific error handling message")
            else:
                logger.error(f"Error in {action}: {e}")
            raise
    # snippet-end:[python.example_code.{service}.{ActionName}]
```

## Paginator Pattern for List Operations
```python
    # snippet-start:[python.example_code.{service}.List{Resources}]
    def list_{resources}(self) -> List[Dict[str, Any]]:
        """
        Lists all {resources} using pagination to retrieve complete results.

        :return: A list of all {resources}.
        """
        try:
            # Use paginator when available
            paginator = self.{service}_client.get_paginator('list_{resources}')
            {resources} = []
            
            for page in paginator.paginate():
                {resources}.extend(page.get('{Resources}', []))
            
            logger.info(f"Retrieved {len({resources})} {resources}")
            return {resources}
        except ClientError as e:
            error_code = e.response["Error"]["Code"]
            if error_code == "BadRequestException":
                logger.error("Invalid request parameters for listing {resources}")
            else:
                logger.error(f"Error listing {resources}: {e}")
            raise
    # snippet-end:[python.example_code.{service}.List{Resources}]
```

## Paginator with Parameters Pattern
```python
    # snippet-start:[python.example_code.{service}.List{Resources}WithFilter]
    def list_{resources}_with_filter(self, filter_criteria: Optional[Dict] = None) -> List[Dict[str, Any]]:
        """
        Lists {resources} with optional filtering, using pagination.

        :param filter_criteria: Optional criteria to filter results.
        :return: A list of filtered {resources}.
        """
        try:
            paginator = self.{service}_client.get_paginator('list_{resources}')
            {resources} = []
            
            # Build pagination parameters
            paginate_params = {}
            if filter_criteria:
                paginate_params['FilterCriteria'] = filter_criteria
            
            for page in paginator.paginate(**paginate_params):
                {resources}.extend(page.get('{Resources}', []))
            
            logger.info(f"Retrieved {len({resources})} filtered {resources}")
            return {resources}
        except ClientError as e:
            logger.error(f"Error listing {resources} with filter: {e}")
            raise
    # snippet-end:[python.example_code.{service}.List{Resources}WithFilter]
```

## Error Handling Requirements

### Service-Specific Errors
Based on the service specification, handle these error types:
- **BadRequestException**: Validate input parameters
- **InternalServerErrorException**: Retry with exponential backoff
- **ResourceNotFoundException**: Handle missing resources gracefully
- **AccessDeniedException**: Provide clear permission guidance

### Error Handling Pattern
```python
try:
    response = self.{service}_client.operation()
    return response
except ClientError as e:
    error_code = e.response["Error"]["Code"]
    if error_code == "BadRequestException":
        logger.error("Validate input parameters and notify user")
    elif error_code == "InternalServerErrorException":
        logger.error("Retry operation with exponential backoff")
    else:
        logger.error(f"Error in operation: {e}")
    raise
```

## Wrapper Class Requirements
- ✅ **ALWAYS** include proper logging for all operations
- ✅ **ALWAYS** provide `from_client()` class method
- ✅ **ALWAYS** handle service-specific errors per specification
- ✅ **ALWAYS** include comprehensive docstrings
- ✅ **ALWAYS** use type hints for parameters and returns
- ✅ **ALWAYS** follow the established naming conventions
- ✅ **ALWAYS** use paginators for list operations when available
- ✅ **ALWAYS** retrieve complete results, not just first page

## Method Naming Conventions
- Use snake_case for method names
- Match AWS API operation names (e.g., `list_buckets` for `ListBuckets`)
- Use descriptive parameter names
- Return appropriate data types (Dict, List, str, etc.)

## Pagination Requirements
- **MANDATORY**: Use paginators when available for list operations
- **Complete Results**: Ensure all pages are retrieved, not just first page
- **Efficient**: Use boto3 paginators instead of manual pagination

## Paginator Usage Guidelines

### When to Use Paginators
- ✅ **List Operations**: Always use for operations that return lists of resources
- ✅ **Large Result Sets**: Essential for services that may return many items
- ✅ **Complete Data**: Ensures all results are retrieved, not just first page

### How to Check for Paginator Availability
```python
# Check if paginator exists for an operation
try:
    paginator = self.{service}_client.get_paginator('list_{resources}')
    # Use paginator
except Exception:
    # Fall back to regular API call if paginator not available
    response = self.{service}_client.list_{resources}()
```

### Common Paginator Operations
- `list_buckets` → Use paginator if available
- `list_objects` → Always use paginator (can return thousands of objects)
- `list_tables` → Use paginator for complete results
- `describe_instances` → Use paginator for large environments
- `list_functions` → Use paginator for complete function list

## Documentation Requirements
- Include module-level docstring explaining service purpose
- Document all parameters with type and description
- Document return values with type and description
- Include usage examples in docstrings where helpful
- Use proper snippet tags for documentation generation
- Document pagination behavior in list method docstrings