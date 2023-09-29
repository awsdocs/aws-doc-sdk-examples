//snippet-sourcedescription:[DeleteDatastore.java demonstrates how to delete a data store in AWS HealthImaging.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.delete_datastore.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DeleteDatastoreRequest;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
//snippet-end:[medicalimaging.java2.delete_datastore.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteDatastore {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <dataStoreID>\n\n" +
                "Where:\n" +
                "    dataStoreID - The ID for the AWS HealthImaging data store to delete.\n\n";

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

        deleteMedicalImagingDatastore(medicalImagingClient, datastoreID);
        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.delete_datastore.main]
    public static void deleteMedicalImagingDatastore(MedicalImagingClient medicalImagingClient,
                                                     String datastoreID) {
        try {
            DeleteDatastoreRequest datastoreRequest = DeleteDatastoreRequest.builder()
                    .datastoreId(datastoreID)
                    .build();
            medicalImagingClient.deleteDatastore(datastoreRequest);
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[medicalimaging.java2.delete_datastore.main]
}