//snippet-sourcedescription:[UpdateDomain.java demonstrates how modify a cluster configuration of the specified domain.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon OpenSearch Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.search;

// snippet-start:[opensearch.java2.update_domain.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import software.amazon.awssdk.services.opensearch.model.ClusterConfig;
import software.amazon.awssdk.services.opensearch.model.OpenSearchException;
import software.amazon.awssdk.services.opensearch.model.UpdateDomainConfigRequest;
import software.amazon.awssdk.services.opensearch.model.UpdateDomainConfigResponse;
// snippet-end:[opensearch.java2.update_domain.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateDomain {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <domainName>\n\n" +
            "Where:\n" +
            "    domainName - The name of the domain to update.\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String domainName = args[0];
        Region region = Region.US_EAST_1;
        OpenSearchClient searchClient = OpenSearchClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        updateSpecificDomain(searchClient, domainName);
        System.out.println("Done");
    }

    // snippet-start:[opensearch.java2.update_domain.main]
    public static void updateSpecificDomain(OpenSearchClient searchClient, String domainName ) {

        try {
            ClusterConfig clusterConfig = ClusterConfig.builder()
                .instanceCount(3)
                .build();

            UpdateDomainConfigRequest updateDomainConfigRequest = UpdateDomainConfigRequest.builder()
                .domainName(domainName)
                .clusterConfig(clusterConfig)
                .build();

            System.out.println("Sending domain update request...");
            UpdateDomainConfigResponse updateResponse = searchClient.updateDomainConfig(updateDomainConfigRequest);
            System.out.println("Domain update response from Amazon OpenSearch Service:");
            System.out.println(updateResponse.toString());

        } catch (OpenSearchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[opensearch.java2.update_domain.main]
}
