//snippet-sourcedescription:[ListObjects.java demonstrates how to list objects located in a given bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[scmacdon-aws]

package com.example.s3;

// snippet-start:[s3.java2.list_objects.complete]
// snippet-start:[s3.java2.list_objects.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.util.List;
import java.util.ListIterator;
// snippet-end:[s3.java2.list_objects.import]


public class ListObjects {

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Please specify a bucket name");
            System.exit(1);
        }

        // snippet-start:[s3.java2.list_objects.main]
       String bucketName = args[0];

        try {
            Region region = Region.US_WEST_2;
            S3Client s3 = S3Client.builder().region(region).build();

            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();

            for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
                S3Object myValue = (S3Object) iterVals.next();
                System.out.print("\n The name of the key is " + myValue.key());
                System.out.print("\n The object is " + calKb(myValue.size()) + " KBs");
                System.out.print("\n The owner is " + myValue.owner());
            }
        }
        catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    //convert bytes to kbs
    private static long calKb(Long val)
    {
        return val/1024;

    }
    // snippet-end:[s3.java2.list_objects.main]
}
// snippet-end:[s3.java2.list_objects.complete]
