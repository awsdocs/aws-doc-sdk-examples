//snippet-sourcedescription:[CreateDistribution.java demonstrates how to create a CloudFront distribution that requires signed URLs/cookies to access resources.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createdistribution.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateDistributionResponse;
import software.amazon.awssdk.services.cloudfront.model.Distribution;
import software.amazon.awssdk.services.cloudfront.model.GetDistributionResponse;
import software.amazon.awssdk.services.cloudfront.model.ItemSelection;
import software.amazon.awssdk.services.cloudfront.model.Method;
import software.amazon.awssdk.services.cloudfront.model.ViewerProtocolPolicy;
import software.amazon.awssdk.services.cloudfront.waiters.CloudFrontWaiter;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Instant;
// snippet-end:[cloudfront.java2.createdistribution.import]

// snippet-start:[cloudfront.java2.createdistribution.main]
public class CreateDistribution {

    private static final Logger logger = LoggerFactory.getLogger(CreateDistribution.class);

    public static Distribution createDistribution(CloudFrontClient cloudFrontClient, S3Client s3Client,
                                                  final String bucketName, final String keyGroupId, final String originAccessControlId) {

        final String region = s3Client.headBucket(b -> b.bucket(bucketName)).sdkHttpResponse().headers().get("x-amz-bucket-region").get(0);
        final String originDomain = bucketName + ".s3." + region + ".amazonaws.com";
        String originId = originDomain; // use the originDomain value for the originId

        // Although some methods, such as DefaultCacheBehavior.Builder#minTTl and #forwardedValue, are deprecated, they are nevertheless required by the service API.
        CreateDistributionResponse createDistResponse = cloudFrontClient.createDistribution(builder -> builder
                .distributionConfig(b1 -> b1
                        .origins(b2 -> b2
                                .quantity(1)
                                .items(b3 -> b3
                                        .domainName(originDomain)
                                        .id(originId)
                                        .s3OriginConfig(builder4 -> builder4.originAccessIdentity(""))
                                        .originAccessControlId(originAccessControlId)))
                        .defaultCacheBehavior(b2 -> b2
                                .viewerProtocolPolicy(ViewerProtocolPolicy.ALLOW_ALL)
                                .targetOriginId(originId)
                                .minTTL(200L)
                                .forwardedValues(b5 -> b5
                                        .cookies(cp -> cp
                                                .forward(ItemSelection.NONE))
                                        .queryString(true))
                                .trustedKeyGroups(b3 -> b3
                                        .quantity(1)
                                        .items(keyGroupId)
                                        .enabled(true))
                                .allowedMethods(b4 -> b4
                                        .quantity(2)
                                        .items(Method.HEAD, Method.GET)
                                        .cachedMethods(b5 -> b5
                                                .quantity(2)
                                                .items(Method.HEAD, Method.GET))))
                        .cacheBehaviors(b -> b
                                .quantity(1)
                                .items(b2 -> b2
                                        .pathPattern("/index.html")
                                        .viewerProtocolPolicy(ViewerProtocolPolicy.ALLOW_ALL)
                                        .targetOriginId(originId)
                                        .trustedKeyGroups(b3 -> b3
                                                .quantity(1)
                                                .items(keyGroupId)
                                                .enabled(true))
                                        .minTTL(200L)
                                        .forwardedValues(b4 -> b4
                                                .cookies(cp -> cp
                                                        .forward(ItemSelection.NONE))
                                                .queryString(true))
                                        .allowedMethods(b5 -> b5.
                                                quantity(2).
                                                items(Method.HEAD, Method.GET)
                                                .cachedMethods(b6 -> b6
                                                        .quantity(2)
                                                        .items(Method.HEAD, Method.GET)))))
                        .enabled(true)
                        .comment("Distribution built with java")
                        .callerReference(Instant.now().toString())
                ));

        final Distribution distribution = createDistResponse.distribution();
        logger.info("Distribution created. DomainName: [{}]  Id: [{}]", distribution.domainName(), distribution.id());
        logger.info("Waiting for distribution to be deployed ...");
        try (CloudFrontWaiter cfWaiter = CloudFrontWaiter.builder().client(cloudFrontClient).build()) {
            ResponseOrException<GetDistributionResponse> responseOrException =
                    cfWaiter.waitUntilDistributionDeployed(builder -> builder.id(distribution.id())).matched();
            responseOrException.response().orElseThrow(() -> new RuntimeException("Distribution not created"));
            logger.info("Distribution deployed. DomainName: [{}]  Id: [{}]", distribution.domainName(), distribution.id());
        }
        return distribution;
    }
}
// snippet-end:[cloudfront.java2.createdistribution.main]