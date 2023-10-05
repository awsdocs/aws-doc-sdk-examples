//snippet-sourcedescription:[StartDicomImportJob.java demonstrates how to import bulk data into in an AWS HealthImaging data store.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.start_dicom_import_job.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DICOMImportJobProperties;
import software.amazon.awssdk.services.medicalimaging.model.GetDicomImportJobRequest;
import software.amazon.awssdk.services.medicalimaging.model.GetDicomImportJobResponse;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
//snippet-end:[medicalimaging.java2.start_dicom_import_job.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetDicomImportJob {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <<datastoreId> <jobId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    jobId - The ID of the job.\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String jobId = args[1];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        DICOMImportJobProperties importJobProperties = getDicomImportJob(medicalImagingClient, datastoreId, jobId);

        System.out.println("The job properties are " + importJobProperties);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.start_dicom_import_job.main]
    public static DICOMImportJobProperties getDicomImportJob(MedicalImagingClient medicalImagingClient,
                                                             String datastoreId,
                                                             String jobId) {

        try {
            GetDicomImportJobRequest getDicomImportJobRequest = GetDicomImportJobRequest.builder()
                    .datastoreId(datastoreId)
                    .jobId(jobId)
                    .build();
            GetDicomImportJobResponse response = medicalImagingClient.getDICOMImportJob(getDicomImportJobRequest);
            return response.jobProperties();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
//snippet-end:[medicalimaging.java2.start_dicom_import_job.main]
}
