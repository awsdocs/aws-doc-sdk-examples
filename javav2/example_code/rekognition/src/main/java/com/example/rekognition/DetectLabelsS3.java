// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.rekognition;

// snippet-start:[rekognition.java2.detect_labels_s3.main]
// snippet-start:[rekognition.java2.detect_labels_s3.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import java.util.List;
// snippet-end:[rekognition.java2.detect_labels_s3.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DetectLabelsS3 {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <bucket> <image>

                Where:
                   bucket - The name of the Amazon S3 bucket that contains the image (for example, ImageBucket).   
                   image - The name of the image located in the Amazon S3 bucket (for example, Lake.png).\s
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String bucket = args[0];
        String image = args[1];
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        getLabelsfromImage(rekClient, bucket, image);
        rekClient.close();
    }

    /**
     * Retrieves labels from an image stored in an Amazon S3 bucket using the Amazon Rekognition service.
     *
     * @param rekClient the Amazon Rekognition client instance
     * @param bucket the name of the S3 bucket where the image is stored
     * @param image the name of the image file in the S3 bucket
     */
    public static void getLabelsfromImage(RekognitionClient rekClient, String bucket, String image) {
        try {
            S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(image)
                    .build();

            Image myImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(myImage)
                    .maxLabels(10)
                    .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            System.out.println("Detected labels for the given photo");
            for (Label label : labels) {
                System.out.println(label.name() + ": " + label.confidence().toString());
            }

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[rekognition.java2.detect_labels_s3.main]
