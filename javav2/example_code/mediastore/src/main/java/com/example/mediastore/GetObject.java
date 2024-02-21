//snippet-sourcedescription:[GetObject.java demonstrates how to download a file from an AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[AWS Elemental MediaStore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediastore;

//snippet-start:[mediastore.java2.get_object.main]
//snippet-start:[mediastore.java2.get_object.import]
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient;
import software.amazon.awssdk.services.mediastoredata.model.GetObjectRequest;
import software.amazon.awssdk.services.mediastoredata.model.GetObjectResponse;
import software.amazon.awssdk.services.mediastoredata.model.MediaStoreDataException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
//snippet-end:[mediastore.java2.get_object.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetObject {
    public static void main(String[] args) throws URISyntaxException {
        final String usage = """

            Usage:    <completePath> <containerName> <savePath>

            Where:
               completePath - The path of the object in the container (for example, Videos5/sampleVideo.mp4).
               containerName - The name of the container.
            """;

      //  if (args.length != 3) {
      //      System.out.println(usage);
      //      System.exit(1);
      //  }

        String completePath = "folder/Rabbit1.mp4" ; // args[0];
        String containerName = "Videos"; //args[1];

        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));
        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
            .endpointOverride(uri)
            .region(region)
            .build();

        getMediaObject(mediaStoreData, completePath);
        mediaStoreData.close();
    }

    public static void getMediaObject(MediaStoreDataClient mediaStoreData, String completePath) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .path(completePath)
                .build();

            // Write out the data to a file.
            ResponseInputStream<GetObjectResponse> data = mediaStoreData.getObject(objectRequest);
            byte[] buffer = data.readAllBytes();
            // Print out the size of the byte array in KB.
            long sizeInKB = buffer.length / 1024;
            System.out.println("Size of the byte array: " + sizeInKB + " KB");

        } catch (MediaStoreDataException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static String getEndpoint(String containerName) {
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
}
//snippet-end:[mediastore.java2.get_object.main]
