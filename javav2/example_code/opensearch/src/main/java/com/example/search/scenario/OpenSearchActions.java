// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.search.scenario;

// snippet-start:[opensearch.java2.actions.main]
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import software.amazon.awssdk.services.opensearch.model.AddTagsRequest;
import software.amazon.awssdk.services.opensearch.model.ClusterConfig;
import software.amazon.awssdk.services.opensearch.model.CreateDomainRequest;
import software.amazon.awssdk.services.opensearch.model.CreateDomainResponse;
import software.amazon.awssdk.services.opensearch.model.DeleteDomainRequest;
import software.amazon.awssdk.services.opensearch.model.DescribeDomainChangeProgressRequest;
import software.amazon.awssdk.services.opensearch.model.DescribeDomainChangeProgressResponse;
import software.amazon.awssdk.services.opensearch.model.DescribeDomainRequest;
import software.amazon.awssdk.services.opensearch.model.DescribeDomainResponse;
import software.amazon.awssdk.services.opensearch.model.DomainInfo;
import software.amazon.awssdk.services.opensearch.model.DomainStatus;
import software.amazon.awssdk.services.opensearch.model.EBSOptions;
import software.amazon.awssdk.services.opensearch.model.GetUpgradeHistoryRequest;
import software.amazon.awssdk.services.opensearch.model.GetUpgradeHistoryResponse;
import software.amazon.awssdk.services.opensearch.model.ListDomainNamesRequest;
import software.amazon.awssdk.services.opensearch.model.ListDomainNamesResponse;
import software.amazon.awssdk.services.opensearch.model.ListTagsRequest;
import software.amazon.awssdk.services.opensearch.model.ListTagsResponse;
import software.amazon.awssdk.services.opensearch.model.NodeToNodeEncryptionOptions;
import software.amazon.awssdk.services.opensearch.model.OpenSearchException;
import software.amazon.awssdk.services.opensearch.model.Tag;
import software.amazon.awssdk.services.opensearch.model.UpdateDomainConfigRequest;
import software.amazon.awssdk.services.opensearch.model.UpdateDomainConfigResponse;
import software.amazon.awssdk.services.opensearch.model.VolumeType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenSearchActions {

    private static OpenSearchClient getClient() {
        return OpenSearchClient.builder()
            .build();
    }

    // snippet-start:[opensearch.java2.create_domain.main]
    public String createNewDomain(String domainName) {
        try {
            ClusterConfig clusterConfig = ClusterConfig.builder()
                .dedicatedMasterEnabled(true)
                .dedicatedMasterCount(3)
                .dedicatedMasterType("t2.small.search")
                .instanceType("t2.small.search")
                .instanceCount(5)
                .build();

            EBSOptions ebsOptions = EBSOptions.builder()
                .ebsEnabled(true)
                .volumeSize(10)
                .volumeType(VolumeType.GP2)
                .build();

            NodeToNodeEncryptionOptions encryptionOptions = NodeToNodeEncryptionOptions.builder()
                .enabled(true)
                .build();

            CreateDomainRequest domainRequest = CreateDomainRequest.builder()
                .domainName(domainName)
                .engineVersion("OpenSearch_1.0")
                .clusterConfig(clusterConfig)
                .ebsOptions(ebsOptions)
                .nodeToNodeEncryptionOptions(encryptionOptions)
                .build();

            System.out.println("Sending domain creation request...");
            CreateDomainResponse createResponse = getClient().createDomain(domainRequest);
            System.out.println("Domain status is " + createResponse.domainStatus().toString());
            System.out.println("Domain Id is " + createResponse.domainStatus().domainId());
            return createResponse.domainStatus().domainId();

        } catch (OpenSearchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[opensearch.java2.create_domain.main]

    // snippet-start:[opensearch.java2.delete_domain.main]
    public void deleteSpecificDomain(String domainName) {
        try {
            DeleteDomainRequest domainRequest = DeleteDomainRequest.builder()
                .domainName(domainName)
                .build();

            getClient().deleteDomain(domainRequest);
            System.out.println(domainName + " was successfully deleted.");

        } catch (OpenSearchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[opensearch.java2.delete_domain.main]

    // snippet-start:[opensearch.java2.describe_domain.main]
    public String describeDomain(String domainName) {
        DescribeDomainRequest request = DescribeDomainRequest.builder()
            .domainName(domainName)
            .build();

        DescribeDomainResponse response = getClient().describeDomain(request);
        DomainStatus domainStatus = response.domainStatus();

        // Extract additional details about the domain
        String endpoint = domainStatus.endpoint();
        String arn = domainStatus.arn();
        String engineVersion = domainStatus.engineVersion();


        System.out.println("Domain endpoint is: " + endpoint);
        System.out.println("ARN: " + arn);
        System.out.println("Engine version "+engineVersion);
        return arn;
    }
    // snippet-end:[opensearch.java2.describe_domain.main]

    // snippet-start:[opensearch.java2.list_domains.main]
    public static void listAllDomains() {
        try {
            ListDomainNamesRequest namesRequest = ListDomainNamesRequest.builder()
                .engineType("OpenSearch")
                .build();

            ListDomainNamesResponse response = getClient().listDomainNames(namesRequest);
            List<DomainInfo> domainInfoList = response.domainNames();
            for (DomainInfo domain : domainInfoList)
                System.out.println("Domain name is " + domain.domainName());

        } catch (OpenSearchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[opensearch.java2.list_domains.main]

    // snippet-start:[opensearch.java2.update_domain.main]
    public static void updateSpecificDomain(String domainName) {
        try {
            ClusterConfig clusterConfig = ClusterConfig.builder()
                .instanceCount(3)
                .build();

            UpdateDomainConfigRequest updateDomainConfigRequest = UpdateDomainConfigRequest.builder()
                .domainName(domainName)
                .clusterConfig(clusterConfig)
                .build();

            System.out.println("Sending domain update request...");
            UpdateDomainConfigResponse updateResponse = getClient().updateDomainConfig(updateDomainConfigRequest);
            System.out.println("Domain update response from Amazon OpenSearch Service:");
            System.out.println(updateResponse.toString());

        } catch (OpenSearchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[opensearch.java2.update_domain.main]

    // snippet-start:[opensearch.java2.change_process.main]
    public void domainChangeProgress(String domainName) {
        boolean isCompleted = false;
        long startTime = System.currentTimeMillis();

        while (!isCompleted) {
            DescribeDomainChangeProgressRequest domainsRequest = DescribeDomainChangeProgressRequest.builder()
                .domainName(domainName)
                .build();

            try {
                // Make the request to check the progress
                DescribeDomainChangeProgressResponse response = getClient().describeDomainChangeProgress(domainsRequest);
                // Get the progress status
                String state = response.changeProgressStatus().statusAsString();  // Status as string

                // Check if status is COMPLETED
                if ("COMPLETED".equals(state)) {
                    System.out.println("\nOpenSearch domain status: Completed");
                    isCompleted = true;
                } else {
                    // Update the clock every second while waiting for the next API call
                    for (int i = 0; i < 5; i++) { // Wait for 5 seconds total (check again every second)
                        long elapsedTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000;
                        String formattedTime = String.format("%02d:%02d", elapsedTimeInSeconds / 60, elapsedTimeInSeconds % 60);
                        System.out.print("\rOpenSearch domain state: " + state + " | Time Elapsed: " + formattedTime);
                        Thread.sleep(1_000); // Sleep for 1 second
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted", e);
            } catch (Exception e) {
                // Handle other exceptions (e.g., AWS SDK exceptions)
                throw new RuntimeException("Failed to check domain progress", e);
            }
        }
    }
    // snippet-end:[opensearch.java2.change_process.main]

    // snippet-start:[opensearch.java2.add_tags.main]
    public void addDomainTags(String domainARN) {
        Tag tag1 = Tag.builder()
            .key("service")
            .value("OpenSearch")
            .build();

        Tag tag2 = Tag.builder()
            .key("instances")
            .value("m3.2xlarge")
            .build();

        List<Tag> tagList = new ArrayList<>();
        tagList.add(tag1);
        tagList.add(tag2);

        AddTagsRequest addTagsRequest = AddTagsRequest.builder()
            .arn(domainARN)
            .tagList(tagList)
            .build();

        getClient().addTags(addTagsRequest);
        System.out.println("Successfully added tags to the domain");
    }
    // snippet-end:[opensearch.java2.add_tags.main]

    // snippet-start:[opensearch.java2.list_tags.main]
    public void listDomainTags(String arn) {
        ListTagsRequest tagsRequest = ListTagsRequest.builder()
            .arn(arn)
            .build();

        ListTagsResponse response = getClient().listTags(tagsRequest);
        List<Tag> tagList = response.tagList();
        for (Tag tag : tagList) {
            System.out.println("Tag key is "+tag.key());
            System.out.println("Tag value is "+tag.value());
        }
    }
    // snippet-end:[opensearch.java2.list_tags.main]
}
// snippet-end:[opensearch.java2.actions.main]

