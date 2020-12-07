// snippet-sourcedescription:[DisplayFacesFrame.java demonstrates how to display a bounding box around faces in an image.]
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

// snippet-start:[rekognition.java2.display_faces.import]
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
// snippet-end:[rekognition.java2.display_faces.import]

public class DisplayFacesFrame extends JPanel {

    static DetectFacesResponse result;
    static BufferedImage image;
    static int scale;

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "DisplayFacesFrame <sourceImage> <bucketName>\n\n" +
                "Where:\n" +
                "sourceImage - the name of the image in an Amazon S3 bucket (for example, people.png). \n\n" +
                "bucketName - the name of the Amazon S3 bucket (for example, myBucket). \n\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String sourceImage = args[0];
        String bucketName = args[1];
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        displayAllFaces(s3, rekClient, sourceImage, bucketName);
        s3.close();
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.display_faces.main]
    public static void displayAllFaces(S3Client s3,
                                       RekognitionClient rekClient,
                                       String sourceImage,
                                       String bucketName) {
        int height = 0;
        int width = 0;

        byte[] data = getObjectBytes (s3, bucketName, sourceImage);
        InputStream is = new ByteArrayInputStream(data);

       try {
           SdkBytes sourceBytes = SdkBytes.fromInputStream(is);
           image = ImageIO.read(sourceBytes.asInputStream());

           width = image.getWidth();
           height = image.getHeight();

        // Create an Image object for the source image
        software.amazon.awssdk.services.rekognition.model.Image souImage = Image.builder()
                .bytes(sourceBytes)
                .build();

        DetectFacesRequest facesRequest = DetectFacesRequest.builder()
                .attributes(Attribute.ALL)
                .image(souImage)
                .build();

         result= rekClient.detectFaces(facesRequest);

        // Show the bounding box info for each face
        List<FaceDetail> faceDetails = result.faceDetails();
        for (FaceDetail face : faceDetails) {

            BoundingBox box = face.boundingBox();
            float left = width * box.left();
            float top = height * box.top();
            System.out.println("Face:");

            System.out.println("Left: " + (int) left);
            System.out.println("Top: " + (int) top);
            System.out.println("Face Width: " + (int) (width * box.width()));
            System.out.println("Face Height: " + (int) (height * box.height()));
            System.out.println();
        }

        // Create the frame and panel
        JFrame frame = new JFrame("RotateImage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DisplayFacesFrame panel = new DisplayFacesFrame(image);
        panel.setPreferredSize(new Dimension(image.getWidth() / scale, image.getHeight() / scale));
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);

     } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
       } catch (IOException e) {
           e.printStackTrace();
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
            byte[] data = objectBytes.asByteArray();
            return data;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public DisplayFacesFrame(BufferedImage bufImage) throws Exception {
        super();
        scale = 1; // increase to shrink image size.
        image = bufImage;
    }

    // Draws the bounding box around the detected faces
    public void paintComponent(Graphics g) {
        float left = 0;
        float top = 0;
        int height = image.getHeight(this);
        int width = image.getWidth(this);

        Graphics2D g2d = (Graphics2D) g; // Create a Java2D version of g

        // Draw the image
        g2d.drawImage(image, 0, 0, width / scale, height / scale, this);
        g2d.setColor(new Color(0, 212, 0));

        // Iterate through the faces and display bounding boxes
        List<FaceDetail> faceDetails = result.faceDetails();
        for (FaceDetail face : faceDetails) {

            BoundingBox box = face.boundingBox();
            left = width * box.left();
            top = height * box.top();
            g2d.drawRect(Math.round(left / scale), Math.round(top / scale),
                    Math.round((width * box.width()) / scale), Math.round((height * box.height())) / scale);
        }
    }
    // snippet-end:[rekognition.java2.display_faces.main]
}
