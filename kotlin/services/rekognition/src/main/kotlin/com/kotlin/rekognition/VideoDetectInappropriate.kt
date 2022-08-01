// snippet-sourcedescription:[VideoDetectInappropriate.kt demonstrates how to detect inappropriate or offensive content in a video stored in an Amazon S3 bucket.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.recognize_video_moderation.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.GetContentModerationRequest
import aws.sdk.kotlin.services.rekognition.model.GetContentModerationResponse
import aws.sdk.kotlin.services.rekognition.model.NotificationChannel
import aws.sdk.kotlin.services.rekognition.model.S3Object
import aws.sdk.kotlin.services.rekognition.model.StartContentModerationRequest
import aws.sdk.kotlin.services.rekognition.model.Video
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.recognize_video_moderation.import]

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
        exitProcess(1)
    }

    val bucket = args[0]
    val video = args[1]
    val topicArn = args[2]
    val roleArnVal = args[3]
    val rekClient = RekognitionClient { region = "us-east-1" }

    val channel = NotificationChannel {
        snsTopicArn = topicArn
        roleArn = roleArnVal
    }

    startModerationDetection(channel, bucket, video)
    getModResults()
    rekClient.close()
}
// snippet-start:[rekognition.kotlin.recognize_video_moderation.main]
suspend fun startModerationDetection(channel: NotificationChannel?, bucketVal: String?, videoVal: String?) {

    val s3Obj = S3Object {
        bucket = bucketVal
        name = videoVal
    }
    val vidOb = Video {
        s3Object = s3Obj
    }
    val request = StartContentModerationRequest {
        jobTag = "Moderation"
        notificationChannel = channel
        video = vidOb
    }

    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        val startModDetectionResult = rekClient.startContentModeration(request)
        startJobId = startModDetectionResult.jobId.toString()
    }
}

suspend fun getModResults() {
    var finished = false
    var status: String
    var yy = 0
    RekognitionClient { region = "us-east-1" }.use { rekClient ->
        var modDetectionResponse: GetContentModerationResponse? = null

        val modRequest = GetContentModerationRequest {
            jobId = startJobId
            maxResults = 10
        }

        // Wait until the job succeeds.
        while (!finished) {
            modDetectionResponse = rekClient.getContentModeration(modRequest)
            status = modDetectionResponse.jobStatus.toString()
            if (status.compareTo("SUCCEEDED") == 0)
                finished = true
            else {
                println("$yy status is: $status")
                delay(1000)
            }
            yy++
        }

        // Proceed when the job is done - otherwise VideoMetadata is null.
        val videoMetaData = modDetectionResponse?.videoMetadata
        println("Format: ${videoMetaData?.format}")
        println("Codec: ${videoMetaData?.codec}")
        println("Duration: ${videoMetaData?.durationMillis}")
        println("FrameRate: ${videoMetaData?.frameRate}")

        modDetectionResponse?.moderationLabels?.forEach { mod ->
            val seconds: Long = mod.timestamp / 1000
            print("Mod label: $seconds ")
            println(mod.moderationLabel)
        }
    }
}
// snippet-end:[rekognition.kotlin.recognize_video_moderation.main]
