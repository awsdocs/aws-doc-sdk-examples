// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;

import java.util.Arrays;

/**
 * Delete multiple objects from an Amazon S3 bucket.
 * 
 * This code expects that you have AWS credentials set up per:
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
                "Ex: DeleteObjects <bucketname> <objectname1> [objectname2, ...]\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];
        String[] object_keys = Arrays.copyOfRange(args, 1, args.length);

        System.out.println("Deleting objects from S3 bucket: " + bucket_name);
        for (String k : object_keys) {
            System.out.println(" * " + k);
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucket_name)
                    .withKeys(object_keys);
            s3.deleteObjects(dor);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
