// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.S3Utilities;

import java.net.URI;
import java.util.List;

class ParseUriTest {

    private final S3Client s3 = S3Client.create();

    @Test
    @Order(24)
    public void s3UriParsingTest() {
        String url = "https://s3.us-west-1.amazonaws.com/myBucket/resources/doc.txt?versionId=abc123&partNumber=77&partNumber=88";

        // Verify the example runs without error.
        ParseUri.parseS3UriExample(s3, url);

        // Directly verify S3Uri parsing results.
        S3Utilities s3Utilities = s3.utilities();
        S3Uri s3Uri = s3Utilities.parseUri(URI.create(url));

        Assertions.assertEquals(Region.US_WEST_1, s3Uri.region().orElse(null));
        Assertions.assertEquals("myBucket", s3Uri.bucket().orElse(null));
        Assertions.assertEquals("resources/doc.txt", s3Uri.key().orElse(null));
        Assertions.assertTrue(s3Uri.isPathStyle());
        Assertions.assertEquals("abc123", s3Uri.firstMatchingRawQueryParameter("versionId").orElse(null));
        Assertions.assertEquals("77", s3Uri.firstMatchingRawQueryParameter("partNumber").orElse(null));
        Assertions.assertEquals(List.of("77", "88"), s3Uri.firstMatchingRawQueryParameters("partNumber"));
    }
}
