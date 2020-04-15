//snippet-sourcedescription:[CopyObject.java demonstrates how to copy an object from one Amazon S3 bucket to another]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
//snippet-sourceauthor:[scmacdon]

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
 * Copies an object from one Amazon S3 bucket to another.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CopyObject {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CopyObject <objectname> <frombucket>  <tobucket>\n\n" +
                "Where:\n" +
                "    objectname - the name of the object (i.e., book.pdf)\n\n" +
                "    frombucket - the bucket name that contains the object (i.e., bucket1)\n" +
                "    tobucket - the bucket to copy the object to (i.e., bucket2)\n" +
                "Example:\n" +
                "    book.pdf bucket1 bucket2 \n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String objectKey = args[0];
        String fromBucket = args[1];
        String toBucket = args[2];

        System.out.format("Copying object %s from bucket %s to %s\n",
                objectKey, fromBucket, toBucket);

        //Create the S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        CopyBucketObject (s3, fromBucket, objectKey, toBucket);
    }

    // snippet-start:[s3.java2.copy_object.main]
    public static String CopyBucketObject (S3Client s3, String fromBucket, String objectKey, String toBucket) {

        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(fromBucket + "/" + objectKey, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("URL could not be encoded: " + e.getMessage());
        }
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .copySource(encodedUrl)
                .bucket(toBucket)
                .key(objectKey)
                .build();

        try {
            CopyObjectResponse copyRes = s3.copyObject(copyReq);
            return copyRes.copyObjectResult().toString();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[s3.java2.copy_object.main]
        return "";
    }
}
