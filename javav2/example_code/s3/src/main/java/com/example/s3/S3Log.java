//snippet-sourcedescription:[S3log.java demonstrates how to log information.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.s3;

// snippet-start:[s3.java2.logging.complete]
// snippet-start:[s3.java2.logging.import]
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
// snippet-end:[s3.java2.logging.import]

// snippet-start:[s3.java2.logging.main]
public class S3Log {

    private static final Logger logger = LogManager.getLogger(S3Log.class);

    public static void main (String[] args) {
        System.out.println("testing logging setup for " + S3Log.class);

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();
        
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));

        logger.info("logging level info");
        logger.debug("logging debug stuff");
        logger.warn("logging warning");
        logger.error("logging error");
        logger.fatal("logging fatal");
    }
}
// snippet-end:[s3.java2.logging.main]
// snippet-end:[s3.java2.logging.complete]
