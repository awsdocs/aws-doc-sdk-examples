// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.medicalimaging;

// snippet-start:[medicalimaging.java2.update_image_set_metadata.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.medicalimaging.MedicalImagingClient;
import software.amazon.awssdk.services.medicalimaging.model.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

// snippet-end:[medicalimaging.java2.update_image_set_metadata.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class UpdateImageSetMetadata {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    <datastoreId> <imagesetId> <versionId> <updateType>n\n" +
                "Where:\n" +
                "    datastoreId - The ID of the data store.\n" +
                "    imagesetId - The ID of the image set.\n" +
                "    versionId - The latest version ID of the image set.\n" +
                "    updateType - Choice of (insert | remove_attribute | remove_instance) for update type.\n";

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String datastoreId = args[0];
        String imagesetId = args[1];
        String versionid = args[2];
        String updateType = args[3];


        if (!updateType.equals("insert") && !updateType.equals("remove_attribute") && !updateType.equals("remove_instance")) {
            System.out.println("Invalid update type, '" + updateType + "'.");
            System.out.println(usage);
            System.exit(1);
        }

        Region region = Region.US_EAST_1;
        MedicalImagingClient medicalImagingClient = MedicalImagingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        if (updateType.equals("insert")) {
            // Add a new attribute or update an existing attribute.
            // snippet-start:[medicalimaging.java2.update_image_set_metadata.insert_or_update_attributes]
            final String insertAttributes = """
                    {
                      "SchemaVersion": 1.1,
                      "Study": {
                        "DICOM": {
                          "StudyDescription": "CT CHEST"
                        }
                      }
                    }
                    """;
            MetadataUpdates metadataInsertUpdates = MetadataUpdates.builder()
                    .dicomUpdates(DICOMUpdates.builder()
                            .updatableAttributes(SdkBytes.fromByteBuffer(
                                    ByteBuffer.wrap(insertAttributes
                                            .getBytes(StandardCharsets.UTF_8))))
                            .build())
                    .build();

            updateMedicalImageSetMetadata(medicalImagingClient, datastoreId, imagesetId,
                    versionid, metadataInsertUpdates);
            // snippet-end:[medicalimaging.java2.update_image_set_metadata.insert_or_update_attributes]
        } else if (updateType.equals("remove_attribute")) {
            // Remove an attribute.
            // snippet-start:[medicalimaging.java2.update_image_set_metadata.remove_attributes]
            final String removeAttributes = """
                    {
                      "SchemaVersion": 1.1,
                      "Study": {
                        "DICOM": {
                          "StudyDescription": "CT CHEST"
                        }
                      }
                    }
                    """;
            MetadataUpdates metadataRemoveUpdates = MetadataUpdates.builder()
                    .dicomUpdates(DICOMUpdates.builder()
                            .removableAttributes(SdkBytes.fromByteBuffer(
                                    ByteBuffer.wrap(removeAttributes
                                            .getBytes(StandardCharsets.UTF_8))))
                            .build())
                    .build();

            updateMedicalImageSetMetadata(medicalImagingClient, datastoreId, imagesetId,
                    versionid, metadataRemoveUpdates);
            // snippet-end:[medicalimaging.java2.update_image_set_metadata.remove_attributes]
        } else if (updateType.equals("remove_instance")) {
            // Remove an instance.
            // snippet-start:[medicalimaging.java2.update_image_set_metadata.remove_instance]
            final String removeInstance = """
                    {
                      "SchemaVersion": 1.1,
                      "Study": {
                        "Series": {
                          "1.1.1.1.1.1.12345.123456789012.123.12345678901234.1": {
                            "Instances": {
                              "1.1.1.1.1.1.12345.123456789012.123.12345678901234.1": {}
                            }
                          }
                        }
                      }
                    }      
                    """;
            MetadataUpdates metadataRemoveUpdates = MetadataUpdates.builder()
                    .dicomUpdates(DICOMUpdates.builder()
                            .removableAttributes(SdkBytes.fromByteBuffer(
                                    ByteBuffer.wrap(removeInstance
                                            .getBytes(StandardCharsets.UTF_8))))
                            .build())
                    .build();

            updateMedicalImageSetMetadata(medicalImagingClient, datastoreId, imagesetId,
                    versionid, metadataRemoveUpdates);
            // snippet-end:[medicalimaging.java2.update_image_set_metadata.remove_instance]
        }


        medicalImagingClient.close();
    }

    // snippet-start:[medicalimaging.java2.update_image_set_metadata.main]
    public static void updateMedicalImageSetMetadata(MedicalImagingClient medicalImagingClient,
                                                     String datastoreId,
                                                     String imagesetId,
                                                     String versionId,
                                                     MetadataUpdates metadataUpdates) {
        try {
            UpdateImageSetMetadataRequest updateImageSetMetadataRequest = UpdateImageSetMetadataRequest
                    .builder()
                    .datastoreId(datastoreId)
                    .imageSetId(imagesetId)
                    .latestVersionId(versionId)
                    .updateImageSetMetadataUpdates(metadataUpdates)
                    .build();

            UpdateImageSetMetadataResponse response = medicalImagingClient.updateImageSetMetadata(updateImageSetMetadataRequest);

            System.out.println("The image set metadata was updated" + response);
        } catch (MedicalImagingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[medicalimaging.java2.update_image_set_metadata.main]
}
