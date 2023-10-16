//snippet-sourcedescription:[ListDataStore.java demonstrates how to list the datastores in an AWS HealthImaging account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.list_datastores.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DatastoreSummary;
import software.amazon.awssdk.services.medicalimaging.model.ListDatastoresRequest;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.medicalimaging.paginators.ListDatastoresIterable;

import java.util.ArrayList;
import java.util.List;

//snippet-end:[medicalimaging.java2.list_datastores.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDatastores {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        List<DatastoreSummary> datastoreSummaries = listMedicalImagingDatastores(medicalImagingClient);
        if (datastoreSummaries != null) {
            System.out.println("Here is the list of data stores:\n" + datastoreSummaries);
        }
        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.list_datastores.main]
    public static List<DatastoreSummary> listMedicalImagingDatastores(MedicalImagingClient medicalImagingClient) {
        try {
            ListDatastoresRequest datastoreRequest = ListDatastoresRequest.builder()
                    .build();
            ListDatastoresIterable responses = medicalImagingClient.listDatastoresPaginator(datastoreRequest);
            List<DatastoreSummary> datastoreSummaries = new ArrayList<>();

            responses.stream().forEach(response -> datastoreSummaries.addAll(response.datastoreSummaries()));

            return datastoreSummaries;
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
    //snippet-end:[medicalimaging.java2.list_datastores.main]
}