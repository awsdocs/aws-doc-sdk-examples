//snippet-sourcedescription:[DeleteSecret.java demonstrates how to delete a secret.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Secrets Manager]
//snippet-service:[AWS Secrets Manager]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/3/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.secrets;

//snippet-start:[secretsmanager.java2.delete_secret.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
//snippet-end:[secretsmanager.java2.delete_secret.import]

public class DeleteSecret {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the secret to delete (ie, tutorials/MyFirstSecret).  \n" +
                "\n" +
                "Ex: DeleteSecret <secretName>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String secretName = args[0];

        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();

        deleteSpecificSecret(secretsClient, secretName);
    }

    //snippet-start:[secretsmanager.java2.delete_secret.main]
    public static void deleteSpecificSecret(SecretsManagerClient secretsClient, String secretName) {

        try {
            DeleteSecretRequest secretRequest = DeleteSecretRequest.builder()
                .secretId(secretName)
                .build();

            secretsClient.deleteSecret(secretRequest);

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[secretsmanager.java2.delete_secret.main]
}
