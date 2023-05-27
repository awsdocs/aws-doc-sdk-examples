//snippet-sourcedescription:[PutSecret.java demonstrates how to create a new version with a new encrypted secret value and attaches it to the secret.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Secrets Manager]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.secrets;

//snippet-start:[secretsmanager.java2.put_secret.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
//snippet-end:[secretsmanager.java2.put_secret.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutSecret {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <secretName> <secretValue>\n\n" +
            "Where:\n" +
            "    secretName - The name of the secret (for example, tutorials/MyFirstSecret). \n"+
            "    secretValue - The text to encrypt and store in the new version of the secret. \n";

        if (args.length < 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String secretName = args[0];
        String secretValue = args[1];
        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        putSecret(secretsClient, secretName, secretValue);
        secretsClient.close();
    }

    //snippet-start:[secretsmanager.java2.put_secret.main]
    public static void putSecret(SecretsManagerClient secretsClient, String secretName, String secretValue) {
        try {
            PutSecretValueRequest secretRequest = PutSecretValueRequest.builder()
                .secretId(secretName)
                .secretString(secretValue)
                .build();

            secretsClient.putSecretValue(secretRequest);
            System.out.println("A new version was created.");

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[secretsmanager.java2.put_secret.main]
}
