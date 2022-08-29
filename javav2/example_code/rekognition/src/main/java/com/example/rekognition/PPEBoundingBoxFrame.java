// snippet-sourcedescription:[DisplayFacesFrame.java demonstrates how to display a green bounding box around a mask in an image.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.display_mask.import]
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.BoundingBox;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentRequest;
import software.amazon.awssdk.services.rekognition.model.EquipmentDetection;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentBodyPart;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentPerson;
import software.amazon.awssdk.services.rekognition.model.ProtectiveEquipmentSummarizationAttributes;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.rekognition.model.DetectProtectiveEquipmentResponse;
// snippet-end:[rekognition.java2.display_mask.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PPEBoundingBoxFrame extends JPanel {

    DetectProtectiveEquipmentResponse result;
    static BufferedImage image;
    static int scale;
    float confidence;

    public static void main(String[] args) throws Exception {

        final String usage = "\n" +
            "Usage: " +
            "   <sourceImage> <bucketName>\n\n" +
            "Where:\n" +
            "   sourceImage - The name of the image in an Amazon S3 bucket that shows a person wearing a mask (for example, masks.png). \n\n" +
            "   bucketName - The name of the Amazon S3 bucket (for example, myBucket). \n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String sourceImage = args[0];
        String bucketName = args[1];
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        RekognitionClient rekClient = RekognitionClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        displayGear(s3, rekClient, sourceImage, bucketName);
        s3.close();
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.display_mask.main]
   public static void displayGear(S3Client s3,
                                       RekognitionClient rekClient,
                                       String sourceImage,
                                       String bucketName) {
       float confidence = 80;
       byte[] data = getObjectBytes(s3, bucketName, sourceImage);
       InputStream is = new ByteArrayInputStream(data);

       try {
           ProtectiveEquipmentSummarizationAttributes summarizationAttributes = ProtectiveEquipmentSummarizationAttributes.builder()
               .minConfidence(70F)
               .requiredEquipmentTypesWithStrings("FACE_COVER")
               .build();

           SdkBytes sourceBytes = SdkBytes.fromInputStream(is);
           image = ImageIO.read(sourceBytes.asInputStream());

           // Create an Image object for the source image.
           software.amazon.awssdk.services.rekognition.model.Image souImage = Image.builder()
               .bytes(sourceBytes)
               .build();

           DetectProtectiveEquipmentRequest request = DetectProtectiveEquipmentRequest.builder()
               .image(souImage)
               .summarizationAttributes(summarizationAttributes)
               .build();

           DetectProtectiveEquipmentResponse result = rekClient.detectProtectiveEquipment(request);
           JFrame frame = new JFrame("Detect PPE");
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           PPEBoundingBoxFrame panel = new PPEBoundingBoxFrame(result, image, confidence);
           panel.setPreferredSize(new Dimension(image.getWidth() / scale, image.getHeight() / scale));
           frame.setContentPane(panel);
           frame.pack();
           frame.setVisible(true);

       } catch (RekognitionException e) {
           e.printStackTrace();
           System.exit(1);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    public static byte[] getObjectBytes (S3Client s3, String bucketName, String keyName) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(keyName)
                .bucket(bucketName)
                .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
     }

    public PPEBoundingBoxFrame(DetectProtectiveEquipmentResponse ppeResult, BufferedImage bufImage, float requiredConfidence) {
        super();
        scale = 1; // increase to shrink image size.
        result = ppeResult;
        image = bufImage;
        confidence=requiredConfidence;
    }

    // Draws the bounding box around the detected masks.
    public void paintComponent(Graphics g) {
        float left = 0;
        float top = 0;
        int height = image.getHeight(this);
        int width = image.getWidth(this);
        int offset=20;

        Graphics2D g2d = (Graphics2D) g; // Create a Java2D version of g.

        // Draw the image.
        g2d.drawImage(image, 0, 0, width / scale, height / scale, this);
        g2d.setColor(new Color(0, 212, 0));

        // Iterate through detected persons and display bounding boxes.
        List<ProtectiveEquipmentPerson> persons = result.persons();
        for (ProtectiveEquipmentPerson person: persons) {

            List<ProtectiveEquipmentBodyPart> bodyParts=person.bodyParts();
            if (!bodyParts.isEmpty()){
                for (ProtectiveEquipmentBodyPart bodyPart: bodyParts) {
                    List<EquipmentDetection> equipmentDetections=bodyPart.equipmentDetections();
                    for (EquipmentDetection item: equipmentDetections) {

                        String myType = item.type().toString();
                        if (myType.compareTo("FACE_COVER") ==0) {

                            // Draw green bounding box depending on mask coverage.
                            BoundingBox box =item.boundingBox();
                            left = width * box.left();
                            top = height * box.top();
                            Color maskColor=new Color( 0, 212, 0);

                            if (item.coversBodyPart().equals(false)) {
                                // red bounding box.
                                maskColor=new Color( 255, 0, 0);
                            }
                            g2d.setColor(maskColor);
                            g2d.drawRect(Math.round(left / scale), Math.round(top / scale),
                                    Math.round((width * box.width()) / scale), Math.round((height * box.height())) / scale);

                            // Check confidence is > supplied confidence.
                            if (item.coversBodyPart().confidence() < confidence) {
                                // Draw a yellow bounding box inside face mask bounding box.
                                maskColor=new Color( 255, 255, 0);
                                g2d.setColor(maskColor);
                                g2d.drawRect(Math.round((left + offset) / scale),
                                        Math.round((top + offset) / scale),
                                        Math.round((width * box.width())- (offset * 2 ))/ scale,
                                        Math.round((height * box.height()) -( offset* 2)) / scale);
                            }
                        }
                    }
                }
            }
       }
    }
    // snippet-end:[rekognition.java2.display_mask.main]
}

