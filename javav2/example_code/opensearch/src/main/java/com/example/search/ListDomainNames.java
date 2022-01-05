//snippet-sourcedescription:[ListDomainNames.java demonstrates how to list Amazon OpenSearch Service domains.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[09/27/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.search;

// snippet-start:[opensearch.java2.list_domains.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import software.amazon.awssdk.services.opensearch.model.DomainInfo;
import software.amazon.awssdk.services.opensearch.model.ListDomainNamesRequest;
import software.amazon.awssdk.services.opensearch.model.ListDomainNamesResponse;
import software.amazon.awssdk.services.opensearch.model.OpenSearchException;
// snippet-end:[opensearch.java2.list_domains.import]

import java.util.List;


public class ListDomainNames {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        OpenSearchClient searchClient = OpenSearchClient.builder()
                .region(region)
                .build();
        listAllDomains(searchClient);
        System.out.println("Done");
    }

    // snippet-start:[opensearch.java2.list_domains.main]
    public static void listAllDomains(OpenSearchClient searchClient){

        try {
            ListDomainNamesRequest namesRequest = ListDomainNamesRequest.builder()
                    .engineType("OpenSearch")
                    .build();

            ListDomainNamesResponse response = searchClient.listDomainNames(namesRequest) ;
            List<DomainInfo> domainInfoList = response.domainNames();
            for (DomainInfo domain: domainInfoList)
                System.out.println("Domain name is "+domain.domainName());

        } catch (OpenSearchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
   }
    // snippet-end:[opensearch.java2.list_domains.main]
}
