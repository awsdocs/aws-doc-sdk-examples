/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.video;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoDetectFaces {

    String topicArn = "arn:aws:sns:us-east-1:814548047983:video";
    String roleArn = "arn:aws:iam::814548047983:role/video";

    private RekognitionClient getRecClient() {
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();
        return rekClient;
    }

    private NotificationChannel getChannel() {

        NotificationChannel channel = NotificationChannel.builder()
                .snsTopicArn(topicArn)
                .roleArn(roleArn)
                .build();
        return channel;
    }

 public String StartFaceDetection(String bucket, String video) {

     String startJobId="";

        try {

            RekognitionClient rekClient = getRecClient();
            software.amazon.awssdk.services.rekognition.model.S3Object s3Obj = S3Object.builder()
                    .bucket(bucket)
                    .name(video)
                    .build();

            Video vidOb = Video.builder()
                    .s3Object(s3Obj)
                    .build();

            StartFaceDetectionRequest faceDetectionRequest = StartFaceDetectionRequest.builder()
                    .jobTag("Faces")
                    .notificationChannel(getChannel())
                    .faceAttributes(FaceAttributes.ALL)
                    .video(vidOb)
                    .build();

            StartFaceDetectionResponse startLabelDetectionResult = rekClient.startFaceDetection(faceDetectionRequest);
            startJobId=startLabelDetectionResult.jobId();
            return startJobId;

        } catch(RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    // Processes the Job and returns of List of labels
    public List<FaceItems> GetFaceResults(String startJobId) {

        List<FaceItems> items =new ArrayList<>();
        try {
            RekognitionClient rekClient = getRecClient();
            String paginationToken=null;
            GetFaceDetectionResponse faceDetectionResponse=null;
            Boolean finished = false;
            String status="";
            int yy=0 ;

            do{
                if (faceDetectionResponse !=null)
                    paginationToken = faceDetectionResponse.nextToken();

                GetFaceDetectionRequest recognitionRequest = GetFaceDetectionRequest.builder()
                        .jobId(startJobId)
                        .nextToken(paginationToken)
                        .maxResults(10)
                        .build();

                // Wait until the job succeeds
                while (!finished) {

                    faceDetectionResponse = rekClient.getFaceDetection(recognitionRequest);
                    status = faceDetectionResponse.jobStatusAsString();

                    if (status.compareTo("SUCCEEDED") == 0)
                        finished = true;
                    else {
                        System.out.println(yy + " status is: " + status);
                        Thread.sleep(1000);
                    }
                    yy++;
                }
                finished = false;

                // Push face information to the list
                List<FaceDetection> faces= faceDetectionResponse.faces();

                FaceItems faceItem;
                for (FaceDetection face: faces) {

                    faceItem = new FaceItems();

                    String age = face.face().ageRange().toString();
                    String beard = face.face().beard().toString();
                    String eyeglasses = face.face().eyeglasses().toString();
                    String eyesOpen = face.face().eyesOpen().toString();
                    String mustache = face.face().mustache().toString();
                    String smile = face.face().smile().toString();

                    faceItem.setAgeRange(age);
                    faceItem.setBeard(beard);
                    faceItem.setEyeglasses(eyeglasses);
                    faceItem.setEyesOpen(eyesOpen);
                    faceItem.setMustache(mustache);
                    faceItem.setSmile(smile);

                    items.add(faceItem);
                   }

            } while (faceDetectionResponse !=null && faceDetectionResponse.nextToken() != null);

            return items;


        } catch(RekognitionException | InterruptedException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
