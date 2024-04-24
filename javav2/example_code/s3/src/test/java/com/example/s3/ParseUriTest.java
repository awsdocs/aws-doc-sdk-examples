// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import com.example.s3.util.MemoryLog4jAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParseUriTest {

    private final S3Client s3 = S3Client.create();

    @Test
    @Order(24)
    public void s3UriParsingTest(){
        String url = "https://s3.us-west-1.amazonaws.com/myBucket/resources/doc.txt?versionId=abc123&partNumber=77&partNumber=88";
        ParseUri.parseS3UriExample(s3,url);
        final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final Configuration configuration = context.getConfiguration();
        final MemoryLog4jAppender memoryLog4jAppender = (MemoryLog4jAppender) configuration.getAppender("MemoryLog4jAppender");
        final Map<String, String> eventMap = memoryLog4jAppender.getEventMap();

        Assertions.assertTrue(() -> eventMap.get("region").equals("us-west-1"));
        Assertions.assertTrue(() -> eventMap.get("bucket").equals("myBucket"));
        Assertions.assertTrue(() -> eventMap.get("key").equals("resources/doc.txt"));
        Assertions.assertTrue(() -> eventMap.get("isPathStyle").equals("true"));
        Assertions.assertTrue(() -> eventMap.get("rawQueryParameters").equals("{versionId=[abc123], partNumber=[77, 88]}"));
        Assertions.assertTrue(() -> eventMap.get("firstMatchingRawQueryParameter-versionId").equals("abc123"));
        Assertions.assertTrue(() -> eventMap.get("firstMatchingRawQueryParameter-partNumber").equals("77"));
        Assertions.assertTrue(() -> eventMap.get("firstMatchingRawQueryParameter").equals("[77, 88]"));
    }
}

