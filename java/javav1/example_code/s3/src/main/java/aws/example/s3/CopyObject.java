//snippet-sourcedescription:[CopyObject.java demonstrates how to copy an object from one bucket to another.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[copyObject]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-16]
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
package aws.example.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Copy an object from one Amazon S3 bucket to another.
 * 
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CopyObject {
    public static void main(String[] args) {
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
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            s3.copyObject(from_bucket, object_key, to_bucket, object_key);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
