//snippet-sourcedescription:[GetObject.java demonstrates how to download a file from an AWS Elemental MediaStore container.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.mediastore;

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

public class GetObject {

    public static void main(String[] args) throws URISyntaxException {

        final String USAGE = "\n" +
                "Usage: " +
                "GetObject  <completePath> <containerName> <savePath>\n\n" +
                "Where:\n" +
                "  completePath - the path of the object in the container (for example, Videos5/sampleVideo.mp4).\n"+
                "  containerName - the name of the container.\n"+
                "  savePath - the path on the local drive where the file is saved, including the file name (for example, C:/AWS/myvid.mp4).\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String completePath = args[0];
        String containerName = args[1];
        String savePath = args[2];

        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));

        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();

        getMediaObject(mediaStoreData, completePath, savePath);
        mediaStoreData.close();
    }

    //snippet-start:[mediastore.java2.get_object.main]
    public static void getMediaObject(MediaStoreDataClient mediaStoreData, String completePath, String savePath) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .path(completePath)
                .build();

            // Write out the data to a file.
            ResponseInputStream<GetObjectResponse> data=  mediaStoreData.getObject(objectRequest);
            byte[] buffer = new byte[data.available()];
            data.read(buffer);

            File targetFile = new File(savePath);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            System.out.println("The data was written to "+savePath);

        } catch (MediaStoreDataException | IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[mediastore.java2.get_object.main]

    private static String getEndpoint(String containerName){

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
