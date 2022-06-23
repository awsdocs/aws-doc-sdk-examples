//snippet-sourcedescription:[CreateJob.kt demonstrates how to create AWS Elemental MediaConvert jobs.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[smacdon - AWS ]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediaconvert


// snippet-start:[mediaconvert.kotlin.createjob.import]
import aws.sdk.kotlin.runtime.endpoint.AwsEndpoint
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.runtime.endpoint.CredentialScope
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.model.*
import java.util.HashMap
import kotlin.system.exitProcess
// snippet-end:[mediaconvert.kotlin.createjob.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

        val usage = """
        
        Usage
            <mcRoleARN> <fileInput> 

        Where:
            mcRoleARN - the MediaConvert Role ARN.
            fileInput -  the URL of an Amazon S3 bucket where the input file is located.
        """

        if (args.size != 2) {
            println(usage)
            exitProcess(0)
        }

        val mcRoleARN = args[0]
        val fileInput = args[1]
        val mcClient = MediaConvertClient{region="us-west-2"}
        val id = createMediaJob(mcClient, mcRoleARN, fileInput)
        println("MediaConvert job is $id")
    }

// snippet-start:[mediaconvert.kotlin.createjob.main]
suspend fun createMediaJob(mcClient: MediaConvertClient, mcRoleARN: String, fileInputVal: String): String? {
    val s3path = fileInputVal.substring(0, fileInputVal.lastIndexOf('/') + 1) + "javasdk/out/"
    val fileOutput = s3path + "index"
    val thumbsOutput = s3path + "thumbs/"
    val mp4Output = s3path + "mp4/"

    try {
        val describeEndpoints = DescribeEndpointsRequest {
            maxResults = 20
        }

        val res = mcClient.describeEndpoints(describeEndpoints)

        if (res.endpoints?.size!! <= 0) {
            println("Cannot find MediaConvert service endpoint URL!")
            exitProcess(0)
        }
        val endpointURL = res.endpoints!!.get(0).url!!
        val mediaConvertClient = MediaConvertClient {

            region = "us-west-2"
            endpointResolver = AwsEndpointResolver { service, region ->
                AwsEndpoint(endpointURL, CredentialScope(region = "us-west-2"))
            }
        }


        // output group Preset HLS low profile
        val hlsLow = createOutput("hls_low", "_low", "_\$dt$", 750000, 7, 1920, 1080, 640)

        // output group Preset HLS medium profile
        val hlsMedium = createOutput("hls_medium", "_medium", "_\$dt$", 1200000, 7, 1920, 1080, 1280)

        // output group Preset HLS high profole
        val hlsHigh = createOutput("hls_high", "_high", "_\$dt$", 3500000, 8, 1920, 1080, 1920)

        val outputSettings = OutputGroupSettings {
            type = OutputGroupType.HlsGroupSettings
        }

        val OutputObsList: MutableList<Output> = mutableListOf()
        if (hlsLow != null) {
            OutputObsList.add(hlsLow)
        }
        if (hlsMedium != null) {
            OutputObsList.add(hlsMedium)
        }
        if (hlsHigh != null) {
            OutputObsList.add(hlsHigh)
        }

        // Create an OutputGroup object.
        val appleHLS = OutputGroup {
            name = "Apple HLS"
            customName = "Example"
            outputGroupSettings = OutputGroupSettings {
                type = OutputGroupType.HlsGroupSettings
                this.hlsGroupSettings = HlsGroupSettings {
                    directoryStructure = HlsDirectoryStructure.SingleDirectory
                    manifestDurationFormat = HlsManifestDurationFormat.Integer
                    streamInfResolution = HlsStreamInfResolution.Include
                    clientCache = HlsClientCache.Enabled
                    captionLanguageSetting = HlsCaptionLanguageSetting.Omit
                    manifestCompression = HlsManifestCompression.None
                    codecSpecification = HlsCodecSpecification.Rfc4281
                    outputSelection = HlsOutputSelection.ManifestsAndSegments
                    programDateTime = HlsProgramDateTime.Exclude
                    programDateTimePeriod = 600
                    timedMetadataId3Frame = HlsTimedMetadataId3Frame.Priv
                    timedMetadataId3Period = 10
                    destination = fileOutput
                    segmentControl = HlsSegmentControl.SegmentedFiles
                    minFinalSegmentLength = 0.toDouble()
                    segmentLength = 4
                    minSegmentLength = 1
                }
            }
            outputs = OutputObsList
        }

        val theOutput = Output {
            extension = "mp4"
            containerSettings = ContainerSettings {
                container = ContainerType.fromValue("MP4")
            }

            videoDescription = VideoDescription {
                width = 1280
                height = 720
                scalingBehavior = ScalingBehavior.Default
                sharpness = 50
                antiAlias = AntiAlias.Enabled
                timecodeInsertion = VideoTimecodeInsertion.Disabled
                colorMetadata = ColorMetadata.Insert
                respondToAfd = RespondToAfd.None
                afdSignaling = AfdSignaling.None
                dropFrameTimecode = DropFrameTimecode.Enabled
                codecSettings = VideoCodecSettings {
                    codec = VideoCodec.H264
                    h264Settings = H264Settings {
                        rateControlMode = H264RateControlMode.Qvbr
                        parControl = H264ParControl.InitializeFromSource
                        qualityTuningLevel = H264QualityTuningLevel.SinglePass
                        qvbrSettings = H264QvbrSettings { qvbrQualityLevel = 8 }
                        codecLevel = H264CodecLevel.Auto
                        codecProfile = H264CodecProfile.Main
                        maxBitrate = 2400000
                        framerateControl = H264FramerateControl.InitializeFromSource
                        gopSize = 2.0
                        gopSizeUnits = H264GopSizeUnits.Seconds
                        numberBFramesBetweenReferenceFrames = 2
                        gopClosedCadence = 1
                        gopBReference = H264GopBReference.Disabled
                        slowPal = H264SlowPal.Disabled
                        syntax = H264Syntax.Default
                        numberReferenceFrames = 3
                        dynamicSubGop = H264DynamicSubGop.Static
                        fieldEncoding = H264FieldEncoding.Paff
                        sceneChangeDetect = H264SceneChangeDetect.Enabled
                        minIInterval = 0
                        telecine = H264Telecine.None
                        framerateConversionAlgorithm = H264FramerateConversionAlgorithm.DuplicateDrop
                        entropyEncoding = H264EntropyEncoding.Cabac
                        slices = 1
                        unregisteredSeiTimecode = H264UnregisteredSeiTimecode.Disabled
                        repeatPps = H264RepeatPps.Disabled
                        adaptiveQuantization = H264AdaptiveQuantization.High
                        spatialAdaptiveQuantization = H264SpatialAdaptiveQuantization.Enabled
                        temporalAdaptiveQuantization = H264TemporalAdaptiveQuantization.Enabled
                        flickerAdaptiveQuantization = H264FlickerAdaptiveQuantization.Disabled
                        softness = 0
                        interlaceMode = H264InterlaceMode.Progressive
                    }
                }
            }

            audioDescriptions = listOf(AudioDescription {
                audioTypeControl = AudioTypeControl.FollowInput
                languageCodeControl = AudioLanguageCodeControl.FollowInput
                codecSettings = AudioCodecSettings {
                    codec = AudioCodec.Aac
                    aacSettings = AacSettings {
                        codecProfile = AacCodecProfile.Lc
                        rateControlMode = AacRateControlMode.Cbr
                        codingMode = AacCodingMode.CodingMode2_0
                        sampleRate = 44100
                        bitrate = 160000
                        rawFormat = AacRawFormat.None
                        specification = AacSpecification.Mpeg4
                        audioDescriptionBroadcasterMix = AacAudioDescriptionBroadcasterMix.Normal
                    }
                }
            })
        }

        // Create an OutputGroup
        val fileMp4 = OutputGroup {
            name = "File Group"
            customName = "mp4"
            outputGroupSettings = OutputGroupSettings {
                type = OutputGroupType.FileGroupSettings
                fileGroupSettings = FileGroupSettings {
                    destination = mp4Output
                }
            }
            outputs = listOf(theOutput)
        }


        val containerSettings1 = ContainerSettings {
            container = ContainerType.Raw
        }

        val thumbs = OutputGroup {
            name = "File Group"
            customName = "thumbs"
            outputGroupSettings = OutputGroupSettings {
                type = OutputGroupType.FileGroupSettings
                fileGroupSettings = FileGroupSettings {
                    destination = thumbsOutput
                }
            }

            outputs = listOf (Output{
                extension = "jpg"

                this.containerSettings = containerSettings1
                videoDescription = VideoDescription {
                    scalingBehavior = ScalingBehavior.Default
                    sharpness = 50
                    antiAlias = AntiAlias.Enabled
                    timecodeInsertion = VideoTimecodeInsertion.Disabled
                    colorMetadata = ColorMetadata.Insert
                    dropFrameTimecode = DropFrameTimecode.Enabled
                    codecSettings = VideoCodecSettings {
                        codec = VideoCodec.FrameCapture
                        frameCaptureSettings = FrameCaptureSettings {
                            framerateNumerator = 1
                            framerateDenominator = 1
                            maxCaptures = 10000000
                            quality = 80
                        }
                    }
                }
            })
        }

       val audioSelectors1: MutableMap<String, AudioSelector> = HashMap()
         audioSelectors1["Audio Selector 1"] =
            AudioSelector {
                defaultSelection = AudioDefaultSelection.Default
                offset = 0
            }


        val jobSettings = JobSettings {
            inputs = listOf(Input {
                audioSelectors = audioSelectors1
                videoSelector = VideoSelector {
                    colorSpace = ColorSpace.Follow
                    rotate = InputRotate.Degree0
                }
                filterEnable = InputFilterEnable.Auto
                filterStrength = 0
                deblockFilter = InputDeblockFilter.Disabled
                denoiseFilter = InputDenoiseFilter.Disabled
                psiControl = InputPsiControl.UsePsi
                timecodeSource = InputTimecodeSource.Embedded
                fileInput = fileInputVal


            outputGroups = listOf(appleHLS, thumbs, fileMp4)
        } )
      }

        val createJobRequest = CreateJobRequest {
            role = mcRoleARN
            settings = jobSettings
        }

         val createJobResponse = mediaConvertClient.createJob(createJobRequest)
         return createJobResponse.job?.id


   } catch (ex: MediaConvertException) {
        println(ex.message)
        mcClient.close()
        exitProcess(0)
    }
 }

fun createOutput(
    customName: String,
    nameModifierVal: String,
    segmentModifierVal: String,
    qvbrMaxBitrate: Int,
    qvbrQualityLevelVal: Int,
    originWidth: Int,
    originHeight: Int,
    targetWidth: Int
): Output? {

    val targetHeight = (Math.round((originHeight * targetWidth / originWidth).toFloat())
            - Math.round((originHeight * targetWidth / originWidth).toFloat()) % 4)

    var output: Output? = null

    try {

        val audio1 = AudioDescription {
            audioTypeControl = AudioTypeControl.FollowInput
            languageCodeControl = AudioLanguageCodeControl.FollowInput
            codecSettings = AudioCodecSettings {
                codec = AudioCodec.Aac
                aacSettings = AacSettings {
                    codecProfile = AacCodecProfile.Lc
                    rateControlMode = AacRateControlMode.Cbr
                    codingMode = AacCodingMode.CodingMode2_0
                    sampleRate = 44100
                    bitrate = 96000
                    rawFormat = AacRawFormat.None
                    specification = AacSpecification.Mpeg4
                    audioDescriptionBroadcasterMix = AacAudioDescriptionBroadcasterMix.Normal
                }
            }
        }

        output = Output {
            nameModifier = nameModifierVal
            outputSettings = OutputSettings {
                hlsSettings = HlsSettings {
                    segmentModifier = segmentModifierVal
                    audioGroupId = "program_audio"
                    iFrameOnlyManifest = HlsIFrameOnlyManifest.Exclude
                }
            }
            containerSettings = ContainerSettings {
                container = ContainerType.M3U8
                this.m3U8Settings = M3U8Settings {
                    audioFramesPerPes = 4
                    pcrControl = M3U8PcrControl.PcrEveryPesPacket
                    pmtPid = 480
                    privateMetadataPid = 503
                    programNumber = 1
                    patInterval = 0
                    pmtInterval = 0
                    scte35Source = M3U8Scte35Source.None
                    scte35Pid = 500
                    nielsenId3 = M3U8NielsenId3.None
                    timedMetadata = TimedMetadata.None
                    timedMetadataPid = 502
                    videoPid = 481
                    audioPids = listOf(482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492)
                }

                videoDescription = VideoDescription {
                    width = targetWidth
                    height = targetHeight
                    scalingBehavior = ScalingBehavior.Default
                    sharpness = 50
                    antiAlias = AntiAlias.Enabled
                    timecodeInsertion = VideoTimecodeInsertion.Disabled
                    colorMetadata = ColorMetadata.Insert
                    respondToAfd = RespondToAfd.None
                    afdSignaling = AfdSignaling.None
                    dropFrameTimecode = DropFrameTimecode.Enabled
                    codecSettings = VideoCodecSettings {
                        codec = VideoCodec.H264
                        h264Settings = H264Settings {
                            rateControlMode = H264RateControlMode.Qvbr
                            parControl = H264ParControl.InitializeFromSource
                            qualityTuningLevel = H264QualityTuningLevel.SinglePass
                            qvbrSettings = H264QvbrSettings {
                                qvbrQualityLevel = qvbrQualityLevelVal
                            }
                            codecLevel = H264CodecLevel.Auto
                            codecProfile =
                                if (targetHeight > 720 && targetWidth > 1280) H264CodecProfile.High else H264CodecProfile.Main
                            maxBitrate = qvbrMaxBitrate
                            framerateControl = H264FramerateControl.InitializeFromSource
                            gopSize = 2.0
                            gopSizeUnits = H264GopSizeUnits.Seconds
                            numberBFramesBetweenReferenceFrames = 2
                            gopClosedCadence = 1
                            gopBReference = H264GopBReference.Disabled
                            slowPal = H264SlowPal.Disabled
                            syntax = H264Syntax.Default
                            numberReferenceFrames = 3
                            dynamicSubGop = H264DynamicSubGop.Static
                            fieldEncoding = H264FieldEncoding.Paff
                            sceneChangeDetect = H264SceneChangeDetect.Enabled
                            minIInterval = 0
                            telecine = H264Telecine.None
                            framerateConversionAlgorithm = H264FramerateConversionAlgorithm.DuplicateDrop
                            entropyEncoding = H264EntropyEncoding.Cabac
                            slices = 1
                            unregisteredSeiTimecode = H264UnregisteredSeiTimecode.Disabled
                            repeatPps = H264RepeatPps.Disabled
                            adaptiveQuantization = H264AdaptiveQuantization.High
                            spatialAdaptiveQuantization = H264SpatialAdaptiveQuantization.Enabled
                            temporalAdaptiveQuantization = H264TemporalAdaptiveQuantization.Enabled
                            flickerAdaptiveQuantization = H264FlickerAdaptiveQuantization.Disabled
                            softness = 0
                            interlaceMode = H264InterlaceMode.Progressive
                        }
                    }
                    audioDescriptions = listOf(audio1)
                }
            }
        }

    } catch (ex: MediaConvertException) {
        println(ex.toString())
        exitProcess(0)
    }
    return output
}
// snippet-end:[mediaconvert.kotlin.createjob.main]