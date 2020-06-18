// snippet-sourcedescription:[DetectLabels.java demonstrates how to capture labels (like water and mountains) in a given image.]
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
// snippet-start:[rekognition.java2.detect_labels.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
// snippet-end:[rekognition.java2.detect_labels.import]

public class DetectLabels {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DetectLabels - how to capture labels within an image\n\n" +
                "Usage: DetectLabels <path>\n\n" +
                "Where:\n" +
                "path - the path to the image (ie, C:\\AWS\\pic1.png) \n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String sourceImage = args[0] ;

        Region region = Region.US_EAST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        detectImageLabels(rekClient, sourceImage );
    }

    // snippet-start:[rekognition.java2.detect_labels.main]
    public static void detectImageLabels(RekognitionClient rekClient, String sourceImage) {

        try {

            InputStream sourceStream = new FileInputStream(new File(sourceImage));
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            // Create an Image object for the source image
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(souImage)
                    .maxLabels(10)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);

            // Display the results
            List<Label> labels = labelsResponse.labels();

            System.out.println("Detected labels for the given photo");
            for (Label label: labels) {
                System.out.println(label.name() + ": " + label.confidence().toString());
            }

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[rekognition.java2.detect_labels.main]
    }
}
