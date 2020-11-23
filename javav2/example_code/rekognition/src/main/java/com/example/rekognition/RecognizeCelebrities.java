// snippet-sourcedescription:[RecognizeCelebrities.java demonstrates how to recognize celebrities in a given image.]
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

// snippet-start:[rekognition.java2.recognize_celebs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.core.SdkBytes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import software.amazon.awssdk.services.rekognition.model.RecognizeCelebritiesRequest;
import software.amazon.awssdk.services.rekognition.model.RecognizeCelebritiesResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Celebrity;
// snippet-end:[rekognition.java2.recognize_celebs.import]

public class RecognizeCelebrities {

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
        Region region = Region.US_EAST_2;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        System.out.println("Locating celebrities in " + sourceImage);
        recognizeAllCelebrities(rekClient, sourceImage);
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.recognize_celebs.main]
    public static void recognizeAllCelebrities(RekognitionClient rekClient, String sourceImage) {

        try {

            InputStream sourceStream = new FileInputStream(new File(sourceImage));
            SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

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

                System.out.println("Further information (if available):");
                for (String url: celebrity.urls()){
                    System.out.println(url);
                }
                System.out.println();
            }
            System.out.println(result.unrecognizedFaces().size() + " face(s) were unrecognized.");

        } catch (RekognitionException | FileNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[rekognition.java2.recognize_celebs.main]
    }
}
