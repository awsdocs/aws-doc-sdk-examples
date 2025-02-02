## Overview
This AWS Entity Resolution basic scenario demonstrates how to interact with the AWS Entity Resolution service using an AWS SDK. The scenario covers various operations such as creating a schema mapping, creating a matching workflow, starting a matching job, and so on. 

## Key Operations

1. **Create an AWS Entity Resolution Schema Mapping**:
   - This step creates an AWS Entity Resolution Schema Mapping by invoking the `createSchemaMapping` method.

2. **Create an AWS Entity Resolution Workflow**:
   - This step creates an AWS Entity Resolution matching Workflow by invoking the `createMatchingWorkflow` method.

3. **Start Matching Workflow**:
   - This step starts the AWS Entity Resolution matching Workflow by invoking the `startMatchingJob` method.

4. **Get Workflow Job Details**:
   - This step gets workflow job details by `getMatchingJob` method.


**Note** See the Eng spec for a full listing of operations. 

## Resources

This Basics scenario requires an IAM role that has permissions to work with the AWS Entity Resolution service, an AWS Glue database, and two S3 buckets. A CDK script is provided to create these resources.

## Implementations

This scenario example will be implemented in the following languages:

- Java
- Python
- Kotlin

## Additional Reading

- [AWS Entity Resolution Documentation](https://docs.aws.amazon.com/entityresolution/latest/userguide/what-is-service.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
