# Amazon Elastic Container Registry Service Scenario Specification

## Overview
This SDK getting started scenario demonstrates how to interact with Amazon Elastic Container Registry Service (Amazon ECR) using the AWS SDK. It demonstrates various tasks such as creating a repository, setting an ECR repository policy, obtaining an authorization token, pushing a docker image to the repository, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with Amazon ECR and the AWS SDK.

## Resources and User Input
The required resources for this SDK scenario are an IAM role and a local Docker image. The IAM role must have permission to interact with the Amazon ECR service. To create an IAM role, see [Creating IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create.html). 

To see the instructions to create a local docker image, see [README.md](README.md).

This scenario uses the Docker client API for a specific programming language. For more information, see [Develop with Docker Engine SDKs](https://docs.docker.com/engine/api/sdk/)

## Hello Amazon ECR
This program is intended for users not familiar with the Amazon ECR SDK to easily get up and running. The logic is to show use of `ecrClient.listImagesPaginator()`.

### Program execution
The following shows the output of the program in the console. 

``` java 
The docker image tag is latest 

```

## Scenario Program Flow
The Amazon ECR SDK getting started scenario executes the following steps:

1. **Parse command-line arguments**: The program checks if the correct number of arguments (2) are provided, which are the IAM role and the account number.

2. **Introduce Amazon ECR**: The program provides a brief introduction to Amazon ECR and the AWS SDK's `EcrClient` interface.

3. **Checks the local machine for a docker image**: The program checks the local mahcine for a docker image named hello-world. If it's not located, the program ends. 

3. **Create an ECR repository**: The program creates the repository using the `createECRRepository` method.

4. **Set an ECR repository policy**: The program sets an ECR repository policy using the `setRepoPolicy` method, which grants the specified IAM role the necessary permissions to access the repository.

5. **Display the ECR repository policy**: The program retrieves and displays the repository policy using the `getRepoPolicy` method.

6. **Retrieve an ECR authorization token**: The program retrieves an ECR authorization token using the `getAuthToken` method, which is required for subsequent operations.

7. **Get the ECR repository URI**: The program retrieves the URI of the ECR repository using the `getRepositoryURI` method.

8. **Set an ECR lifecycle policy**: The program sets an ECR lifecycle policy using the `setLifeCyclePolicy` method, which automatically removes old or unused Docker images from the repository.

9. **Push a Docker image to the ECR repository**: The program pushes a local Docker image to the ECR repository using the Docker Client (its not a recommended way to upload using ECRClient).

10. **Verify the image in the ECR repository**: The program verifies that the Docker image was successfully pushed to the ECR repository using the `verifyImage` method.

11. Provide optional steps on how a user can run the image in the ECR repo. 

12. **Delete the ECR repository**: The program prompts the user to delete the ECR repository and its contents using the `deleteECRRepository` method.


### Program execution
The following shows the output of the Amazon ECR program in the console. 

``` java
  The Amazon Elastic Container Registry (ECR) is a fully-managed Docker container registry
  service provided by AWS. It allows developers and organizations to securely
  store, manage, and deploy Docker container images.
  ECR provides a simple and scalable way to manage container images throughout their lifecycle,
  from building and testing to production deployment. 

  The `EcrAsyncClient` interface in the AWS SDK for Java 2.x provides a set of methods to
  programmatically interact with the Amazon ECR service. This allows developers to
  automate the storage, retrieval, and management of container images as part of their application
  deployment pipelines. With ECR, teams can focus on building and deploying their
  applications without having to worry about the underlying infrastructure required to
  host and manage a container registry.

 This scenario walks you through how to perform key operations for this service.
 Let's get started...


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
1. Create an ECR repository.

The first task is to ensure we have a local Docker image named hello-world.
If this image exists, then an Amazon ECR repository is created.

An ECR repository is a private Docker container repository provided
by Amazon Web Services (AWS). It is a managed service that makes it easy
to store, manage, and deploy Docker container images. 

hello-world:latest
The local image named hello-world exists.
ECR repository already exists, moving on...
The ARN of the ECR repository is arn:aws:ecr:us-east-1:1234567890:repository/hello-world

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
2. Set an ECR repository policy.

Setting an ECR repository policy using the `setRepositoryPolicy` function is crucial for maintaining
the security and integrity of your container images. The repository policy allows you to
define specific rules and restrictions for accessing and managing the images stored within your ECR
repository.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Repository policy set successfully.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
3. Display ECR repository policy.

Now we will retrieve the ECR policy to ensure it was successfully set.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Repository policy retrieved successfully.
Policy Text:
{
  "Version" : "2012-10-17",
  "Statement" : [ {
    "Sid" : "new statement",
    "Effect" : "Allow",
    "Principal" : {
      "AWS" : "arn:aws:iam::1234567890:role/Admin"
    },
    "Action" : "ecr:BatchGetImage"
  } ]
}

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
4. Retrieve an ECR authorization token.

You need an authorization token to securely access and interact with the Amazon ECR registry.
The `getAuthorizationToken` method of the `EcrAsyncClient` is responsible for securely accessing
and interacting with an Amazon ECR repository. This operation is responsible for obtaining a
valid authorization token, which is required to authenticate your requests to the ECR service.

Without a valid authorization token, you would not be able to perform any operations on the
ECR repository, such as pushing, pulling, or managing your Docker images.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The token was successfully retrieved.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
 5. Get the ECR Repository URI.

 The URI  of an Amazon ECR repository is important. When you want to deploy a container image to
 a container orchestration platform like Amazon Elastic Kubernetes Service (EKS)
 or Amazon Elastic Container Service (ECS), you need to specify the full image URI,
 which includes the ECR repository URI. This allows the container runtime to pull the
 correct container image from the ECR repository.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The repository URI is 1234567890.dkr.ecr.us-east-1.amazonaws.com/hello-world

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
6. Set an ECR Lifecycle Policy.

An ECR Lifecycle Policy is used to manage the lifecycle of Docker images stored in your ECR repositories.
These policies allow you to automatically remove old or unused Docker images from your repositories,
freeing up storage space and reducing costs.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Lifecycle policy preview started successfully.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
7. Push a docker image to the Amazon ECR Repository.

The `pushImageCmd()` method pushes a local Docker image to an Amazon ECR repository.
It sets up the Docker client by connecting to the local Docker host using the default port.
It then retrieves the authorization token for the ECR repository by making a call to the AWS SDK.

The method uses the authorization token to create an `AuthConfig` object, which is used to authenticate
the Docker client when pushing the image. Finally, the method tags the Docker image with the specified
repository name and image tag, and then pushes the image to the ECR repository using the Docker client.
If the push operation is successful, the method prints a message indicating that the image was pushed to ECR.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Pushing hello-world to hello-world will take a few seconds
The hello-world was pushed to ECR

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
8. Verify if the image is in the ECR Repository.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Image is present in the repository.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
9. As an optional step, you can interact with the image in Amazon ECR by using the CLI.
Would you like to view instructions on how to use the CLI to run the image? (y/n)
y
1. Authenticate with ECR - Before you can pull the image from Amazon ECR, you need to authenticate with the registry. You can do this using the AWS CLI:

    aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 1234567890.dkr.ecr.us-east-1.amazonaws.com

2. Describe the image using this command:

   aws ecr describe-images --repository-name hello-world --image-ids imageTag=hello-world

3. Run the Docker container and view the output using this command:

   docker run --rm 1234567890.dkr.ecr.us-east-1.amazonaws.com/hello-world:hello-world


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
10. Delete the ECR Repository.
If the repository isn't empty, you must either delete the contents of the repository
or use the force option (used in this scenario) to delete the repository and have Amazon ECR delete all of its contents
on your behalf.

Would you like to delete the Amazon ECR Repository? (y/n)
y
You selected to delete the AWS ECR resources.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

You have successfully deleted the hello-world repository
--------------------------------------------------------------------------------
This concludes the Amazon ECR SDK Getting Started scenario
--------------------------------------------------------------------------------
```

## SOS Tags

The following table describes the metadata used in this SDK Getting Started Scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `deleteRepository`           | ecr_metadata.yaml            | ecr_DeleteRepository                    |
| `describeImages              | ecr_metadata.yaml            | ecr_DescribeImages                      |
| `startLifecyclePolicyPreview`| ecr_metadata.yaml            | ecr_LifecyclePolicyPreview              |
| `describeRepositories`       | ecr_metadata.yaml            | ecr_DescribeRepositories                |
| `getAuthorizationToken`      | ecr_metadata.yaml            | ecr_GetAuthorizationToken               |
| `getRepositoryPolicy`        | ecr_metadata.yaml            | ecr_GetRepositoryPolicy                 |
| `setRepositoryPolicy`        | ecr_metadata.yaml            | ecr_SetRepositoryPolicy                 |
| `createRepository `          | ecr_metadata.yaml            | ecr_CreateRepository                    |
| `PutImage `                  | ecr_metadata.yaml            | ecr_PutImage                            |
| `listImagesPaginator `       | ecr_metadata.yaml            | ecr_Hello                               |
| `scenario                    | ecr_metadata.yaml            | ecr_Scenario                            |

