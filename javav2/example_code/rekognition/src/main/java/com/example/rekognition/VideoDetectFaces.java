// snippet-sourcedescription:[VideoDetectFaces.java demonstrates how to detect faces in a video stored in an Amazon S3 bucket.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.recognize_video_faces.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import java.util.List;
// snippet-end:[rekognition.java2.recognize_video_faces.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class VideoDetectFaces {

    private static String startJobId ="";
    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <bucket> <video> <topicArn> <roleArn>\n\n" +
                "Where:\n" +
                "   bucket - The name of the bucket in which the video is located (for example, (for example, myBucket). \n\n"+
                "   video - The name of video (for example, people.mp4). \n\n" +
                "   topicArn - The ARN of the Amazon Simple Notification Service (Amazon SNS) topic. \n\n" +
                "   roleArn - The ARN of the AWS Identity and Access Management (IAM) role to use. \n\n" ;

       if (args.length != 4) {
             System.out.println(usage);
             System.exit(1);
       }

        String bucket = args[0];
        String video = args[1];
        String topicArn = args[2];
        String roleArn = args[3];

        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        NotificationChannel channel = NotificationChannel.builder()
                .snsTopicArn(topicArn)
                .roleArn(roleArn)
                .build();

        StartFaceDetection(rekClient, channel, bucket, video);
        GetFaceResults(rekClient);
        System.out.println("This example is done!");
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.recognize_video_faces.main]
    public static void StartFaceDetection(RekognitionClient rekClient,
                                          NotificationChannel channel,
                                          String bucket,
                                          String video) {

        try {
            S3Object s3Obj = S3Object.builder()
                    .bucket(bucket)
                    .name(video)
                    .build();

            Video vidOb = Video.builder()
                    .s3Object(s3Obj)
                    .build();

            StartFaceDetectionRequest  faceDetectionRequest = StartFaceDetectionRequest.builder()
                    .jobTag("Faces")
                    .faceAttributes(FaceAttributes.ALL)
                    .notificationChannel(channel)
                    .video(vidOb)
                    .build();

            StartFaceDetectionResponse startLabelDetectionResult = rekClient.startFaceDetection(faceDetectionRequest);
            startJobId=startLabelDetectionResult.jobId();

        } catch(RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void GetFaceResults(RekognitionClient rekClient) {

        try {
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

                // Proceed when the job is done - otherwise VideoMetadata is null
                VideoMetadata videoMetaData=faceDetectionResponse.videoMetadata();

                System.out.println("Format: " + videoMetaData.format());
                System.out.println("Codec: " + videoMetaData.codec());
                System.out.println("Duration: " + videoMetaData.durationMillis());
                System.out.println("FrameRate: " + videoMetaData.frameRate());
                System.out.println("Job");

                // Show face information
                List<FaceDetection> faces= faceDetectionResponse.faces();

                for (FaceDetection face: faces) {

                    String age = face.face().ageRange().toString();
                    String beard = face.face().beard().toString();
                    String eyeglasses = face.face().eyeglasses().toString();
                    String eyesOpen = face.face().eyesOpen().toString();
                    String mustache = face.face().mustache().toString();
                    String smile = face.face().smile().toString();
                }

            } while (faceDetectionResponse !=null && faceDetectionResponse.nextToken() != null);

        } catch(RekognitionException | InterruptedException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rekognition.java2.recognize_video_faces.main]
  }

