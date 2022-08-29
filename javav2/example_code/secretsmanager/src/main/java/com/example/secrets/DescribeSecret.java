//snippet-sourcedescription:[DescribeSecret.java demonstrates how to describe a secret.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Secrets Manager]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.secrets;

//snippet-start:[secretsmanager.java2.describe_secret.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
//snippet-end:[secretsmanager.java2.describe_secret.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeSecret {

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

        describeGivenSecret(secretsClient, secretName);
        secretsClient.close();
    }

    //snippet-start:[secretsmanager.java2.describe_secret.main]
    public static void describeGivenSecret(SecretsManagerClient secretsClient, String secretName) {

        try {
            DescribeSecretRequest secretRequest = DescribeSecretRequest.builder()
                .secretId(secretName)
                .build();

            DescribeSecretResponse secretResponse = secretsClient.describeSecret(secretRequest);
            Instant lastChangedDate = secretResponse.lastChangedDate();

            // Convert the Instant to readable date.
            DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale( Locale.US)
                        .withZone( ZoneId.systemDefault() );

            formatter.format( lastChangedDate );
            System.out.println("The date of the last change to "+ secretResponse.name() +" is " + lastChangedDate );

        } catch (SecretsManagerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[secretsmanager.java2.describe_secret.main]
}
