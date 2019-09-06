//snippet-sourcedescription:[AthenaClientFactory.java demonstrates how to create and configure an Amazon Athena client]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Athena]
//snippet-service:[athena]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-15]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[athena.java2.AthenaClientFactory.client]
//snippet-start:[athena.java.AthenaClientFactory.client]
package aws.example.athena;

//snippet-start:[athena.java2.AthenaClientFactory.client.import]
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.AthenaClientBuilder;
//snippet-end:[athena.java2.AthenaClientFactory.client.import]

/**
 * AthenaClientFactory
 * -------------------------------------
 * This code shows how to create and configure an Amazon Athena client.
 */
public class AthenaClientFactory {
    //snippet-start:[athena.java2.AthenaClientFactory.client.main]
    /**
     * AthenaClientClientBuilder to build Athena with the following properties:
     * - Set the region of the client
     * - Use the instance profile from the EC2 instance as the credentials provider
     * - Configure the client to increase the execution timeout.
     */
    private final AthenaClientBuilder builder = AthenaClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(InstanceProfileCredentialsProvider.create());

    public AthenaClient createClient() {
        return builder.build();
    }
    //snippet-end:[athena.java2.AthenaClientFactory.client.main]
}
//snippet-end:[athena.java.AthenaClientFactory.client]
//snippet-end:[athena.java2.AthenaClientFactory.client]