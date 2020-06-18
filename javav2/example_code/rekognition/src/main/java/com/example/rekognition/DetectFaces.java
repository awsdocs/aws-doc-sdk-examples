// snippet-sourcedescription:[DetectFaces.java demonstrates how to detect faces in an image.]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[6-10-2020]
// snippet-sourceauthor:[scmacdon - AWS]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.example.rekognition;

// snippet-start:[rekognition.java2.detect_faces.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.DetectFacesRequest;
import software.amazon.awssdk.services.rekognition.model.DetectFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Attribute;
import software.amazon.awssdk.services.rekognition.model.FaceDetail;
import software.amazon.awssdk.services.rekognition.model.AgeRange;
import software.amazon.awssdk.core.SdkBytes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
// snippet-end:[rekognition.java2.detect_faces.import]

public class DetectFaces {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DetectFaces - how to detect faces in an image\n\n" +
                "Usage: DetectFaces <path>\n\n" +
                "Where:\n" +
                "path - the path to the image (ie, C:\\AWS\\pic1.png) \n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String sourceImage = args[0];

        Region region = Region.US_EAST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        detectFacesinImage(rekClient, sourceImage );
    }


    // snippet-start:[rekognition.java2.detect_faces.main]
    public static void detectFacesinImage(RekognitionClient rekClient,String sourceImage ) {

        try {
            InputStream sourceStream = new FileInputStream(new File(sourceImage));
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            // Create an Image object for the source image
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectFacesRequest facesRequest = DetectFacesRequest.builder()
                    .attributes(Attribute.ALL)
                    .image(souImage)
                    .build();

            DetectFacesResponse facesResponse = rekClient.detectFaces(facesRequest);
            List<FaceDetail> faceDetails = facesResponse.faceDetails();

            for (FaceDetail face : faceDetails) {
                //if (facesRequest.attributes().contains("ALL")) {
                    AgeRange ageRange = face.ageRange();
                    System.out.println("The detected face is estimated to be between "
                            + ageRange.low().toString() + " and " + ageRange.high().toString()
                            + " years old.");
                    System.out.println("The gender appears as : "+face.gender().value().toString());

                System.out.println("There is a smile : "+face.smile().value().toString());
            }

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[rekognition.java2.detect_faces.main]
    }
 }
