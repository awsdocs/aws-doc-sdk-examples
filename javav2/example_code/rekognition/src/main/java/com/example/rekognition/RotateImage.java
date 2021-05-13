// snippet-sourcedescription:[RotateImage.java demonstrates how to to get the estimated orientation of an image and to translate bounding box coordinates.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11-03-2020]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.recognize_image_orientation.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.RecognizeCelebritiesRequest;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.RecognizeCelebritiesResponse;
import software.amazon.awssdk.services.rekognition.model.Celebrity;
import software.amazon.awssdk.services.rekognition.model.ComparedFace;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
// snippet-end:[rekognition.java2.recognize_image_orientation.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class RotateImage {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "RecognizeCelebrities <sourceImage>\n\n" +
                "Where:\n" +
                "sourceImage - the path to the image (for example, C:\\AWS\\pic1.png). \n\n";

       if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
       }

        String sourceImage = args[0];
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        System.out.println("Locating celebrities in " + sourceImage);
        recognizeAllCelebrities(rekClient, sourceImage);
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.recognize_image_orientation.main]
    public static void recognizeAllCelebrities(RekognitionClient rekClient, String sourceImage) {

        try {
            BufferedImage image = null;
            InputStream sourceStream = new FileInputStream(new File(sourceImage));
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

            image = ImageIO.read(sourceBytes.asInputStream());
            int height = image.getHeight();
            int width = image.getWidth();

            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            RecognizeCelebritiesRequest request = RecognizeCelebritiesRequest.builder()
                    .image(souImage)
                    .build();

            RecognizeCelebritiesResponse result = rekClient.recognizeCelebrities(request) ;

            List<Celebrity> celebs=result.celebrityFaces();
            System.out.println(celebs.size() + " celebrity(s) were recognized.\n");

            for (Celebrity celebrity: celebs) {
                System.out.println("Celebrity recognized: " + celebrity.name());
                System.out.println("Celebrity ID: " + celebrity.id());
                ComparedFace  face = celebrity.face();
                ShowBoundingBoxPositions(height,
                        width,
                        face.boundingBox(),
                        result.orientationCorrectionAsString());
            }

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ShowBoundingBoxPositions(int imageHeight, int imageWidth, BoundingBox box, String rotation) {

        float left = 0;
        float top = 0;

        if(rotation==null){
            System.out.println("No estimated estimated orientation.");
            return;
        }
        // Calculate face position based on the image orientation
        switch (rotation) {
            case "ROTATE_0":
                left = imageWidth * box.left();
                top = imageHeight * box.top();
                break;
            case "ROTATE_90":
                left = imageHeight * (1 - (box.top() + box.height()));
                top = imageWidth * box.left();
                break;
            case "ROTATE_180":
                left = imageWidth - (imageWidth * (box.left() + box.width()));
                top = imageHeight * (1 - (box.top() + box.height()));
                break;
            case "ROTATE_270":
                left = imageHeight * box.top();
                top = imageWidth * (1 - box.left() - box.width());
                break;
            default:
                System.out.println("No estimated orientation information. Check Exif data.");
                return;
        }

        System.out.println("Left: " + String.valueOf((int) left));
        System.out.println("Top: " + String.valueOf((int) top));
        System.out.println("Face Width: " + String.valueOf((int)(imageWidth * box.width())));
        System.out.println("Face Height: " + String.valueOf((int)(imageHeight * box.height())));
    }

    // snippet-end:[rekognition.java2.recognize_image_orientation.main]
}
