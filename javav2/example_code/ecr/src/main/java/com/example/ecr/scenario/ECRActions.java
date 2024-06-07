// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ecr.scenario;

// snippet-start:[ecr.java2_scenario.main]
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecr.EcrAsyncClient;
import software.amazon.awssdk.services.ecr.model.AuthorizationData;
import software.amazon.awssdk.services.ecr.model.BatchCheckLayerAvailabilityRequest;
import software.amazon.awssdk.services.ecr.model.CompleteLayerUploadRequest;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.CreateRepositoryResponse;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryRequest;
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryResponse;
import software.amazon.awssdk.services.ecr.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ecr.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesRequest;
import software.amazon.awssdk.services.ecr.model.DescribeRepositoriesResponse;
import software.amazon.awssdk.services.ecr.model.EcrException;
import software.amazon.awssdk.services.ecr.model.GetAuthorizationTokenRequest;
import software.amazon.awssdk.services.ecr.model.GetAuthorizationTokenResponse;
import software.amazon.awssdk.services.ecr.model.GetRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.GetRepositoryPolicyResponse;
import software.amazon.awssdk.services.ecr.model.ImageIdentifier;
import software.amazon.awssdk.services.ecr.model.InitiateLayerUploadRequest;
import software.amazon.awssdk.services.ecr.model.InvalidParameterException;
import software.amazon.awssdk.services.ecr.model.PutImageRequest;
import software.amazon.awssdk.services.ecr.model.RepositoryPolicyNotFoundException;
import software.amazon.awssdk.services.ecr.model.SetRepositoryPolicyRequest;
import software.amazon.awssdk.services.ecr.model.SetRepositoryPolicyResponse;
import software.amazon.awssdk.services.ecr.model.StartLifecyclePolicyPreviewRequest;
import software.amazon.awssdk.services.ecr.model.StartLifecyclePolicyPreviewResponse;
import software.amazon.awssdk.services.ecr.model.UploadLayerPartRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/*

 In this code example, notice the use of Asynchronous Error Handling. This
 is designed for asynchronous error handling in CompletableFuture chains.
 When an exception occurs in any stage of the CompletableFuture pipeline,
 the exceptionally method allows you to handle the exception without blocking the thread.
 */
public class ECRActions {
    private static EcrAsyncClient ecrClient;

    private static EcrAsyncClient getAsyncClient() {
        if (ecrClient == null) {
            ecrClient = EcrAsyncClient.builder()
                .region(Region.US_EAST_1)
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
    public void verifyImage(String repositoryName, String imageTag) {
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

        // Use whenComplete to handle the response or any exceptions
        response.whenComplete((lifecyclePolicyPreviewResponse, ex) -> {
            if (lifecyclePolicyPreviewResponse != null) {
                System.out.println("Lifecycle policy preview started successfully.");
                // Add any additional actions you want to perform upon completion
            } else {
                if (ex.getCause() instanceof EcrException) {
                    EcrException e = (EcrException) ex.getCause();
                    System.err.println("Error setting lifecycle policy for repository: " + repoName + " - " + e.awsErrorDetails().errorMessage());
                } else {
                    System.err.println("Unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete
        response.join();
    }

    // snippet-end:[ecr.java2.set.policy.main]

    // snippet-start:[ecr.java2.describe.policy.main]
    /**
     * Retrieves the repository URI for the specified repository name.
     *
     * @param repoName the name of the repository to retrieve the URI for.
     * @throws EcrException if there is an error retrieving the repository information.
     * @throws InterruptedException if the thread is interrupted while waiting for the asynchronous operation to complete.
     * @throws CompletionException if the asynchronous operation completes exceptionally.
     */
    public void getRepositoryURI(String repoName) {
        if (repoName == null || repoName.isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        DescribeRepositoriesRequest request = DescribeRepositoriesRequest.builder()
            .repositoryNames(repoName)
            .build();

        // Retrieve the repository URI asynchronously.
        CompletableFuture<DescribeRepositoriesResponse> response = getAsyncClient().describeRepositories(request);

        // Use whenComplete to handle the response or any exceptions.
        response.whenComplete((describeRepositoriesResponse, ex) -> {
            try {
                if (describeRepositoriesResponse != null && !describeRepositoriesResponse.repositories().isEmpty()) {
                    System.out.println("The repository URI is " + describeRepositoriesResponse.repositories().get(0).repositoryUri());
                } else {
                    // Handle the case where no repositories are returned.
                    System.err.println("No repositories found for the given name.");
                }
            } catch (EcrException e) {
                Throwable cause = ex.getCause();
                if (cause instanceof InterruptedException) {
                    System.err.println("Thread interrupted while waiting for asynchronous operation: " + cause.getMessage());
                } else if (cause instanceof CompletionException) {
                    System.err.println("Asynchronous operation completed exceptionally: " + cause.getCause().getMessage());
                } else {
                    System.err.println("Unexpected error: " + cause.getMessage());
                }
            }
        });

        // Wait for the CompletableFuture to complete.
        response.join();
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

        // Wait for the CompletableFuture to complete and return the result
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
     * @param repositoryName the name of the ECR repository to push the image to
     * @param imageName the name of the Docker image to be pushed
     * @throws ExecutionException if the asynchronous operation encounters an exception
     * @throws InterruptedException if the current thread is interrupted while waiting for the asynchronous operation to complete
     */
    public void pushDockerImage(String repositoryName, String imageName) throws ExecutionException, InterruptedException {
        // Retrieve the authorization token for ECR.
        GetAuthorizationTokenRequest authorizationTokenRequest = GetAuthorizationTokenRequest.builder().build();
        CompletableFuture<GetAuthorizationTokenResponse> authorizationTokenResponseFuture = getAsyncClient().getAuthorizationToken(authorizationTokenRequest);
        authorizationTokenResponseFuture.thenCompose(authorizationTokenResponse -> {
            AuthorizationData authorizationData = authorizationTokenResponse.authorizationData().get(0);
            String dockerLoginCommand = authorizationData.authorizationToken();
            System.out.println("Docker login command: " + new String(Base64.getDecoder().decode(dockerLoginCommand)));
            System.out.println("Pushing the image to the "+repositoryName +" repository may take a few secs...");

            // Get the Docker image bytes.
            byte[] dockerFileBytes;
            try {
                dockerFileBytes = getDockerImageBytes(imageName);
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }

            // Calculate the digest of the Docker image.
            List<byte[]> imageBytesList = List.of(dockerFileBytes);

            // Store layer digests.
            JsonArray layers = new JsonArray();
            CompletableFuture<Void> uploadFuture = CompletableFuture.completedFuture(null);

            for (byte[] imageBytes : imageBytesList) {
                String layerDigest = sha256Hex(imageBytes);

                // Check if the layer exists.
                BatchCheckLayerAvailabilityRequest checkLayerRequest = BatchCheckLayerAvailabilityRequest.builder()
                    .repositoryName(repositoryName)
                    .layerDigests(List.of("sha256:" + layerDigest))
                    .build();

                uploadFuture = uploadFuture.thenCompose(v ->
                    getAsyncClient().batchCheckLayerAvailability(checkLayerRequest)
                        .thenCompose(checkLayerResponse -> {
                            if (checkLayerResponse.failures().isEmpty()) {
                                InitiateLayerUploadRequest initiateLayerUploadRequest = InitiateLayerUploadRequest.builder()
                                    .repositoryName(repositoryName)
                                    .build();

                                return getAsyncClient().initiateLayerUpload(initiateLayerUploadRequest)
                                    .thenCompose(initiateLayerUploadResponse -> {
                                        String uploadId = initiateLayerUploadResponse.uploadId();

                                        UploadLayerPartRequest uploadLayerPartRequest = UploadLayerPartRequest.builder()
                                            .repositoryName(repositoryName)
                                            .uploadId(uploadId)
                                            .partFirstByte(0L)
                                            .partLastByte((long) (imageBytes.length - 1))
                                            .layerPartBlob(SdkBytes.fromByteBuffer(ByteBuffer.wrap(imageBytes)))
                                            .build();

                                        return getAsyncClient().uploadLayerPart(uploadLayerPartRequest)
                                            .thenCompose(uploadLayerPartResponse -> {
                                                CompleteLayerUploadRequest completeLayerUploadRequest = CompleteLayerUploadRequest.builder()
                                                    .repositoryName(repositoryName)
                                                    .uploadId(uploadId)
                                                    .layerDigests(List.of("sha256:" + layerDigest))
                                                    .build();
                                                return getAsyncClient().completeLayerUpload(completeLayerUploadRequest);
                                            }).thenApply(completeLayerUploadResponse -> {
                                                // Add layer to manifest.
                                                JsonObject layer = new JsonObject();
                                                layer.addProperty("mediaType", "application/vnd.docker.image.rootfs.diff.tar.gzip");
                                                layer.addProperty("size", imageBytes.length);
                                                layer.addProperty("digest", "sha256:" + layerDigest);
                                                layers.add(layer);
                                                return null;
                                            });
                                    });
                            } else {
                                return CompletableFuture.completedFuture(null);
                            }
                        }));
            }

            return uploadFuture.thenCompose(v -> {
                // Create a JSON document that represents the image manifest.
                JsonObject manifestJson = new JsonObject();
                manifestJson.addProperty("schemaVersion", 2);
                manifestJson.addProperty("mediaType", "application/vnd.docker.distribution.manifest.v2+json");

                // Add config.
                JsonObject config = new JsonObject();
                config.addProperty("mediaType", "application/vnd.docker.container.image.v1+json");
                config.addProperty("size", dockerFileBytes.length);
                config.addProperty("digest", "sha256:" + sha256Hex(dockerFileBytes));
                manifestJson.add("config", config);

                // Add layers to manifest.
                manifestJson.add("layers", layers);
                String imageManifest = manifestJson.toString();
                PutImageRequest putImageRequest = PutImageRequest.builder()
                    .repositoryName(repositoryName)
                    .imageTag("latest")
                    .imageManifest(imageManifest)
                    .build();

                // Put the image into the ECR repository.
                return getAsyncClient().putImage(putImageRequest).thenApply(putImageResponse -> {
                    System.out.println("Image pushed: " + putImageResponse.image().imageId().imageTag());
                    return null;
                });
            });
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).join(); // Blocking to make sure the whole process completes before the method exits.
    }
    // snippet-end:[ecr.java2.push.image.main]

    // Method to calculate SHA-256 digest (use any suitable method/library).
    private String sha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get Docker image bytes.
    private byte[] getDockerImageBytes(String imageName) throws IOException {
        // Execute docker save command to save the image to a tar archive.
        Process process = Runtime.getRuntime().exec("docker save " + imageName);

        // Read the output of the process (image bytes).
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = process.getInputStream().read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        // Wait for the process to complete.
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Return the image bytes.
        return output.toByteArray();
    }
}
// snippet-end:[ecr.java2_scenario.main]