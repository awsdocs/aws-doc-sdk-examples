// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.outposts;

// snippet-start:[s3-outposts.java2.create_endpoint.import]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointResponse;
import software.amazon.awssdk.services.s3control.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3control.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3outposts.S3OutpostsClient;
import software.amazon.awssdk.services.s3outposts.model.CreateEndpointResponse;
import software.amazon.awssdk.services.s3outposts.model.EndpointAccessType;

import java.io.File;
import java.util.Map;
// snippet-end:[s3-outposts.java2.create_endpoint.import]

public class CreateOutpostsEndPoint {
    private static final Logger logger = LoggerFactory.getLogger(CreateOutpostsEndPoint.class);

    public static void main(String[] args) {
        createOutpostsEndPoint();
    }

    // snippet-start:[s3-outposts.java2.create_endpoint]
    public static void createOutpostsEndPoint() {
        // Use an SDK S3OutpostsClient to create an S3 on Outposts endpoint.
        try (S3OutpostsClient s3OutpostsClient = S3OutpostsClient.create()) {
            try {
                CreateEndpointResponse response = s3OutpostsClient.createEndpoint(b -> b
                        .outpostId("op-<123456789abcdefgh>")
                        .subnetId("<subnet-id>")
                        .securityGroupId("<security-group-id")
                        .accessType(EndpointAccessType.CUSTOMER_OWNED_IP)
                        .customerOwnedIpv4Pool("<ipv4pool-coip-12345678901234567>"));
                logger.info("Endpoint point created with Arn: [{}]",  response.endpointArn());
            } catch (SdkException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    // snippet-end:[s3-outposts.java2.create_endpoint]
}
