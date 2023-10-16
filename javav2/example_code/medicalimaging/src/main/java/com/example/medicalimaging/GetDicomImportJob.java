//snippet-sourcedescription:[StartDicomImportJob.java demonstrates how to get an import job's properties.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.get_dicom_import_job.import]

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DICOMImportJobProperties;
import software.amazon.awssdk.services.medicalimaging.model.GetDicomImportJobRequest;
import software.amazon.awssdk.services.medicalimaging.model.GetDicomImportJobResponse;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//snippet-end:[medicalimaging.java2.get_dicom_import_job.import]

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

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        List<String> imagesets = getImageSetsForImportJobProperties(s3Client, importJobProperties);

        System.out.println("The imported imagesets are " + imagesets);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.get_dicom_import_job.main]
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
//snippet-end:[medicalimaging.java2.get_dicom_import_job.main]

    public static List<String> getImageSetsForImportJobProperties(S3Client s3client,
                                                                  DICOMImportJobProperties jobProperties) {
        try {
            S3Utilities s3Utilities = s3client.utilities();
            URI manifestUri = URI.create(jobProperties.outputS3Uri() + "job-output-manifest.json");
            S3Uri manifestS3Uri = s3Utilities.parseUri(manifestUri);

            System.out.println("bucket " + manifestS3Uri.bucket().orElse("not found") +
                    ", key " + manifestS3Uri.key().orElse("not found"));
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(manifestS3Uri.bucket().get())
                    .key(manifestS3Uri.key().get())
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3client.getObjectAsBytes(getObjectRequest);
            String manifest = objectBytes.asUtf8String();
            JsonObject jsonObject = JsonParser.parseString(manifest).getAsJsonObject();
            JsonObject jobSummary = jsonObject.getAsJsonObject("jobSummary");
            List<String> result = new ArrayList<>();
            for (JsonElement imageSetSummary : jobSummary.getAsJsonArray("imageSetsSummary")) {
                result.add(imageSetSummary.getAsJsonObject().get("imageSetId").getAsString());
            }

            return result;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return null;
    }
}
