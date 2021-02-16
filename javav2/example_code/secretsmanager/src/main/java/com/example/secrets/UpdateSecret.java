//snippet-sourcedescription:[UpdateSecret.java demonstrates how to update a secret for AWS Secrets Manager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Secrets Manager]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/6/2020]
//snippet-sourceauthor:[scmacdon-AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.secrets;

//snippet-start:[secretsmanager.java2.update_secret.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretRequest;
//snippet-end:[secretsmanager.java2.update_secret.import]

/**
 * To run this AWS code example, ensure that you have setup your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 *https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UpdateSecret {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateSecret  <secretName> <secretValue>\n\n" +
                "Where:\n" +
                "    secretName - the name of the secret (for example, tutorials/MyFirstSecret). \n"+
                "    secretValue - the secret value that is updated. \n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String secretName = args[0];
        String secretValue = args[1];

        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();

        updateMySecret(secretsClient, secretName, secretValue);
        secretsClient.close();
    }

    //snippet-start:[secretsmanager.java2.update_secret.main]
    public static void updateMySecret(SecretsManagerClient secretsClient, String secretName, String secretValue) {

        try {
            UpdateSecretRequest secretRequest = UpdateSecretRequest.builder()
                .secretId(secretName)
                .secretString(secretValue)
                .build();

            secretsClient.updateSecret(secretRequest);
        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[secretsmanager.java2.update_secret.main]
}
