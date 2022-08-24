// snippet-sourcedescription:[CreateStreamProcessor.java demonstrates how to create an Amazon Rekognition stream processor that you can use to detect and recognize faces in a streaming video..]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rekognition;

// snippet-start:[rekognition.java2.create_streamprocessor.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.CreateStreamProcessorRequest;
import software.amazon.awssdk.services.rekognition.model.CreateStreamProcessorResponse;
import software.amazon.awssdk.services.rekognition.model.FaceSearchSettings;
import software.amazon.awssdk.services.rekognition.model.KinesisDataStream;
import software.amazon.awssdk.services.rekognition.model.KinesisVideoStream;
import software.amazon.awssdk.services.rekognition.model.ListStreamProcessorsRequest;
import software.amazon.awssdk.services.rekognition.model.ListStreamProcessorsResponse;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.StreamProcessor;
import software.amazon.awssdk.services.rekognition.model.StreamProcessorInput;
import software.amazon.awssdk.services.rekognition.model.StreamProcessorSettings;
import software.amazon.awssdk.services.rekognition.model.StreamProcessorOutput;
import software.amazon.awssdk.services.rekognition.model.StartStreamProcessorRequest;
import software.amazon.awssdk.services.rekognition.model.DescribeStreamProcessorRequest;
import software.amazon.awssdk.services.rekognition.model.DescribeStreamProcessorResponse;
// snippet-end:[rekognition.java2.create_streamprocessor.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateStreamProcessor {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <role> <kinInputStream> <kinOutputStream> <collectionName> <StreamProcessorName>\n\n" +
            "Where:\n" +
            "   role - The ARN of the AWS Identity and Access Management (IAM) role to use.  \n\n" +
            "   kinInputStream - The ARN of the Kinesis video stream. \n\n" +
            "   kinOutputStream - The ARN of the Kinesis data stream. \n\n" +
            "   collectionName - The name of the collection to use that contains content.  \n\n" +
            "   StreamProcessorName - The name of the Stream Processor.  \n\n";

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String role = args[0];
        String kinInputStream = args[1];
        String kinOutputStream = args[2] ;
        String collectionName = args[3];
        String streamProcessorName = args[4];

        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        processCollection(rekClient,streamProcessorName, kinInputStream, kinOutputStream, collectionName, role);
        startSpecificStreamProcessor(rekClient, streamProcessorName);
        listStreamProcessors(rekClient);
        describeStreamProcessor(rekClient, streamProcessorName);
        deleteSpecificStreamProcessor(rekClient, streamProcessorName);
    }

    // snippet-start:[rekognition.java2.create_streamprocessor.main]
    public static void listStreamProcessors(RekognitionClient rekClient) {

        ListStreamProcessorsRequest request = ListStreamProcessorsRequest.builder()
            .maxResults(15)
            .build();

        ListStreamProcessorsResponse listStreamProcessorsResult = rekClient.listStreamProcessors(request);
        for (StreamProcessor streamProcessor : listStreamProcessorsResult.streamProcessors()) {
            System.out.println("StreamProcessor name - " + streamProcessor.name());
            System.out.println("Status - " + streamProcessor.status());
        }
    }

    private static void describeStreamProcessor(RekognitionClient rekClient, String StreamProcessorName) {

        DescribeStreamProcessorRequest streamProcessorRequest = DescribeStreamProcessorRequest.builder()
            .name(StreamProcessorName)
            .build();

        DescribeStreamProcessorResponse describeStreamProcessorResult = rekClient.describeStreamProcessor(streamProcessorRequest);
        System.out.println("Arn - " + describeStreamProcessorResult.streamProcessorArn());
        System.out.println("Input kinesisVideo stream - "
              + describeStreamProcessorResult.input().kinesisVideoStream().arn());
        System.out.println("Output kinesisData stream - "
              + describeStreamProcessorResult.output().kinesisDataStream().arn());
        System.out.println("RoleArn - " + describeStreamProcessorResult.roleArn());
        System.out.println(
              "CollectionId - " + describeStreamProcessorResult.settings().faceSearch().collectionId());
        System.out.println("Status - " + describeStreamProcessorResult.status());
        System.out.println("Status message - " + describeStreamProcessorResult.statusMessage());
        System.out.println("Creation timestamp - " + describeStreamProcessorResult.creationTimestamp());
        System.out.println("Last update timestamp - " + describeStreamProcessorResult.lastUpdateTimestamp());
    }

    private static void startSpecificStreamProcessor(RekognitionClient rekClient, String StreamProcessorName) {
        try {
            StartStreamProcessorRequest streamProcessorRequest = StartStreamProcessorRequest.builder()
                .name(StreamProcessorName)
                .build();

            rekClient.startStreamProcessor(streamProcessorRequest);
            System.out.println("Stream Processor " + StreamProcessorName + " started.");

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void processCollection(RekognitionClient rekClient, String StreamProcessorName, String kinInputStream, String kinOutputStream, String collectionName, String role ) {

        try {
            KinesisVideoStream videoStream = KinesisVideoStream.builder()
                .arn(kinInputStream)
                .build();

            KinesisDataStream dataStream = KinesisDataStream.builder()
                .arn(kinOutputStream)
                .build();

            StreamProcessorOutput processorOutput = StreamProcessorOutput.builder()
                .kinesisDataStream(dataStream)
                .build();

            StreamProcessorInput processorInput = StreamProcessorInput.builder()
                .kinesisVideoStream(videoStream)
                .build();

            FaceSearchSettings searchSettings = FaceSearchSettings.builder()
                .faceMatchThreshold(75f)
                .collectionId(collectionName)
                .build() ;

            StreamProcessorSettings processorSettings = StreamProcessorSettings.builder()
                .faceSearch(searchSettings)
                .build();

            CreateStreamProcessorRequest processorRequest = CreateStreamProcessorRequest.builder()
                .name(StreamProcessorName)
                .input(processorInput)
                .output(processorOutput)
                .roleArn(role)
                .settings(processorSettings)
                .build();

            CreateStreamProcessorResponse response = rekClient.createStreamProcessor(processorRequest);
            System.out.println("The ARN for the newly create stream processor is "+response.streamProcessorArn());

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void deleteSpecificStreamProcessor(RekognitionClient rekClient, String StreamProcessorName) {

        rekClient.stopStreamProcessor(a->a.name(StreamProcessorName));
        rekClient.deleteStreamProcessor(a->a.name(StreamProcessorName));
        System.out.println("Stream Processor " + StreamProcessorName + " deleted.");
    }
    // snippet-end:[rekognition.java2.create_streamprocessor.main]
 }
