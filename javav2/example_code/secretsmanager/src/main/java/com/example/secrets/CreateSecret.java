//snippet-sourcedescription:[CreateSecret.java demonstrates how to create a secret for AWS Secrets Manager.]
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

//snippet-start:[secretsmanager.java2.create_secret.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
//snippet-end:[secretsmanager.java2.create_secret.import]

public class CreateSecret {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of the secret (for example, tutorials/MyFirstSecret), and the secret value.  \n" +
                "\n" +
                "Ex: GetSecretValue <secretName><secretValue>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String secretName = args[0];
        String secretValue= args[1];

        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();

       String secretARN = createNewSecret(secretsClient, secretName, secretValue);
       System.out.println("The secret Amazon Resource Name (ARN) is "+ secretARN);
    }

    //snippet-start:[secretsmanager.java2.create_secret.main]
    public static String createNewSecret( SecretsManagerClient secretsClient, String secretName, String secretValue) {

        try {
            CreateSecretRequest secretRequest = CreateSecretRequest.builder()
                .name(secretName)
                .description("This secret was created by the AWS Secrets Manager Java API")
                .secretString(secretValue)
                .build();

            CreateSecretResponse secretResponse = secretsClient.createSecret(secretRequest);
            return secretResponse.arn();

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    //snippet-end:[secretsmanager.java2.create_secret.main]
}
