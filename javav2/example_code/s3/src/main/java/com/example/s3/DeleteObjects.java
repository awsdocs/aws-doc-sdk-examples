//snippet-sourcedescription:[DeleteObjects.java demonstrates how to delete multiple objects from an Amazon S3 bucket]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
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

// snippet-start:[s3.java2.delete_objects.import]
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import java.util.ArrayList;
import java.util.Arrays;
// snippet-end:[s3.java2.delete_objects.import]

/**
 * Delete multiple objects from an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 *
 * ++ Warning ++ This code will actually delete the objects that you specify!
 */

public class DeleteObjects {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "To run this example, supply the name of an S3 bucket and at least\n" +
                "one object name (key) to delete.\n" +
                "\n" +
                "Ex: DeleteObjects <bucketname> <objectname>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectName = args[1];

        System.out.println("Deleting an object from the S3 bucket: " + bucketName);

        // Create a S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        DeleteBucketObjects(s3, bucketName, objectName);
    }

    // snippet-start:[s3.java2.delete_objects.main]
    public static void DeleteBucketObjects(S3Client s3, String bucketName, String objectName) {

        // Create a S3Client object
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<ObjectIdentifier>();
        toDelete.add(ObjectIdentifier.builder().key(objectName).build());

        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();
            s3.deleteObjects(dor);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
// snippet-end:[s3.java2.delete_objects.main]
