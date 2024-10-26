// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[s3.java2.vpc.example.main]
// snippet-start:[s3.java2.vpc.example.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
// snippet-end:[s3.java2.vpc.example.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class VPCS3Example {
    public static void main(String[] args) throws URISyntaxException {
        final String usage = """

                Usage:
                    <bucketName> <vpcBucketURL>\s

                Where:
                    bucketName - The Amazon S3 bucket from which objects are read.\s
                    vpcBucketURL - The URL of the bucket located in your virtual private cloud (VPC) (for example,  https://bucket.vpxx-xxxxxc4d-5e6f.s3.us-east-1.vpce.amazonaws.com
                    """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String bucketName = args[0];
        String vpcBucketURL = args[1];
        URI myURI = new URI(vpcBucketURL);
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .endpointOverride(myURI)
                .region(region)
                .build();

        listBucketObjects(s3, bucketName);
        s3.close();
    }

    /**
     * Lists the objects in an Amazon S3 bucket.
     *
     * @param s3 an instance of the S3Client class, which is used to interact with the Amazon S3 service
     * @param bucketName the name of the S3 bucket to list the objects for
     */
    public static void listBucketObjects(S3Client s3, String bucketName) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object s3Object : objects) {
                System.out.print("\n The name of the key is " + s3Object.key());
                System.out.print("\n The object is " + convertBToKb(s3Object.size()) + " KBs");
                System.out.print("\n The owner is " + s3Object.owner());
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    // convert bytes to kbs.
    private static long convertBToKb(Long val) {
        return val / 1024;
    }
}
// snippet-end:[s3.java2.vpc.example.main]
