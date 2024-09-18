// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iotsitewise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iotsitewise.IoTSiteWiseAsyncClient;
import software.amazon.awssdk.services.iotsitewise.model.AssetSummary;
import software.amazon.awssdk.services.iotsitewise.model.ListAssetsRequest;
import software.amazon.awssdk.services.iotsitewise.paginators.ListAssetsPublisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

// snippet-start:[iotsitewise.hello.main]
public class HelloSitewise {
    private static final Logger logger = LoggerFactory.getLogger(HelloSitewise.class);
    public static void main(String[] args) {
        final String usage = """
            Usage:
               <assetModelId>

            Where:
                assetModelId - The Id value of the asset model used in the IoT SiteWise program.
            """;

        if (args.length != 1) {
            logger.info(usage);
            return;
        }

        String assetModelId = args[0];
        IoTSiteWiseAsyncClient siteWiseAsyncClient = IoTSiteWiseAsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        fetchAssets(siteWiseAsyncClient, assetModelId);
    }

    /**
     * Fetches assets from AWS IoT SiteWise using the provided {@link IoTSiteWiseAsyncClient}.
     *
     * @param siteWiseAsyncClient the AWS IoT SiteWise asynchronous client to use for the request
     * @param modelId the ID of the asset model to fetch assets for
     */
    public static void fetchAssets(IoTSiteWiseAsyncClient siteWiseAsyncClient, String modelId) {
        ListAssetsRequest assetsRequest = ListAssetsRequest.builder()
            .maxResults(10)
            .assetModelId(modelId)
            .build();

        // Asynchronous paginator - process paginated results.
        ListAssetsPublisher listAssetsPaginator = siteWiseAsyncClient.listAssetsPaginator(assetsRequest);
        CompletableFuture<Void> future = listAssetsPaginator.subscribe(response -> {
            response.assetSummaries().forEach(assetSummary ->
                logger.info("Asset Name: {} ", assetSummary.name())
            );
        });

        // Wait for the asynchronous operation to complete
        future.join();
    }
}
// snippet-end:[iotsitewise.hello.main]