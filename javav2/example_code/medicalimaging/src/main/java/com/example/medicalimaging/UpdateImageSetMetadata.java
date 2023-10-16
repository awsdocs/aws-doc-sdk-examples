//snippet-sourcedescription:[GetImageSet.java demonstrates how to update an image set's metadata.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[AWS HealthImaging]

package com.example.medicalimaging;

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

//snippet-start:[medicalimaging.java2.update_image_set_metadata.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.DICOMUpdates;
import software.amazon.awssdk.services.medicalimaging.model.MedicalImagingException;
import software.amazon.awssdk.services.medicalimaging.model.MetadataUpdates;
import software.amazon.awssdk.services.medicalimaging.model.UpdateImageSetMetadataRequest;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

//snippet-end:[medicalimaging.java2.update_image_set_metadata.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateImageSetMetadata {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imagesetId> <versionId>\n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imagesetId - The ID of the image set.\n" +
                "    versionId - The latest version ID of the image set.\n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String imagesetId = args[1];
        String versionid = args[2];

        Region region = Region.US_WEST_2;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        final String updateAttributes = "{\"SchemaVersion\":1.1,\"Patient\":{\"DICOM\":{\"PatientName\":\"MX^MX\"}}}";
        final String removeAttributes = "{\"SchemaVersion\":1.1,\"Patient\":{\"DICOM\":{\"PatientSex\":\"X\"}}}";
        MetadataUpdates metadataUpdates = MetadataUpdates.builder()
                .dicomUpdates(DICOMUpdates.builder()
                        .updatableAttributes(SdkBytes.fromByteBuffer(
                                ByteBuffer.wrap(updateAttributes.getBytes(StandardCharsets.UTF_8))))
                        .removableAttributes(SdkBytes.fromByteBuffer(
                                ByteBuffer.wrap(removeAttributes.getBytes(StandardCharsets.UTF_8))))
                        .build())
                .build();

        updateMedicalImageSetMetadata(medicalImagingClient, datastoreId, imagesetId,
                versionid, metadataUpdates);

        medicalImagingClient.close();
    }

    //snippet-start:[medicalimaging.java2.update_image_set_metadata.main]
    public static void updateMedicalImageSetMetadata(MedicalImagingClient medicalImagingClient,
                                                     String datastoreId,
                                                     String imagesetId,
                                                     String versionId,
                                                     MetadataUpdates metadataUpdates) {
        try {
            UpdateImageSetMetadataRequest updateImageSetMetadataRequest = UpdateImageSetMetadataRequest.builder()
                    .datastoreId(datastoreId)
                    .imageSetId(imagesetId)
                    .latestVersionId(versionId)
                    .updateImageSetMetadataUpdates(metadataUpdates)
                    .build();

            medicalImagingClient.updateImageSetMetadata(updateImageSetMetadataRequest);

            System.out.println("The image set metadata was updated");
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
//snippet-end:[medicalimaging.java2.update_image_set_metadata.main]
}
