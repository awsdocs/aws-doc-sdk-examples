# AWS Entity Resolution Java Program

## Overview
This AWS Entity Resolution basic scenario demonstrates how to interact with the AWS Entity Resolution service using an AWS SDK. This Java application demonstrates how to use AWS Entity Resolution to integrate and deduplicate data from multiple sources using machine learning-based matching. The program walks through setting up AWS resources, uploading structured data, defining schema mappings, creating a matching workflow, and running a matching job.


**Note:** See the [specification document](SPECIFICATION.md) for a complete list of operations. 

## Features

1. Uses AWS CloudFormation to create necessary resources:

- AWS Glue Data Catalog table

- AWS IAM role

- AWS S3 bucket

- AWS Entity Resolution Schema

2. Uploads sample JSON and CSV data to S3

3. Creates schema mappings for JSON and CSV datasets

4. Creates and starts an Entity Resolution matching workflow

5. Retrieves job details and schema mappings

6. Lists available schema mappings

7. Tags AWS resources for better organization

8. Views the results of the workflow

## Resources

This Basics scenario requires an IAM role that has permissions to work with the AWS Entity Resolution service, 
an AWS Glue database, and an S3 bucket. A CDK script is provided to create these resources. 
See the resources [Readme](../../../resources/cdk/entityresolution_resources/README.md) file.

## Implementations

This scenario example will be implemented in the following languages:

- Java
- Python
- Kotlin

## Additional Reading

- [AWS Entity Resolution Documentation](https://docs.aws.amazon.com/entityresolution/latest/userguide/what-is-service.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
