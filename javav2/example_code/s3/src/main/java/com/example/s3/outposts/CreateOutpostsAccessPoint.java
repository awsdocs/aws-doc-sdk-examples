// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.outposts;

// snippet-start:[s3-outposts.java2.create_accesspoint.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointResponse;
// snippet-end:[s3-outposts.java2.create_accesspoint.import]

public class CreateOutpostsAccessPoint {
    private static final Logger logger = LoggerFactory.getLogger(CreateOutpostsAccessPoint.class);

    public static void main(String[] args) {
        createAccessPoint();
    }

    // snippet-start:[s3-outposts.java2.create_accesspoint]
    public static void createAccessPoint() {
        try (S3ControlClient s3ControlClient = S3ControlClient.create()) {
            try {
                CreateAccessPointResponse response = s3ControlClient.createAccessPoint(b -> b
                        .bucket("arn:aws:s3-outposts:<region>:<accountId>:outpost/op-<123456789abcdefgh>/bucket/<bucketName>")
                        .accountId("<account-id>")
                        .name("<access-point-name>")
                        .vpcConfiguration(b1 -> b1.vpcId("<vpc-12345>")));
                logger.info("Access point created with Arn: [{}]", response.accessPointArn());
            } catch (SdkException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    // snippet-end:[s3-outposts.java2.create_accesspoint]
}

