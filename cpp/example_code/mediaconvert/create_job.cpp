/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


// Imports needed
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/mediaconvert/MediaConvertClient.h>
#include <aws/mediaconvert/Model/DescribeEndpointsRequest.h>
#include <aws/mediaconvert/Model/CreateJobRequest.h>
#include <aws/mediaconvert/Model/CreateJobResult.h>

/* ----------------------------------------------
 * Permissions IAM user needs to run this example
 * ----------------------------------------------
 *
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "VisualEditor0",
                "Effect": "Allow",
                "Action": [
                    "mediaconvert:DescribeEndpoints",
                    "mediaconvert:CreateJob"
                ],
                "Resource": "*"
            }
        ]
    }
*/
/* --------------------------------------
 * JSON job settings used in this example
 * --------------------------------------
 *
    {
      "Queue": "arn:aws:mediaconvert:us-west-2:505474453218:queues/Default",
      "UserMetadata": {
        "Customer": "Amazon"
      },
      "Role": "Your AWS Elemental MediaConvert role ARN",
      "Settings": {
        "OutputGroups": [
          {
            "Name": "File Group",
            "OutputGroupSettings": {
              "Type": "FILE_GROUP_SETTINGS",
              "FileGroupSettings": {
                "Destination": "s3://youroutputdestination"
              }
            },
            "Outputs": [
              {
                "VideoDescription": {
                  "ScalingBehavior": "DEFAULT",
                  "TimecodeInsertion": "DISABLED",
                  "AntiAlias": "ENABLED",
                  "Sharpness": 50,
                  "CodecSettings": {
                    "Codec": "H_264",
                    "H264Settings": {
                      "InterlaceMode": "PROGRESSIVE",
                      "NumberReferenceFrames": 3,
                      "Syntax": "DEFAULT",
                      "Softness": 0,
                      "GopClosedCadence": 1,
                      "GopSize": 90,
                      "Slices": 1,
                      "GopBReference": "DISABLED",
                      "SlowPal": "DISABLED",
                      "SpatialAdaptiveQuantization": "ENABLED",
                      "TemporalAdaptiveQuantization": "ENABLED",
                      "FlickerAdaptiveQuantization": "DISABLED",
                      "EntropyEncoding": "CABAC",
                      "Bitrate": 5000000,
                      "FramerateControl": "SPECIFIED",
                      "RateControlMode": "CBR",
                      "CodecProfile": "MAIN",
                      "Telecine": "NONE",
                      "MinIInterval": 0,
                      "AdaptiveQuantization": "HIGH",
                      "CodecLevel": "AUTO",
                      "FieldEncoding": "PAFF",
                      "SceneChangeDetect": "ENABLED",
                      "QualityTuningLevel": "SINGLE_PASS",
                      "FramerateConversionAlgorithm": "DUPLICATE_DROP",
                      "UnregisteredSeiTimecode": "DISABLED",
                      "GopSizeUnits": "FRAMES",
                      "ParControl": "SPECIFIED",
                      "NumberBFramesBetweenReferenceFrames": 2,
                      "RepeatPps": "DISABLED",
                      "FramerateNumerator": 30,
                      "FramerateDenominator": 1,
                      "ParNumerator": 1,
                      "ParDenominator": 1
                    }
                  },
                  "AfdSignaling": "NONE",
                  "DropFrameTimecode": "ENABLED",
                  "RespondToAfd": "NONE",
                  "ColorMetadata": "INSERT"
                },
                "AudioDescriptions": [
                  {
                    "AudioTypeControl": "FOLLOW_INPUT",
                    "CodecSettings": {
                      "Codec": "AAC",
                      "AacSettings": {
                        "AudioDescriptionBroadcasterMix": "NORMAL",
                        "RateControlMode": "CBR",
                        "CodecProfile": "LC",
                        "CodingMode": "CODING_MODE_2_0",
                        "RawFormat": "NONE",
                        "SampleRate": 48000,
                        "Specification": "MPEG4",
                        "Bitrate": 64000
                      }
                    },
                    "LanguageCodeControl": "FOLLOW_INPUT",
                    "AudioSourceName": "Audio Selector 1"
                  }
                ],
                "ContainerSettings": {
                  "Container": "MP4",
                  "Mp4Settings": {
                    "CslgAtom": "INCLUDE",
                    "FreeSpaceBox": "EXCLUDE",
                    "MoovPlacement": "PROGRESSIVE_DOWNLOAD"
                  }
                },
                "NameModifier": "_1"
              }
            ]
          }
        ],
        "AdAvailOffset": 0,
        "Inputs": [
          {
            "AudioSelectors": {
              "Audio Selector 1": {
                "Offset": 0,
                "DefaultSelection": "NOT_DEFAULT",
                "ProgramSelection": 1,
                "SelectorType": "TRACK",
                "Tracks": [
                  1
                ]
              }
            },
            "VideoSelector": {
              "ColorSpace": "FOLLOW"
            },
            "FilterEnable": "AUTO",
            "PsiControl": "USE_PSI",
            "FilterStrength": 0,
            "DeblockFilter": "DISABLED",
            "DenoiseFilter": "DISABLED",
            "TimecodeSource": "EMBEDDED",
            "FileInput": "s3://yourinputfile"
          }
        ],
        "TimecodeConfig": {
          "Source": "EMBEDDED"
        }
      }
    }
*/

/*
 *
 *  main function
 *
 *  Usage: 'run_create_job <media_convert_role> <file_input> <file_output> [media_convert_endpoint]'
 *
 *  Prerequisites:
 *  1. IAM role for media convert.
 *  2. Input media file in an S3 bucket.
 *
 */


int main(int argc, char **argv) {
    if (argc < 4) {
        std::cout << R"(
Usage:
    run_create_job <media_convert_role> <file_input> <file_output> [media_convert_endpoint]
Where:
    media_convert_role - IAM role for media convert.
    file_input - S3 input location.
    file_output - S3 output location and the output filename base.
    media_convert_endpoint - optional media convert endpoint.
)";
        return 1;
    }

    //	Initialize the C++ SDK
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        std::string mediaConvertRole = argv[1];
        std::string fileInput = argv[2];
        std::string fileOutput = argv[3];
        // Once you know what your customer endpoint is, set it in the arguments.
        std::string mediaConvertEndpoint = "";
        if (argc > 4)
        {
            mediaConvertEndpoint = argv[4];
        }

        // If we do not have our customer-specific endpoint
        if (mediaConvertEndpoint.empty())
        {
            // Obtain the customer-specific MediaConvert endpoint
            Aws::Client::ClientConfiguration clientConfig;
  //          clientConfig.region = "us-west-2";
            Aws::MediaConvert::MediaConvertClient client(clientConfig);
            Aws::MediaConvert::Model::DescribeEndpointsRequest request;
            // need to strip https:// from endpoint for C++
            //mediaConvertEndpoint = client.DescribeEndpoints(request).GetResult().GetEndpoints().at(0).GetUrl().substr(8);
            auto outcome = client.DescribeEndpoints(request);
            if (outcome.IsSuccess())
            {
                auto endpoints = outcome.GetResult().GetEndpoints();
                if (endpoints.empty())
                {
                    std::cerr << "DescribeEndpoints, no endpoints returned" <<  std::endl;
                    return -1;
                }
                 mediaConvertEndpoint = endpoints[0].GetUrl().substr(8);

                std::cout << "Using media convert endpoint '" << mediaConvertEndpoint << "'." << std::endl;
            }
            else{
                std::cerr << "DescribeEndpoints error - " << outcome.GetError().GetMessage() << std::endl;
                return -1;
            }

        }

        // Create MediaConvert client with the endpoints and region from above
        Aws::Client::ClientConfiguration mcClientConfig;
        // Also need to set region endpoint, must match endpoint embedded in custom endpoint
    //    mcClientConfig.region = "us-west-2";
        mcClientConfig.endpointOverride = mediaConvertEndpoint;
        Aws::MediaConvert::MediaConvertClient mcClient(mcClientConfig);

        // Create job request
        Aws::MediaConvert::Model::CreateJobRequest createJobRequest;

        createJobRequest.SetRole(mediaConvertRole);
        Aws::Http::HeaderValueCollection hvc;
        hvc.emplace("Customer", "Amazon");
        createJobRequest.SetUserMetadata(hvc);

        // Create job settings
        Aws::MediaConvert::Model::JobSettings jobSettings;

        jobSettings.SetAdAvailOffset(0);
        Aws::MediaConvert::Model::TimecodeConfig timecodeConfig;
        timecodeConfig.SetSource(Aws::MediaConvert::Model::TimecodeSource::EMBEDDED);
        jobSettings.SetTimecodeConfig(timecodeConfig);
        createJobRequest.SetSettings(jobSettings);

        // Output Group
        Aws::MediaConvert::Model::OutputGroup og;
        og.SetName("File Group");
        Aws::MediaConvert::Model::OutputGroupSettings ogs;
        ogs.SetType(Aws::MediaConvert::Model::OutputGroupType::FILE_GROUP_SETTINGS);
        Aws::MediaConvert::Model::FileGroupSettings fgs;
        fgs.SetDestination(fileOutput);
        ogs.SetFileGroupSettings(fgs);
        og.SetOutputGroupSettings(ogs);

        Aws::MediaConvert::Model::Output output;
        output.SetNameModifier("_1");

        Aws::MediaConvert::Model::VideoDescription vdes;
        vdes.SetScalingBehavior(Aws::MediaConvert::Model::ScalingBehavior::DEFAULT);
        vdes.SetTimecodeInsertion(Aws::MediaConvert::Model::VideoTimecodeInsertion::DISABLED);
        vdes.SetAntiAlias(Aws::MediaConvert::Model::AntiAlias::ENABLED);
        vdes.SetSharpness(50);
        vdes.SetAfdSignaling(Aws::MediaConvert::Model::AfdSignaling::NONE);
        vdes.SetDropFrameTimecode(Aws::MediaConvert::Model::DropFrameTimecode::ENABLED);
        vdes.SetRespondToAfd(Aws::MediaConvert::Model::RespondToAfd::NONE);
        vdes.SetColorMetadata(Aws::MediaConvert::Model::ColorMetadata::INSERT);

        Aws::MediaConvert::Model::VideoCodecSettings vcs;
        vcs.SetCodec(Aws::MediaConvert::Model::VideoCodec::H_264);
        Aws::MediaConvert::Model::H264Settings h264;
        h264.SetNumberReferenceFrames(3);
        h264.SetSyntax(Aws::MediaConvert::Model::H264Syntax::DEFAULT);
        h264.SetSoftness(0);
        h264.SetGopClosedCadence(1);
        h264.SetGopSize(90);
        h264.SetSlices(1);
        h264.SetGopBReference(Aws::MediaConvert::Model::H264GopBReference::DISABLED);
        h264.SetSlowPal(Aws::MediaConvert::Model::H264SlowPal::DISABLED);
        h264.SetSpatialAdaptiveQuantization(Aws::MediaConvert::Model::H264SpatialAdaptiveQuantization::ENABLED);
        h264.SetTemporalAdaptiveQuantization(Aws::MediaConvert::Model::H264TemporalAdaptiveQuantization::ENABLED);
        h264.SetFlickerAdaptiveQuantization(Aws::MediaConvert::Model::H264FlickerAdaptiveQuantization::DISABLED);
        h264.SetEntropyEncoding(Aws::MediaConvert::Model::H264EntropyEncoding::CABAC);
        h264.SetBitrate(5000000);
        h264.SetFramerateControl(Aws::MediaConvert::Model::H264FramerateControl::SPECIFIED);
        h264.SetRateControlMode(Aws::MediaConvert::Model::H264RateControlMode::CBR);
        h264.SetCodecProfile(Aws::MediaConvert::Model::H264CodecProfile::MAIN);
        h264.SetTelecine(Aws::MediaConvert::Model::H264Telecine::NONE);
        h264.SetMinIInterval(0);
        h264.SetAdaptiveQuantization(Aws::MediaConvert::Model::H264AdaptiveQuantization::HIGH);
        h264.SetCodecLevel(Aws::MediaConvert::Model::H264CodecLevel::AUTO);
        h264.SetFieldEncoding(Aws::MediaConvert::Model::H264FieldEncoding::PAFF);
        h264.SetSceneChangeDetect(Aws::MediaConvert::Model::H264SceneChangeDetect::ENABLED);
        h264.SetQualityTuningLevel(Aws::MediaConvert::Model::H264QualityTuningLevel::SINGLE_PASS);
        h264.SetFramerateConversionAlgorithm(Aws::MediaConvert::Model::H264FramerateConversionAlgorithm::DUPLICATE_DROP);
        h264.SetUnregisteredSeiTimecode(Aws::MediaConvert::Model::H264UnregisteredSeiTimecode::DISABLED);
        h264.SetGopSizeUnits(Aws::MediaConvert::Model::H264GopSizeUnits::FRAMES);
        h264.SetParControl(Aws::MediaConvert::Model::H264ParControl::SPECIFIED);
        h264.SetNumberBFramesBetweenReferenceFrames(2);
        h264.SetRepeatPps(Aws::MediaConvert::Model::H264RepeatPps::DISABLED);
        h264.SetFramerateNumerator(30);
        h264.SetFramerateDenominator(1);
        h264.SetParNumerator(1);
        h264.SetParDenominator(1);
        vcs.SetH264Settings(h264);
        vdes.SetCodecSettings(vcs);
        output.SetVideoDescription(vdes);

        Aws::MediaConvert::Model::AudioDescription ades;
        ades.SetLanguageCodeControl(Aws::MediaConvert::Model::AudioLanguageCodeControl::FOLLOW_INPUT);
        // This name matches one specified in the Inputs below
        ades.SetAudioSourceName("Audio Select 1");
        Aws::MediaConvert::Model::AudioCodecSettings acs;
        acs.SetCodec(Aws::MediaConvert::Model::AudioCodec::AAC);
        Aws::MediaConvert::Model::AacSettings aac;
        aac.SetAudioDescriptionBroadcasterMix(Aws::MediaConvert::Model::AacAudioDescriptionBroadcasterMix::NORMAL);
        aac.SetRateControlMode(Aws::MediaConvert::Model::AacRateControlMode::CBR);
        aac.SetCodecProfile(Aws::MediaConvert::Model::AacCodecProfile::LC);
        aac.SetCodingMode(Aws::MediaConvert::Model::AacCodingMode::CODING_MODE_2_0);
        aac.SetRawFormat(Aws::MediaConvert::Model::AacRawFormat::NONE);
        aac.SetSampleRate(48000);
        aac.SetSpecification(Aws::MediaConvert::Model::AacSpecification::MPEG4);
        aac.SetBitrate(64000);
        acs.SetAacSettings(aac);
        ades.SetCodecSettings(acs);
        Aws::Vector<Aws::MediaConvert::Model::AudioDescription> adess;
        adess.emplace_back(ades);
        output.SetAudioDescriptions(adess);

        Aws::MediaConvert::Model::ContainerSettings mp4container;
        mp4container.SetContainer(Aws::MediaConvert::Model::ContainerType::MP4);
        Aws::MediaConvert::Model::Mp4Settings mp4;
        mp4.SetCslgAtom(Aws::MediaConvert::Model::Mp4CslgAtom::INCLUDE);
        mp4.SetFreeSpaceBox(Aws::MediaConvert::Model::Mp4FreeSpaceBox::EXCLUDE);
        mp4.SetMoovPlacement(Aws::MediaConvert::Model::Mp4MoovPlacement::PROGRESSIVE_DOWNLOAD);
        mp4container.SetMp4Settings(mp4);
        output.SetContainerSettings(mp4container);

        og.AddOutputs(output);
        jobSettings.AddOutputGroups(og);

        // End Output Group

        Aws::MediaConvert::Model::Input input;
        input.SetFilterEnable(Aws::MediaConvert::Model::InputFilterEnable::AUTO);
        input.SetPsiControl(Aws::MediaConvert::Model::InputPsiControl::USE_PSI);
        input.SetFilterStrength(0);
        input.SetDeblockFilter(Aws::MediaConvert::Model::InputDeblockFilter::DISABLED);
        input.SetDenoiseFilter(Aws::MediaConvert::Model::InputDenoiseFilter::DISABLED);
        input.SetTimecodeSource(Aws::MediaConvert::Model::InputTimecodeSource::EMBEDDED);
        input.SetFileInput(fileInput);

        Aws::MediaConvert::Model::AudioSelector audsel;
        audsel.SetOffset(0);
        audsel.SetDefaultSelection(Aws::MediaConvert::Model::AudioDefaultSelection::NOT_DEFAULT);
        audsel.SetProgramSelection(1);
        audsel.SetSelectorType(Aws::MediaConvert::Model::AudioSelectorType::TRACK);
        audsel.AddTracks(1);
        input.AddAudioSelectors("Audio Select 1", audsel);

        Aws::MediaConvert::Model::VideoSelector vidsel;
        vidsel.SetColorSpace(Aws::MediaConvert::Model::ColorSpace::FOLLOW);
        input.SetVideoSelector(vidsel);

        jobSettings.AddInputs(input);

        createJobRequest.SetSettings(jobSettings);

        Aws::MediaConvert::Model::CreateJobOutcome createJobResponse = mcClient.CreateJob(createJobRequest);
        if (createJobResponse.IsSuccess()) {
            std::cout << "Job successfully created with ID - " << createJobResponse.GetResult().GetJob().GetId() << std::endl;
        }
        else
        {
            std::cerr << "Error CreateJob - " << createJobResponse.GetError().GetMessage() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);
    return 0;
}