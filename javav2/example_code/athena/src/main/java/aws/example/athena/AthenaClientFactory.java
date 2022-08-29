//snippet-sourcedescription:[AthenaClientFactory.java demonstrates how to create and configure an Amazon Athena client.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Athena]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
//snippet-start:[athena.java2.AthenaClientFactory.client]
//snippet-start:[athena.java.AthenaClientFactory.client]
package aws.example.athena;

//snippet-start:[athena.java2.AthenaClientFactory.client.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.AthenaClientBuilder;
//snippet-end:[athena.java2.AthenaClientFactory.client.import]

//snippet-start:[athena.java2.AthenaClientFactory.client.main]
public class AthenaClientFactory {

    private final AthenaClientBuilder builder = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create());

    public AthenaClient createClient() {
        return builder.build();
    }
    //snippet-end:[athena.java2.AthenaClientFactory.client.main]
}
//snippet-end:[athena.java.AthenaClientFactory.client]
//snippet-end:[athena.java2.AthenaClientFactory.client]