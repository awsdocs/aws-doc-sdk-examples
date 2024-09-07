// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.search.scenario;

// snippet-start:[opensearch.java2.actions.main]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchAsyncClient;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OpenSearchActions {

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchActions.class);
    private static OpenSearchAsyncClient openSearchClientAsyncClient;

    private static OpenSearchAsyncClient getAsyncClient() {
        if (openSearchClientAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryPolicy(RetryPolicy.builder()
                    .numRetries(3)
                    .build())
                .build();

            openSearchClientAsyncClient = OpenSearchAsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return openSearchClientAsyncClient;
    }

    private static OpenSearchClient getClient() {
        return OpenSearchClient.builder()
            .build();
    }

    // snippet-start:[opensearch.java2.create_domain.main]
    /**
     * Creates a new OpenSearch domain asynchronously.
     *
     * @param domainName the name of the new OpenSearch domain to create
     * @return a {@link CompletableFuture} containing the domain ID of the newly created domain
     */
    public CompletableFuture<String> createNewDomainAsync(String domainName) {
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
        return getAsyncClient().createDomain(domainRequest)
            .thenApply(createResponse -> {
                System.out.println("Domain status is " + createResponse.domainStatus().toString());
                System.out.println("Domain Id is " + createResponse.domainStatus().domainId());
                return createResponse.domainStatus().domainId();
            })
            .exceptionally(ex -> {
                throw new RuntimeException("Failed to create domain", ex);
            });
    }

    // snippet-end:[opensearch.java2.create_domain.main]

    // snippet-start:[opensearch.java2.delete_domain.main]
    /**
     * Deletes a specific domain asynchronously.
     *
     * @param domainName the name of the domain to be deleted
     * @return a {@link CompletableFuture} that completes when the domain has been deleted
     *         or throws a {@link RuntimeException} if the deletion fails
     */
    public CompletableFuture<Void> deleteSpecificDomainAsync(String domainName) {
        DeleteDomainRequest domainRequest = DeleteDomainRequest.builder()
            .domainName(domainName)
            .build();

        // Delete domain asynchronously
        return getAsyncClient().deleteDomain(domainRequest)
            .thenRun(() -> {
            })
            .exceptionally(ex -> {
                throw new RuntimeException("Failed to delete the domain: " + domainName, ex);
            });
    }
    // snippet-end:[opensearch.java2.delete_domain.main]

    // snippet-start:[opensearch.java2.describe_domain.main]
    /**
     * Describes the specified domain asynchronously.
     *
     * @param domainName the name of the domain to describe
     * @return a {@link CompletableFuture} that completes with the ARN of the domain
     * @throws RuntimeException if the domain description fails
     */
    public CompletableFuture<String> describeDomainAsync(String domainName) {
        DescribeDomainRequest request = DescribeDomainRequest.builder()
            .domainName(domainName)
            .build();

        return getAsyncClient().describeDomain(request)
            .thenApply(response -> {
                DomainStatus domainStatus = response.domainStatus();
                String endpoint = domainStatus.endpoint();
                String arn = domainStatus.arn();
                String engineVersion = domainStatus.engineVersion();
                System.out.println("Domain endpoint is: " + endpoint);
                System.out.println("ARN: " + arn);
                System.out.println("Engine version: " + engineVersion);

                return arn;
            })
            .exceptionally(ex -> {
                throw new RuntimeException("Failed to describe domain", ex);
            });
    }
    // snippet-end:[opensearch.java2.describe_domain.main]

    // snippet-start:[opensearch.java2.list_domains.main]
    public CompletableFuture<List<DomainInfo>> listAllDomainsAsync() {
        ListDomainNamesRequest namesRequest = ListDomainNamesRequest.builder()
            .engineType("OpenSearch")
            .build();

        return getAsyncClient().listDomainNames(namesRequest)
            .thenApply(ListDomainNamesResponse::domainNames)
            .exceptionally(ex -> {
                throw new RuntimeException("Failed to list all domains", ex);
            });
    }
    // snippet-end:[opensearch.java2.list_domains.main]

    // snippet-start:[opensearch.java2.update_domain.main]
    /**
     * Updates the configuration of a specific domain asynchronously.
     *
     * @param domainName the name of the domain to update
     * @return a {@link CompletableFuture} that represents the asynchronous operation of updating the domain configuration
     */
    public CompletableFuture<UpdateDomainConfigResponse> updateSpecificDomainAsync(String domainName) {
        ClusterConfig clusterConfig = ClusterConfig.builder()
            .instanceCount(3)
            .build();

        UpdateDomainConfigRequest updateDomainConfigRequest = UpdateDomainConfigRequest.builder()
            .domainName(domainName)
            .clusterConfig(clusterConfig)
            .build();

        return getAsyncClient().updateDomainConfig(updateDomainConfigRequest)
            .exceptionally(ex -> {
                throw new RuntimeException("Failed to update the domain configuration", ex);
            });
    }
    // snippet-end:[opensearch.java2.update_domain.main]

    // snippet-start:[opensearch.java2.change_process.main]
    /**
     * Asynchronously checks the progress of a domain change operation in Amazon OpenSearch Service.
     *
     * @param domainName the name of the OpenSearch domain to check the progress for
     * @return a {@link CompletableFuture} that completes when the domain change operation is completed
     */
    public CompletableFuture<Void> domainChangeProgressAsync(String domainName) {
        DescribeDomainChangeProgressRequest request = DescribeDomainChangeProgressRequest.builder()
            .domainName(domainName)
            .build();

        return CompletableFuture.runAsync(() -> {
            boolean isCompleted = false;
            long startTime = System.currentTimeMillis();
            while (!isCompleted) {
                try {
                    // Check the progress
                    DescribeDomainChangeProgressResponse response = getAsyncClient().describeDomainChangeProgress(request).join();
                    String state = response.changeProgressStatus().statusAsString();  // Get the status as string

                    if ("COMPLETED".equals(state)) {
                        System.out.println("\nOpenSearch domain status: Completed");
                        isCompleted = true;
                    } else {
                        for (int i = 0; i < 5; i++) { // Wait for 5 seconds (checking every second)
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
                    throw new RuntimeException("Failed to check domain progress", e);
                }
            }
        });
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

