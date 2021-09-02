// snippet-sourcedescription:[VideoDetectFaces.kt demonstrates how to detect faces in a video stored in an Amazon S3 bucket.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06-09-2021]
// snippet-sourceauthor:[scmacdon - AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.rekognition

// snippet-start:[rekognition.kotlin.recognize_video_faces.import]
import aws.sdk.kotlin.services.rekognition.RekognitionClient
import aws.sdk.kotlin.services.rekognition.model.StartFaceDetectionRequest
import aws.sdk.kotlin.services.rekognition.model.NotificationChannel
import aws.sdk.kotlin.services.rekognition.model.S3Object
import aws.sdk.kotlin.services.rekognition.model.Video
import aws.sdk.kotlin.services.rekognition.model.FaceAttributes
import aws.sdk.kotlin.services.rekognition.model.RekognitionException
import aws.sdk.kotlin.services.rekognition.model.GetFaceDetectionResponse
import aws.sdk.kotlin.services.rekognition.model.GetFaceDetectionRequest
import kotlinx.coroutines.delay
import kotlin.system.exitProcess
// snippet-end:[rekognition.kotlin.recognize_video_faces.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

private var startJobId = ""
suspend fun main(args: Array<String>){

    val usage = """
        
        Usage: 
            <bucket> <video> <topicArn> <roleArn>
        
        Where:
            bucket - the name of the bucket in which the video is located (for example, (for example, myBucket). 
            video - the name of the video (for example, people.mp4). 
            topicArn - the ARN of the Amazon Simple Notification Service (Amazon SNS) topic. 
            roleArn - the ARN of the AWS Identity and Access Management (IAM) role to use. 
        
        """

    if (args.size != 4) {
         println(usage)
         exitProcess(0)
     }

    val bucket =args[0]
    val video = args[1]
    val topicArn = args[2]
    val roleArnVal = args[3]
    val rekClient = RekognitionClient{ region = "us-east-1"}

    val channel = NotificationChannel {
        snsTopicArn = topicArn
        roleArn = roleArnVal
    }

    startFaceDetection(rekClient, channel, bucket, video)
    getFaceResults(rekClient)
    rekClient.close()
}

// snippet-start:[rekognition.kotlin.recognize_video_faces.main]
suspend fun startFaceDetection(
    rekClient: RekognitionClient,
    channelVal: NotificationChannel?,
    bucketVal: String?,
    videoVal: String?
) {
    try {
        val s3Obj = S3Object {
            bucket = bucketVal
            name = videoVal
        }
        val vidOb = Video {
            s3Object = s3Obj
        }

        val faceDetectionRequest = StartFaceDetectionRequest {
             jobTag = "Faces"
             faceAttributes = FaceAttributes.All
             notificationChannel = channelVal
             video = vidOb
        }

        val startLabelDetectionResult = rekClient.startFaceDetection(faceDetectionRequest)
        startJobId = startLabelDetectionResult.jobId.toString()

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    }
}

suspend fun getFaceResults(rekClient: RekognitionClient) {
    try {
        var paginationToken: String? = null
        var faceDetectionResponse: GetFaceDetectionResponse? = null
        var finished = false
        var status = ""
        var yy = 0
        do {
            if (faceDetectionResponse != null)
                paginationToken = faceDetectionResponse.nextToken

            val recognitionRequest = GetFaceDetectionRequest {
                jobId = startJobId
                nextToken = paginationToken
                maxResults = 10
            }

            // Wait until the job succeeds
            while (!finished) {
                faceDetectionResponse = rekClient.getFaceDetection(recognitionRequest)
                status = faceDetectionResponse.jobStatus.toString()
                if (status.compareTo("SUCCEEDED") == 0)
                    finished = true
                else {
                    println("$yy status is: $status")
                    delay(1000)
                }
                yy++
            }
            finished = false

            // Proceed when the job is done - otherwise VideoMetadata is null
            val videoMetaData = faceDetectionResponse!!.videoMetadata
            if (videoMetaData != null)
                println("Format: ${videoMetaData.format}")

            if (videoMetaData != null)
                println("Codec: ${videoMetaData.codec}")

            if (videoMetaData != null)
                println("Duration: ${videoMetaData.durationMillis}")

            if (videoMetaData != null)
                println("FrameRate: ${videoMetaData.frameRate}")

            // Show face information
            val faces = faceDetectionResponse.faces
            if (faces != null) {
                for (face in faces) {
                    println("Age: ${face.face?.ageRange.toString()}")
                    println("Face: ${face.face?.beard.toString()}")
                    println("Eye glasses: ${face?.face?.eyeglasses.toString()}")
                    println("Mustache: ${face.face?.mustache.toString()}")
                    println("Smile: ${face.face?.smile.toString()}")

                }
            }
        } while (faceDetectionResponse != null && faceDetectionResponse.nextToken != null)

    } catch (e: RekognitionException) {
        println(e.message)
        rekClient.close()
        exitProcess(0)
    } catch (e: InterruptedException) {
        println(e.message)
        exitProcess(0)
    }
}
// snippet-end:[rekognition.kotlin.recognize_video_faces.main]
