//snippet-sourcedescription:[CreateAccessPoint.java demonstrates how to create an access point for an Amazon S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/28/2020]
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

// snippet-start:[s3.java2.create_access_point.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;
// snippet-end:[s3.java2.create_access_point.import]

public class CreateAccessPoint {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    CopyObject <accountId><bucketName> <name>\n\n" +
                "Where:\n" +
                "    accountId - the account id that owns the S3 bucket \n\n" +
                "    bucketName - the bucket name \n" +
                "    name - the name of the access point \n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args*/
        String accountId = "814548047983"; //args[0];
        String bucketName = "buckettestsept"; //args[1];
        String name = "example-ap3";  //args[2];

        //Create the S3Client object
        Region region = Region.US_EAST_1;

        S3ControlClient s3ControlClient = S3ControlClient.builder()
                .region(region)
                .build();

        createSpecificAccessPoint(s3ControlClient, accountId, bucketName, name );
    }

    // snippet-start:[s3.java2.create_access_point.main]
    public static void createSpecificAccessPoint(S3ControlClient s3ControlClient,
                                                 String accountId,
                                                 String bucketName,
                                                 String name ) {


        try {
            CreateAccessPointRequest accessPointRequest = CreateAccessPointRequest.builder()
                .accountId(accountId)
                .bucket(bucketName)
                .name(name)
                .build();

            s3ControlClient.createAccessPoint(accessPointRequest);
            System.out.println("The Access Point was created" );

        } catch (S3ControlException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[s3.java2.create_access_point.main]
}
