//snippet-sourcedescription:[GetDataStore.java demonstrates how to get a data store's pproperties in AWS HealthImaging.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.get_datastore.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DatastoreProperties;
import software.amazon.awssdk.services.medicalimaging.model.GetDatastoreRequest;
import software.amazon.awssdk.services.medicalimaging.model.GetDatastoreResponse;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
//snippet-end:[medicalimaging.java2.get_datastore.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDatastore {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreID>\n\n" +
                "Where:\n" +
                "    datastoreID - The ID for the AWS HealthImaging data store.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreID = args[0];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        DatastoreProperties properties = getMedicalImageDatastore(medicalImagingClient, datastoreID);
        if (properties != null) {
            System.out.println("The medical imaging data store properties are " + properties);
        }
        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.get_datastore.main]
    public static DatastoreProperties getMedicalImageDatastore(MedicalImagingClient medicalImagingClient,
                                                               String datastoreID) {
        try {
            GetDatastoreRequest datastoreRequest = GetDatastoreRequest.builder()
                    .datastoreId(datastoreID)
                    .build();
            GetDatastoreResponse response = medicalImagingClient.getDatastore(datastoreRequest);
            return response.datastoreProperties();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
    //snippet-end:[medicalimaging.java2.get_datastore.main]
}