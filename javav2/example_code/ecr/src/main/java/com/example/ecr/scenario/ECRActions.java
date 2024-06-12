// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ecr.scenario;

// snippet-start:[ecr.java2_scenario.main]
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecr.EcrAsyncClient;
import software.amazon.awssdk.services.ecr.model.AuthorizationData;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryResponse;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryResponse;
import software.amazon.awssdk.services.ecr.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ecr.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesRequest;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesResponse;
import software.amazon.awssdk.services.ecr.model.EcrException;
import software.amazon.awssdk.services.ecr.model.GetAuthorizationTokenResponse;
import software.amazon.awssdk.services.ecr.model.GetRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.GetRepositoryPolicyResponse;
import software.amazon.awssdk.services.ecr.model.ImageIdentifier;
import software.amazon.awssdk.services.ecr.model.InvalidParameterException;
import software.amazon.awssdk.services.ecr.model.Repository;
import software.amazon.awssdk.services.ecr.model.RepositoryPolicyNotFoundException;
import software.amazon.awssdk.services.ecr.model.SetRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.SetRepositoryPolicyResponse;
import software.amazon.awssdk.services.ecr.model.StartLifecyclePolicyPreviewRequest;
import software.amazon.awssdk.services.ecr.model.StartLifecyclePolicyPreviewResponse;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/*

 In this code example, notice the use of Asynchronous Error Handling. This
 is designed for asynchronous error handling in CompletableFuture chains.
 When an exception occurs in any stage of the CompletableFuture pipeline,
 the exceptionally method allows you to handle the exception without blocking the thread.
 */
public class ECRActions {
    private static EcrAsyncClient ecrClient;

    /**
     * Retrieves an asynchronous Amazon Elastic Container Registry (ECR) client.
     *
     * <p>The returned client is configured with the following settings:
     * <ul>
     *   <li>Maximum concurrency: 50 (can be adjusted as needed)</li>
     *   <li>Connection timeout: 60 seconds</li>
     *   <li>Read timeout: 60 seconds</li>
     *   <li>Write timeout: 60 seconds</li>
     *   <li>API call timeout: 2 minutes</li>
     *   <li>API call attempt timeout: 90 seconds</li>
     *   <li>Region: US_EAST_1</li>
     *   <li>Credentials provider: {@link DefaultCredentialsProvider#create()}</li>
     * </ul>
     *
     * @return the configured ECR asynchronous client
     */
    private static EcrAsyncClient getAsyncClient() {

        /*
         The `NettyNioAsyncHttpClient` class is part of the AWS SDK for Java, version 2,
         and it is designed to provide a high-performance, asynchronous HTTP client for interacting with AWS services.
         It uses the Netty framework to handle the underlying network communication and the Java NIO API to
         provide a non-blocking, event-driven approach to HTTP requests and responses.
         */
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
            .maxConcurrency(50)  // Adjust as needed.
            .connectionTimeout(Duration.ofSeconds(60))  // Set the connection timeout.
            .readTimeout(Duration.ofSeconds(60))  // Set the read timeout.
            .writeTimeout(Duration.ofSeconds(60))  // Set the write timeout.
            .build();

        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMinutes(2))  // Set the overall API call timeout.
            .apiCallAttemptTimeout(Duration.ofSeconds(90))  // Set the individual call attempt timeout.
            .build();

        if (ecrClient == null) {
            ecrClient = EcrAsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return ecrClient;
    }

    // snippet-start:[ecr.java2.delete.repo.main]
    /**
     * Deletes an ECR (Elastic Container Registry) repository.
     *
     * @param repoName the name of the repository to be deleted
     * @throws IllegalArgumentException if the repository name is null or empty
     * @throws EcrException if there is an error deleting the repository
     * @throws RuntimeException if an unexpected error occurs during the deletion process
     */
    public void deleteECRRepository(String repoName) {
        if (repoName == null || repoName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        DeleteRepositoryRequest repositoryRequest = DeleteRepositoryRequest.builder()
            .force(true)
            .repositoryName(repoName)
            .build();

        // Execute the request asynchronously.
        CompletableFuture<DeleteRepositoryResponse> response = getAsyncClient().deleteRepository(repositoryRequest);

        // Use whenComplete to handle the response or any exceptions
        response.whenComplete((deleteRepositoryResponse, ex) -> {
            if (deleteRepositoryResponse != null) {
                System.out.println("You have successfully deleted the " + repoName + " repository");
            } else {
                Throwable cause = ex.getCause();
                if (cause instanceof EcrException) {
                    System.err.println("Error deleting repository: " + ((EcrException) cause).awsErrorDetails().errorMessage());
                } else {
                    System.err.println("Unexpected error: " + cause.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete
        response.join();
    }
    // snippet-end:[ecr.java2.delete.repo.main]

    // snippet-start:[ecr.java2.verify.image.main]
    /**
     * Verifies the existence of an image in an Amazon Elastic Container Registry (Amazon ECR) repository asynchronously.
     *
     * @param repositoryName The name of the Amazon ECR repository.
     * @param imageTag       The tag of the image to verify.
     * @throws IllegalArgumentException if the repository name or image tag is null or empty.
     * @throws EcrException             if there is an error retrieving the image information from Amazon ECR.
     * @throws CompletionException      if the asynchronous operation completes exceptionally.
     */
    public static void verifyImage(String repositoryName, String imageTag) {
        if (repositoryName == null || repositoryName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        if (imageTag == null || imageTag.isEmpty()) {
            throw new IllegalArgumentException("Image tag cannot be null or empty");
        }

        DescribeImagesRequest request = DescribeImagesRequest.builder()
            .repositoryName(repositoryName)
            .imageIds(ImageIdentifier.builder().imageTag(imageTag).build())
            .build();

        // Retrieve the image details asynchronously.
        CompletableFuture<DescribeImagesResponse> response = getAsyncClient().describeImages(request);

        // Use whenComplete to handle the response or any exceptions.
        response.whenComplete((describeImagesResponse, ex) -> {
            try {
                if (describeImagesResponse != null && !describeImagesResponse.imageDetails().isEmpty()) {
                    System.out.println("Image is present in the repository.");
                } else {
                    System.out.println("Image is not present in the repository.");
                }
            } catch (Exception e) {
                Throwable cause = ex.getCause();
                if (cause instanceof EcrException) {
                    System.err.println("Error retrieving image information: " + cause.getMessage());
                } else if (cause instanceof CompletionException) {
                    System.err.println("Asynchronous operation completed exceptionally: " + cause.getCause().getMessage());
                } else {
                    System.err.println("Unexpected error: " + cause.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete
        response.join();
    }
    // snippet-end:[ecr.java2.verify.image.main]

    // snippet-start:[ecr.java2.set.policy.main]
    public void setLifeCyclePolicy(String repoName) {
        String polText = """
        {
        "rules": [
            {
                "rulePriority": 1,
                "description": "Expire images older than 14 days",
                "selection": {
                    "tagStatus": "any",
                    "countType": "sinceImagePushed",
                    "countUnit": "days",
                    "countNumber": 14
                },
                "action": {
                    "type": "expire"
                }
            }
       ]
       }
       """;

        StartLifecyclePolicyPreviewRequest lifecyclePolicyPreviewRequest = StartLifecyclePolicyPreviewRequest.builder()
            .lifecyclePolicyText(polText)
            .repositoryName(repoName)
            .build();

        // Execute the request asynchronously.
        CompletableFuture<StartLifecyclePolicyPreviewResponse> response = getAsyncClient().startLifecyclePolicyPreview(lifecyclePolicyPreviewRequest);

        // Use whenComplete to handle the response or any exceptions.
        response.whenComplete((lifecyclePolicyPreviewResponse, ex) -> {
            if (lifecyclePolicyPreviewResponse != null) {
                System.out.println("Lifecycle policy preview started successfully.");
                // Add any additional actions you want to perform upon completion.
            } else {
                if (ex.getCause() instanceof EcrException) {
                    EcrException e = (EcrException) ex.getCause();
                    System..mainerr.println("Error setting lifecycle policy for repository: " + repoName + " - " + e.awsErrorDetails().errorMessage());
                } else {
                    System.err.println("Unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete.
        response.join();
    }

    // snippet-end:[ecr.java2.set.policy.main]

    // snippet-start:[ecr.java2.describe.policy]
    /**
     * Retrieves the repository URI for the specified repository name.
     *
     * @param repoName the name of the repository to retrieve the URI for.
     * @return the repository URI for the specified repository name.
     * @throws EcrException if there is an error retrieving the repository information.
     * @throws InterruptedException if the thread is interrupted while waiting for the asynchronous operation to complete.
     * @throws CompletionException if the asynchronous operation completes exceptionally.
     */
    public String getRepositoryURI(String repoName) {
        if (repoName == null || repoName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        DescribeRepositoriesRequest request = DescribeRepositoriesRequest.builder()
            .repositoryNames(repoName)
            .build();

        // Retrieve the repository URI asynchronously.
        CompletableFuture<DescribeRepositoriesResponse> response = getAsyncClient().describeRepositories(request);

        // Use whenComplete to handle the response or any exceptions.
        try {
            DescribeRepositoriesResponse describeRepositoriesResponse = response.join();
            if (!describeRepositoriesResponse.repositories().isEmpty()) {
                return describeRepositoriesResponse.repositories().get(0).repositoryUri();
            } else {
                // Handle the case where no repositories are returned.
                System.out.println("No repositories found for the given name.");
            }
        } catch (Throwable ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof InterruptedException) {
                throw new RuntimeException("Thread interrupted while waiting for asynchronous operation: " + cause.getMessage(), cause);
            } else if (cause instanceof CompletionException) {
                throw new RuntimeException("Asynchronous operation completed exceptionally: " + cause.getCause().getMessage(), cause);
            } else {
                throw new RuntimeException("Unexpected error: " + cause.getMessage(), cause);
            }
        }
        return "";
    }
    // snippet-end:[ecr.java2.describe.policy.main]

    // snippet-start:[ecr.java2.get.token.main]
    /**
     * Retrieves the authorization token for Amazon Elastic Container Registry (ECR).
     * This method makes an asynchronous call to the ECR client to retrieve the authorization token.
     * If the operation is successful, the method prints the token to the console.
     * If an exception occurs, the method handles the exception and prints the error message.
     *
     * @throws EcrException if there is an error retrieving the authorization token from ECR
     * @throws RuntimeException if there is an unexpected error during the operation
     */
    public void getAuthToken() {
        // Retrieve the authorization token for ECR asynchronously.
        CompletableFuture<GetAuthorizationTokenResponse> response = getAsyncClient().getAuthorizationToken();

        // Use whenComplete to handle the response or any exceptions
        response.whenComplete((authorizationTokenResponse, ex) -> {
            if (authorizationTokenResponse != null) {
                AuthorizationData authorizationData = authorizationTokenResponse.authorizationData().get(0);
                String token = authorizationData.authorizationToken();
                System.out.println("The token is:");
                System.out.println(token);
            } else {
                if (ex.getCause() instanceof EcrException) {
                    EcrException e = (EcrException) ex.getCause();
                    System.err.println("Error retrieving authorization token: " + e.awsErrorDetails().errorMessage());
                    e.printStackTrace();
                } else {
                    System.err.println("Unexpected error occurred: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Wait for the CompletableFuture to complete
        response.join();
    }
    // snippet-end:[ecr.java2.get.token.main]

    // snippet-start:[ecr.java2.get.repo.policy.main]
    /**
     * Gets the repository policy for the specified repository.
     *
     * @param repoName the name of the repository.
     * @throws EcrException if an AWS error occurs while getting the repository policy.
     */
    public String getRepoPolicy(String repoName) {
        if (repoName == null || repoName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        // Create the request
        GetRepositoryPolicyRequest getRepositoryPolicyRequest = GetRepositoryPolicyRequest.builder()
            .repositoryName(repoName)
            .build();

        // Execute the request asynchronously and store the CompletableFuture
        CompletableFuture<GetRepositoryPolicyResponse> response = getAsyncClient().getRepositoryPolicy(getRepositoryPolicyRequest);

        // Use whenComplete to handle the response or any exceptions
        response.whenComplete((resp, ex) -> {
            if (resp != null) {
                System.out.println("Repository policy retrieved successfully.");
            } else {
                if (ex.getCause() instanceof EcrException) {
                    EcrException e = (EcrException) ex.getCause();
                    String errorMessage = "Error getting repository policy for repository: " + repoName + " - " + e.awsErrorDetails().errorMessage();
                    System.err.println(errorMessage);
                    e.printStackTrace();
                } else {
                    String errorMessage = "Unexpected error occurred: " + ex.getMessage();
                    System.err.println(errorMessage);
                    ex.printStackTrace();
                }
            }
        });

        // Wait for the CompletableFuture to complete and return the result.
        GetRepositoryPolicyResponse result = response.join();
        return result != null ? result.policyText() : null;
    }
    // snippet-end:[ecr.java2.get.repo.policy.main]

    // snippet-start:[ecr.java2.set.repo.policy.main]
    /**
     * Sets the repository policy for the specified ECR repository.
     *
     * @param repoName the name of the ECR repository
     * @param iamRole the IAM role to be granted access to the repository
     * @throws InvalidParameterException if the specified IAM role is invalid
     * @throws RepositoryPolicyNotFoundException if the repository policy does not exist
     * @throws EcrException if there is an unexpected error setting the repository policy
     */
    public void setRepoPolicy(String repoName, String iamRole) {
        String policyDocumentTemplate = """
             {
              "Version" : "2012-10-17",
              "Statement" : [ {
                "Sid" : "new statement",
                "Effect" : "Allow",
                "Principal" : {
                  "AWS" : "%s"
                },
                "Action" : "ecr:BatchGetImage"
              } ]
            }
             """;

        String policyDocument = String.format(policyDocumentTemplate, iamRole);
        SetRepositoryPolicyRequest setRepositoryPolicyRequest = SetRepositoryPolicyRequest.builder()
            .repositoryName(repoName)
            .policyText(policyDocument)
            .build();

        CompletableFuture<SetRepositoryPolicyResponse> response = getAsyncClient().setRepositoryPolicy(setRepositoryPolicyRequest);
        response.whenComplete((resp, ex) -> {
            if (resp != null) {
                System.out.println("Repository policy set successfully.");
            } else {
                if (ex.getCause() instanceof InvalidParameterException) {
                    InvalidParameterException e = (InvalidParameterException) ex.getCause();
                    System.out.format("Error setting repository policy for repository: %s. The IAM role '%s' is invalid. %s", repoName, iamRole, e.getMessage());
                } else if (ex.getCause() instanceof RepositoryPolicyNotFoundException) {
                    RepositoryPolicyNotFoundException e = (RepositoryPolicyNotFoundException) ex.getCause();
                    System.out.format("Error setting repository policy for repository: %s. The repository policy does not exist. %s", repoName, e.getMessage());
                } else if (ex.getCause() instanceof EcrException) {
                    EcrException e = (EcrException) ex.getCause();
                    System.out.format("Unexpected error setting repository policy for repository: %s. %s", repoName, e.getMessage());
                } else {
                    System.err.println("Unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete
        response.join();
    }
    // snippet-end:[ecr.java2.set.repo.policy.main]

    // snippet-start:[ecr.java2.create.repo.main]
    /**
     * Creates an Amazon Elastic Container Registry (Amazon ECR) repository.
     *
     * @param repoName the name of the repository to create
     * @return the Amazon Resource Name (ARN) of the created repository, or an empty string if the operation failed
     * @throws IllegalArgumentException if the repository name is null or empty
     * @throws RuntimeException         if an error occurs while creating the repository
     */
    public String createECRRepository(String repoName) {
        if (repoName == null || repoName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        CreateRepositoryRequest request = CreateRepositoryRequest.builder()
            .repositoryName(repoName)
            .build();

        // Execute the request asynchronously.
        CompletableFuture<CreateRepositoryResponse> response = getAsyncClient().createRepository(request);

        // Use whenComplete to handle the response.
        response.whenComplete((createRepositoryResponse, ex) -> {
            if (createRepositoryResponse != null) {
                System.out.println("Repository created successfully. ARN: " + createRepositoryResponse.repository().repositoryArn());
            } else {
                if (ex.getCause() instanceof EcrException) {
                    EcrException e = (EcrException) ex.getCause();
                    System.err.println("Error creating ECR repository: " + e.awsErrorDetails().errorMessage());
                } else {
                    System.err.println("Unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete and return the result.
        CreateRepositoryResponse result = response.join();
        if (result != null) {
            return result.repository().repositoryArn();
        } else {
            throw new RuntimeException("Unexpected response type");
        }
    }
    // snippet-end:[ecr.java2.create.repo.main]

    // snippet-start:[ecr.java2.push.image.main]
    /**
     * Pushes a Docker image to an Amazon Elastic Container Registry (ECR) repository.
     *
     * @param repoName the name of the ECR repository to push the image to
     * @param imageName the name of the Docker image to be pushed
     * @param imageTag the tag to apply to the Docker image
     */
    public void pushDockerImage(String repoName, String imageName, String imageTag) {
        // Make sure Docker Desktop is running.
        String dockerHost = "tcp://localhost:2375"; // Use the Docker Desktop default port
        DockerCmdExecFactory dockerCmdExecFactory = new NettyDockerCmdExecFactory().withReadTimeout(20000).withConnectTimeout(20000);
        DockerClient dockerClient = DockerClientBuilder.getInstance(dockerHost).withDockerCmdExecFactory(dockerCmdExecFactory).build();
        System.out.println(dockerClient.infoCmd().exec());

        CompletableFuture<AuthConfig> authResponseFuture = getAsyncClient().getAuthorizationToken()
            .thenApply(response -> {
                String token = response.authorizationData().get(0).authorizationToken();
                String decodedToken = new String(Base64.getDecoder().decode(token));
                String password = decodedToken.substring(4);

                DescribeRepositoriesResponse descrRepoResponse = getAsyncClient().describeRepositories(b -> b.repositoryNames(repoName)).join();
                Repository repoData = descrRepoResponse.repositories().stream().filter(r -> r.repositoryName().equals(repoName)).findFirst().orElse(null);
                String registryURL = repoData.repositoryUri().split("/")[0];

                AuthConfig authConfig = new AuthConfig()
                    .withUsername("AWS")
                    .withPassword(password)
                    .withRegistryAddress(registryURL);
                return authConfig;
            })
            .thenCompose(authConfig -> {
                DescribeRepositoriesResponse descrRepoResponse = getAsyncClient().describeRepositories(b -> b.repositoryNames(repoName)).join();
                Repository repoData = descrRepoResponse.repositories().stream().filter(r -> r.repositoryName().equals(repoName)).findFirst().orElse(null);

                //dockerClient.tagImageCmd("hello-world:latest", repoData.repositoryUri() + ":latest", imageName).exec();
                dockerClient.tagImageCmd(imageName+":latest", repoData.repositoryUri() + ":latest", imageName).exec();
                try {
                   // dockerClient.pushImageCmd(repoData.repositoryUri()).withTag(imageName).withAuthConfig(authConfig).start().awaitCompletion();
                    dockerClient.pushImageCmd(repoData.repositoryUri()).withTag("hello-world").withAuthConfig(authConfig).start().awaitCompletion();
                    System.out.println("The "+imageName+" was pushed to ECR");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return CompletableFuture.completedFuture(authConfig);
            });

        authResponseFuture.join();
    }
    // snippet-end:[ecr.java2.push.image.main]
}
// snippet-end:[ecr.java2_scenario.main]