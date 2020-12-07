// snippet-sourcedescription:[VideoDetect.java demonstrates how to detect labels in a video by using Amazon Rekognition Video label detection operation and an Amazon SNS topic.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[12-01-2020]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.rekognition;

// snippet-start:[rekognition.java2.recognize_video_detect.import]
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.StartLabelDetectionResponse;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.Video;
import software.amazon.awssdk.services.rekognition.model.StartLabelDetectionRequest;
import software.amazon.awssdk.services.rekognition.model.GetLabelDetectionRequest;
import software.amazon.awssdk.services.rekognition.model.GetLabelDetectionResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.LabelDetectionSortBy;
import software.amazon.awssdk.services.rekognition.model.VideoMetadata;
import software.amazon.awssdk.services.rekognition.model.LabelDetection;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.Instance;
import software.amazon.awssdk.services.rekognition.model.Parent;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import java.util.List;
// snippet-end:[rekognition.java2.recognize_video_detect.import]

/**
 *  To run this code example, ensure that you perform the Prerequisites as stated in the Amazon Rekognition Guide:
 *  https://docs.aws.amazon.com/rekognition/latest/dg/video-analyzing-with-sqs.html
 */
public class VideoDetect {

    private static String startJobId ="";

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "VideoDetect <bucket> <video> <queueUrl> <topicArn> <roleArn>\n\n" +
                "Where:\n" +
                "bucket - the name of the bucket in which the video is located (for example, (for example, myBucket). \n\n"+
                "video - the name of video (for example, people.mp4). \n\n" +
                "queueUrl- the URL of a SQS queue \n\n" +
                "topicArn - the ARN of the Amazon Simple Notification Service (Amazon SNS) topic. \n\n" +
                "roleArn - the ARN of the AWS Identity and Access Management (IAM) role to use. \n\n" ;

         if (args.length != 5) {
             System.out.println(USAGE);
             System.exit(1);
         }

        String bucket = args[0];
        String video = args[1];
        String queueUrl = args[2];
        String topicArn = args[3];
        String roleArn = args[4];

        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        SqsClient sqs = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        NotificationChannel channel = NotificationChannel.builder()
                .snsTopicArn(topicArn)
                .roleArn(roleArn)
                .build();

        startLabels(rekClient, channel, bucket, video);
        getLabelJob(rekClient, sqs,  queueUrl);
        System.out.println("This example is done!");
        sqs.close();
        rekClient.close();
    }

    // snippet-start:[rekognition.java2.recognize_video_detect.main]
    public static void startLabels(RekognitionClient rekClient,
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

            StartLabelDetectionRequest labelDetectionRequest = StartLabelDetectionRequest.builder()
                    .jobTag("DetectingLabels")
                    .notificationChannel(channel)
                    .video(vidOb)
                    .minConfidence(50F)
                    .build();

            StartLabelDetectionResponse labelDetectionResponse = rekClient.startLabelDetection(labelDetectionRequest);
            startJobId = labelDetectionResponse.jobId();

            boolean ans = true;
            String status = "";
            int yy = 0;
            while (ans) {

                GetLabelDetectionRequest detectionRequest = GetLabelDetectionRequest.builder()
                        .jobId(startJobId)
                        .maxResults(10)
                        .build();

                GetLabelDetectionResponse result = rekClient.getLabelDetection(detectionRequest);
                status = result.jobStatusAsString();

                if (status.compareTo("SUCCEEDED") == 0)
                    ans = false;
                else
                    System.out.println(yy +" status is: "+status);

                Thread.sleep(1000);
                yy++;
            }

            System.out.println(startJobId +" status is: "+status);
        } catch(RekognitionException | InterruptedException e) {
            e.getMessage();
            System.exit(1);
        }
    }

    public static void getLabelJob(RekognitionClient rekClient,
                                   SqsClient sqs,
                                   String queueUrl) {

        List<Message> messages=null;
        ReceiveMessageRequest messageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .build();

        try {
            messages = sqs.receiveMessage(messageRequest).messages();

            if (!messages.isEmpty()) {
                //Loop through messages received.
                for (Message message: messages) {
                    String notification = message.body();

                    // Get status and job id from notification.
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonMessageTree = mapper.readTree(notification);
                    JsonNode messageBodyText = jsonMessageTree.get("Message");
                    ObjectMapper operationResultMapper = new ObjectMapper();
                    JsonNode jsonResultTree = operationResultMapper.readTree(messageBodyText.textValue());
                    JsonNode operationJobId = jsonResultTree.get("JobId");
                    JsonNode operationStatus = jsonResultTree.get("Status");
                    System.out.println("Job found in JSON is " + operationJobId);
                    // Found job. Get the results and display.

                    DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .build();

                    String jobId = operationJobId.textValue();

                    if (startJobId.compareTo(jobId)==0) {

                        System.out.println("Job id: " + operationJobId );
                        System.out.println("Status : " + operationStatus.toString());

                        if (operationStatus.asText().equals("SUCCEEDED"))
                              GetResultsLabels(rekClient);
                        else
                            System.out.println("Video analysis failed");

                        sqs.deleteMessage(deleteMessageRequest);
                    }

                    else{
                        System.out.println("Job received was not job " +  startJobId);

                        // Delete unknown message. Consider moving message to dead letter queue
                        sqs.deleteMessage(deleteMessageRequest);
                    }
                }
            }

        } catch(RekognitionException e) {
            e.getMessage();
            System.exit(1);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Gets the results of labels detection by calling GetLabelDetection. Label
    // detection is started by a call to StartLabelDetection.
    private static void GetResultsLabels(RekognitionClient rekClient) {

        int maxResults=10;
        String paginationToken=null;
        GetLabelDetectionResponse labelDetectionResult=null;

        try {
            do {
                if (labelDetectionResult !=null)
                    paginationToken = labelDetectionResult.nextToken();


                GetLabelDetectionRequest labelDetectionRequest= GetLabelDetectionRequest.builder()
                    .jobId(startJobId)
                    .sortBy(LabelDetectionSortBy.TIMESTAMP)
                    .maxResults(maxResults)
                    .nextToken(paginationToken)
                    .build();

                labelDetectionResult = rekClient.getLabelDetection(labelDetectionRequest);
                VideoMetadata videoMetaData=labelDetectionResult.videoMetadata();

                System.out.println("Format: " + videoMetaData.format());
                System.out.println("Codec: " + videoMetaData.codec());
                System.out.println("Duration: " + videoMetaData.durationMillis());
                System.out.println("FrameRate: " + videoMetaData.frameRate());

                List<LabelDetection> detectedLabels= labelDetectionResult.labels();
                for (LabelDetection detectedLabel: detectedLabels) {
                    long seconds=detectedLabel.timestamp();
                    Label label=detectedLabel.label();
                    System.out.println("Millisecond: " + Long.toString(seconds) + " ");

                    System.out.println("   Label:" + label.name());
                    System.out.println("   Confidence:" + detectedLabel.label().confidence().toString());

                    List<Instance> instances = label.instances();
                    System.out.println("   Instances of " + label.name());

                    if (instances.isEmpty()) {
                        System.out.println("        " + "None");
                    }  else {
                        for (Instance instance : instances) {
                            System.out.println("        Confidence: " + instance.confidence().toString());
                            System.out.println("        Bounding box: " + instance.boundingBox().toString());
                        }
                    }
                    System.out.println("   Parent labels for " + label.name() + ":");
                    List<Parent> parents = label.parents();

                    if (parents.isEmpty()) {
                        System.out.println("        None");
                    } else {
                        for (Parent parent : parents) {
                            System.out.println("        " + parent.name());
                        }
                    }
                    System.out.println();
                }
            } while (labelDetectionResult !=null && labelDetectionResult.nextToken() != null);

        } catch(RekognitionException e) {
            e.getMessage();
            System.exit(1);
        }
        }
    // snippet-end:[rekognition.java2.recognize_video_detect.main]
}

