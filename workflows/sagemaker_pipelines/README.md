# Create and run a SageMaker geospatial pipeline using an AWS SDK

## Overview

This example scenario demonstrates using AWS Lambda and Amazon Simple Queue Service (Amazon SQS) as part of an Amazon [SageMaker pipeline](https://docs.aws.amazon.com/sagemaker/latest/dg/pipelines.html). The pipeline itself executes a [geospatial job](https://docs.aws.amazon.com/sagemaker/latest/dg/geospatial-vej.html) to reverse geocode a sample set of coordinates into human-readable addresses. Input and output files are located in an Amazon Simple Storage Service (Amazon S3) bucket.

When you run the example console application, you can execute the following steps:

- Create the AWS resources and roles needed for the pipeline.
- Create the AWS Lambda function.
- Create the SageMaker pipeline.
- Upload an input file into an S3 bucket.
- Execute the pipeline and monitor its status.
- Display some output from the output file.
- Clean up the pipeline resources.

These steps are completed using AWS SDKs as part of an interactive demo that runs at a command prompt.

## Implementations

This example is implemented in the following languages:

- [.NET](../../dotnetv3/SageMaker/Scenarios/README.md)
- [Java](../../javav2/usecases/workflow_sagemaker_pipes/Readme.md)
- [Kotlin](../../kotlin/usecases/workflow_sagemaker_pipes/Readme.md)
- [JavaScript](../../javascriptv3/example_code/sagemaker/scenarios/wkflw-sagemaker-geospatial-pipeline/README.md)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
