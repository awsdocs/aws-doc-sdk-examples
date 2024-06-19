# Amazon Elastic Container Registry Service Basic Scenario

## Overview

 This Amazon Elastic Container Registry Service (Amazon ECR) basic scenario demonstrates how to interact with the Amazon ECR service using an AWS SDK.  The scenario covers various operations such as creating an Amazon ECR repository, pushing images to the repository, setting an ECR repository policy, and so on. Here are the 6 top service operations this scenario will cover. 

1. **Create an ECR repository**: The program prompts the user to enter a name for the ECR repository, then creates the repository using the `createECRRepository` method.

2. **Set an ECR repository policy**: The program sets an ECR repository policy using the `setRepoPolicy` method, which grants the specified IAM role the necessary permissions to access the repository.

3. **Retrieve an ECR authorization token**: The program retrieves an ECR authorization token using the `getAuthToken` method, which is required for subsequent operations.

4. **Get the ECR repository URI**: The program retrieves the URI of the ECR repository using the `getRepositoryURI` method.

5. **Push a Docker image to the ECR repository**: The program pushes a local Docker image to the ECR repository using a Docker client.

6. **Verify the image in the ECR repository**: The program verifies that the Docker image was successfully pushed to the ECR repository using the `verifyImage` method.

Note: These steps are not the complete program, but summarizes the 5-6 high-level steps. See the Eng Spec for a complete listing of ECR operations. 

### Resources

The basic scenario requires an IAM role that has Amazon ECR permissions. To create an IAM role, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html). 

This scenario requires a local Docker image created using Bash. To create a local Docker image named "echo-text" using Bash, perform these steps:

1. **Install Docker**: First, you'll need to install Docker and Docker Desktop on your system. You can download the appropriate Docker version for your operating system from the official Docker website (https://www.docker.com/get-started).

2. **Go to the resource folder**: Go to the resource sub folder and notice the two files: `Dockerfile` and `hello.sh`.

3. **Build the Docker image**: In the resource folder that contains `Dockerfile` and `hello.sh`, run the following command to build the Docker image:

   ```bash
   docker build -t echo-text .
   ```

   This command builds a Docker image with the tag "echo-text" using the instructions in the Dockerfile.

4. **Run the Docker container**: Once the image is built, you can run a container based on the "echo-text" image by executing the following command:

   ```bash
   docker run echo-text
   ```


## Implementations

This scenario example will be implemented in the following languages:

- Java
- Python
- Kotlin

## Additional reading

- [Amazon ECR](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
