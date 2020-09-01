//snippet-sourcedescription:[PutObject.java demonstrates how to upload a MP4 file to an AWS Elemental MediaStore container.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Elemental MediaStore]
//snippet-service:[AWS Elemental MediaStore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[9/1/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.mediastore;

//snippet-start:[mediastore.java2.put_object.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient ;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.mediastoredata.model.PutObjectRequest;
import software.amazon.awssdk.services.mediastoredata.model.MediaStoreDataException;
import software.amazon.awssdk.services.mediastoredata.model.PutObjectResponse;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
//snippet-end:[mediastore.java2.put_object.import]

public class PutObject {

    public static void main(String[] args) throws URISyntaxException {

        final String USAGE = "\n" +
                "To run this example, supply the name of a container, a file location to use, and path in the container \n" +
               "\n" +
                "Ex: PutObject <containerName><filePath><completePath>\n";

         if (args.length < 3) {
             System.out.println(USAGE);
             System.exit(1);
        }

        /* Read the name from command args */
        String containerName = args[0];
        String filePath = args[1];
        String completePath = args[2];

        Region region = Region.US_EAST_1;
        URI uri = new URI(getEndpoint(containerName));

        MediaStoreDataClient mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();

        putMediaObject(mediaStoreData, filePath, completePath);
    }

    //snippet-start:[mediastore.java2.put_object.main]
    public static void putMediaObject(MediaStoreDataClient mediaStoreData, String filePath, String completePath) {

        try{
            File myFile = new File(filePath);
            RequestBody requestBody = RequestBody.fromFile(myFile);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .path(completePath)
                 .contentType("video/mp4")
                .build();

            PutObjectResponse response = mediaStoreData.putObject(objectRequest, requestBody );
            System.out.println("The saved object is " +response.storageClass().toString());

    } catch (MediaStoreDataException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
  }
    //snippet-end:[mediastore.java2.put_object.main]

  public static String getEndpoint(String containerName){

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
