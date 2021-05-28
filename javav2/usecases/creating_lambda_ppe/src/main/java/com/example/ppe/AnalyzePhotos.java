/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ppe;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentSummarizationAttributes;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentRequest;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentResponse;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentPerson;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentBodyPart;
import software.amazon.awssdk.services.rekognition.model.EquipmentDetection;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import java.util.ArrayList;
import java.util.List;

public class AnalyzePhotos {

    // Returns a list of GearItem objects that contains PPE information.
    public ArrayList<GearItem> detectLabels(byte[] bytes, String key) {

        Region region = Region.US_EAST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        ArrayList<GearItem> gearList = new ArrayList<>();

        try {

            SdkBytes sourceBytes = SdkBytes.fromByteArray(bytes);

            // Create an Image object for the source image.
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            ProtectiveEquipmentSummarizationAttributes summarizationAttributes = ProtectiveEquipmentSummarizationAttributes.builder()
                    .minConfidence(80F)
                    .requiredEquipmentTypesWithStrings("FACE_COVER", "HAND_COVER", "HEAD_COVER")
                    .build();

            DetectProtectiveEquipmentRequest request = DetectProtectiveEquipmentRequest.builder()
                    .image(souImage)
                    .summarizationAttributes(summarizationAttributes)
                    .build();

            DetectProtectiveEquipmentResponse result = rekClient.detectProtectiveEquipment(request);
            List<ProtectiveEquipmentPerson> persons = result.persons();

            // Create a GearItem object.
            GearItem gear;

            for (ProtectiveEquipmentPerson person : persons) {

                List<ProtectiveEquipmentBodyPart> bodyParts = person.bodyParts();
                if (bodyParts.isEmpty()) {
                    System.out.println("\tNo body parts detected");
                } else
                    for (ProtectiveEquipmentBodyPart bodyPart : bodyParts) {

                        List<EquipmentDetection> equipmentDetections = bodyPart.equipmentDetections();

                        if (equipmentDetections.isEmpty()) {
                            System.out.println("\t\tNo PPE Detected on " + bodyPart.name());
                        } else {
                            for (EquipmentDetection item : equipmentDetections) {

                                gear = new GearItem();
                                gear.setKey(key);

                                String itemType = item.type().toString();
                                String confidence = item.confidence().toString();
                                String myDesc = "Item: " + item.type() + ". Confidence: " + item.confidence().toString();
                                String bodyPartDes = "Covers body part: "
                                        + item.coversBodyPart().value().toString() + ". Confidence: " + item.coversBodyPart().confidence().toString();

                                gear.setName(itemType);
                                gear.setConfidence(confidence);
                                gear.setItemDescription(myDesc);
                                gear.setBodyCoverDescription(bodyPartDes);

                                // push the object.
                                gearList.add(gear);
                            }
                        }
                    }
            }

            if (gearList.isEmpty())
                    return null ;
            else
                return gearList;

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}

