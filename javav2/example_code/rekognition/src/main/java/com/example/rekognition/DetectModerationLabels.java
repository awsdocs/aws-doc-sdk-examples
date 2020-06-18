// snippet-sourcedescription:[DetectModerationLabels.java demonstrates how to detect unsafe content in an image.]
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

// snippet-start:[rekognition.java2.detect_mod_labels.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectModerationLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectModerationLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.ModerationLabel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
// snippet-end:[rekognition.java2.detect_mod_labels.import]

public class DetectModerationLabels {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DetectModerationLabels - detects unsafe content in an image.\n\n" +
                "Usage: DetectModerationLabels <path>\n\n" +
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

        detectModLabels(rekClient, sourceImage);
    }

    // snippet-start:[rekognition.java2.detect_mod_labels.main]
    public static void detectModLabels(RekognitionClient rekClient, String sourceImage) {

    try {

        InputStream sourceStream = new FileInputStream(new File(sourceImage));
        SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

        // Create an Image object for the source image
        Image souImage = Image.builder()
                .bytes(sourceBytes)
                .build();

        DetectModerationLabelsRequest moderationLabelsRequest = DetectModerationLabelsRequest.builder()
                .image(souImage)
                .minConfidence(60F)
                .build();

        DetectModerationLabelsResponse moderationLabelsResponse = rekClient.detectModerationLabels(moderationLabelsRequest);

        // Get the results
        List<ModerationLabel> labels = moderationLabelsResponse.moderationLabels();
        System.out.println("Detected labels for image");

        for (ModerationLabel label : labels) {
            System.out.println("Label: " + label.name()
                    + "\n Confidence: " + label.confidence().toString() + "%"
                    + "\n Parent:" + label.parentName());
        }

    } catch (RekognitionException | FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
    }
    // snippet-end:[rekognition.java2.detect_mod_labels.main]
  }
}
