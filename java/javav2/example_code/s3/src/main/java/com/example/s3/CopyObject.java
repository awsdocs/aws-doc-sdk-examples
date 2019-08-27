//snippet-sourcedescription:[CopyObject.java demonstrates how to copy an object from one Amazon S3 bucket to another.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package com.example.s3;
// snippet-start:[s3.java2.copy_object.complete]
// snippet-start:[s3.java2.copy_object.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// snippet-end:[s3.java2.copy_object.import]
/**
 * Copy an object from one Amazon S3 bucket to another.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
// snippet-start:[s3.java2.copy_object.main]
public class CopyObject
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name (key) of an S3 object, the bucket name\n" +
            "that it's contained within, and the bucket to copy it to.\n" +
            "\n" +
            "Ex: CopyObject <objectname> <frombucket> <tobucket>\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String object_key = args[0];
        String from_bucket = args[1];
        String to_bucket = args[2];

        System.out.format("Copying object %s from bucket %s to %s\n",
                object_key, from_bucket, to_bucket);
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(from_bucket + "/" + object_key, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("URL could not be encoded: " + e.getMessage());
        }

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
        		.copySource(encodedUrl)
        		.bucket(to_bucket)
        		.key(object_key)
        		.build();

        try {
        	CopyObjectResponse copyRes = s3.copyObject(copyReq);
        	System.out.println(copyRes.copyObjectResult().toString());
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
 
// snippet-end:[s3.java2.copy_object.main]
// snippet-end:[s3.java2.copy_object.complete]
