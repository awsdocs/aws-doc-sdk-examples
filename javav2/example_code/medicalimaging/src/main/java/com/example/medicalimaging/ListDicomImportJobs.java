//snippet-sourcedescription:[StartDicomImportJob.java demonstrates how to list import jobs in AWS HealthImaging.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.list_dicom_import_jobs.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DICOMImportJobSummary;
import software.amazon.awssdk.services.medicalimaging.model.ListDicomImportJobsRequest;
import software.amazon.awssdk.services.medicalimaging.model.ListDicomImportJobsResponse;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;

import java.util.ArrayList;
import java.util.List;

//snippet-end:[medicalimaging.java2.list_dicom_import_jobs.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListDicomImportJobs {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        List<DICOMImportJobSummary> summaries = listDicomImportJobs(medicalImagingClient, datastoreId);

        System.out.println("The import jobs are " + summaries);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.list_dicom_import_jobs.main]
    public static List<DICOMImportJobSummary> listDicomImportJobs(MedicalImagingClient medicalImagingClient,
                                                                  String datastoreId) {

        try {
            ListDicomImportJobsRequest listDicomImportJobsRequest = ListDicomImportJobsRequest.builder()
                    .datastoreId(datastoreId)
                    .build();
            ListDicomImportJobsResponse response = medicalImagingClient.listDICOMImportJobs(listDicomImportJobsRequest);
            return response.jobSummaries();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return new ArrayList<>();
    }
//snippet-end:[medicalimaging.java2.list_dicom_import_jobs.main]
}
