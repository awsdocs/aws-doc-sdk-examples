//snippet-sourcedescription:[CreateOriginAccessControl.java demonstrates how to have Amazon CloudFront access Amazon Simple Storage Service (Amazon S3) resources by using signed requests.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createoriginaccesscontrol.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateOriginAccessControlResponse;
import software.amazon.awssdk.services.cloudfront.model.OriginAccessControlSigningBehaviors;
import software.amazon.awssdk.services.cloudfront.model.OriginAccessControlSigningProtocols;

import java.util.UUID;
// snippet-end:[cloudfront.java2.createoriginaccesscontrol.import]

// snippet-start:[cloudfront.java2.createoriginaccesscontrol.main]
public class CreateOriginAccessControl {
    private static final Logger logger = LoggerFactory.getLogger(CreateOriginAccessControl.class);

    public static String createOriginAccessControl(CloudFrontClient cloudFrontClient) {
        CreateOriginAccessControlResponse response = cloudFrontClient.createOriginAccessControl(b -> b
                .originAccessControlConfig(o -> o
                        .originAccessControlOriginType("s3")
                        .name("OACJava-" + UUID.randomUUID())
                        .signingBehavior(OriginAccessControlSigningBehaviors.ALWAYS)
                        .signingProtocol(OriginAccessControlSigningProtocols.SIGV4)));
        String originAccessControlId = response.originAccessControl().id();
        logger.info("Origin Access Control created. Id: [{}]", originAccessControlId);
        return originAccessControlId;

    }
}
// snippet-end:[cloudfront.java2.createoriginaccesscontrol.main]
