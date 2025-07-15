// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.mediaconvert

// snippet-start:[mediaconvert.kotlin.createjob.import]
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.endpoints.MediaConvertEndpointProvider
import aws.sdk.kotlin.services.mediaconvert.model.ContainerSettings
import aws.sdk.kotlin.services.mediaconvert.model.ContainerType
import aws.sdk.kotlin.services.mediaconvert.model.CreateJobRequest
import aws.sdk.kotlin.services.mediaconvert.model.DescribeEndpointsRequest
import aws.sdk.kotlin.services.mediaconvert.model.FileGroupSettings
import aws.sdk.kotlin.services.mediaconvert.model.H264CodecLevel
import aws.sdk.kotlin.services.mediaconvert.model.H264CodecProfile
import aws.sdk.kotlin.services.mediaconvert.model.H264FramerateControl
import aws.sdk.kotlin.services.mediaconvert.model.H264QvbrSettings
import aws.sdk.kotlin.services.mediaconvert.model.H264RateControlMode
import aws.sdk.kotlin.services.mediaconvert.model.H264Settings
import aws.sdk.kotlin.services.mediaconvert.model.Input
import aws.sdk.kotlin.services.mediaconvert.model.JobSettings
import aws.sdk.kotlin.services.mediaconvert.model.Output
import aws.sdk.kotlin.services.mediaconvert.model.OutputGroup
import aws.sdk.kotlin.services.mediaconvert.model.OutputGroupSettings
import aws.sdk.kotlin.services.mediaconvert.model.OutputGroupType
import aws.sdk.kotlin.services.mediaconvert.model.VideoCodec
import aws.sdk.kotlin.services.mediaconvert.model.VideoCodecSettings
import aws.sdk.kotlin.services.mediaconvert.model.VideoDescription
import aws.smithy.kotlin.runtime.client.endpoints.Endpoint
import kotlin.system.exitProcess
// snippet-end:[mediaconvert.kotlin.createjob.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

 In the following example, the output of the job is placed in the same Amazon S3 bucket in a folder named out.
*/

suspend fun main(args: Array<String>) {
    val usage = """
         Usage
            <mcRoleARN> <fileInput> 

        Where:
            mcRoleARN - the MediaConvert Role ARN.
            fileInput -  the URL of an Amazon S3 bucket where the input file is located (for example s3://<bucket name>/<mp4 file name>).
        """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val mcRoleARN = args[0]
    val fileInput = args[1]
    val mcClient = MediaConvertClient.fromEnvironment { region = "us-west-2" }
    val id = createMediaJob(mcClient, mcRoleARN, fileInput)
    println("MediaConvert job $id was created successfully!")
}

// snippet-start:[mediaconvert.kotlin.createjob.main]
suspend fun createMediaJob(
    mcClient: MediaConvertClient,
    mcRoleARN: String,
    fileInput1: String,
): String? {
    // Step 1: Describe endpoints to get the MediaConvert endpoint URL
    val describeResponse = mcClient.describeEndpoints(
        DescribeEndpointsRequest {
            maxResults = 1
        },
    )

    val endpointUrl = describeResponse.endpoints?.firstOrNull()?.url
        ?: error("No MediaConvert endpoint found")

    // Step 2: Create MediaConvert client with resolved endpoint
    val mediaConvert = MediaConvertClient.fromEnvironment {
        region = "us-west-2"
        endpointProvider = MediaConvertEndpointProvider {
            Endpoint(endpointUrl)
        }
    }

    // Output destination folder in S3 - put in 'output/' folder beside input
    val outputDestination = fileInput1.substringBeforeLast('/') + "/output/"

    // Step 3: Create the job request with minimal valid video codec settings
    val jobRequest = CreateJobRequest {
        role = mcRoleARN
        settings = JobSettings {
            inputs = listOf(
                Input {
                    fileInput = fileInput1
                },
            )
            outputGroups = listOf(
                OutputGroup {
                    outputGroupSettings = OutputGroupSettings {
                        type = OutputGroupType.FileGroupSettings
                        fileGroupSettings = FileGroupSettings {
                            destination = outputDestination
                        }
                    }
                    outputs = listOf(
                        Output {
                            containerSettings = ContainerSettings {
                                container = ContainerType.Mp4
                            }
                            videoDescription = VideoDescription {
                                width = 1280
                                height = 720
                                codecSettings = VideoCodecSettings {
                                    codec = VideoCodec.H264
                                    h264Settings = H264Settings {
                                        rateControlMode = H264RateControlMode.Qvbr
                                        qvbrSettings = H264QvbrSettings {
                                            qvbrQualityLevel = 7
                                        }
                                        maxBitrate = 5_000_000
                                        codecLevel = H264CodecLevel.Auto
                                        codecProfile = H264CodecProfile.Main
                                        framerateControl = H264FramerateControl.InitializeFromSource
                                    }
                                }
                            }
                        },
                    )
                },
            )
        }
    }

    // Step 4: Call MediaConvert to create the job
    val response = mediaConvert.createJob(jobRequest)

    // Return the job ID or null if not found
    return response.job?.id
}
// snippet-end:[mediaconvert.kotlin.createjob.main]
