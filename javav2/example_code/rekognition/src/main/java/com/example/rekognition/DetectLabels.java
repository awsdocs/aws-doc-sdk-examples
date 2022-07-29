// snippet-sourcedescription:[DetectLabels.java demonstrates how to capture labels (like water and mountains) in a given image.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.detect_labels.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
// snippet-end:[rekognition.java2.detect_labels.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectLabels {

    public static void main(String[] args) {

       final String usage = "\n" +
           "Usage: " +
           "   <sourceImage>\n\n" +
           "Where:\n" +
           "   sourceImage - The path to the image (for example, C:\\AWS\\pic1.png). \n\n";

       if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
       }

       String sourceImage = args[0] ;
       Region region = Region.US_EAST_1;
       RekognitionClient rekClient = RekognitionClient.builder()
           .region(region)
           .credentialsProvider(ProfileCredentialsProvider.create())
           .build();

       detectImageLabels(rekClient, sourceImage );
       rekClient.close();
    }

    // snippet-start:[rekognition.java2.detect_labels.main]
    public static void detectImageLabels(RekognitionClient rekClient, String sourceImage) {

        try {
            InputStream sourceStream = new FileInputStream(sourceImage);
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            // Create an Image object for the source image.
            Image souImage = Image.builder()
                .bytes(sourceBytes)
                .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(souImage)
                .maxLabels(10)
                .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            System.out.println("Detected labels for the given photo");
            for (Label label: labels) {
                System.out.println(label.name() + ": " + label.confidence().toString());
            }

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rekognition.java2.detect_labels.main]
}
