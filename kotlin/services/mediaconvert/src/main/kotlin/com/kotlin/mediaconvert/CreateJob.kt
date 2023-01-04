// snippet-sourcedescription:[CreateJob.kt demonstrates how to create AWS Elemental MediaConvert jobs.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Elemental MediaConvert]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.mediaconvert

// snippet-start:[mediaconvert.kotlin.createjob.import]
import aws.sdk.kotlin.services.mediaconvert.MediaConvertClient
import aws.sdk.kotlin.services.mediaconvert.model.AacAudioDescriptionBroadcasterMix
import aws.sdk.kotlin.services.mediaconvert.model.AacCodecProfile
import aws.sdk.kotlin.services.mediaconvert.model.AacCodingMode
import aws.sdk.kotlin.services.mediaconvert.model.AacRateControlMode
import aws.sdk.kotlin.services.mediaconvert.model.AacRawFormat
import aws.sdk.kotlin.services.mediaconvert.model.AacSettings
import aws.sdk.kotlin.services.mediaconvert.model.AacSpecification
import aws.sdk.kotlin.services.mediaconvert.model.AfdSignaling
import aws.sdk.kotlin.services.mediaconvert.model.AntiAlias
import aws.sdk.kotlin.services.mediaconvert.model.AudioCodec
import aws.sdk.kotlin.services.mediaconvert.model.AudioCodecSettings
import aws.sdk.kotlin.services.mediaconvert.model.AudioDefaultSelection
import aws.sdk.kotlin.services.mediaconvert.model.AudioDescription
import aws.sdk.kotlin.services.mediaconvert.model.AudioLanguageCodeControl
import aws.sdk.kotlin.services.mediaconvert.model.AudioSelector
import aws.sdk.kotlin.services.mediaconvert.model.AudioTypeControl
import aws.sdk.kotlin.services.mediaconvert.model.ColorMetadata
import aws.sdk.kotlin.services.mediaconvert.model.ColorSpace
import aws.sdk.kotlin.services.mediaconvert.model.ContainerSettings
import aws.sdk.kotlin.services.mediaconvert.model.ContainerType
import aws.sdk.kotlin.services.mediaconvert.model.CreateJobRequest
import aws.sdk.kotlin.services.mediaconvert.model.DescribeEndpointsRequest
import aws.sdk.kotlin.services.mediaconvert.model.DropFrameTimecode
import aws.sdk.kotlin.services.mediaconvert.model.FileGroupSettings
import aws.sdk.kotlin.services.mediaconvert.model.FrameCaptureSettings
import aws.sdk.kotlin.services.mediaconvert.model.H264AdaptiveQuantization
import aws.sdk.kotlin.services.mediaconvert.model.H264CodecLevel
import aws.sdk.kotlin.services.mediaconvert.model.H264CodecProfile
import aws.sdk.kotlin.services.mediaconvert.model.H264DynamicSubGop
import aws.sdk.kotlin.services.mediaconvert.model.H264EntropyEncoding
import aws.sdk.kotlin.services.mediaconvert.model.H264FieldEncoding
import aws.sdk.kotlin.services.mediaconvert.model.H264FlickerAdaptiveQuantization
import aws.sdk.kotlin.services.mediaconvert.model.H264FramerateControl
import aws.sdk.kotlin.services.mediaconvert.model.H264FramerateConversionAlgorithm
import aws.sdk.kotlin.services.mediaconvert.model.H264GopBReference
import aws.sdk.kotlin.services.mediaconvert.model.H264GopSizeUnits
import aws.sdk.kotlin.services.mediaconvert.model.H264InterlaceMode
import aws.sdk.kotlin.services.mediaconvert.model.H264ParControl
import aws.sdk.kotlin.services.mediaconvert.model.H264QualityTuningLevel
import aws.sdk.kotlin.services.mediaconvert.model.H264QvbrSettings
import aws.sdk.kotlin.services.mediaconvert.model.H264RateControlMode
import aws.sdk.kotlin.services.mediaconvert.model.H264RepeatPps
import aws.sdk.kotlin.services.mediaconvert.model.H264SceneChangeDetect
import aws.sdk.kotlin.services.mediaconvert.model.H264Settings
import aws.sdk.kotlin.services.mediaconvert.model.H264SlowPal
import aws.sdk.kotlin.services.mediaconvert.model.H264SpatialAdaptiveQuantization
import aws.sdk.kotlin.services.mediaconvert.model.H264Syntax
import aws.sdk.kotlin.services.mediaconvert.model.H264Telecine
import aws.sdk.kotlin.services.mediaconvert.model.H264TemporalAdaptiveQuantization
import aws.sdk.kotlin.services.mediaconvert.model.H264UnregisteredSeiTimecode
import aws.sdk.kotlin.services.mediaconvert.model.HlsCaptionLanguageSetting
import aws.sdk.kotlin.services.mediaconvert.model.HlsClientCache
import aws.sdk.kotlin.services.mediaconvert.model.HlsCodecSpecification
import aws.sdk.kotlin.services.mediaconvert.model.HlsDirectoryStructure
import aws.sdk.kotlin.services.mediaconvert.model.HlsGroupSettings
import aws.sdk.kotlin.services.mediaconvert.model.HlsIFrameOnlyManifest
import aws.sdk.kotlin.services.mediaconvert.model.HlsManifestCompression
import aws.sdk.kotlin.services.mediaconvert.model.HlsManifestDurationFormat
import aws.sdk.kotlin.services.mediaconvert.model.HlsOutputSelection
import aws.sdk.kotlin.services.mediaconvert.model.HlsProgramDateTime
import aws.sdk.kotlin.services.mediaconvert.model.HlsSegmentControl
import aws.sdk.kotlin.services.mediaconvert.model.HlsSettings
import aws.sdk.kotlin.services.mediaconvert.model.HlsStreamInfResolution
import aws.sdk.kotlin.services.mediaconvert.model.HlsTimedMetadataId3Frame
import aws.sdk.kotlin.services.mediaconvert.model.Input
import aws.sdk.kotlin.services.mediaconvert.model.InputDeblockFilter
import aws.sdk.kotlin.services.mediaconvert.model.InputDenoiseFilter
import aws.sdk.kotlin.services.mediaconvert.model.InputFilterEnable
import aws.sdk.kotlin.services.mediaconvert.model.InputPsiControl
import aws.sdk.kotlin.services.mediaconvert.model.InputRotate
import aws.sdk.kotlin.services.mediaconvert.model.InputTimecodeSource
import aws.sdk.kotlin.services.mediaconvert.model.JobSettings
import aws.sdk.kotlin.services.mediaconvert.model.M3U8NielsenId3
import aws.sdk.kotlin.services.mediaconvert.model.M3U8PcrControl
import aws.sdk.kotlin.services.mediaconvert.model.M3U8Scte35Source
import aws.sdk.kotlin.services.mediaconvert.model.M3U8Settings
import aws.sdk.kotlin.services.mediaconvert.model.MediaConvertException
import aws.sdk.kotlin.services.mediaconvert.model.Output
import aws.sdk.kotlin.services.mediaconvert.model.OutputGroup
import aws.sdk.kotlin.services.mediaconvert.model.OutputGroupSettings
import aws.sdk.kotlin.services.mediaconvert.model.OutputGroupType
import aws.sdk.kotlin.services.mediaconvert.model.OutputSettings
import aws.sdk.kotlin.services.mediaconvert.model.RespondToAfd
import aws.sdk.kotlin.services.mediaconvert.model.ScalingBehavior
import aws.sdk.kotlin.services.mediaconvert.model.TimedMetadata
import aws.sdk.kotlin.services.mediaconvert.model.VideoCodec
import aws.sdk.kotlin.services.mediaconvert.model.VideoCodecSettings
import aws.sdk.kotlin.services.mediaconvert.model.VideoDescription
import aws.sdk.kotlin.services.mediaconvert.model.VideoSelector
import aws.sdk.kotlin.services.mediaconvert.model.VideoTimecodeInsertion
import aws.smithy.kotlin.runtime.http.endpoints.Endpoint
import aws.smithy.kotlin.runtime.http.endpoints.EndpointProvider
import java.util.HashMap
import kotlin.math.roundToInt
import kotlin.system.exitProcess
// snippet-end:[mediaconvert.kotlin.createjob.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main(args: Array<String>) {
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
    val mcClient = MediaConvertClient { region = "us-west-2" }
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
        val mediaConvert = MediaConvertClient.fromEnvironment {
            region = "us-west-2"
            endpointProvider = EndpointProvider {
                Endpoint(endpointURL)
            }
        }

        // output group Preset HLS low profile
        val hlsLow = createOutput("_low", "_\$dt$", 750000, 7, 1920, 1080, 640)

        // output group Preset HLS medium profile
        val hlsMedium = createOutput("_medium", "_\$dt$", 1200000, 7, 1920, 1080, 1280)

        // output group Preset HLS high profole
        val hlsHigh = createOutput("_high", "_\$dt$", 3500000, 8, 1920, 1080, 1920)

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

            audioDescriptions = listOf(
                AudioDescription {
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
                }
            )
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

            outputs = listOf(
                Output {
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
                }
            )
        }

        val audioSelectors1: MutableMap<String, AudioSelector> = HashMap()
        audioSelectors1["Audio Selector 1"] =
            AudioSelector {
                defaultSelection = AudioDefaultSelection.Default
                offset = 0
            }

        val jobSettings = JobSettings {
            inputs = listOf(
                Input {
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
                }
            )
        }

        val createJobRequest = CreateJobRequest {
            role = mcRoleARN
            settings = jobSettings
        }

        val createJobResponse = mediaConvert.createJob(createJobRequest)
        return createJobResponse.job?.id
    } catch (ex: MediaConvertException) {
        println(ex.message)
        mcClient.close()
        exitProcess(0)
    }
}

fun createOutput(
    nameModifierVal: String,
    segmentModifierVal: String,
    qvbrMaxBitrate: Int,
    qvbrQualityLevelVal: Int,
    originWidth: Int,
    originHeight: Int,
    targetWidth: Int
): Output? {
    val targetHeight = (
        (originHeight * targetWidth / originWidth).toFloat().roundToInt() -
            (originHeight * targetWidth / originWidth).toFloat().roundToInt() % 4
        )

    var output: Output?
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
