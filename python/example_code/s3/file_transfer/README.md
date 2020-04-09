# Amazon S3 managed file transfers

## Purpose

Use the Boto 3 transfer manager to manage multipart uploads to and downloads
from an Amazon S3 bucket.

When the file to transfer is larger than the specified threshold, the transfer
manager automatically uses multipart uploads or downloads. This example
shows how to use several of the available transfer manager settings, and reports
thread usage and time to transfer.

## Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.6 or later
- Boto 3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)
- An Amazon S3 bucket to hold uploaded objects
- A folder on your local drive to hold created and downloaded files

## Running the code

Interactively demonstrates the code in file_transfer.py by running the following
in a command window in the file_transfer folder.

    python -m demo_file_transfer

The demonstration script asks questions, takes actions to upload and download
files with various configurations, and manages artifact creation and cleanup.

Amazon S3 objects and downloaded files created during the demonstration are cleaned 
up at the end.

## Running the tests

Tests can be run in two modes. By default, tests use monkeypatch mocking,
which captures requests before they are sent to AWS and returns a mocked response.
Tests can also be run against your AWS account, in which case they will create and 
manipulate AWS resources, which may incur charges on your account.

To run all of the file transfer tests with mocks, run the following in
your [GitHub root]/python/example_code/s3/file_transfer folder.

    python -m pytest -o log_cli=1 --log-cli-level=INFO

The '-o log_cli=1 --log-cli-level=INFO' flags configure pytest to output
logs to stdout during the test run. Without them, pytest captures logs and prints
them only when the test fails.

To run the tests using your AWS account and default shared credentials, include the
'--use-real-aws-may-incur-charges' flag.

    python -m pytest -o log_cli=1 --log-cli-level=INFO --use-real-aws-may-incur-charges

When run in this mode, a best effort is made to clean up any resources created during 
the test. But it's your responsibility to verify that all resources have actually 
been cleaned up.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
