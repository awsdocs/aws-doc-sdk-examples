//snippet-sourcedescription:[DescribeSecret.java demonstrates how to describe a secret.]
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

//snippet-start:[secretsmanager.java2.describe_secret.import]
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
 * To run this AWS code example, ensure that you have setup your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 *https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeSecret {

    public static void main(String[] args) {

       final String USAGE = "\n" +
                "Usage:\n" +
                "    DescribeSecret  <secretName> \n\n" +
                "Where:\n" +
                "    secretName - the name of the secret (for example, tutorials/MyFirstSecret). \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String secretName = args[0];
        Region region = Region.US_EAST_1;
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
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

            // Convert the Instant to readable date
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
