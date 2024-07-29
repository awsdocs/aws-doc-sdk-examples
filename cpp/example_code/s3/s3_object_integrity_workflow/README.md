# Amazon S3 Object Integrity Workflow

## Overview

- The workflow demonstrates how to use the AWS SDK for C++ to verify the integrity of objects uploaded to Amazon S3.
- It shows how object integrity is verified for different upload methods: PutObject, TransferManager, and multipart upload.
- The workflow demonstrates the use of all 5 hash algorithms supported by S3 for object verification: MD5, CRC32, CRC32C, SHA1, and SHA256.
- This workflow demonstrates the different options provided by the SDK for hashing.
- To demonstrate how the hashes are calculated, the workflow calculates the hashes in the code and compares the results with the hashes calculated automatically by the SDK.


The workflow runs as a command-line application that prompts the user for input.

## Scenario

### Prerequisites

To run this workflow, you'll need the following:

- CMake - A C++ cross-platform build system.
- AWS SDK for C++.

### Build and Run the Workflow



```shell
mkdir build
cd build
cmake .. -DCMAKE_BUILD_TYPE=Debug
cmake --build . --config=Debug
./run_medical_image_sets_and_frames_workflow
```

## Additional Resources

- [Checking Object Integrity](https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html)
- [Amazon S3 User Guide](https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html)
- [AWS SDK for C++ Developer Guide](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/welcome.html)
- [AWS SDK for C++ API Reference](https://sdk.amazonaws.com/cpp/api/LATEST/index.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0