//snippet-sourcedescription:[DeleteObjects.java demonstrates how to delete multiple objects from an S3 bucket.]
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
// snippet-start:[s3.java.delete_objects.complete]
// snippet-start:[s3.java.delete_objects.import]
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;

import java.util.ArrayList;
import java.util.Arrays;

// snippet-end:[s3.java.delete_objects.import]
/**
 * Delete multiple objects from an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 *
 * ++ Warning ++ This code will actually delete the objects that you specify!
 */
// snippet-start:[s3.java.delete_objects.main]
public class DeleteObjects
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of an S3 bucket and at least\n" +
            "one object name (key) to delete.\n" +
            "\n" +
            "Ex: DeleteObjects <bucketname> <objectname1> [objectname2, ...]\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];
        String[] object_keys = Arrays.copyOfRange(args, 1, args.length);
        ArrayList<ObjectIdentifier> to_delete = new ArrayList<ObjectIdentifier>();

        System.out.println("Deleting objects from S3 bucket: " + bucket_name);
        for (String k : object_keys) {
            System.out.println(" * " + k);
            to_delete.add(ObjectIdentifier.builder().key(k).build());
        }

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();
        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucket_name)
                    .delete(Delete.builder().objects(to_delete).build())
                    .build();
            s3.deleteObjects(dor);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

// snippet-end:[s3.java.delete_objects.main]
// snippet-end:[s3.java.delete_objects.complete]
