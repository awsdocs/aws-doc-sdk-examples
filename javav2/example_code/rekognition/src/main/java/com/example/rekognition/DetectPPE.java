// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.rekognition;

// snippet-start:[rekognition.java2.detect_ppe.main]
// snippet-start:[rekognition.java2.detect_ppe.import]

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentRequest;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentResponse;
import software.amazon.awssdk.services.rekognition.model.EquipmentDetection;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentBodyPart;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentSummarizationAttributes;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentPerson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
// snippet-end:[rekognition.java2.detect_ppe.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectPPE {
    public static void main(String[] args) {
        final String usage = """
                 Usage:  <bucketName> <sourceImage> 
                
                 Where:
                     bucketName - The name of the Amazon S3 bucket (for example, myBucket).\s
                     sourceImage - The name of the image in an Amazon S3 bucket (for example, people.png).\s
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String bucketName = args[0];
        String sourceImage = args[1];
        Region region = Region.US_WEST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        displayGear(rekClient, sourceImage, bucketName);
        rekClient.close();
        System.out.println("This example is done!");
    }

    /**
     * Displays the protective equipment detected in the specified image using the AWS Rekognition service.
     *
     * @param rekClient   the Rekognition client used to detect protective equipment
     * @param sourceImage the name of the source image file
     * @param bucketName  the name of the S3 bucket containing the source image
     */
    public static void displayGear(RekognitionClient rekClient, String sourceImage, String bucketName) {
        try {
            software.amazon.awssdk.services.rekognition.model.Image rekImage = software.amazon.awssdk.services.rekognition.model.Image.builder()
                    .s3Object(s3Object -> s3Object
                            .bucket(bucketName)
                            .name(sourceImage)
                    )
                    .build();

            ProtectiveEquipmentSummarizationAttributes summarizationAttributes = ProtectiveEquipmentSummarizationAttributes
                    .builder()
                    .minConfidence(80F)
                    .requiredEquipmentTypesWithStrings("FACE_COVER", "HAND_COVER", "HEAD_COVER")
                    .build();

            // Create the request to detect protective equipment from Rekognition
            DetectProtectiveEquipmentRequest request = DetectProtectiveEquipmentRequest.builder()
                    .image(rekImage)
                    .summarizationAttributes(summarizationAttributes)
                    .build();

            // Call Rekognition to detect protective equipment
            DetectProtectiveEquipmentResponse result = rekClient.detectProtectiveEquipment(request);
            List<ProtectiveEquipmentPerson> persons = result.persons();
            for (ProtectiveEquipmentPerson person : persons) {
                System.out.println("ID: " + person.id());
                List<ProtectiveEquipmentBodyPart> bodyParts = person.bodyParts();
                if (bodyParts.isEmpty()) {
                    System.out.println("\tNo body parts detected");
                } else {
                    for (ProtectiveEquipmentBodyPart bodyPart : bodyParts) {
                        System.out.println("\t" + bodyPart.name() + ". Confidence: " + bodyPart.confidence().toString());
                        List<EquipmentDetection> equipmentDetections = bodyPart.equipmentDetections();

                        if (equipmentDetections.isEmpty()) {
                            System.out.println("\t\tNo PPE Detected on " + bodyPart.name());
                        } else {
                            for (EquipmentDetection item : equipmentDetections) {
                                System.out.println(
                                        "\t\tItem: " + item.type() + ". Confidence: " + item.confidence().toString());
                                System.out.println("\t\tCovers body part: "
                                        + item.coversBodyPart().value().toString() + ". Confidence: "
                                        + item.coversBodyPart().confidence().toString());

                                System.out.println("\t\tBounding Box");
                                BoundingBox box = item.boundingBox();
                                System.out.println("\t\tLeft: " + box.left().toString());
                                System.out.println("\t\tTop: " + box.top().toString());
                                System.out.println("\t\tWidth: " + box.width().toString());
                                System.out.println("\t\tHeight: " + box.height().toString());
                                System.out.println("\t\tConfidence: " + item.confidence().toString());
                                System.out.println();
                            }
                        }
                    }
                }
            }

            // Display summary statistics
            System.out.println("Person ID Summary\n-----------------");

            displaySummary("With required equipment", result.summary().personsWithRequiredEquipment());
            displaySummary("Without required equipment", result.summary().personsWithoutRequiredEquipment());
            displaySummary("Indeterminate", result.summary().personsIndeterminate());

        } catch (RekognitionException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    static void displaySummary(String summaryType, List<Integer> idList) {
        System.out.print(summaryType + "\n\tIDs  ");
        if (idList.isEmpty()) {
            System.out.println("None");
        } else {
            int count = 0;
            for (Integer id : idList) {
                if (count++ == idList.size() - 1) {
                    System.out.println(id.toString());
                } else {
                    System.out.print(id.toString() + ", ");
                }
            }
        }
        System.out.println();
    }
}
// snippet-end:[rekognition.java2.detect_ppe.main]
