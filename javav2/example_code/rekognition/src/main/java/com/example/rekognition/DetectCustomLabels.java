package com.example.myapp;

import java.io.IOException;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.DetectCustomLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectCustomLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.CustomLabel;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


public class DetectCustomLabels {

    public static void main(String[] args) {
        
        final String USAGE = "\n" +
                "Usage: " +
                "DetectLabels <project arn> <S3 bucket> <S3 key>\n\n" +
                "Where:\n" +
                "project arn - the arn of the model in Rekognition Custom Labels to the image (for example, arn:aws:rekognition:us-east-1:XXXXXXXXXXXX:project/YOURPROJECT/version/YOURPROJECT.YYYY-MM-DDT00.00.00/1234567890123). \n" +
                "S3 bucket - the bucket where your image is stored (for example, my-bucket-name \n" +
                "S3 key - the path of the image inside your bucket (for example, myfolder/pic1.png). \n\n";

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String arn = args[0] ;
        String bucket = args[1];
        String key = args[2];
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        detectImageCustomLabels(rekClient, arn, bucket, key );
        rekClient.close();
    }

    public static void detectImageCustomLabels(RekognitionClient rekClient, String arn, String bucket, String key ) {

        try {
            
            S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(key)
                    .build();
            
            // Create an Image object for the source image
            Image s3Image = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectCustomLabelsRequest detectCustomLabelsRequest = DetectCustomLabelsRequest.builder()
                    .image(s3Image)
                    .projectVersionArn(arn)
                    .build();

            DetectCustomLabelsResponse customLabelsResponse = rekClient.detectCustomLabels(detectCustomLabelsRequest);
            List<CustomLabel> customLabels = customLabelsResponse.customLabels();

            System.out.println("Detected labels for the given photo");
            for (CustomLabel customLabel: customLabels) {
                System.out.println(customLabel.name() + ": " + customLabel.confidence().toString());
            }

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}