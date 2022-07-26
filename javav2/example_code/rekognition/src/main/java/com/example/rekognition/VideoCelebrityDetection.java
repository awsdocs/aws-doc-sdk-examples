// snippet-sourcedescription:[VideoCelebrityDetection.java demonstrates how to get celebrity results from a video located in an Amazon S3 bucket.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.rekognition;

// snippet-start:[rekognition.java2.recognize_video_celebrity.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.rekognition.model.Video;
import software.amazon.awssdk.services.rekognition.model.StartCelebrityRecognitionResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.CelebrityRecognitionSortBy;
import software.amazon.awssdk.services.rekognition.model.VideoMetadata;
import software.amazon.awssdk.services.rekognition.model.CelebrityRecognition;
import software.amazon.awssdk.services.rekognition.model.CelebrityDetail;
import software.amazon.awssdk.services.rekognition.model.StartCelebrityRecognitionRequest;
import software.amazon.awssdk.services.rekognition.model.GetCelebrityRecognitionRequest;
import software.amazon.awssdk.services.rekognition.model.GetCelebrityRecognitionResponse;
import java.util.List;
// snippet-end:[rekognition.java2.recognize_video_celebrity.import]

/**
 *  To run this code example, ensure that you perform the Prerequisites as stated in the Amazon Rekognition Guide:
 *  https://docs.aws.amazon.com/rekognition/latest/dg/video-analyzing-with-sqs.html
 *
 * Also, ensure that set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class VideoCelebrityDetection {

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

       StartCelebrityDetection(rekClient, channel, bucket, video);
       GetCelebrityDetectionResults(rekClient);
       System.out.println("This example is done!");
       rekClient.close();
    }

    // snippet-start:[rekognition.java2.recognize_video_celebrity.main]
    public static void StartCelebrityDetection(RekognitionClient rekClient,
                                                NotificationChannel channel,
                                                String bucket,
                                                String video){
        try {
            S3Object s3Obj = S3Object.builder()
                .bucket(bucket)
                .name(video)
                .build();

            Video vidOb = Video.builder()
                .s3Object(s3Obj)
                .build();

            StartCelebrityRecognitionRequest recognitionRequest = StartCelebrityRecognitionRequest.builder()
                .jobTag("Celebrities")
                .notificationChannel(channel)
                .video(vidOb)
                .build();

            StartCelebrityRecognitionResponse startCelebrityRecognitionResult = rekClient.startCelebrityRecognition(recognitionRequest);
            startJobId = startCelebrityRecognitionResult.jobId();

        } catch(RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void GetCelebrityDetectionResults(RekognitionClient rekClient) {

        try {
            String paginationToken=null;
            GetCelebrityRecognitionResponse recognitionResponse = null;
            boolean finished = false;
            String status;
            int yy=0 ;

            do{
                if (recognitionResponse !=null)
                    paginationToken = recognitionResponse.nextToken();

                GetCelebrityRecognitionRequest recognitionRequest = GetCelebrityRecognitionRequest.builder()
                    .jobId(startJobId)
                    .nextToken(paginationToken)
                    .sortBy(CelebrityRecognitionSortBy.TIMESTAMP)
                    .maxResults(10)
                    .build();

                // Wait until the job succeeds
                while (!finished) {
                    recognitionResponse = rekClient.getCelebrityRecognition(recognitionRequest);
                    status = recognitionResponse.jobStatusAsString();

                    if (status.compareTo("SUCCEEDED") == 0)
                        finished = true;
                    else {
                        System.out.println(yy + " status is: " + status);
                        Thread.sleep(1000);
                    }
                    yy++;
                }

                finished = false;

                // Proceed when the job is done - otherwise VideoMetadata is null.
                VideoMetadata videoMetaData=recognitionResponse.videoMetadata();
                System.out.println("Format: " + videoMetaData.format());
                System.out.println("Codec: " + videoMetaData.codec());
                System.out.println("Duration: " + videoMetaData.durationMillis());
                System.out.println("FrameRate: " + videoMetaData.frameRate());
                System.out.println("Job");

                List<CelebrityRecognition> celebs= recognitionResponse.celebrities();
                for (CelebrityRecognition celeb: celebs) {
                    long seconds=celeb.timestamp()/1000;
                    System.out.print("Sec: " + seconds + " ");
                    CelebrityDetail details=celeb.celebrity();
                    System.out.println("Name: " + details.name());
                    System.out.println("Id: " + details.id());
                    System.out.println();
                }

            } while (recognitionResponse.nextToken() != null);

        } catch(RekognitionException | InterruptedException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rekognition.java2.recognize_video_celebrity.main]
}
