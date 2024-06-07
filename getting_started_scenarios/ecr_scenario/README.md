# Amazon Elastic Container Registry Service Getting Started Scenario

## Overview

 This Amazon Elastic Container Registry Service (Amazon ECR) getting started scenario demonstrates how to interact with the Amazon ECR service using an AWS SDK.  The scenario covers various operations such as creating an Amazon ECR repository, pushing images to the repository, setting an ECR repository policy, and so on. Here are the 6 top service operations this scenario will cover. 

1. **Create an ECR repository**: The program prompts the user to enter a name for the ECR repository, then creates the repository using the `createECRRepository` method.

2. **Set an ECR repository policy**: The program sets an ECR repository policy using the `setRepoPolicy` method, which grants the specified IAM role the necessary permissions to access the repository.

3. **Retrieve an ECR authorization token**: The program retrieves an ECR authorization token using the `getAuthToken` method, which is required for subsequent operations.

4. **Get the ECR repository URI**: The program retrieves the URI of the ECR repository using the `getRepositoryURI` method.

5. **Push a Docker image to the ECR repository**: The program pushes a local Docker image to the ECR repository using the `pushDockerImage` method, which includes calculating the image's SHA-256 hash, checking layer availability, and completing the image upload.

6. **Verify the image in the ECR repository**: The program verifies that the Docker image was successfully pushed to the ECR repository using the `verifyImage` method.

These steps cover the core KMS operations, including creating and managing keys, encrypting and decrypting data, controlling access through grants and policies, and managing the key's lifecycle. 

Note: These steps are not the complete program, but summarizes the 5-6 high-level steps. See the Eng Spec for a complete listing of ECR operations. 

### Resources

The getting started scenario requires an IAM role that has Amazon ECR permissions. In addtion, this scenario requires a local Docker image created using Bash. To create a local "Hello World" Docker image using Bash:

1. **Install Docker**: First, you'll need to install Docker on your system. You can download the appropriate Docker version for your operating system from the official Docker website (https://www.docker.com/get-started).

2. **Create a Dockerfile**: Create a new file named `Dockerfile` in your preferred directory. This file will contain the instructions for building your Docker image.

   ```bash
   touch Dockerfile
   ```

3. **Open the Dockerfile**: Open the `Dockerfile` in a text editor and add the following contents:

   ```docker
   # Use the official Alpine Linux image as the base image
   FROM alpine:latest

   # Set the working directory to /app
   WORKDIR /app

   # Copy the "hello.sh" script into the container
   COPY hello.sh .

   # Make the "hello.sh" script executable
   RUN chmod +x hello.sh

   # Define the command to run the "hello.sh" script
   CMD ["./hello.sh"]
   ```

   In this Dockerfile, we:
   - Use the official Alpine Linux image as the base image.
   - Set the working directory to `/app`.
   - Copy the `hello.sh` script into the container.
   - Make the `hello.sh` script executable.
   - Define the command to run the `hello.sh` script when the container starts.

4. **Create the "hello.sh" script**: Create a new file named `hello.sh` in the same directory as the Dockerfile and add the following content:

   ```bash
   #!/bin/sh
   echo "Hello, World!"
   ```

   This is a simple Bash script that prints "Hello, World!" to the console.

5. **Build the Docker image**: In the same directory as the Dockerfile and `hello.sh` script, run the following command to build the Docker image:

   ```bash
   docker build -t hello-world .
   ```

   This command builds a Docker image with the tag "hello-world" using the instructions in the Dockerfile.

6. **Run the Docker container**: Once the image is built, you can run a container based on the "hello-world" image by executing the following command:

   ```bash
   docker run hello-world
   ```


## Implementations

This example will be implemented in the following languages:

- Java
- Python
- Kotlin

## Additional reading

- [Amazon ECR](https://docs.aws.amazon.com/AmazonECR/latest/userguide/what-is-ecr.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
