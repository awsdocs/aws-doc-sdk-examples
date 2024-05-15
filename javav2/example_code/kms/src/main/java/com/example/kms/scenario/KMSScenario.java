// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kms.scenario;

// snippet-start:[kms.java2_scenario.main]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.AliasListEntry;
import software.amazon.awssdk.services.kms.model.CreateAliasRequest;
import software.amazon.awssdk.services.kms.model.CreateGrantRequest;
import software.amazon.awssdk.services.kms.model.CreateGrantResponse;
import software.amazon.awssdk.services.kms.model.CreateKeyRequest;
import software.amazon.awssdk.services.kms.model.CreateKeyResponse;
import software.amazon.awssdk.services.kms.model.CustomerMasterKeySpec;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.DeleteAliasRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyResponse;
import software.amazon.awssdk.services.kms.model.DisableKeyRequest;
import software.amazon.awssdk.services.kms.model.EnableKeyRequest;
import software.amazon.awssdk.services.kms.model.EnableKeyRotationRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.GetKeyPolicyRequest;
import software.amazon.awssdk.services.kms.model.GetKeyPolicyResponse;
import software.amazon.awssdk.services.kms.model.GrantListEntry;
import software.amazon.awssdk.services.kms.model.GrantOperation;
import software.amazon.awssdk.services.kms.model.KeyState;
import software.amazon.awssdk.services.kms.model.KeyUsageType;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListAliasesRequest;
import software.amazon.awssdk.services.kms.model.ListAliasesResponse;
import software.amazon.awssdk.services.kms.model.ListGrantsRequest;
import software.amazon.awssdk.services.kms.model.ListGrantsResponse;
import software.amazon.awssdk.services.kms.model.ListKeyPoliciesRequest;
import software.amazon.awssdk.services.kms.model.ListKeyPoliciesResponse;
import software.amazon.awssdk.services.kms.model.OriginType;
import software.amazon.awssdk.services.kms.model.PutKeyPolicyRequest;
import software.amazon.awssdk.services.kms.model.RevokeGrantRequest;
import software.amazon.awssdk.services.kms.model.ScheduleKeyDeletionRequest;
import software.amazon.awssdk.services.kms.model.SignRequest;
import software.amazon.awssdk.services.kms.model.SignResponse;
import software.amazon.awssdk.services.kms.model.SigningAlgorithmSpec;
import software.amazon.awssdk.services.kms.model.Tag;
import software.amazon.awssdk.services.kms.model.TagResourceRequest;
import software.amazon.awssdk.services.kms.model.VerifyRequest;
import software.amazon.awssdk.services.kms.model.VerifyResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceExistsException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

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

    public static void main(String[] args) {

        String granteePrincipal = "arn:aws:iam::814548047983:role/aws-service-role/cks.kms.amazonaws.com/AWSServiceRoleForKeyManagementServiceCustomKeyStores";
        String policyName = "testPolicy1" ;
        Scanner scanner = new Scanner(System.in);
        String keyDesc = "Created by the AWS KMS API";

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
            .region(region)
            .build();

        System.out.println(DASHES);
        System.out.println("""
            Welcome to the AWS Key Management SDK Getting Started scenario.
            This program demonstrates how to interact with AWS Key Management using the AWS SDK for Java (v2).
            The AWS Key Management Service (KMS) is a secure and highly available service that allows you to create 
            and manage AWS KMS keys and control their use across a wide range of AWS services and applications. 
            KMS provides a centralized and unified approach to managing encryption keys, making it easier to meet your 
            data protection and regulatory compliance requirements.
            
            This Getting Started scenario creates two key types. A symmetric encryption key is used to encrypt and decrypt data, 
            and an asymmetric key used to digitally sign data. 
            Let's get started...
            """);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("First, we will create an AWS KMS key that is used to encrypt and decrypt data.");
        waitForInputToContinue(scanner);
        String targetKeyId = createKey(kmsClient, keyDesc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
        By default, when you create an AWS key, it is enabled. Check to make sure if the key is enabled. If not,
        then enabled it. 
        """);
        waitForInputToContinue(scanner);
        boolean isEnabled = isKeyEnabled(kmsClient, targetKeyId);
        if (!isEnabled)
            enableKey(kmsClient, targetKeyId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        String plaintext = "Hello, AWS KMS!";
        System.out.println(plaintext);
        System.out.println("Lets encrypt this data.");
        waitForInputToContinue(scanner);
        SdkBytes ciphertext = encryptData(kmsClient, targetKeyId, plaintext);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Please enter an alias name for the key that is prefixed by alias/ (default is alias/dev-encryption-key.");
        String aliasName = scanner.nextLine();
        String fullAliasName = aliasName.isEmpty() ? "alias/dev-encryption-key" : aliasName;
        createCustomAlias(kmsClient, targetKeyId, fullAliasName);
        listAllAliases(kmsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
            Enable automatic rotation of the KMS key.
            
            By default, when you enable automatic rotation of a KMS key, 
            KMS rotates the key material of the KMS key one year (approximately 365 days) from the enable date and every year 
            thereafter. 
            """);
        waitForInputToContinue(scanner);
        enableKeyRotation(kmsClient, targetKeyId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
            A grant is a policy instrument that allows Amazon Web Services principals to use KMS keys.
            
            It also can allow them to view a KMS key (DescribeKey) and create and manage grants. 
            When authorizing access to a KMS key, grants are considered along with key policies and IAM policies.
            """);

        waitForInputToContinue(scanner);
        String grantId = grantKey(kmsClient, targetKeyId, granteePrincipal);
        System.out.println("The grant id is "+grantId);
        System.out.println("List grants for the KMS key.");
        waitForInputToContinue(scanner);
        displayGrantIds(kmsClient, targetKeyId);
        System.out.println("Revoke the grant.");
        waitForInputToContinue(scanner);
        revokeKeyGrant(kmsClient, targetKeyId, grantId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Lets decrypt the data that was encrypted in an early step.");
        waitForInputToContinue(scanner);
        String decryptText = decryptData(kmsClient, ciphertext, targetKeyId);
        System.out.println("Decrypted text is: "+decryptText);

        System.out.println(DASHES);
        System.out.println("""
        A key policy is a resource policy for an KMS key. Key policies are the primary way to control 
        access to KMS keys. Every KMS key must have exactly one key policy. The statements in the key policy 
        determine who has permission to use the KMS key and how they can use it. 
        You can also use IAM policies and grants to control access to the KMS key, but every KMS key 
        must have a key policy..
        """);
        System.out.println("Set a Key policy.");
        waitForInputToContinue(scanner);
        createPolicy(kmsClient,targetKeyId, policyName);
        System.out.println("Lets get the key policy to make sure it exists. ");
        waitForInputToContinue(scanner);
        getKeyPolicy(kmsClient, targetKeyId, policyName);
        System.out.println(DASHES);

        System.out.println("""
            Signing your data with an AWS key can provide several benefits that make it an attractive option 
            for your data signing needs. By using an AWS KMS key, you can leverage the 
            security controls and compliance features provided by AWS,
            which can help you meet various regulatory requirements and enhance the overall security posture 
            of your organization.
           """);
        waitForInputToContinue(scanner);
        signVerifyData(kmsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
            Tag your KMS Key.
            
            By using tags, you can improve the overall management, security, and governance of your 
            KMS keys, making it easier to organize, track, and control access to your encrypted data within 
            your AWS environment
            """);
        waitForInputToContinue(scanner);
        tagKMSKey(kmsClient, targetKeyId);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("""
            Schedules the deletion of a KMS key. By default, KMS applies a waiting period of 30 days, 
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
            deleteSpecificAlias(kmsClient, fullAliasName);
            disableKey(kmsClient, targetKeyId);
            deleteKey(kmsClient, targetKeyId);

        } else {
            System.out.println("The Key Management resources will not be deleted");
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("This concludes the AWS Key Management SDK Getting Started scenario");
        System.out.println(DASHES);
    }
    // snippet-start:[kms.java2_list_aliases.main]
    public static void listAllAliases(KmsClient kmsClient) {
        try {
            ListAliasesRequest aliasesRequest = ListAliasesRequest.builder()
                .limit(15)
                .build();

            ListAliasesResponse aliasesResponse = kmsClient.listAliases(aliasesRequest);
            List<AliasListEntry> aliases = aliasesResponse.aliases();
            for (AliasListEntry alias : aliases) {
                System.out.println("The alias name is: " + alias.aliasName());
            }

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_list_aliases.main]

    // snippet-start:[kms.java2_disable_key.main]
    public static void disableKey(KmsClient kmsClient, String keyId) {
        try {
            DisableKeyRequest keyRequest = DisableKeyRequest.builder()
                .keyId(keyId)
                .build();

            kmsClient.disableKey(keyRequest);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_disable_key.main]

    // snippet-start:[kms.java2_sign.main]
    public static void signVerifyData(KmsClient kmsClient) {
        String signMessage = "Here is the message that will be digitally signed";

        // Create an AWS KMS key used to digitally sign data.
        CreateKeyRequest request = CreateKeyRequest.builder()
            .customerMasterKeySpec(CustomerMasterKeySpec.RSA_2048) // Specify key spec
            .keyUsage(KeyUsageType.SIGN_VERIFY) // Specify key usage
            .origin(OriginType.AWS_KMS) // Specify key origin
            .build();

        CreateKeyResponse response = kmsClient.createKey(request);
        String keyId2 = response.keyMetadata().keyId();
        System.out.println("Created KMS key with ID: " + keyId2);

        SdkBytes bytes = SdkBytes.fromString(signMessage, Charset.defaultCharset());
        SignRequest signRequest = SignRequest.builder()
            .keyId(keyId2)
            .message(bytes)
            .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PSS_SHA_256)
            .build();

        SignResponse signResponse = kmsClient.sign(signRequest);
        byte[] signedBytes = signResponse.signature().asByteArray();

        // Verify the digital signature.
        VerifyRequest verifyRequest = VerifyRequest.builder()
            .keyId(keyId2)
            .message(SdkBytes.fromByteArray(signMessage.getBytes(Charset.defaultCharset())))
            .signature(SdkBytes.fromByteBuffer(ByteBuffer.wrap(signedBytes)))
            .signingAlgorithm(SigningAlgorithmSpec.RSASSA_PSS_SHA_256)
            .build();

        VerifyResponse verifyResponse = kmsClient.verify(verifyRequest);
        System.out.println("Signature verification result: " + verifyResponse.signatureValid());
    }
    // snippet-end:[kms.java2_sign.main]

    // snippet-start:[kms.java2_tag.main]
    public static void tagKMSKey(KmsClient kmsClient, String keyId) {
        try {
            Tag tag = Tag.builder()
                .tagKey("Environment")
                .tagValue("Production")
                .build();

            TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                .keyId(keyId)
                .tags(tag)
                .build();

            kmsClient.tagResource(tagResourceRequest);
            System.out.println("Tagged KMS key with key-value pair");

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_tag.main]

    // snippet-start:[kms.java2_get_policy.main]
    public static void getKeyPolicy(KmsClient kmsClient, String keyId, String policyName) {
       try {
        GetKeyPolicyRequest policyRequest = GetKeyPolicyRequest.builder()
            .keyId(keyId)
            .policyName(policyName)
            .build();

        GetKeyPolicyResponse response = kmsClient.getKeyPolicy(policyRequest);
        System.out.println("The response is "+response.policy());
       } catch (KmsException e) {
           if (e.getMessage().contains("No such policy exists")) {
               System.out.println("The policy cannot be found. Error message: " + e.getMessage());
           } else {
               throw e;
           }
       }
    }
    // snippet-end:[kms.java2_get_policy.main]

    // snippet-start:[kms.java2_set_policy.main]
    public static void createPolicy(KmsClient kmsClient, String keyId, String policyName) {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [{
                    "Effect": "Allow",
                    "Principal": {"AWS": "arn:aws:iam::814548047983:root"},
                    "Action": "kms:*",
                    "Resource": "*"
                  }]
                }
                """;
        try {
            doesKeyHavePolicy(kmsClient, keyId, policyName);

            // Before we create a new policy, determine if the key has a policy on it.
            PutKeyPolicyRequest keyPolicyRequest = PutKeyPolicyRequest.builder()
                .keyId(keyId)
                .policyName(policyName)
                .policy(policy)
                .build();

            kmsClient.putKeyPolicy(keyPolicyRequest);
            System.out.println("Done");

        } catch (KmsException e) {
            if (e.getMessage().contains("LimitExceededException")) {
                System.out.println("Policy limit reached. Unable to create the policy.");
            } else if (e.getMessage().contains("Only one policy per key is supported")) {
                System.out.println("Only one policy per key is supported. Unable to create the policy.");
            } else {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    // snippet-end:[kms.java2_set_policy.main]

    public static String doesKeyHavePolicy(KmsClient kmsClient, String keyId, String policyName){
        ListKeyPoliciesRequest policiesRequest = ListKeyPoliciesRequest.builder()
            .keyId(keyId)
            .build();

        String polName = "" ;
        ListKeyPoliciesResponse response = kmsClient.listKeyPolicies(policiesRequest);
        List<String>policyNames = response.policyNames();
        for (String pol : policyNames) {
            System.out.println("Policy Name: " + pol);
            polName = pol;
        }
        return polName;
    }

    // snippet-start:[kms.java2_delete_key.main]
    public static void deleteKey(KmsClient kmsClient, String keyId) {
        try {
            ScheduleKeyDeletionRequest deletionRequest = ScheduleKeyDeletionRequest.builder()
                .keyId(keyId)
                .pendingWindowInDays(7)
                .build();

            kmsClient.scheduleKeyDeletion(deletionRequest);
            System.out.println("The key will be deleted in 7 days.");

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_delete_key.main]

    // snippet-start:[kms.java2_delete_alias.main]
    public static void deleteSpecificAlias(KmsClient kmsClient, String aliasName) {
        try {
            DeleteAliasRequest deleteAliasRequest = DeleteAliasRequest.builder()
                .aliasName(aliasName)
                .build();

            kmsClient.deleteAlias(deleteAliasRequest);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_delete_alias.main]

    // snippet-start:[kms.java2_describe_key.main]
    public static boolean isKeyEnabled(KmsClient kmsClient, String keyId) {
        try {
            DescribeKeyRequest keyRequest = DescribeKeyRequest.builder()
                .keyId(keyId)
                .build();

            DescribeKeyResponse response = kmsClient.describeKey(keyRequest);
            KeyState keyState = response.keyMetadata().keyState();
            if (keyState == KeyState.ENABLED) {
                System.out.println("The key is enabled.");
                return true;
            } else {
                System.out.println("The key is not enabled. Key state: " + keyState);
            }

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return false;
    }
    // snippet-end:[kms.java2_describe_key.main]

    // snippet-start:[kms.java2_decrypt_data.main]
    public static String decryptData(KmsClient kmsClient, SdkBytes encryptedData, String keyId) {
        try {
            DecryptRequest decryptRequest = DecryptRequest.builder()
                .ciphertextBlob(encryptedData)
                .keyId(keyId)
                .build();

            DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
            return decryptResponse.plaintext().asString(StandardCharsets.UTF_8);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[kms.java2_decrypt_data.main]

    // snippet-start:[kms.java2_revoke_grant.main]
    public static void revokeKeyGrant(KmsClient kmsClient, String keyId, String grantId) {
        try {
            RevokeGrantRequest grantRequest = RevokeGrantRequest.builder()
                .keyId(keyId)
                .grantId(grantId)
                .build();

            kmsClient.revokeGrant(grantRequest);
            System.out.println(grantId +" was successfully revoked!");

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_revoke_grant.main]

    // snippet-start:[kms.java2_list_grant.main]
    public static void displayGrantIds(KmsClient kmsClient, String keyId) {
        try {
            ListGrantsRequest grantsRequest = ListGrantsRequest.builder()
                .keyId(keyId)
                .limit(15)
                .build();

            ListGrantsResponse response = kmsClient.listGrants(grantsRequest);
            List<GrantListEntry> grants = response.grants();
            for (GrantListEntry grant : grants) {
                System.out.println("The grant Id is : " + grant.grantId());
            }

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_list_grant.main]

    // snippet-start:[kms.java2_create_grant.main]
    public static String grantKey(KmsClient kmsClient, String keyId, String granteePrincipal)  {
        try {
            // Add the desired KMS Grant permissions.
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

            CreateGrantResponse response = kmsClient.createGrant(grantRequest);
            return response.grantId();

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[kms.java2_create_grant.main]


    // snippet-start:[kms.java2._key_rotation.main]
    public static void enableKeyRotation(KmsClient kmsClient, String keyId) {
        try {
            EnableKeyRotationRequest enableKeyRotationRequest = EnableKeyRotationRequest.builder()
                .keyId(keyId)
                .build();

            kmsClient.enableKeyRotation(enableKeyRotationRequest);
            System.out.println("You have enabled key rotation for key "+keyId);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2._key_rotation.main]

    // snippet-start:[kms.java2._create_alias.main]
    public static void createCustomAlias(KmsClient kmsClient, String targetKeyId, String aliasName) {
        try {
            CreateAliasRequest aliasRequest = CreateAliasRequest.builder()
                .aliasName(aliasName)
                .targetKeyId(targetKeyId)
                .build();

            kmsClient.createAlias(aliasRequest);
        } catch (ResourceExistsException e) {
            System.err.println("Alias already exists: " + e.getMessage());
            System.err.println("Moving on...");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            System.err.println("Moving on...");
        }
    }
    // snippet-end:[kms.java2._create_alias.main]

    public static SdkBytes encryptData(KmsClient kmsClient, String keyId, String text) {
        try {
            SdkBytes myBytes = SdkBytes.fromUtf8String(text);
            EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(myBytes)
                .build();

            EncryptResponse response = kmsClient.encrypt(encryptRequest);
            String algorithm = response.encryptionAlgorithm().toString();
            System.out.println("The encryption algorithm is " + algorithm);

            // Get the encrypted data.
            SdkBytes encryptedData = response.ciphertextBlob();
            return encryptedData;

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    // snippet-start:[kms.java2_create_key.main]
    public static String createKey(KmsClient kmsClient, String keyDesc) {
        try {
            CreateKeyRequest keyRequest = CreateKeyRequest.builder()
                .description(keyDesc)
                .customerMasterKeySpec(CustomerMasterKeySpec.SYMMETRIC_DEFAULT)
                .keyUsage("ENCRYPT_DECRYPT")
                .build();

            CreateKeyResponse result = kmsClient.createKey(keyRequest);
            System.out.println("Created a customer key with ARN " +result.keyMetadata().arn());
            return result.keyMetadata().keyId();

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[kms.java2_create_key.main]

    // snippet-start:[kms.java2_enable_key.main]
    // Enable the customer key.
    public static void enableKey(KmsClient kmsClient, String keyId) {
        try {
            EnableKeyRequest enableKeyRequest = EnableKeyRequest.builder()
                .keyId(keyId)
                .build();

            kmsClient.enableKey(enableKeyRequest);

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_enable_key.main]
    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("Please enter 'c' to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                // Continue with the program
                System.out.println("Continuing with the program...");
                break; // Exit the loop
            } else {
                // Handle invalid input
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

}
// snippet-end:[kms.java2_scenario.main]