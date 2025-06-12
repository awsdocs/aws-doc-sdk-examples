# AWS Control Tower Basics Scenario

## Overview

This example shows how to use AWS SDKs to work with AWS Control Tower and Control Catalog services. The scenario demonstrates how to manage baselines, controls, and landing zones in AWS Control Tower.

[AWS Control Tower](https://docs.aws.amazon.com/controltower/latest/userguide/what-is-control-tower.html) helps you set up and govern a secure, multi-account AWS environment based on best practices.

This example illustrates typical interactions with AWS Control Tower, including:

1. Listing available baselines and controls.
2. Managing baselines (enabling, disabling, and resetting).
3. Working with controls (enabling, disabling, and checking operation status).
4. Interacting with landing zones.

The scenario follows these steps:

### Hello
- Set up the service client.
- List available baselines by name.

### Scenario
#### Setup
- List available landing zones and prompt the user if they would like to use an existing landing zone.
- If no landing zones exist, provide information about setting up a landing zone.

#### Baselines
- List available baselines.
- If a landing zone exists:
  - List enabled baselines.
  - Enable a baseline.
  - Get the operational status of the baseline operation.
  - Reset the baseline.
  - Disable the baseline.

#### Controls
- List controls in Control Catalog.
- If a landing zone exists:
  - Enable a control.
  - Get the operational status of the control.
  - List enabled controls.
  - Disable the control.

## Implementations

This example is implemented in the following languages:

- [Python](../../../python/example_code/controltower/README.md)

## Additional resources

- [Documentation: AWS Control Tower User Guide](https://docs.aws.amazon.com/controltower/latest/userguide/what-is-control-tower.html)
- [Documentation: AWS Control Tower API Reference](https://docs.aws.amazon.com/controltower/latest/APIReference/Welcome.html)
---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0