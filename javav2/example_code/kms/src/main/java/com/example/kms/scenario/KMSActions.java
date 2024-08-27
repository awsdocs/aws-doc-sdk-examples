// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kms.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsAsyncClient;
import software.amazon.awssdk.services.kms.model.CreateAliasRequest;
import software.amazon.awssdk.services.kms.model.CreateGrantRequest;
import software.amazon.awssdk.services.kms.model.CreateGrantResponse;
import software.amazon.awssdk.services.kms.model.CreateKeyRequest;
import software.amazon.awssdk.services.kms.model.CustomerMasterKeySpec;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DeleteAliasRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyResponse;
import software.amazon.awssdk.services.kms.model.DisableKeyRequest;
import software.amazon.awssdk.services.kms.model.EnableKeyRequest;
import software.amazon.awssdk.services.kms.model.EnableKeyRotationRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.GetKeyPolicyRequest;
import software.amazon.awssdk.services.kms.model.GrantOperation;
import software.amazon.awssdk.services.kms.model.KeySpec;
import software.amazon.awssdk.services.kms.model.KeyState;
import software.amazon.awssdk.services.kms.model.KeyUsageType;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListAliasesRequest;
import software.amazon.awssdk.services.kms.model.ListGrantsRequest;
import software.amazon.awssdk.services.kms.model.OriginType;
import software.amazon.awssdk.services.kms.model.PutKeyPolicyRequest;
import software.amazon.awssdk.services.kms.model.RevokeGrantRequest;
import software.amazon.awssdk.services.kms.model.RevokeGrantResponse;
import software.amazon.awssdk.services.kms.model.ScheduleKeyDeletionRequest;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.model.SigningAlgorithmSpec;
import software.amazon.awssdk.services.kms.model.Tag;
import software.amazon.awssdk.services.kms.model.TagResourceRequest;
import software.amazon.awssdk.services.kms.model.VerifyRequest;
import software.amazon.awssdk.services.kms.paginators.ListAliasesPublisher;
import software.amazon.awssdk.services.kms.paginators.ListGrantsPublisher;
import software.amazon.awssdk.services.secretsmanager.model.ResourceExistsException;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KMSActions {
    private static final Logger logger = LoggerFactory.getLogger(KMSActions.class);
    private static KmsAsyncClient kmsAsyncClient;

    private static KmsAsyncClient getAsyncClient() {
        if (kmsAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryPolicy(RetryPolicy.builder()
                    .numRetries(3)
                    .build())
                .build();

            kmsAsyncClient = KmsAsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        }
        return kmsAsyncClient;
    }

    // snippet-start:[kms.java2_create_key.main]
    /**
     * Creates a new symmetric encryption key asynchronously.
     *
     * @param keyDesc the description of the key to be created
     * @return a {@link CompletableFuture} that completes with the ID of the newly created key
     * @throws RuntimeException if an error occurs while creating the key
     */
    public CompletableFuture<String> createKeyAsync(String keyDesc) {
        CreateKeyRequest keyRequest = CreateKeyRequest.builder()
            .description(keyDesc)
            .keySpec(KeySpec.SYMMETRIC_DEFAULT)
            .keyUsage(KeyUsageType.ENCRYPT_DECRYPT)
            .build();

        return getAsyncClient().createKey(keyRequest)
            .thenApply(resp -> resp.keyMetadata().keyId())
            .exceptionally(ex -> {
                throw new RuntimeException("An error occurred while creating the key: " + ex.getMessage(), ex);
            });
    }
    // snippet-end:[kms.java2_create_key.main]

    // snippet-start:[kms.java2_describe_key.main]
    public CompletableFuture<Boolean> isKeyEnabledAsync(String keyId) {
        DescribeKeyRequest keyRequest = DescribeKeyRequest.builder()
            .keyId(keyId)
            .build();

        CompletableFuture<DescribeKeyResponse> responseFuture = getAsyncClient().describeKey(keyRequest);
        return responseFuture.whenComplete((resp, ex) -> {
            if (resp != null) {
                KeyState keyState = resp.keyMetadata().keyState();
                if (keyState == KeyState.ENABLED) {
                    logger.info("The key is enabled.");
                } else {
                    logger.info("The key is not enabled. Key state: {}", keyState);
                }
            } else {
                throw new RuntimeException(ex);
            }
        }).thenApply(resp -> resp.keyMetadata().keyState() == KeyState.ENABLED);
    }
    // snippet-end:[kms.java2_describe_key.main]

    // snippet-start:[kms.java2_enable_key.main]
    /**
     * Asynchronously enables the specified key.
     *
     * @param keyId the ID of the key to enable
     * @return a {@link CompletableFuture} that completes when the key has been enabled
     */
    public CompletableFuture<Void> enableKeyAsync(String keyId) {
        EnableKeyRequest enableKeyRequest = EnableKeyRequest.builder()
            .keyId(keyId)
            .build();

        CompletableFuture<Void> responseFuture = new CompletableFuture<>();

        getAsyncClient().enableKey(enableKeyRequest).whenComplete((resp, ex) -> {
            if (ex != null) {
                responseFuture.completeExceptionally(ex);
            } else {
                logger.info("Key with ID [{}] has been enabled.", keyId);
                responseFuture.complete(null);
            }
        });

        return responseFuture;
    }
    // snippet-end:[kms.java2_enable_key.main]

    // snippet-start:[kms.java2_encrypt_data.main]
    /**
     * Encrypts the given text asynchronously using the specified KMS client and key ID.
     *
     * @param keyId the ID of the KMS key to use for encryption
     * @param text the text to encrypt
     * @return a CompletableFuture that completes with the encrypted data as an SdkBytes object
     */
    public CompletableFuture<SdkBytes> encryptDataAsync(String keyId, String text) {
        SdkBytes myBytes = SdkBytes.fromUtf8String(text);
        EncryptRequest encryptRequest = EncryptRequest.builder()
            .keyId(keyId)
            .plaintext(myBytes)
            .build();

        CompletableFuture<EncryptResponse> responseFuture = getAsyncClient().encrypt(encryptRequest).toCompletableFuture();
        return responseFuture.whenComplete((response, ex) -> {
            if (response != null) {
                String algorithm = response.encryptionAlgorithm().toString();
                logger.info("The string was encrypted with algorithm {}.", algorithm);
            } else {
                throw new RuntimeException(ex);
            }
        }).thenApply(response -> response.ciphertextBlob());
    }
    // snippet-end:[kms.java2_encrypt_data.main]

    // snippet-start:[kms.java2._create_alias.main]
    public CompletableFuture<Void> createCustomAliasAsync(String targetKeyId, String aliasName) {
        CreateAliasRequest aliasRequest = CreateAliasRequest.builder()
            .aliasName(aliasName)
            .targetKeyId(targetKeyId)
            .build();

        CompletableFuture<Void> responseFuture = new CompletableFuture<>();
        getAsyncClient().createAlias(aliasRequest).whenComplete((response, ex) -> {
            if (ex != null) {
                if (ex instanceof ResourceExistsException) {
                      responseFuture.complete(null);
                } else {
                     responseFuture.completeExceptionally(ex);
                }
            } else {
                logger.info("{} was successfully created.", aliasName);
                responseFuture.complete(null);
            }
        });

        return responseFuture;
    }
    // snippet-end:[kms.java2._create_alias.main]

    // snippet-start:[kms.java2_list_aliases.main]
    /**
     * Asynchronously lists all the aliases in the current AWS account.
     *
     * @return a {@link CompletableFuture} that completes when the list of aliases has been processed
     */
    public static CompletableFuture<Object> listAllAliasesAsync() {
        ListAliasesRequest aliasesRequest = ListAliasesRequest.builder()
            .limit(15)
            .build();

        ListAliasesPublisher paginator = getAsyncClient().listAliasesPaginator(aliasesRequest);
        return paginator.subscribe(response -> {
                response.aliases().forEach(alias ->
                    System.out.println("The alias name is: " + alias.aliasName())
                );
            })
            .thenApply(v -> null)
            .exceptionally(ex -> {
                if (ex.getCause() instanceof KmsException) {
                    KmsException e = (KmsException) ex.getCause();
                    throw new RuntimeException("A KMS exception occurred: " + e.getMessage());
                } else {
                    throw new RuntimeException("An unexpected error occurred: " + ex.getMessage());
                }
            });
    }
    // snippet-end:[kms.java2_list_aliases.main]

    // snippet-start:[kms.java2._key_rotation.main]
    public CompletableFuture<Void> enableKeyRotationAsync(String keyId) {
        EnableKeyRotationRequest enableKeyRotationRequest = EnableKeyRotationRequest.builder()
            .keyId(keyId)
            .build();

        CompletableFuture<Void> response = getAsyncClient().enableKeyRotation(enableKeyRotationRequest)
            .thenAccept(r -> {
                // Process the response if needed
                logger.info("Key rotation has been enabled for key with id [{}]", keyId);
            })
            .exceptionally(ex -> {
                // Handle exceptions
                if (ex instanceof KmsException) {
                    logger.error("Failed to enable key rotation: {}", ex.getMessage());
                } else {
                    logger.error("An unexpected error occurred: {}", ex.getMessage());
                }
                return null; // Return null or handle the exception as needed
            });

        return response;
    }
    // snippet-end:[kms.java2._key_rotation.main]


    // snippet-start:[kms.java2_create_grant.main]
    /**
     * Grants permissions to a specified principal on a customer master key (CMK) asynchronously.
     *
     * @param keyId             The unique identifier for the customer master key (CMK) that the grant applies to.
     * @param granteePrincipal  The principal that is given permission to perform the operations that the grant permits on the CMK.
     * @return A {@link CompletableFuture} that, when completed, contains the ID of the created grant.
     * @throws RuntimeException If an error occurs during the grant creation process.
     */
    public CompletableFuture<String> grantKeyAsync(String keyId, String granteePrincipal) {
        // Create the grant request
        List<GrantOperation> grantPermissions = new ArrayList<>();
        grantPermissions.add(GrantOperation.ENCRYPT);
        grantPermissions.add(GrantOperation.DECRYPT);
        grantPermissions.add(GrantOperation.DESCRIBE_KEY);

        CreateGrantRequest grantRequest = CreateGrantRequest.builder()
            .keyId(keyId)
            .name("grant1")
            .granteePrincipal(granteePrincipal)
            .operations(grantPermissions)
            .build();

        // Send the request asynchronously and handle response.
        return getAsyncClient().createGrant(grantRequest)
            .whenComplete((response, ex) -> {
                if (response != null) {
                    System.out.println("Grant created successfully with ID: " + response.grantId());
                } else {
                    throw new RuntimeException(ex);
                }
            })
            .thenApply(CreateGrantResponse::grantId) // Extract grant ID from response
            .exceptionally(ex -> {
                // Handle exceptions and return null or a default value
                if (ex instanceof KmsException) {
                    System.err.println("Failed to create grant: " + ex.getMessage());
                } else {
                    System.err.println("An unexpected error occurred: " + ex.getMessage());
                }
                return null;
            });
    }
    // snippet-end:[kms.java2_create_grant.main]

    // snippet-start:[kms.java2_list_grant.main]
    /**
     * Asynchronously displays the grant IDs for the specified key ID.
     *
     * @param keyId the ID of the AWS KMS key for which to list the grants
     * @return a {@link CompletableFuture} that, when completed, will be null if the operation succeeded, or will throw a {@link RuntimeException} if the operation failed
     * @throws RuntimeException if there was an error listing the grants, either due to an {@link KmsException} or an unexpected error
     */
    public CompletableFuture<Object> displayGrantIdsAsync(String keyId) {
        ListGrantsRequest grantsRequest = ListGrantsRequest.builder()
            .keyId(keyId)
            .limit(15)
            .build();

        ListGrantsPublisher paginator = getAsyncClient().listGrantsPaginator(grantsRequest);
        return paginator.subscribe(response -> {
                response.grants().forEach(grant -> {
                    System.out.println("The grant Id is: " + grant.grantId());
                });
            })
            .thenApply(v -> null)
            .exceptionally(ex -> {
                Throwable cause = ex.getCause();
                if (cause instanceof KmsException) {
                    throw new RuntimeException("Failed to list grants: " + cause.getMessage(), cause);
                } else {
                    throw new RuntimeException("An unexpected error occurred: " + cause.getMessage(), cause);
                }
            });
    }
    // snippet-end:[kms.java2_list_grant.main]

    // snippet-start:[kms.java2_revoke_grant.main]
    /**
     * Revokes a grant for the specified AWS KMS key asynchronously.
     *
     * @param keyId   The ID or key ARN of the AWS KMS key.
     * @param grantId The identifier of the grant to be revoked.
     * @return A {@link CompletableFuture} representing the asynchronous operation of revoking the grant.
     *         The {@link CompletableFuture} will complete with a {@link RevokeGrantResponse} object
     *         if the operation is successful, or with a {@code null} value if an error occurs.
     */
    public CompletableFuture<RevokeGrantResponse> revokeKeyGrantAsync(String keyId, String grantId) {
        RevokeGrantRequest grantRequest = RevokeGrantRequest.builder()
            .keyId(keyId)
            .grantId(grantId)
            .build();

        return getAsyncClient().revokeGrant(grantRequest)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof KmsException kmsEx) {
                        if (kmsEx.getMessage().contains("Grant does not exist")) {
                            // Grant does not exist.
                            logger.info("The grant ID '" + grantId + "' does not exist. Moving on...");
                        } else {
                            logger.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                        }
                    } else {
                        logger.error("An unexpected error occurred: " + exception.getMessage(), exception);
                    }
                } else {
                    logger.info("Grant ID: [" + grantId + "] was successfully revoked!");
                }
            })
            .exceptionally(throwable -> {
                logger.error("Failed to revoke grant ID: [" + grantId + "]", throwable);
                return null;
            });
    }
    // snippet-end:[kms.java2_revoke_grant.main]

    // snippet-start:[kms.java2_decrypt_data.main]
    /**
     * Asynchronously decrypts the given encrypted data using the specified key ID.
     *
     * @param encryptedData The encrypted data to be decrypted.
     * @param keyId The ID of the key to be used for decryption.
     * @return A CompletableFuture that, when completed, will contain the decrypted data as a String.
     *         If an error occurs during the decryption process, the CompletableFuture will complete
     *         exceptionally with the error, and the method will return an empty String.
     */
    public CompletableFuture<String> decryptDataAsync(SdkBytes encryptedData, String keyId) {
        DecryptRequest decryptRequest = DecryptRequest.builder()
            .ciphertextBlob(encryptedData)
            .keyId(keyId)
            .build();

        return getAsyncClient().decrypt(decryptRequest)
            .thenApply(decryptResponse -> decryptResponse.plaintext().asString(StandardCharsets.UTF_8))
            .exceptionally(throwable -> {
                logger.error("Failed to decrypt data: " + throwable.getMessage(), throwable);
                return "";
            });
    }
    // snippet-end:[kms.java2_decrypt_data.main]

    // snippet-start:[kms.java2_set_policy.main]
    /**
     * Asynchronously replaces the policy for the specified KMS key.
     *
     * @param keyId       the ID of the KMS key to replace the policy for
     * @param policyName  the name of the policy to be replaced
     * @param accountId   the AWS account ID to be used in the policy
     * @return a {@link CompletableFuture} that completes with a boolean indicating
     *         whether the policy replacement was successful or not
     */
    public CompletableFuture<Boolean> replacePolicyAsync(String keyId, String policyName, String accountId) {
        // Change the principle in the below JSON.
        String policy = """
        {
          "Version": "2012-10-17",
          "Statement": [{
            "Effect": "Allow",
            "Principal": {"AWS": "arn:aws:iam::%s:root"},
            "Action": "kms:*",
            "Resource": "*"
          }]
        }
        """.formatted(accountId);

        PutKeyPolicyRequest keyPolicyRequest = PutKeyPolicyRequest.builder()
            .keyId(keyId)
            .policyName(policyName)
            .policy(policy)
            .build();

        return getAsyncClient().putKeyPolicy(keyPolicyRequest)
            .thenApply(response -> {
                logger.info("The key policy has been replaced.");
                return true;
            })
            .exceptionally(throwable -> {
                throw new RuntimeException("Error replacing policy", throwable);
            });
    }
    // snippet-end:[kms.java2_set_policy.main]

    // snippet-start:[kms.java2_get_policy.main]
    /**
     * Asynchronously retrieves the key policy for the specified key ID and policy name.
     *
     * @param keyId       the ID of the AWS KMS key for which to retrieve the policy
     * @param policyName the name of the key policy to retrieve
     * @return a {@link CompletableFuture} that, when completed, contains the key policy as a {@link String}
     */
    public CompletableFuture<String> getKeyPolicyAsync(String keyId, String policyName) {
        GetKeyPolicyRequest policyRequest = GetKeyPolicyRequest.builder()
            .keyId(keyId)
            .policyName(policyName)
            .build();

        return getAsyncClient().getKeyPolicy(policyRequest)
            .thenApply(response -> {
                String policy = response.policy();
                logger.info("The response is: " + policy);
                return policy;
            })
            .exceptionally(ex -> {
                throw new RuntimeException("Failed to get key policy", ex);
            });
    }
    // snippet-end:[kms.java2_get_policy.main]

    // snippet-start:[kms.java2_sign.main]
    /**
     * Asynchronously signs and verifies data using AWS KMS.
     *
     * <p>The method performs the following steps:
     * <ol>
     *     <li>Creates an AWS KMS key with the specified key spec, key usage, and origin.</li>
     *     <li>Signs the provided message using the created KMS key and the RSASSA-PSS-SHA-256 algorithm.</li>
     *     <li>Verifies the signature of the message using the created KMS key and the RSASSA-PSS-SHA-256 algorithm.</li>
     * </ol>
     *
     * @return a {@link CompletableFuture} that completes with the result of the signature verification,
     *         {@code true} if the signature is valid, {@code false} otherwise.
     * @throws KmsException if any error occurs during the KMS operations.
     * @throws RuntimeException if an unexpected error occurs.
     */
    public CompletableFuture<Boolean> signVerifyDataAsync() {
        String signMessage = "Here is the message that will be digitally signed";

        // Create an AWS KMS key used to digitally sign data.
        CreateKeyRequest createKeyRequest = CreateKeyRequest.builder()
            .keySpec(KeySpec.RSA_2048)
            .keyUsage(KeyUsageType.SIGN_VERIFY)
            .origin(OriginType.AWS_KMS)
            .build();

        return getAsyncClient().createKey(createKeyRequest)
            .thenCompose(createKeyResponse -> {
                String keyId = createKeyResponse.keyMetadata().keyId();

                SdkBytes messageBytes = SdkBytes.fromString(signMessage, Charset.defaultCharset());
                SignRequest signRequest = SignRequest.builder()
                    .keyId(keyId)
                    .message(messageBytes)
                    .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PSS_SHA_256)
                    .build();

                return getAsyncClient().sign(signRequest)
                    .thenCompose(signResponse -> {
                        byte[] signedBytes = signResponse.signature().asByteArray();

                        VerifyRequest verifyRequest = VerifyRequest.builder()
                            .keyId(keyId)
                            .message(SdkBytes.fromByteArray(signMessage.getBytes(Charset.defaultCharset())))
                            .signature(SdkBytes.fromByteBuffer(ByteBuffer.wrap(signedBytes)))
                            .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PSS_SHA_256)
                            .build();

                        return getAsyncClient().verify(verifyRequest)
                            .thenApply(verifyResponse -> {
                                boolean isSignatureValid = verifyResponse.signatureValid();
                                return isSignatureValid;
                            });
                    });
            })
            .exceptionally(throwable -> {
                // Rethrow the exception to propagate it to the calling code
                throw new RuntimeException("Failed to sign or verify data", throwable);
            });
    }
    // snippet-end:[kms.java2_sign.main]

    // snippet-start:[kms.java2_tag.main]
    /**
     * Asynchronously tags a KMS key with a specific tag.
     *
     * @param keyId the ID of the KMS key to be tagged
     * @return a {@link CompletableFuture} that completes when the tagging operation is finished
     */
    public static CompletableFuture<Void> tagKMSKeyAsync(String keyId) {
        Tag tag = Tag.builder()
            .tagKey("Environment")
            .tagValue("Production")
            .build();

        TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
            .keyId(keyId)
            .tags(tag)
            .build();

        return getAsyncClient().tagResource(tagResourceRequest)
            .thenRun(() -> {
                logger.info("{} key was tagged", keyId);
            })
            .exceptionally(throwable -> {
                throw new RuntimeException("Failed to tag the KMS key", throwable);
            });
    }
    // snippet-end:[kms.java2_tag.main]

    // snippet-start:[kms.java2_delete_alias.main]
    /**
     * Deletes a specific KMS alias asynchronously.
     *
     * @param aliasName the name of the alias to be deleted
     * @return a {@link CompletableFuture} representing the asynchronous operation of deleting the specified alias
     */
    public CompletableFuture<Void> deleteSpecificAliasAsync(String aliasName) {
        DeleteAliasRequest deleteAliasRequest = DeleteAliasRequest.builder()
            .aliasName(aliasName)
            .build();

        return getAsyncClient().deleteAlias(deleteAliasRequest)
            .thenRun(() -> {
                logger.info("Alias {} has been deleted successfully", aliasName);
            })
            .exceptionally(throwable -> {
                throw new RuntimeException("Failed to delete alias: " + aliasName, throwable);
            });
    }
    // snippet-end:[kms.java2_delete_alias.main]


    // snippet-start:[kms.java2_disable_key.main]
    /**
     * Asynchronously disables the specified AWS Key Management Service (KMS) key.
     *
     * @param keyId the ID or Amazon Resource Name (ARN) of the KMS key to be disabled
     * @return a CompletableFuture that, when completed, indicates that the key has been disabled successfully
     */
    public CompletableFuture<Void> disableKeyAsync(String keyId) {
        DisableKeyRequest keyRequest = DisableKeyRequest.builder()
            .keyId(keyId)
            .build();

        return getAsyncClient().disableKey(keyRequest)
            .thenRun(() -> {
                logger.info("Key {} has been disabled successfully",keyId);
            })
            .exceptionally(throwable -> {
                throw new RuntimeException("Failed to disable key: " + keyId, throwable);
            });
    }
    // snippet-end:[kms.java2_disable_key.main]

    // snippet-start:[kms.java2_delete_key.main]
    /**
     * Deletes a KMS key asynchronously.
     *
     * <p><strong>Warning:</strong> Deleting a KMS key is a destructive and potentially dangerous operation.
     * When a KMS key is deleted, all data that was encrypted under the KMS key becomes unrecoverable.
     * This means that any files, databases, or other data that were encrypted using the deleted KMS key
     * will become permanently inaccessible. Exercise extreme caution when deleting KMS keys.</p>
     *
     * @param keyId the ID of the KMS key to delete
     * @return a {@link CompletableFuture} that completes when the key deletion is scheduled
     */
    public static CompletableFuture<Void> deleteKeyAsync(String keyId) {
        ScheduleKeyDeletionRequest deletionRequest = ScheduleKeyDeletionRequest.builder()
            .keyId(keyId)
            .pendingWindowInDays(7)
            .build();

        return getAsyncClient().scheduleKeyDeletion(deletionRequest)
            .thenRun(() -> {
                logger.info("Key {} will be deleted in 7 days", keyId);
            })
            .exceptionally(throwable -> {
                throw new RuntimeException("Failed to schedule key deletion for key ID: " + keyId, throwable);
            });
    }
    // snippet-end:[kms.java2_delete_key.main]


    public String getAccountId(){
        try (StsClient stsClient = StsClient.create()){
            GetCallerIdentityResponse callerIdentity = stsClient.getCallerIdentity();
            return callerIdentity.account();
        }
    }

}
