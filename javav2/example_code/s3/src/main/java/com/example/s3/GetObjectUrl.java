// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetObjectUrl.java demonstrates how to get an URL for an object located in an Amazon Simple Storage Service (Amazon S3) bucket.]
///snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[01/06/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.s3;

// snippet-start:[s3.java2.getobjecturl.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.net.URL;
// snippet-end:[s3.java2.getobjecturl.import]

public class GetObjectUrl {


    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetObjectUrl <bucketName> <keyName> \n\n" +
                "Where:\n" +
                "    bucketName - the Amazon S3 bucket name.\n\n"+
                "    keyName - a key name that represents the object. \n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String keyName = args[1];
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        getURL(s3,bucketName,keyName);
        s3.close();
    }


    // snippet-start:[s3.java2.getobjecturl.main]
    public static void getURL(S3Client s3, String bucketName, String keyName ) {

        try {

            GetUrlRequest request = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            URL url = s3.utilities().getUrl(request);
            System.out.println("The URL for  "+keyName +" is "+url.toString());

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[s3.java2.getobjecturl.main]
}

