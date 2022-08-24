//snippet-sourcedescription:[SetKeyPolicy.java demonstrates how to set an AWS Key Management Service (AWS KMS) key policy.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Key Management Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kms;

// snippet-start:[kms.java2_set_policy.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.PutKeyPolicyRequest;
// snippet-end:[kms.java2_set_policy.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class SetKeyPolicy {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <keyId> <policyName> \n\n" +
            "Where:\n" +
            "    keyId - A unique identifier for the customer master key (CMK) (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" +
            "    policyName - The name of the key policy. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String keyId = args[0];
        String policyName = args[1];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createPolicy(kmsClient, keyId, policyName );
        kmsClient.close();
    }

    // snippet-start:[kms.java2_set_policy.main]
    public static void createPolicy(KmsClient kmsClient, String keyId, String policyName) {
        String policy = "{" +
            "  \"Version\": \"2012-10-17\"," +
            "  \"Statement\": [{" +
            "    \"Effect\": \"Allow\"," +
            // Replace the following user ARN with one for a real user.
            "    \"Principal\": {\"AWS\": \"arn:aws:iam::814548047983:root\"}," +
            "    \"Action\": \"kms:*\"," +
            "    \"Resource\": \"*\"" +
            "  }]" +
            "}";
        try {

            PutKeyPolicyRequest keyPolicyRequest = PutKeyPolicyRequest.builder()
                .keyId(keyId)
                .policyName(policyName)
                .policy(policy)
                .build();

            kmsClient.putKeyPolicy(keyPolicyRequest);
            System.out.println("Done");

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[kms.java2_set_policy.main]
}
