//snippet-sourcedescription:[GeneratePresignedPutUrl.java demonstrates how to generatee a pre-signed PUT URL for  uploading a file to an Amazon S3 bucket.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[GeneratePresignedUrlRequest]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-11-01]
//snippet-sourceauthor:[walkerk1980]
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
package aws.example.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import java.net.URL;

/**
 * Generate a pre-signed PUT URL for uploading a file to an Amazon S3 bucket.
 * 
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class GeneratePresignedPutUrl {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "To run this example, supply the name of an S3 bucket and a file to\n" +
                "upload to it.\n" +
                "\n" +
                "Ex: GeneratePresignedPutUrl <bucketname> <filename>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];
        String key_name = args[1];

        System.out.format("Creating a pre-signed URL for uploading %s to S3 bucket %s...\n", key_name, bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

        // Set the pre-signed URL to expire after 12 hours.
        java.util.Date expiration = new java.util.Date();
        long expirationInMs = expiration.getTime();
        expirationInMs += 1000 * 60 * 60 * 12;
        expiration.setTime(expirationInMs);

        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket_name, key_name)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);
            URL url = s3.generatePresignedUrl(generatePresignedUrlRequest);
            //print URL
            System.out.println("\n\rGenerated URL: " + url.toString());
            //Print curl command to consume URL
            System.out.println("\n\rExample command to use URL for file upload: \n\r");
            System.out.println("curl --request PUT --upload-file /path/to/" + key_name + " '" + url.toString() + "' -# > /dev/null");
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}

