// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.secrets;

// snippet-start:[secretsmanager.java2.delete_secret.main]
// snippet-start:[secretsmanager.java2.delete_secret.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
// snippet-end:[secretsmanager.java2.delete_secret.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteSecret {
    public static void main(String[] args) {

        final String usage = """

                Usage:
                     <secretName>\s

                Where:
                    secretName - The name of the secret (for example, tutorials/MyFirstSecret).\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String secretName = args[0];
        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();

        deleteSpecificSecret(secretsClient, secretName);
        secretsClient.close();
    }

    public static void deleteSpecificSecret(SecretsManagerClient secretsClient, String secretName) {
        try {
            DeleteSecretRequest secretRequest = DeleteSecretRequest.builder()
                    .secretId(secretName)
                    .build();

            secretsClient.deleteSecret(secretRequest);
            System.out.println(secretName + " is deleted.");

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[secretsmanager.java2.delete_secret.main]
