//snippet-sourcedescription:[PutObject.java demonstrates how to upload a MP4 file to an AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Elemental MediaStore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.mediastore;

// snippet-start:[mediastore.java2.put_object.main]
// snippet-start:[mediastore.java2.put_object.import]
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.mediastoredata.model.PutObjectRequest;
import software.amazon.awssdk.services.mediastoredata.model.MediaStoreDataException;
import software.amazon.awssdk.services.mediastoredata.model.PutObjectResponse;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
// snippet-end:[mediastore.java2.put_object.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutObject {
    public static void main(String[] args) throws URISyntaxException {
        final String USAGE = """

                To run this example, supply the name of a container, a file in an S3 bucket, the S3 bucket, and 
                and path in the container\s

                Ex: <containerName> <file> <bucketName> <completePath>
                """;

       // if (args.length < 3) {
       //     System.out.println(USAGE);
       //     System.exit(1);
       // }

        String containerName = args[0];
        String file = args[1];
        String bucketName = args[2];
        String completePath = args[3];

        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));
        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
            .endpointOverride(uri)
            .region(region)
            .build();

        S3Client s3 = S3Client.builder()
            .region(region)
            .build();

        putMediaObject(mediaStoreData, s3, bucketName, file, completePath);
        mediaStoreData.close();
    }

    public static void putMediaObject(MediaStoreDataClient mediaStoreData, S3Client s3, String bucketName, String key, String completePath) {
        try {
            RequestBody requestBody = RequestBody.fromBytes(getObjectBytes(s3, bucketName, key));
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .path(completePath)
                .contentType("video/mp4")
                .build();

            PutObjectResponse response = mediaStoreData.putObject(objectRequest, requestBody);
            System.out.println("The saved object is " + response.storageClass().toString());

        } catch (MediaStoreDataException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static String getEndpoint(String containerName) {

        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
            .region(region)
            .build();

        DescribeContainerRequest containerRequest = DescribeContainerRequest.builder()
            .containerName(containerName)
            .build();

        DescribeContainerResponse response = mediaStoreClient.describeContainer(containerRequest);
        return response.container().endpoint();
    }

    public static byte[] getObjectBytes(S3Client s3, String bucketName, String key) {
        try {
            // Retrieve the object content from S3
            GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(key)
                .bucket(bucketName)
                .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            return data;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
}
// snippet-end:[mediastore.java2.put_object.main]