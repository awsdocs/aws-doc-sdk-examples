//snippet-sourcedescription:[GetSecretValue.java demonstrates how to get the value of a secret from AWS Secrets Manager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Secrets Manager]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.secrets;

//snippet-start:[secretsmanager.java2.get_secret.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
//snippet-end:[secretsmanager.java2.get_secret.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetSecretValue {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <secretName> \n\n" +
            "Where:\n" +
            "    secretName - The name of the secret (for example, tutorials/MyFirstSecret). \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String secretName = args[0];
        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getValue(secretsClient, secretName);
        secretsClient.close();
    }

    //snippet-start:[secretsmanager.java2.get_secret.main]
    public static void getValue(SecretsManagerClient secretsClient,String secretName) {

        try {
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

            GetSecretValueResponse valueResponse = secretsClient.getSecretValue(valueRequest);
            String secret = valueResponse.secretString();
            System.out.println(secret);

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[secretsmanager.java2.get_secret.main]
}
