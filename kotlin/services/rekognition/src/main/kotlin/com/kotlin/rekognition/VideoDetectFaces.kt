// snippet-sourcedescription:[VideoDetectFaces.kt demonstrates how to detect faces in a video stored in an Amazon S3 bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.recognize_video_faces.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.FaceAttributes
import aws.sdk.kotlin.services.rekognition.model.GetFaceDetectionRequest
import aws.sdk.kotlin.services.rekognition.model.GetFaceDetectionResponse
import aws.sdk.kotlin.services.rekognition.model.NotificationChannel
import aws.sdk.kotlin.services.rekognition.model.S3Object
import aws.sdk.kotlin.services.rekognition.model.StartFaceDetectionRequest
import aws.sdk.kotlin.services.rekognition.model.Video
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.recognize_video_faces.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

private var startJobId = ""
suspend fun main(args: Array<String>) {

    val usage = """
        
        Usage: 
            <bucket> <video> <topicArn> <roleArn>
        
        Where:
            bucket - The name of the bucket in which the video is located (for example, (for example, myBucket). 
            video - The name of the video (for example, people.mp4). 
            topicArn - The ARN of the Amazon Simple Notification Service (Amazon SNS) topic. 
            roleArn - The ARN of the AWS Identity and Access Management (IAM) role to use. 
        
        """

    if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val bucket = args[0]
    val video = args[1]
    val topicArn = args[2]
    val roleArnVal = args[3]

    val channel = NotificationChannel {
        snsTopicArn = topicArn
        roleArn = roleArnVal
    }

    startFaceDetection(channel, bucket, video)
    getFaceResults()
}

// snippet-start:[rekognition.kotlin.recognize_video_faces.main]
suspend fun startFaceDetection(channelVal: NotificationChannel?, bucketVal: String, videoVal: String) {

    val s3Obj = S3Object {
        bucket = bucketVal
        name = videoVal
    }
    val vidOb = Video {
        s3Object = s3Obj
    }

    val request = StartFaceDetectionRequest {
        jobTag = "Faces"
        faceAttributes = FaceAttributes.All
        notificationChannel = channelVal
        video = vidOb
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val startLabelDetectionResult = rekClient.startFaceDetection(request)
        startJobId = startLabelDetectionResult.jobId.toString()
    }
}

suspend fun getFaceResults() {

    var finished = false
    var status: String
    var yy = 0
    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        var response: GetFaceDetectionResponse? = null

        val recognitionRequest = GetFaceDetectionRequest {
            jobId = startJobId
            maxResults = 10
        }

        // Wait until the job succeeds.
        while (!finished) {
            response = rekClient.getFaceDetection(recognitionRequest)
            status = response.jobStatus.toString()
            if (status.compareTo("SUCCEEDED") == 0)
                finished = true
            else {
                println("$yy status is: $status")
                delay(1000)
            }
            yy++
        }

        // Proceed when the job is done - otherwise VideoMetadata is null.
        val videoMetaData = response?.videoMetadata
        println("Format: ${videoMetaData?.format}")
        println("Codec: ${videoMetaData?.codec}")
        println("Duration: ${videoMetaData?.durationMillis}")
        println("FrameRate: ${videoMetaData?.frameRate}")

        // Show face information.
        response?.faces?.forEach { face ->
            println("Age: ${face.face?.ageRange}")
            println("Face: ${face.face?.beard}")
            println("Eye glasses: ${face?.face?.eyeglasses}")
            println("Mustache: ${face.face?.mustache}")
            println("Smile: ${face.face?.smile}")
        }
    }
}
// snippet-end:[rekognition.kotlin.recognize_video_faces.main]
