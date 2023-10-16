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
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.medicalimaging.model.StartDicomImportJobRequest;
import software.amazon.awssdk.services.medicalimaging.model.StartDicomImportJobResponse;

//snippet-end:[medicalimaging.java2.start_dicom_import_job.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class StartDicomImportJob {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <jobName> <datastoreId> <dataAccessRoleArn> <inputS3Uri> <outputS3Uri>\n\n" +
                "Where:\n" +
                "    jobName - The import job name.\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    dataAccessRoleArn - The Amazon Resource Name (ARN) of the IAM role that grants permission to access medical imaging resource.\n" +
                "    inputS3Uri - The input prefix path for the S3 bucket that contains the DICOM files to be imported.\n" +
                "    outputS3Uri - The output prefix of the S3 bucket to upload the results of the DICOM import job.\n";

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String jobName = args[0];
        String datastoreId = args[1];
        String dataAccessRoleArn = args[2];
        String inputS3Uri = args[3];
        String outputS3Uri = args[4];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String jobID = startDicomImportJob(medicalImagingClient, jobName, datastoreId, dataAccessRoleArn, inputS3Uri, outputS3Uri);

        System.out.println("The job ID is " + jobID);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.start_dicom_import_job.main]
    public static String startDicomImportJob(MedicalImagingClient medicalImagingClient,
                                             String jobName,
                                             String datastoreId,
                                             String dataAccessRoleArn,
                                             String inputS3Uri,
                                             String outputS3Uri) {

        try {
            StartDicomImportJobRequest startDicomImportJobRequest = StartDicomImportJobRequest.builder()
                    .jobName(jobName)
                    .datastoreId(datastoreId)
                    .dataAccessRoleArn(dataAccessRoleArn)
                    .inputS3Uri(inputS3Uri)
                    .outputS3Uri(outputS3Uri)
                    .build();
            StartDicomImportJobResponse response = medicalImagingClient.startDICOMImportJob(startDicomImportJobRequest);
            return response.jobId();
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }
//snippet-end:[medicalimaging.java2.start_dicom_import_job.main]
}
