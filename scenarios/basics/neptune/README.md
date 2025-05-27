## Overview
This Amazon Neptune basic scenario demonstrates how to interact with Amazon Neptune using an AWS SDK. The scenario covers various operations such as creating a cluster, creating an instance, starting and stopping the cluster, and so on.

## Key Operations

1. **Create a Neptune DB Subnet Group**:
   - Creates a Neptune DB Subnet Group by invoking `createDBSubnetGroup`.

2. **Create a Neptune Cluster**:
   - Description: Creates a Neptune Cluster by invoking `createDBCluster`.

3. **Create a Neptune DB Instance**:
   - Description: Creates a Neptune DB Instance by invoking `createDBInstance`. 

4. **Check the status of the Neptune DB Instance**:
   - Description: Check the status of the DB instance by invoking `describeDBInstances`. Poll the instance until it reaches an `availbale`state. 

**Note** See the [Engineering specification](SPECIFICATION.md) for a full listing of operations.

## Resources

This Basics scenario does not require any additional AWS resources.

## Implementations

This scenario example will be implemented in the following languages:

- Java
- Python
- Kotlin

## Additional Reading

- [Amazon Neptune Documentation](https://docs.aws.amazon.com/neptune/latest/userguide/intro.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
