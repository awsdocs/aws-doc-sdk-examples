// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kms.scenario;

// snippet-start:[kms.java2_scenario.main]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.kms.model.EnableKeyRotationResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.RevokeGrantResponse;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class KMSScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static String accountId = "";

    private static final Logger logger = LoggerFactory.getLogger(KMSScenario.class);

    public static void main(String[] args) {
        final String usage = """
                Usage: <granteePrincipal>

                Where:
                   granteePrincipal - The principal (user, service account, or group) to whom the grant or permission is being given. 
                """;

       // if (args.length != 1) {
       //     logger.info(usage);
       //     return;
       // }
        String granteePrincipal = "arn:aws:iam::814548047983:user/kmsuser" ; //args[0];
        String policyName = "default";

        KMSActions kmsActions = new KMSActions();
        accountId = kmsActions.getAccountId();
        Scanner scanner = new Scanner(System.in);
        String keyDesc = "Created by the AWS KMS API";

        System.out.println(DASHES);
        logger.info("""
            Welcome to the AWS Key Management SDK Basics scenario.
            
            This program demonstrates how to interact with AWS Key Management using the AWS SDK for Java (v2).
            The AWS Key Management Service (KMS) is a secure and highly available service that allows you to create 
            and manage AWS KMS keys and control their use across a wide range of AWS services and applications. 
            KMS provides a centralized and unified approach to managing encryption keys, making it easier to meet your 
            data protection and regulatory compliance requirements.
                        
            This Basics scenario creates two key types:
            
            - A symmetric encryption key is used to encrypt and decrypt data.
            - An asymmetric key used to digitally sign data. 
            
            Let's get started...
            """);
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        logger.info("1. Create a symmetric KMS key\n");
        logger.info("First, the program will creates a symmetric KMS key that you can used to encrypt and decrypt data.");
        waitForInputToContinue(scanner);
        String targetKeyId = null;
        try {
            CompletableFuture<String> futureKeyId = kmsActions.createKeyAsync(keyDesc);
            targetKeyId = futureKeyId.join();
            logger.info("A symmetric key was successfully created "+targetKeyId);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("""
            2. Enable a KMS key
                         
            By default, when the SDK creates an AWS key it is enabled. The next bit of code checks to 
            determine if the key is enabled. If it is not enabled, the code enables it. 
             """);
        waitForInputToContinue(scanner);
        boolean isEnabled = false;
        try {
            CompletableFuture<Boolean> futureIsKeyEnabled = kmsActions.isKeyEnabledAsync(targetKeyId);
            isEnabled = futureIsKeyEnabled.join();
            logger.info("Is the key enabled? {}", isEnabled);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }

        if (!isEnabled)
            try {
                CompletableFuture<Void> future = kmsActions.enableKeyAsync(targetKeyId);
                future.join();

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof KmsException kmsEx) {
                    logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("3. Encrypt data using the symmetric KMS key");
        String plaintext = "Hello, AWS KMS!";
        System.out.printf("""
                One of the main uses of symmetric keys is to encrypt and decrypt data.
                Next, the code encrypts the string '%s' with the SYMMETRIC_DEFAULT encryption algorithm.
                %n""", plaintext);
        waitForInputToContinue(scanner);
        SdkBytes encryptedData = null;
        try {
            CompletableFuture<SdkBytes> future = kmsActions.encryptDataAsync(targetKeyId, plaintext);
            encryptedData = future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("4. Create an alias");
        System.out.println("""
            
           Enter an alias name for the key. The name should be prefixed with 'alias/'. 
           The default, 'alias/dev-encryption-key'.
            """);
        waitForInputToContinue(scanner);
        String aliasName = "alias/dev-encryption-key";
        try {
            CompletableFuture<Void> future = kmsActions.createCustomAliasAsync(targetKeyId, aliasName);
            future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                if (kmsEx.getMessage().contains("already exists")) {
                   logger.info("The alias '" + aliasName + "' already exists. Moving on...");
                } else {
                    logger.error("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                }
            } else {
                logger.error("An unexpected error occurred: " + rt.getMessage(), rt);
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("5. List all of your aliases");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Object> future = kmsActions.listAllAliasesAsync();
            future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("6. Enable automatic rotation of the KMS key");
        System.out.println("""
            
            By default, when the SDK enables automatic rotation of a KMS key,
            KMS rotates the key material of the KMS key one year (approximately 365 days) from the enable date and every year 
            thereafter. 
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<EnableKeyRotationResponse> future = kmsActions.enableKeyRotationAsync(targetKeyId);
            future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("""
            7. Create a grant
            
            A grant is a policy instrument that allows Amazon Web Services principals to use KMS keys.
            It also can allow them to view a KMS key (DescribeKey) and create and manage grants.
            When authorizing access to a KMS key, grants are considered along with key policies and IAM policies.
            """);

        waitForInputToContinue(scanner);
        String grantId = null;
        try {
            CompletableFuture<String> futureGrantId = kmsActions.grantKeyAsync(targetKeyId, granteePrincipal);
            grantId = futureGrantId.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        logger.info(DASHES);
        logger.info("8. List grants for the KMS key");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Object> future = kmsActions.displayGrantIdsAsync(targetKeyId);
            future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("9. Revoke the grant");
        logger.info("""
            The revocation of a grant immediately removes the permissions and access that the grant had provided. 
            This means that any principal (user, role, or service) that was granted access to perform specific 
            KMS operations on a KMS key will no longer be able to perform those operations.
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<RevokeGrantResponse> future = kmsActions.revokeKeyGrantAsync(targetKeyId, grantId);
            future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                if (kmsEx.getMessage().contains("Grant does not exist")) {
                    logger.info("The grant ID '" + grantId + "' does not exist. Moving on...");
                } else {
                    logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                }
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("10. Decrypt the data\n");
        logger.info("""
            Lets decrypt the data that was encrypted in an early step.
            The code uses the same key to decrypt the string that we encrypted earlier in the program.
            """);
        waitForInputToContinue(scanner);
        String decryptedData = "";
        try {
            CompletableFuture<String> future = kmsActions.decryptDataAsync(encryptedData, targetKeyId);
            decryptedData = future.join();
            logger.info("Decrypted data: " + decryptedData);

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        logger.info("Decrypted text is: " + decryptedData);
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("11. Replace a key policy\n");
        System.out.println("""
            A key policy is a resource policy for a KMS key. Key policies are the primary way to control 
            access to KMS keys. Every KMS key must have exactly one key policy. The statements in the key policy 
            determine who has permission to use the KMS key and how they can use it. 
            You can also use IAM policies and grants to control access to the KMS key, but every KMS key 
            must have a key policy.
            
            By default, when you create a key by using the SDK, a policy is created that 
            gives the AWS account that owns the KMS key full access to the KMS key.
            
            Let's try to replace the automatically created policy with the following policy.
                    
                "Version": "2012-10-17",
                "Statement": [{
                "Effect": "Allow",
                "Principal": {"AWS": "arn:aws:iam::0000000000:root"},
                "Action": "kms:*",
                "Resource": "*"
                }] 
            """);

        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Boolean> future = kmsActions.replacePolicyAsync(targetKeyId, policyName, accountId);
            boolean success = future.join();
            if (success) {
                logger.info("Key policy replacement succeeded.");
            } else {
                logger.error("Key policy replacement failed.");
            }

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("12. Get the key policy\n");
        System.out.println("The next bit of code that runs gets the key policy to make sure it exists.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = kmsActions.getKeyPolicyAsync(targetKeyId, policyName);
            String policy = future.join();
            if (!policy.isEmpty()) {
                logger.info("Retrieved policy: " + policy);
            }

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("13. Create an asymmetric KMS key and sign your data\n");
        System.out.println("""
            Signing your data with an AWS key can provide several benefits that make it an attractive option 
            for your data signing needs. By using an AWS KMS key, you can leverage the 
            security controls and compliance features provided by AWS,
            which can help you meet various regulatory requirements and enhance the overall security posture 
            of your organization.
           """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Boolean> future = kmsActions.signVerifyDataAsync();
            boolean success = future.join(); // Wait for the operation to complete
            if (success) {
                logger.info("Sign and verify data operation succeeded.");
            } else {
                logger.error("Sign and verify data operation failed.");
            }

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("14. Tag your symmetric KMS Key\n");
        System.out.println("""
            By using tags, you can improve the overall management, security, and governance of your 
            KMS keys, making it easier to organize, track, and control access to your encrypted data within 
            your AWS environment
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = kmsActions.tagKMSKeyAsync(targetKeyId);
            future.join();

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof KmsException kmsEx) {
                logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("15. Schedule the deletion of the KMS key\n");
        System.out.println("""
            By default, KMS applies a waiting period of 30 days,
            but you can specify a waiting period of 7-30 days. When this operation is successful, 
            the key state of the KMS key changes to PendingDeletion and the key can't be used in any 
            cryptographic operations. It remains in this state for the duration of the waiting period.
                
            Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted, 
            all data that was encrypted under the KMS key is unrecoverable.\s
            """);
        System.out.println("Would you like to delete the Key Management resources? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete the AWS KMS resources.");
            waitForInputToContinue(scanner);
            try {
                CompletableFuture<Void> future = kmsActions.deleteSpecificAliasAsync(aliasName);
                future.join();

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof KmsException kmsEx) {
                    logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }
            waitForInputToContinue(scanner);
            try {
                CompletableFuture<Void> future = kmsActions.disableKeyAsync(targetKeyId);
                future.join();

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof KmsException kmsEx) {
                    logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }

            try {
                CompletableFuture<Void> future = kmsActions.deleteKeyAsync(targetKeyId);
                future.join();

            } catch (RuntimeException rt) {
                Throwable cause = rt.getCause();
                if (cause instanceof KmsException kmsEx) {
                    logger.info("KMS error occurred: Error message: {}, Error code {}", kmsEx.getMessage(), kmsEx.awsErrorDetails().errorCode());
                } else {
                    logger.info("An unexpected error occurred: " + rt.getMessage());
                }
            }

        } else {
            System.out.println("The Key Management resources will not be deleted");
        }

        System.out.println(DASHES);
        System.out.println("This concludes the AWS Key Management SDK scenario");
        System.out.println(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[kms.java2_scenario.main]