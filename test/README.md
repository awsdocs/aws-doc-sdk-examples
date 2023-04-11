# Multi-Language Integration Test Runner
This directory contains the source code for a fully-functional language-agnostic "integration test runner".

The following design features make this tool easy to use:
* **Serverless**. Requires no stateful infrastructure
* **No pipeline**. Relies on events, not manual interaction.
* **All code**. The entire solution is deployable via CDK.

## Architecture
In addition to the source code in this repository, this solution consists of the following CDK stacks:

| Stack                                                | Function                                                          | Purpose                                                                                                                                                    |
|------------------------------------------------------|-------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Public Images](./public_ecr_repositories)     | Holds versions of language-specialized Docker images.             | Event-based production of ready-to-run Docker images for each [supported SDK](https://docs.aws.amazon.com/sdkref/latest/guide/version-support-matrix.html) |
| [Producer](./eventbridge_rule_with_sns_fanout) | Publishes a scheduled message to an SNS topic.                    | Centralized cron-based triggering of integration tests.                                                                                                    |
| [Consumer](./sqs_lambda_to_batch_fargate)      | Consumes a message to trigger integration tests on Batch Fargate. | Federated integration testing of example code for each [supported SDK](https://docs.aws.amazon.com/sdkref/latest/guide/version-support-matrix.html).       |

The following diagram shows the behavior of this GitHub repository and the above stacks: 

![weathertop-high-level-architecture.png](architecture_diagrams%2Fpng%2Fweathertop-high-level-architecture.png)

