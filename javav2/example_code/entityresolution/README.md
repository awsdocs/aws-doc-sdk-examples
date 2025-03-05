# AWS Entity Resolution code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with AWS Entity Resolution.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Entity Resolution helps organizations extract, link, and organize information from multiple data sources._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Entity Resolution](src/main/java/com/example/entity/HelloEntityResoultion.java#L19) (`listMatchingWorkflows`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/entity/scenario/EntityResScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CheckWorkflowStatus](src/main/java/com/example/entity/scenario/EntityResActions.java#L391)
- [CreateMatchingWorkflow](src/main/java/com/example/entity/scenario/EntityResActions.java#L429)
- [CreateSchemaMapping](src/main/java/com/example/entity/scenario/EntityResActions.java#L230)
- [DeleteMatchingWorkflow](src/main/java/com/example/entity/scenario/EntityResActions.java#L196)
- [DeleteSchemaMapping](src/main/java/com/example/entity/scenario/EntityResActions.java#L137)
- [GetMatchingJob](src/main/java/com/example/entity/scenario/EntityResActions.java#L317)
- [GetSchemaMapping](src/main/java/com/example/entity/scenario/EntityResActions.java#L280)
- [ListSchemaMappings](src/main/java/com/example/entity/scenario/EntityResActions.java#L173)
- [StartMatchingJob](src/main/java/com/example/entity/scenario/EntityResActions.java#L354)
- [TagEntityResource](src/main/java/com/example/entity/scenario/EntityResActions.java#L516)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Entity Resolution

This example shows you how to get started using AWS Entity Resolution.


#### Learn the basics

This example shows you how to do the following:

- Create Schema Mapping.
- Create an AWS Entity Resolution workflow.
- Start the matching job for the workflow.
- Get details for the matching job.
- Get Schema Mapping.
- List all Schema Mappings.
- Tag the Schema Mapping resource.
- Delete the AWS Entity Resolution Assets.

<!--custom.basic_prereqs.entityresolution_Scenario.start-->
<!--custom.basic_prereqs.entityresolution_Scenario.end-->


<!--custom.basics.entityresolution_Scenario.start-->
<!--custom.basics.entityresolution_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Entity Resolution User Guide](https://docs.aws.amazon.com/entityresolution/latest/userguide/what-is-service.html)
- [AWS Entity Resolution API Reference](https://docs.aws.amazon.com/entityresolution/latest/apireference/Welcome.html)
- [SDK for Java 2.x AWS Entity Resolution reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/entityresolution/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
