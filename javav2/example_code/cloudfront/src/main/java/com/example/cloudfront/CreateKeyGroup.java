//snippet-sourcedescription:[CreateKeyGroup.java demonstrates how to create a trusted key group for CloudFront that is used to verify signed URLs/cookies.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon CloudFront]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createkeygroup.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

import java.util.UUID;
// snippet-end:[cloudfront.java2.createkeygroup.import]

// snippet-start:[cloudfront.java2.createkeygroup.main]
public class CreateKeyGroup {
    private static final Logger logger = LoggerFactory.getLogger(CreateKeyGroup.class);

    public static String createKeyGroup(CloudFrontClient cloudFrontClient, String publicKeyId) {
        String keyGroupId = cloudFrontClient.createKeyGroup(b -> b.
                        keyGroupConfig(c -> c
                                .items(publicKeyId)
                                .name("JavaKeyGroup"+ UUID.randomUUID())))
                .keyGroup().id();
        logger.info("KeyGroup created with ID: [{}]", keyGroupId);
        return keyGroupId;
    }
}
// snippet-end:[cloudfront.java2.createkeygroup.main]
