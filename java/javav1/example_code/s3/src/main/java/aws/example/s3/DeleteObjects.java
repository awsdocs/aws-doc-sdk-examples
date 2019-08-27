//snippet-sourcedescription:[DeleteObjects.java demonstrates how to delete multiple objects in an S3 bucket.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[deleteObjects]
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
