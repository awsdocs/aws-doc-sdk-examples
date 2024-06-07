# Amazon Elastic Container Registry Service Scenario Specification

## Overview
This SDK getting started scenario demonstrates how to interact with Amazon Elastic Container Registry Service (Amazon ECR) using the AWS SDK. It demonstrates various tasks such as creating a repository, setting an ECR repository policy, obtaining an authorization token, pushing a docker image to the repository, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with Amazon ECR and the AWS SDK.

## Resources and User Input
The only required resource for this SDK getting started scenario is an IAM role and a local docker image. The IAM role must have permission to interact with the Amazon ECR service.  

## Hello Amazon ECR
This program is intended for users not familiar with the Amazon ECR SDK to easily get up and running. The logic is to show use of `ecrClient.listImagesPaginator()`.

### Program execution
The following shows the output of the program in the console. 

``` java 
The docker image tag is latest 

```

## Scenario Program Flow
The Amazon ECR SDK getting started scenario executes the following steps:

1. **Parse command-line arguments**: The program checks if the correct number of arguments (2) are provided, which are the IAM role and the local Docker image name.

2. **Introduce Amazon ECR**: The program provides a brief introduction to Amazon ECR and the AWS SDK's `EcrClient` interface.

3. **Create an ECR repository**: The program prompts the user to enter a name for the ECR repository, then creates the repository using the `createECRRepository` method.

4. **Set an ECR repository policy**: The program sets an ECR repository policy using the `setRepoPolicy` method, which grants the specified IAM role the necessary permissions to access the repository.

5. **Display the ECR repository policy**: The program retrieves and displays the repository policy using the `getRepoPolicy` method.

6. **Retrieve an ECR authorization token**: The program retrieves an ECR authorization token using the `getAuthToken` method, which is required for subsequent operations.

7. **Get the ECR repository URI**: The program retrieves the URI of the ECR repository using the `getRepositoryURI` method.

8. **Set an ECR lifecycle policy**: The program sets an ECR lifecycle policy using the `setLifeCyclePolicy` method, which automatically removes old or unused Docker images from the repository.

9. **Push a Docker image to the ECR repository**: The program pushes a local Docker image to the ECR repository using the `pushDockerImage` method, which includes calculating the image's SHA-256 hash, checking layer availability, and completing the image upload.

10. **Verify the image in the ECR repository**: The program verifies that the Docker image was successfully pushed to the ECR repository using the `verifyImage` method.

11. **Delete the ECR repository**: The program prompts the user to delete the ECR repository and its contents using the `deleteECRRepository` method.


### Program execution
The following shows the output of the program in the console. 

``` java
  The Amazon Elastic Container Registry (ECR) is a fully-managed Docker container registry
  service provided by AWS. It allows developers and organizations to securely
  store, manage, and deploy Docker container images.
  ECR provides a simple and scalable way to manage container images throughout their lifecycle,
  from building and testing to production deployment. 

  The `EcrClient` interface in the AWS SDK provides a set of methods to
  programmatically interact with the Amazon ECR service. This allows developers to
  automate the storage, retrieval, and management of container images as part of their application
  deployment pipelines. With ECR, teams can focus on building and deploying their
  applications without having to worry about the underlying infrastructure required to
  host and manage a container registry.

 This Getting Started scenario walks you through how to perform key operations for this service.
 Let's get started...


Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
1. Create an ECR repository.

An ECR repository is a private Docker container registry provided
by Amazon Web Services (AWS). It is a managed service that makes it easy to store, manage, and deploy Docker container images. 

Enter a repository name.
For example, 'ecr1'

ecr200
The ARN of the ECR repository is arn:aws:ecr:us-east-1:814548047983:repository/ecr200

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
2. Set an ECR repository.

Setting an ECR repository policy using the `setRepositoryPolicy` function is crucial for maintaining
the security and integrity of your container images. The repository policy allows you to
define specific rules and restrictions for accessing and managing the images stored within your ECR
repository.


Press <ENTER> to continue:

Continuing with the program...

Repository policy set successfully.

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
3. Display ECR repository policy.

Now we will retrieve the ECR policy to ensure it was successfully set.

Press <ENTER> to continue:

Continuing with the program...

Repository policy text: {
  "Version" : "2012-10-17",
  "Statement" : [ {
    "Sid" : "new statement",
    "Effect" : "Allow",
    "Principal" : {
      "AWS" : "arn:aws:iam::814548047983:role/Admin"
    },
    "Action" : "ecr:BatchGetImage"
  } ]
}

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
4. Retrieve an ECR authorization token.

The `getAuthorizationToken` operation of the `ecrClient` is crucial for securely accessing
and interacting with an Amazon ECR repository. This operation is responsible for obtaining a valid
authorization token, which is required to authenticate your requests to the ECR service.
Without a valid authorization token, you would not be able to perform any operations on the ECR repository,
 such as pushing, pulling, or managing your Docker images.

Press <ENTER> to continue:

Continuing with the program...

The token is:
QVdTOmV5SndZWGxzYjJGa0lqb2lObk5MUjAxeVF5ODJXR3d3YUc1SGFGZG1helJEYURWcFkwMXhVMUYyYlZkdlRWcHNZV0ZqZGtnM09HdG1SekZPTnpObk56ZFRURFYzUzFRdmNXUTVTbmxGWjJ4V1YxbzFiQzlIYzI1dlYyRkZjV1J1WkM5VFJEQXJNVGRKU2swNFEwZGxWVFYwVkd0NGJXSkNWSFVyTlV0TkswUXpWeTl3VDNVMlNFTjVXRFJxTkVvMldEbEZNV0puZEc1NVVYVk9SR05rY3pSdVZGSnllbG94Y2tsak1sRXlURTFyWkhRNE5Yb3JiVmRDWm5NM015OTVlbkpYY0hCM05XTm5hMjVaV1cxMGJXNVhUR1pYV1VOS01URmtlVUYxVTNwNFduWXZWRGRrVXpoNllVNTNXVVUzTDNScmVWZFFkVUZoTVhCelYyeEdWMEpuUzBGNGNFdDRTMmRhVTI5aFVtRnFNRGRTTm5RNVVUaEZSM28xVWxWcFYwZEdVbHAzVlRJMmNqaHFRMWx...

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
 5. Get the ECR Repository URI.

 The URI  of an Amazon ECR repository is important. When you want to deploy a container image to
 a container orchestration platform like Amazon Elastic Kubernetes Service (EKS)
 or Amazon Elastic Container Service (ECS), you need to specify the full image URI,
 which includes the ECR repository URI. This allows the container runtime to pull the
 correct container image from the ECR repository.

Press <ENTER> to continue:

Continuing with the program...

The repository URI is 814548047983.dkr.ecr.us-east-1.amazonaws.com/ecr200

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
 6. Set an ECR Lifecycle Policy.

An ECR Lifecycle Policy is used to manage the lifecycle of Docker images stored in your ECR repositories.
These policies allow you to automatically remove old or unused Docker images from your repositories,
freeing up storage space and reducing costs.

Press <ENTER> to continue:

Continuing with the program...

Press <ENTER> to continue:

Continuing with the program...

7. Push a docker image to the Amazon ECR Repository.

The `pushDockerImage` method demonstrates the process of pushing a Docker image to the ECR repository,
including calculating the SHA-256 hash of the image, checking layer availability,
and completing the image upload.

When pushing a docker image to an ECR repository, you calculate the SHA-256 hash of a given byte array.
This is used when uploading a Docker image to ECR because ECR requires the image digest
(a unique identifier based on the image content) to be provided during the image push operation.

Press <ENTER> to continue:

Continuing with the program...

Docker login command: AWS:eyJwYXlsb2FkIjoickw4SC8vM1poVUw0a3hrU001akpvdGpPK0hOUFU3VzJRcCtDZHUyajBsN2pRUjY5djYvZDllMUdQanhvd3ZSL3lIZTlST2ZaUHBlb2hFbWN6RVdDclVEUFZYV2lFenVSTmZXa2JScnRBVUNrcUNCZnZDNDAvd1lUMkZkZlRqcGI1MzVvNFBSbXlIY0xBZzRUcDh6azFob1RleEY2VU1GVDYzWCs3YnR4dElyMUNZYnRXYTBZZjJ6Vm1ybjEwZkJTK1lLeTBSWWczcjAwRnVGVVBSeWdOaHlrQ3h6Y0d2aDBlTGI3UndFS21PTDFXNVo5SWRIRURtcklKcVJFV1NhbEp4Z2lrbHZ1UFNhWEw3T0xrajNSK1BGbSt0a1lsSGFQemdvTG5SVE5QeWxPUXgvcGVTemhCRUpvcCtpbUd6T3BIK1JWM2dvdUhPTXlxN1cyY2F0R1lyVmIwcWtoWU42WFNjVFVOQ29XeEM0VDJJVkphbkpueUtQMTFFQmdEUnRISEFwekd6MDU0ZHlkRW9ac3RmTElRQ1lXVkhhSkJuSmVnbHZJZ2RwRWp3em5MR1lTcy84b0I1VEIra3BhZk1uemNiQUFEZGl3K3ZkY3UwU2tYSjQ1K01NaTI0T0NKOTk0WmtrRzZXekcxUDRwL01yRHRISm5DWW1UVVBmNmZqckVad1VwRDdqazgwZTBUTTdrcTV3VVpnK1VFWGlSRjRoTnMxY3ptL1lWYkluMk5ZNmlIV3AvWk90eFZpYW1PeWQ1QzdCUysxL2VkUG1BcmtYQ3FkQlBYc0hkQjFYVkV4Mm5SRENJRjFMTHMyZmFBVWtwU09yUnIzMHVjYk9UNGsxZ21sR0FLditEZ1NYc...
Image pushed: latest

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
8. Verify if the image is in the ECR Repository.

Press <ENTER> to continue:

Continuing with the program...

Image is present in the repository.

Press <ENTER> to continue:

Continuing with the program...

--------------------------------------------------------------------------------
9. Delete the ECR Repository.
If the repository isn't empty, you must either delete the contents of the repository
or use the force option (used in this scenario) to delete the repository and have Amazon ECR delete all of its contents
on your behalf.

Would you like to delete the Amazon ECR Repository? (y/n)
y
You selected to delete the AWS ECR resources.

Press <ENTER> to continue:

Continuing with the program...

You have successfully deleted the ecr200 repository
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

