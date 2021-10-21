# AWS SDK for Rust code examples for AWS IoT

AWS IoT provides secure, bi-directional communication for Internet-connected devices (such as sensors, actuators, embedded devices, wireless devices, and smart appliances) to connect to the AWS Cloud over MQTT, HTTPS, and LoRaWAN.

## Purpose

These examples demonstrate how to perform several AWS IoT operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### describe-endpoint

This example returns a unique endpoint specific to the AWS account making the call, in the Region.

`cargo run --bin describe-endpoint -- -e ENDPOINT-TYPE [-r REGION] [-v]`

- _ENDPOINT-TYPE_ is the type of the endpoint. It must be one of:
  - iot:Data - Returns a VeriSign signed data endpoint.
  - iot:Data-ATS - Returns an ATS signed data endpoint.
  - iot:CredentialProvider - Returns an AWS IoT credentials provider API endpoint.
  - iot:Jobs - Returns an AWS IoT device management Jobs API endpoint.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-things

This example lists the name, type, and ARN of your IoT things in the Region. 

`cargo run --bin list-things -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0