//snippet-sourcedescription:[CreateDataStore.java demonstrates how to create a data store in AWS HealthImaging.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.create_datastore.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.CreateDatastoreRequest;
import software.amazon.awssdk.services.medicalimaging.model.CreateDatastoreResponse;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
//snippet-end:[medicalimaging.java2.create_datastore.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateDatastore {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <dataStoreName>\n\n" +
                "Where:\n" +
                "    dataStoreName - The name for the AWS HealthImaging data store.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String dataStoreName = args[0];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String dataStoreId = createMedicalImageDatastore(medicalImagingClient, dataStoreName);
        System.out.println("The medical imaging data store id is " + dataStoreId);
        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.create_datastore.main]
    public static String createMedicalImageDatastore(MedicalImagingClient medicalImagingClient,
                                                     String datastoreName) {
        try {
            CreateDatastoreRequest datastoreRequest = CreateDatastoreRequest.builder()
                    .datastoreName(datastoreName)
                    .build();
            CreateDatastoreResponse response = medicalImagingClient.createDatastore(datastoreRequest);
            return response.datastoreId();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }
    //snippet-end:[medicalimaging.java2.create_datastore.main]
}