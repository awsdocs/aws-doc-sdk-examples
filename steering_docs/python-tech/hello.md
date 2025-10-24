# Python Hello Examples Generation

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
Generate simple "Hello" examples that demonstrate basic service connectivity and the most fundamental operation using direct boto3 client calls.

## Requirements
- **MANDATORY**: Every AWS service MUST include a "Hello" scenario
- **Simplicity**: Should be the most basic, minimal example possible
- **Standalone**: Must work independently of other examples
- **Direct Client**: Use boto3 client directly, no wrapper classes needed

## File Structure
```
python/example_code/{service}/
├── {service}_hello.py          # Hello example file
```

## Hello Example Pattern
```python
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to get started with {AWS Service} by {basic operation description}.
"""

import logging
import boto3
from botocore.exceptions import ClientError, NoCredentialsError

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.{service}.Hello]
def hello_{service}():
    """
    Use the AWS SDK for Python (Boto3) to create an {AWS Service} client and 
    {basic operation description}.
    This example uses the default settings specified in your shared credentials
    and config files.
    """
    try:
        # Create service client
        {service}_client = boto3.client("{service}")
        
        # Perform the most basic operation for this service
        response = {service}_client.{basic_operation}()
        
        print("Hello, {AWS Service}!")
        # Display appropriate result information
        
    except ClientError as e:
        error_code = e.response["Error"]["Code"]
        if error_code == "UnauthorizedOperation":
            print("You don't have permission to access {AWS Service}.")
        else:
            print(f"Couldn't access {AWS Service}. Error: {e}")
    except NoCredentialsError:
        print("No AWS credentials found. Please configure your credentials.")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")


if __name__ == "__main__":
    hello_{service}()
# snippet-end:[python.example_code.{service}.Hello]
```

## Hello Examples by Service Type

### List-Based Services (S3, DynamoDB, etc.)
- **Operation**: List primary resources (buckets, tables, etc.)
- **Message**: Show count and names of resources

### Status-Based Services (GuardDuty, Config, etc.)  
- **Operation**: Check service status or list detectors/configurations
- **Message**: Show service availability and basic status

### Compute Services (EC2, Lambda, etc.)
- **Operation**: List instances/functions or describe regions
- **Message**: Show available resources or regions

## Validation Requirements
- ✅ **Must run without errors** (with proper credentials)
- ✅ **Must handle credential issues gracefully**
- ✅ **Must display meaningful output**
- ✅ **Must use direct boto3 client calls**
- ✅ **Must include proper snippet tags**

## Common Patterns
- Always use `boto3.client("{service}")` directly
- Include comprehensive error handling
- Provide user-friendly output messages
- Use appropriate logging levels
- Handle both service-specific and general errors
- Keep it as simple as possible - no additional classes or complexity