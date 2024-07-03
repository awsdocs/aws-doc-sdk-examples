# Amazon S3 Object Integrity Workflow

## Overview

- The workflow demonstrates how to use AWS SDKs to verify the integrity of objects uploaded to Amazon S3.
- It shows how object integrity is verified for different upload methods: PutObject, TransferManager, and multipart upload.
- The workflow demonstrates the use of all 5 hash algorithms supported by S3 for object verification: MD5, CRC32, CRC32C, SHA1, and SHA256.
- This workflow demonstrates the different options provided by the SDK for hashing.
- To demonstrate how the hashes are calculated, the workflow calculates the hashes in the code and compares the results with the hashes calculated automatically by the SDK.


The workflow runs as a command-line application that prompts the user for input.

### Resources

The workflow scenario steps create the bucket and objects needed for the example. No additional resources are required.

## Implementations

This example is implemented in the following languages:

- [C++](../../cpp/example_code/s3/s3_object_integrity_workflow/README.md)

## Additional reading

- [Checking Object Integrity](https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
