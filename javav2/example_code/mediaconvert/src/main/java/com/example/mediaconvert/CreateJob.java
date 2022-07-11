//snippet-sourcedescription:[CreateJob.java demonstrates how to create AWS Elemental MediaConvert jobs.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Elemental MediaConvert]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
// snippet-start:[mediaconvert.java.createjob.complete]
package com.example.mediaconvert;

// snippet-start:[mediaconvert.java.createjob.import]
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.Output;
import software.amazon.awssdk.services.mediaconvert.model.MediaConvertException;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroup;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.HlsGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupType;
import software.amazon.awssdk.services.mediaconvert.model.HlsDirectoryStructure;
import software.amazon.awssdk.services.mediaconvert.model.HlsManifestDurationFormat;
import software.amazon.awssdk.services.mediaconvert.model.HlsStreamInfResolution;
import software.amazon.awssdk.services.mediaconvert.model.HlsClientCache;
import software.amazon.awssdk.services.mediaconvert.model.HlsCaptionLanguageSetting;
import software.amazon.awssdk.services.mediaconvert.model.HlsManifestCompression;
import software.amazon.awssdk.services.mediaconvert.model.HlsCodecSpecification;
import software.amazon.awssdk.services.mediaconvert.model.HlsOutputSelection;
import software.amazon.awssdk.services.mediaconvert.model.HlsProgramDateTime;
import software.amazon.awssdk.services.mediaconvert.model.HlsTimedMetadataId3Frame;
import software.amazon.awssdk.services.mediaconvert.model.HlsSegmentControl;
import software.amazon.awssdk.services.mediaconvert.model.FileGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.ContainerSettings;
import software.amazon.awssdk.services.mediaconvert.model.VideoDescription;
import software.amazon.awssdk.services.mediaconvert.model.ContainerType;
import software.amazon.awssdk.services.mediaconvert.model.ScalingBehavior;
import software.amazon.awssdk.services.mediaconvert.model.VideoTimecodeInsertion;
import software.amazon.awssdk.services.mediaconvert.model.ColorMetadata;
import software.amazon.awssdk.services.mediaconvert.model.RespondToAfd;
import software.amazon.awssdk.services.mediaconvert.model.AfdSignaling;
import software.amazon.awssdk.services.mediaconvert.model.DropFrameTimecode;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264Settings;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodec;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.H264RateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.H264QualityTuningLevel;
import software.amazon.awssdk.services.mediaconvert.model.H264SceneChangeDetect;
import software.amazon.awssdk.services.mediaconvert.model.AacAudioDescriptionBroadcasterMix;
import software.amazon.awssdk.services.mediaconvert.model.H264ParControl;
import software.amazon.awssdk.services.mediaconvert.model.AacRawFormat;
import software.amazon.awssdk.services.mediaconvert.model.H264QvbrSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264FramerateConversionAlgorithm;
import software.amazon.awssdk.services.mediaconvert.model.H264CodecLevel;
import software.amazon.awssdk.services.mediaconvert.model.H264FramerateControl;
import software.amazon.awssdk.services.mediaconvert.model.AacCodingMode;
import software.amazon.awssdk.services.mediaconvert.model.H264Telecine;
import software.amazon.awssdk.services.mediaconvert.model.H264FlickerAdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264GopSizeUnits;
import software.amazon.awssdk.services.mediaconvert.model.H264CodecProfile;
import software.amazon.awssdk.services.mediaconvert.model.H264GopBReference;
import software.amazon.awssdk.services.mediaconvert.model.AudioTypeControl;
import software.amazon.awssdk.services.mediaconvert.model.AntiAlias;
import software.amazon.awssdk.services.mediaconvert.model.H264SlowPal;
import software.amazon.awssdk.services.mediaconvert.model.H264SpatialAdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264Syntax;
import software.amazon.awssdk.services.mediaconvert.model.M3u8Settings;
import software.amazon.awssdk.services.mediaconvert.model.InputDenoiseFilter;
import software.amazon.awssdk.services.mediaconvert.model.H264TemporalAdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.H264UnregisteredSeiTimecode;
import software.amazon.awssdk.services.mediaconvert.model.H264EntropyEncoding;
import software.amazon.awssdk.services.mediaconvert.model.InputPsiControl;
import software.amazon.awssdk.services.mediaconvert.model.ColorSpace;
import software.amazon.awssdk.services.mediaconvert.model.H264RepeatPps;
import software.amazon.awssdk.services.mediaconvert.model.H264FieldEncoding;
import software.amazon.awssdk.services.mediaconvert.model.M3u8NielsenId3;
import software.amazon.awssdk.services.mediaconvert.model.InputDeblockFilter;
import software.amazon.awssdk.services.mediaconvert.model.InputRotate;
import software.amazon.awssdk.services.mediaconvert.model.H264DynamicSubGop;
import software.amazon.awssdk.services.mediaconvert.model.TimedMetadata;
import software.amazon.awssdk.services.mediaconvert.model.JobSettings;
import software.amazon.awssdk.services.mediaconvert.model.AudioDefaultSelection;
import software.amazon.awssdk.services.mediaconvert.model.VideoSelector;
import software.amazon.awssdk.services.mediaconvert.model.AacSpecification;
import software.amazon.awssdk.services.mediaconvert.model.Input;
import software.amazon.awssdk.services.mediaconvert.model.OutputSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264AdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.AudioLanguageCodeControl;
import software.amazon.awssdk.services.mediaconvert.model.InputFilterEnable;
import software.amazon.awssdk.services.mediaconvert.model.AudioDescription;
import software.amazon.awssdk.services.mediaconvert.model.H264InterlaceMode;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.AacSettings;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodec;
import software.amazon.awssdk.services.mediaconvert.model.AacRateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.AacCodecProfile;
import software.amazon.awssdk.services.mediaconvert.model.HlsIFrameOnlyManifest;
import software.amazon.awssdk.services.mediaconvert.model.FrameCaptureSettings;
import software.amazon.awssdk.services.mediaconvert.model.AudioSelector;
import software.amazon.awssdk.services.mediaconvert.model.M3u8PcrControl;
import software.amazon.awssdk.services.mediaconvert.model.InputTimecodeSource;
import software.amazon.awssdk.services.mediaconvert.model.HlsSettings;
import software.amazon.awssdk.services.mediaconvert.model.M3u8Scte35Source;
// snippet-end:[mediaconvert.java.createjob.import]

/**
 * Create a MediaConvert job. Must supply MediaConvert access role Amazon Resource Name (ARN), and a
 * valid video input file via Amazon S3 URL.
 *
 * Also, set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */
public class CreateJob {

   public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <mcRoleARN> <fileInput> \n\n" +
                "Where:\n" +
                "    mcRoleARN - The MediaConvert Role ARN. \n"+
                "    fileInput -  The URL of an Amazon S3 bucket where the input file is located. \n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String mcRoleARN = args[0];
        String fileInput = args[1];
        Region region = Region.US_WEST_2;
        MediaConvertClient mc = MediaConvertClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

       String id = createMediaJob(mc, mcRoleARN, fileInput);
       System.out.println("MediaConvert job created. Job Id = " +id );
       mc.close();
    }

    public static String createMediaJob(MediaConvertClient mc, String mcRoleARN, String fileInput) {

        String s3path = fileInput.substring(0, fileInput.lastIndexOf('/') + 1) + "javasdk/out/";
        String fileOutput = s3path + "index";
        String thumbsOutput = s3path + "thumbs/";
        String mp4Output = s3path + "mp4/";

        try {
            // snippet-start:[mediaconvert.java.createjob.getendpointurl]
            DescribeEndpointsResponse res = mc
                    .describeEndpoints(DescribeEndpointsRequest.builder().maxResults(20).build());

            if (res.endpoints().size() <= 0) {
                System.out.println("Cannot find MediaConvert service endpoint URL!");
                System.exit(1);
            }
            String endpointURL = res.endpoints().get(0).url();
            System.out.println("MediaConvert service URL: " + endpointURL);
            System.out.println("MediaConvert role arn: " + mcRoleARN);
            System.out.println("MediaConvert input file: " + fileInput);
            System.out.println("MediaConvert output path: " + s3path);
            // snippet-end:[mediaconvert.java.createjob.getendpointurl]

            MediaConvertClient emc = MediaConvertClient.builder()
                    .region(Region.US_WEST_2)
                    .endpointOverride(URI.create(endpointURL))
                    .build();

            // output group Preset HLS low profile
            Output hlsLow = createOutput("hls_low", "_low", "_$dt$", 750000, 7, 1920, 1080, 640);
            // output group Preset HLS media profile
            Output hlsMedium = createOutput("hls_medium", "_medium", "_$dt$", 1200000, 7, 1920, 1080, 1280);
            // output group Preset HLS high profole
            Output hlsHigh = createOutput("hls_high", "_high", "_$dt$", 3500000, 8, 1920, 1080, 1920);
            // snippet-start:[mediaconvert.java.createjob.create_hls_output]

            OutputGroup appleHLS = OutputGroup.builder().name("Apple HLS").customName("Example")
                    .outputGroupSettings(OutputGroupSettings.builder().type(OutputGroupType.HLS_GROUP_SETTINGS)
                            .hlsGroupSettings(HlsGroupSettings.builder()
                                    .directoryStructure(HlsDirectoryStructure.SINGLE_DIRECTORY)
                                    .manifestDurationFormat(HlsManifestDurationFormat.INTEGER)
                                    .streamInfResolution(HlsStreamInfResolution.INCLUDE)
                                    .clientCache(HlsClientCache.ENABLED)
                                    .captionLanguageSetting(HlsCaptionLanguageSetting.OMIT)
                                    .manifestCompression(HlsManifestCompression.NONE)
                                    .codecSpecification(HlsCodecSpecification.RFC_4281)
                                    .outputSelection(HlsOutputSelection.MANIFESTS_AND_SEGMENTS)
                                    .programDateTime(HlsProgramDateTime.EXCLUDE).programDateTimePeriod(600)
                                    .timedMetadataId3Frame(HlsTimedMetadataId3Frame.PRIV).timedMetadataId3Period(10)
                                    .destination(fileOutput).segmentControl(HlsSegmentControl.SEGMENTED_FILES)
                                    .minFinalSegmentLength((double) 0).segmentLength(4).minSegmentLength(0).build())
                            .build())
                    .outputs(hlsLow, hlsMedium, hlsHigh).build();
            // snippet-end:[mediaconvert.java.createjob.create_hls_output]
            // snippet-start:[mediaconvert.java.createjob.create_file_output]

            OutputGroup fileMp4 = OutputGroup.builder().name("File Group").customName("mp4")
                    .outputGroupSettings(OutputGroupSettings.builder().type(OutputGroupType.FILE_GROUP_SETTINGS)
                            .fileGroupSettings(FileGroupSettings.builder().destination(mp4Output).build()).build())
                    .outputs(Output.builder().extension("mp4")
                            .containerSettings(ContainerSettings.builder().container(ContainerType.MP4).build())
                            .videoDescription(VideoDescription.builder().width(1280).height(720)
                                    .scalingBehavior(ScalingBehavior.DEFAULT).sharpness(50).antiAlias(AntiAlias.ENABLED)
                                    .timecodeInsertion(VideoTimecodeInsertion.DISABLED)
                                    .colorMetadata(ColorMetadata.INSERT).respondToAfd(RespondToAfd.NONE)
                                    .afdSignaling(AfdSignaling.NONE).dropFrameTimecode(DropFrameTimecode.ENABLED)
                                    .codecSettings(VideoCodecSettings.builder().codec(VideoCodec.H_264)
                                            .h264Settings(H264Settings.builder()
                                                    .rateControlMode(H264RateControlMode.QVBR)
                                                    .parControl(H264ParControl.INITIALIZE_FROM_SOURCE)
                                                    .qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
                                                    .qvbrSettings(
                                                            H264QvbrSettings.builder().qvbrQualityLevel(8).build())
                                                    .codecLevel(H264CodecLevel.AUTO).codecProfile(H264CodecProfile.MAIN)
                                                    .maxBitrate(2400000)
                                                    .framerateControl(H264FramerateControl.INITIALIZE_FROM_SOURCE)
                                                    .gopSize(2.0).gopSizeUnits(H264GopSizeUnits.SECONDS)
                                                    .numberBFramesBetweenReferenceFrames(2).gopClosedCadence(1)
                                                    .gopBReference(H264GopBReference.DISABLED)
                                                    .slowPal(H264SlowPal.DISABLED).syntax(H264Syntax.DEFAULT)
                                                    .numberReferenceFrames(3).dynamicSubGop(H264DynamicSubGop.STATIC)
                                                    .fieldEncoding(H264FieldEncoding.PAFF)
                                                    .sceneChangeDetect(H264SceneChangeDetect.ENABLED).minIInterval(0)
                                                    .telecine(H264Telecine.NONE)
                                                    .framerateConversionAlgorithm(
                                                            H264FramerateConversionAlgorithm.DUPLICATE_DROP)
                                                    .entropyEncoding(H264EntropyEncoding.CABAC).slices(1)
                                                    .unregisteredSeiTimecode(H264UnregisteredSeiTimecode.DISABLED)
                                                    .repeatPps(H264RepeatPps.DISABLED)
                                                    .adaptiveQuantization(H264AdaptiveQuantization.HIGH)
                                                    .spatialAdaptiveQuantization(
                                                            H264SpatialAdaptiveQuantization.ENABLED)
                                                    .temporalAdaptiveQuantization(
                                                            H264TemporalAdaptiveQuantization.ENABLED)
                                                    .flickerAdaptiveQuantization(
                                                            H264FlickerAdaptiveQuantization.DISABLED)
                                                    .softness(0).interlaceMode(H264InterlaceMode.PROGRESSIVE).build())
                                            .build())
                                    .build())
                            .audioDescriptions(AudioDescription.builder()
                                    .audioTypeControl(AudioTypeControl.FOLLOW_INPUT)
                                    .languageCodeControl(AudioLanguageCodeControl.FOLLOW_INPUT)
                                    .codecSettings(AudioCodecSettings.builder().codec(AudioCodec.AAC)
                                            .aacSettings(AacSettings.builder().codecProfile(AacCodecProfile.LC)
                                                    .rateControlMode(AacRateControlMode.CBR)
                                                    .codingMode(AacCodingMode.CODING_MODE_2_0).sampleRate(44100)
                                                    .bitrate(160000).rawFormat(AacRawFormat.NONE)
                                                    .specification(AacSpecification.MPEG4)
                                                    .audioDescriptionBroadcasterMix(
                                                            AacAudioDescriptionBroadcasterMix.NORMAL)
                                                    .build())
                                            .build())
                                    .build())
                            .build())
                    .build();
            // snippet-end:[mediaconvert.java.createjob.create_file_output]
            // snippet-start:[mediaconvert.java.createjob.create_thumbnail_output]
            OutputGroup thumbs = OutputGroup.builder().name("File Group").customName("thumbs")
                    .outputGroupSettings(OutputGroupSettings.builder().type(OutputGroupType.FILE_GROUP_SETTINGS)
                            .fileGroupSettings(FileGroupSettings.builder().destination(thumbsOutput).build()).build())
                    .outputs(Output.builder().extension("jpg")
                            .containerSettings(ContainerSettings.builder().container(ContainerType.RAW).build())
                            .videoDescription(VideoDescription.builder().scalingBehavior(ScalingBehavior.DEFAULT)
                                    .sharpness(50).antiAlias(AntiAlias.ENABLED)
                                    .timecodeInsertion(VideoTimecodeInsertion.DISABLED)
                                    .colorMetadata(ColorMetadata.INSERT).dropFrameTimecode(DropFrameTimecode.ENABLED)
                                    .codecSettings(VideoCodecSettings.builder().codec(VideoCodec.FRAME_CAPTURE)
                                            .frameCaptureSettings(FrameCaptureSettings.builder().framerateNumerator(1)
                                                    .framerateDenominator(1).maxCaptures(10000000).quality(80).build())
                                            .build())
                                    .build())
                            .build())
                    .build();

            // snippet-end:[mediaconvert.java.createjob.create_thumbnail_output]
            Map<String, AudioSelector> audioSelectors = new HashMap<>();
            audioSelectors.put("Audio Selector 1",
                    AudioSelector.builder().defaultSelection(AudioDefaultSelection.DEFAULT).offset(0).build());

            JobSettings jobSettings = JobSettings.builder().inputs(Input.builder().audioSelectors(audioSelectors)
                    .videoSelector(
                            VideoSelector.builder().colorSpace(ColorSpace.FOLLOW).rotate(InputRotate.DEGREE_0).build())
                    .filterEnable(InputFilterEnable.AUTO).filterStrength(0).deblockFilter(InputDeblockFilter.DISABLED)
                    .denoiseFilter(InputDenoiseFilter.DISABLED).psiControl(InputPsiControl.USE_PSI)
                    .timecodeSource(InputTimecodeSource.EMBEDDED).fileInput(fileInput).build())
                    .outputGroups(appleHLS, thumbs, fileMp4).build();

            CreateJobRequest createJobRequest = CreateJobRequest.builder().role(mcRoleARN).settings(jobSettings)
                    .build();

           CreateJobResponse createJobResponse = emc.createJob(createJobRequest);
          return createJobResponse.job().id();

        } catch (MediaConvertException e) {
            System.out.println(e.toString());
            System.exit(0);
        }
        return "";
    }

    // snippet-start:[mediaconvert.java.createjob.create_output]
    private final static Output createOutput(String customName,
                                             String nameModifier,
                                             String segmentModifier,
                                             int qvbrMaxBitrate,
                                             int qvbrQualityLevel,
                                             int originWidth,
                                             int originHeight,
                                             int targetWidth) {

        int targetHeight = Math.round(originHeight * targetWidth / originWidth)
                - (Math.round(originHeight * targetWidth / originWidth) % 4);
        Output output = null;
        try {
            output = Output.builder().nameModifier(nameModifier).outputSettings(OutputSettings.builder()
                    .hlsSettings(HlsSettings.builder().segmentModifier(segmentModifier).audioGroupId("program_audio")
                            .iFrameOnlyManifest(HlsIFrameOnlyManifest.EXCLUDE).build())
                    .build())
                    .containerSettings(ContainerSettings.builder().container(ContainerType.M3_U8)
                            .m3u8Settings(M3u8Settings.builder().audioFramesPerPes(4)
                                    .pcrControl(M3u8PcrControl.PCR_EVERY_PES_PACKET).pmtPid(480).privateMetadataPid(503)
                                    .programNumber(1).patInterval(0).pmtInterval(0).scte35Source(M3u8Scte35Source.NONE)
                                    .scte35Pid(500).nielsenId3(M3u8NielsenId3.NONE).timedMetadata(TimedMetadata.NONE)
                                    .timedMetadataPid(502).videoPid(481)
                                    .audioPids(482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492).build())
                            .build())
                    .videoDescription(
                            VideoDescription.builder().width(targetWidth).height(targetHeight)
                                    .scalingBehavior(ScalingBehavior.DEFAULT).sharpness(50).antiAlias(AntiAlias.ENABLED)
                                    .timecodeInsertion(VideoTimecodeInsertion.DISABLED)
                                    .colorMetadata(ColorMetadata.INSERT).respondToAfd(RespondToAfd.NONE)
                                    .afdSignaling(AfdSignaling.NONE).dropFrameTimecode(DropFrameTimecode.ENABLED)
                                    .codecSettings(VideoCodecSettings.builder().codec(VideoCodec.H_264)
                                            .h264Settings(H264Settings.builder()
                                                    .rateControlMode(H264RateControlMode.QVBR)
                                                    .parControl(H264ParControl.INITIALIZE_FROM_SOURCE)
                                                    .qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
                                                    .qvbrSettings(H264QvbrSettings.builder()
                                                            .qvbrQualityLevel(qvbrQualityLevel).build())
                                                    .codecLevel(H264CodecLevel.AUTO)
                                                    .codecProfile((targetHeight > 720 && targetWidth > 1280)
                                                            ? H264CodecProfile.HIGH
                                                            : H264CodecProfile.MAIN)
                                                    .maxBitrate(qvbrMaxBitrate)
                                                    .framerateControl(H264FramerateControl.INITIALIZE_FROM_SOURCE)
                                                    .gopSize(2.0).gopSizeUnits(H264GopSizeUnits.SECONDS)
                                                    .numberBFramesBetweenReferenceFrames(2).gopClosedCadence(1)
                                                    .gopBReference(H264GopBReference.DISABLED)
                                                    .slowPal(H264SlowPal.DISABLED).syntax(H264Syntax.DEFAULT)
                                                    .numberReferenceFrames(3).dynamicSubGop(H264DynamicSubGop.STATIC)
                                                    .fieldEncoding(H264FieldEncoding.PAFF)
                                                    .sceneChangeDetect(H264SceneChangeDetect.ENABLED).minIInterval(0)
                                                    .telecine(H264Telecine.NONE)
                                                    .framerateConversionAlgorithm(
                                                            H264FramerateConversionAlgorithm.DUPLICATE_DROP)
                                                    .entropyEncoding(H264EntropyEncoding.CABAC).slices(1)
                                                    .unregisteredSeiTimecode(H264UnregisteredSeiTimecode.DISABLED)
                                                    .repeatPps(H264RepeatPps.DISABLED)
                                                    .adaptiveQuantization(H264AdaptiveQuantization.HIGH)
                                                    .spatialAdaptiveQuantization(
                                                            H264SpatialAdaptiveQuantization.ENABLED)
                                                    .temporalAdaptiveQuantization(
                                                            H264TemporalAdaptiveQuantization.ENABLED)
                                                    .flickerAdaptiveQuantization(
                                                            H264FlickerAdaptiveQuantization.DISABLED)
                                                    .softness(0).interlaceMode(H264InterlaceMode.PROGRESSIVE).build())
                                            .build())
                                    .build())
                    .audioDescriptions(AudioDescription.builder().audioTypeControl(AudioTypeControl.FOLLOW_INPUT)
                            .languageCodeControl(AudioLanguageCodeControl.FOLLOW_INPUT)
                            .codecSettings(AudioCodecSettings.builder().codec(AudioCodec.AAC).aacSettings(AacSettings
                                    .builder().codecProfile(AacCodecProfile.LC).rateControlMode(AacRateControlMode.CBR)
                                    .codingMode(AacCodingMode.CODING_MODE_2_0).sampleRate(44100).bitrate(96000)
                                    .rawFormat(AacRawFormat.NONE).specification(AacSpecification.MPEG4)
                                    .audioDescriptionBroadcasterMix(AacAudioDescriptionBroadcasterMix.NORMAL).build())
                                    .build())
                            .build())
                    .build();
        } catch (MediaConvertException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return output;
    }
    // snippet-end:[mediaconvert.java.createjob.create_output]
    // snippet-start:[mediaconvert.java.createjob.main]
}
// snippet-end:[mediaconvert.java.createjob.main]
// snippet-end:[mediaconvert.java.createjob.complete]