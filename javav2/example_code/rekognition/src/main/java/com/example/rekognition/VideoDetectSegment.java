// snippet-sourcedescription:[VideoDetectSegment.java demonstrates how to detect technical cue segments and shot detection segments in a video stored in an Amazon S3 bucket.]
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

// snippet-start:[rekognition.java2.recognize_video_segments.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.rekognition.model.Video;
import software.amazon.awssdk.services.rekognition.model.StartShotDetectionFilter;
import software.amazon.awssdk.services.rekognition.model.StartTechnicalCueDetectionFilter;
import software.amazon.awssdk.services.rekognition.model.StartSegmentDetectionFilters;
import software.amazon.awssdk.services.rekognition.model.StartSegmentDetectionRequest;
import software.amazon.awssdk.services.rekognition.model.StartSegmentDetectionResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.GetSegmentDetectionResponse;
import software.amazon.awssdk.services.rekognition.model.GetSegmentDetectionRequest;
import software.amazon.awssdk.services.rekognition.model.VideoMetadata;
import software.amazon.awssdk.services.rekognition.model.SegmentDetection;
import software.amazon.awssdk.services.rekognition.model.TechnicalCueSegment;
import software.amazon.awssdk.services.rekognition.model.ShotSegment;
import software.amazon.awssdk.services.rekognition.model.SegmentType;
import software.amazon.awssdk.services.sqs.SqsClient;
import java.util.List;
// snippet-end:[rekognition.java2.recognize_video_segments.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class VideoDetectSegment {

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

        SqsClient sqs = SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        NotificationChannel channel = NotificationChannel.builder()
                .snsTopicArn(topicArn)
                .roleArn(roleArn)
                .build();

        StartSegmentDetection(rekClient, channel, bucket, video);
        getSegmentResults(rekClient);
        System.out.println("This example is done!");
        sqs.close();
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.recognize_video_segments.main]
    public static void StartSegmentDetection (RekognitionClient rekClient,
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

            StartShotDetectionFilter cueDetectionFilter = StartShotDetectionFilter.builder()
                    .minSegmentConfidence(60F)
                    .build();


            StartTechnicalCueDetectionFilter technicalCueDetectionFilter = StartTechnicalCueDetectionFilter.builder()
                    .minSegmentConfidence(60F)
                    .build();

            StartSegmentDetectionFilters filters = StartSegmentDetectionFilters.builder()
                    .shotFilter(cueDetectionFilter)
                    .technicalCueFilter(technicalCueDetectionFilter)
                    .build();

            StartSegmentDetectionRequest segDetectionRequest = StartSegmentDetectionRequest.builder()
                    .jobTag("DetectingLabels")
                    .notificationChannel(channel)
                    .segmentTypes(SegmentType.TECHNICAL_CUE , SegmentType.SHOT)
                    .video(vidOb)
                    .filters(filters)
                    .build();

            StartSegmentDetectionResponse segDetectionResponse = rekClient.startSegmentDetection(segDetectionRequest);
            startJobId = segDetectionResponse.jobId();

        } catch(RekognitionException e) {
            e.getMessage();
            System.exit(1);
        }
    }

    public static void getSegmentResults(RekognitionClient rekClient) {

        try {
            String paginationToken = null;
            GetSegmentDetectionResponse segDetectionResponse = null;
            Boolean finished = false;
            String status = "";
            int yy = 0;

            do {

                if (segDetectionResponse != null)
                    paginationToken = segDetectionResponse.nextToken();

                GetSegmentDetectionRequest recognitionRequest = GetSegmentDetectionRequest.builder()
                        .jobId(startJobId)
                        .nextToken(paginationToken)
                        .maxResults(10)
                        .build();

                // Wait until the job succeeds
                while (!finished) {

                    segDetectionResponse = rekClient.getSegmentDetection(recognitionRequest);
                    status = segDetectionResponse.jobStatusAsString();

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
                List<VideoMetadata> videoMetaData = segDetectionResponse.videoMetadata();

                for (VideoMetadata metaData : videoMetaData) {
                    System.out.println("Format: " + metaData.format());
                    System.out.println("Codec: " + metaData.codec());
                    System.out.println("Duration: " + metaData.durationMillis());
                    System.out.println("FrameRate: " + metaData.frameRate());
                    System.out.println("Job");
                }

                List<SegmentDetection> detectedSegment = segDetectionResponse.segments();
                String type = detectedSegment.get(0).type().toString();

                if (type.contains(SegmentType.TECHNICAL_CUE.toString())) {
                    System.out.println("Technical Cue");
                    TechnicalCueSegment segmentCue = detectedSegment.get(0).technicalCueSegment();
                    System.out.println("\tType: " + segmentCue.type());
                    System.out.println("\tConfidence: " + segmentCue.confidence().toString());
                }
                if (type.contains(SegmentType.SHOT.toString())) {
                    System.out.println("Shot");
                    ShotSegment segmentShot = detectedSegment.get(0).shotSegment();
                    System.out.println("\tIndex " + segmentShot.index());
                    System.out.println("\tConfidence: " + segmentShot.confidence().toString());
                }
                long seconds = detectedSegment.get(0).durationMillis();
                System.out.println("\tDuration : " + Long.toString(seconds) + " milliseconds");
                System.out.println("\tStart time code: " + detectedSegment.get(0).startTimecodeSMPTE());
                System.out.println("\tEnd time code: " + detectedSegment.get(0).endTimecodeSMPTE());
                System.out.println("\tDuration time code: " + detectedSegment.get(0).durationSMPTE());
                System.out.println();

        } while (segDetectionResponse !=null && segDetectionResponse.nextToken() != null);

        } catch(RekognitionException | InterruptedException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[rekognition.java2.recognize_video_segments.main]
}
