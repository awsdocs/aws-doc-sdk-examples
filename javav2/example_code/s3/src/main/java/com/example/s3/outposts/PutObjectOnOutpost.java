// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.outposts;

// snippet-start:[s3-outposts.java2.put_object.imports]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.util.Map;
// snippet-end:[s3-outposts.java2.put_object.imports]

public class PutObjectOnOutpost {
    private static final Logger logger = LoggerFactory.getLogger(PutObjectOnOutpost.class);

    public static void main(String[] args) {
        putObjectOnOutpost();
    }

    // snippet-start:[s3-outposts.java2.put_object]
    public static void putObjectOnOutpost() {
        String accessPointArn = "arn:aws:s3-outposts:<region>:<accountId>:outpost/op-<123456789abcdefgh>/bucket/<bucketName>";
        String stringObjKeyName = "<test-object-key>";
        String fileObjKeyName = "<file-object-key>";
        String fileName = "<path-to-local-file>";

        try (S3Client s3Client = S3Client.builder().useArnRegion(true).build()) {
            try {
                // Upload a text string as a new object.
                PutObjectResponse response = s3Client.putObject(b -> b
                                .bucket(accessPointArn)
                                .key(stringObjKeyName),
                        RequestBody.fromString("Uploaded String Object"));

                logger.info("Uploaded a text string as new object, ETag is: [{}]", response.eTag());

                // Upload a file as a new object with ContentType and title specified.
                response = s3Client.putObject(b -> b
                                .bucket(accessPointArn)
                                .key(fileObjKeyName)
                                .contentType("plain/text")
                                .metadata(Map.of("title", "someTitle")),
                        new File(fileName).toPath());

                logger.info("Uploaded a file as a new object with ContentType and title specified, ETag is: [{}]", response.eTag());
            } catch (SdkException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    // snippet-end:[s3-outposts.java2.put_object]
}
