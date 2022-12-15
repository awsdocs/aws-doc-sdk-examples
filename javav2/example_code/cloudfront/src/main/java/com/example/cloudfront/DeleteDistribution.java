//snippet-sourcedescription:[DeleteDistribution.java demonstrates how to disable an Amazon CloudFront distribution by updating the distribution. After the change is deployed, the code shows how to delete the distribution.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.deletedistribution.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.DeleteDistributionResponse;
import software.amazon.awssdk.services.cloudfront.model.DistributionConfig;
import software.amazon.awssdk.services.cloudfront.model.GetDistributionResponse;
import software.amazon.awssdk.services.cloudfront.waiters.CloudFrontWaiter;
// snippet-end:[cloudfront.java2.deletedistribution.import]

// snippet-start:[cloudfront.java2.deletedistribution.main]
public class DeleteDistribution {
    private static final Logger logger = LoggerFactory.getLogger(DeleteDistribution.class);

    public static void deleteDistribution(final CloudFrontClient cloudFrontClient, final String distributionId) {
        // First, disable the distribution by updating it.
        GetDistributionResponse response = cloudFrontClient.
                getDistribution(b -> b
                        .id(distributionId));
        String etag = response.eTag();
        DistributionConfig distConfig = response.distribution().distributionConfig();

        cloudFrontClient.updateDistribution(builder -> builder
                .id(distributionId)
                .distributionConfig(builder1 -> builder1
                        .cacheBehaviors(distConfig.cacheBehaviors())
                        .defaultCacheBehavior(distConfig.defaultCacheBehavior())
                        .enabled(false)
                        .origins(distConfig.origins())
                        .comment(distConfig.comment())
                        .callerReference(distConfig.callerReference())
                        .defaultCacheBehavior(distConfig.defaultCacheBehavior())
                        .priceClass(distConfig.priceClass())
                        .aliases(distConfig.aliases())
                        .logging(distConfig.logging())
                        .defaultRootObject(distConfig.defaultRootObject())
                        .customErrorResponses(distConfig.customErrorResponses())
                        .httpVersion(distConfig.httpVersion())
                        .isIPV6Enabled(distConfig.isIPV6Enabled())
                        .restrictions(distConfig.restrictions())
                        .viewerCertificate(distConfig.viewerCertificate())
                        .webACLId(distConfig.webACLId())
                        .originGroups(distConfig.originGroups()))
                .ifMatch(etag));

        logger.info("Distribution [{}] is DISABLED, waiting for deployment before deleting ...", distributionId);
        GetDistributionResponse distributionResponse;
        try (CloudFrontWaiter cfWaiter = CloudFrontWaiter.builder().client(cloudFrontClient).build()) {
            ResponseOrException<GetDistributionResponse> responseOrException =
                    cfWaiter.waitUntilDistributionDeployed(builder -> builder.id(distributionId)).matched();
            distributionResponse = responseOrException.response().orElseThrow(() -> new RuntimeException("Could not disable distribution"));
        }

        DeleteDistributionResponse deleteDistributionResponse = cloudFrontClient.deleteDistribution(builder -> builder
                .id(distributionId)
                .ifMatch(distributionResponse.eTag()));
        if ( deleteDistributionResponse.sdkHttpResponse().isSuccessful() ){
            logger.info("Distribution [{}] DELETED", distributionId);
        }
    }
}
// snippet-end:[cloudfront.java2.deletedistribution.main]
