//snippet-sourcedescription:[HlsJobCreationSample.java demonstrates how to create an HLS job.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Transcoder]
//snippet-service:[elastictranscoder]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// snippet-start:[elastictranscoder.java.create_hls_job.import]
package com.amazonaws.services.elastictranscoder.samples;

import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobPlaylist;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.Job;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.elastictranscoder.samples.utils.TranscoderSampleUtilities;

/**
 * The purpose of this sample is to show how to create an HLS job and HLS
 * playlist file which can be used to play an adaptive bitrate stream.  HLS
 * playlists can be played in the browser using tools such as jwplayer or using
 * media players such as VLC.
 */
public class HlsJobCreationSample {

    // This is the ID of the Elastic Transcoder pipeline that was created when
    // setting up your AWS environment:
    // http://docs.aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html#java-pipeline
    private static final String PIPELINE_ID = "Enter your pipeline id here.";
    
    // This is the name of the input key that you would like to transcode.
    private static final String INPUT_KEY = "Enter your input key here.";
    
    // HLS Presets that will be used to create an adaptive bitrate playlist.
    private static final String HLS_64K_AUDIO_PRESET_ID = "1351620000001-200071";
    private static final String HLS_0400K_PRESET_ID     = "1351620000001-200050";
    private static final String HLS_0600K_PRESET_ID     = "1351620000001-200040";
    private static final String HLS_1000K_PRESET_ID     = "1351620000001-200030";
    private static final String HLS_1500K_PRESET_ID     = "1351620000001-200020";
    private static final String HLS_2000K_PRESET_ID     = "1351620000001-200010";
    
    // HLS Segment duration that will be targeted.
    private static final String SEGMENT_DURATION = "2";
    
    // All outputs will have this prefix prepended to their output key.
    private static final String OUTPUT_KEY_PREFIX = "elastic-transcoder-samples/output/hls/";
    
    // Clients are built using the default credentials provider chain.  This
    // will attempt to get your credentials in the following order:
    //      1. Environment variables (AWS_ACCESS_KEY and AWS_SECRET_KEY).
    //      2. Java system properties (AwsCredentials.properties).
    //      3. Instance profile credentials on EC2 instances.
    private static final AmazonElasticTranscoder amazonElasticTranscoder = new AmazonElasticTranscoderClient();
    
    public static void main(String[] args) throws Exception {
        
        Job job = createElasticTranscoderHlsJob();
        System.out.println("HLS job has been created: " + job);
    }
    
    /**
     * Creates a job which outputs an HLS playlist for adaptive bitrate playback.
     * @return Job that was created by Elastic Transcoder.
     * @throws Exception
     */
    private static Job createElasticTranscoderHlsJob() throws Exception {
        
        // Setup the job input using the provided input key.
        JobInput input = new JobInput()
            .withKey(INPUT_KEY);
        
        // Setup the job outputs using the HLS presets.
        String outputKey = TranscoderSampleUtilities.inputKeyToOutputKey(INPUT_KEY);
        CreateJobOutput hlsAudio = new CreateJobOutput()
            .withKey("hlsAudio/" + outputKey)
            .withPresetId(HLS_64K_AUDIO_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);
        CreateJobOutput hls0400k = new CreateJobOutput()
            .withKey("hls0400k/" + outputKey)
            .withPresetId(HLS_0400K_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);
        CreateJobOutput hls0600k = new CreateJobOutput()
            .withKey("hls0600k/" + outputKey)
            .withPresetId(HLS_0600K_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);
        CreateJobOutput hls1000k = new CreateJobOutput()
            .withKey("hls1000k/" + outputKey)
            .withPresetId(HLS_1000K_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);
        CreateJobOutput hls1500k = new CreateJobOutput()
            .withKey("hls1500k/" + outputKey)
            .withPresetId(HLS_1500K_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);
        CreateJobOutput hls2000k = new CreateJobOutput()
            .withKey("hls2000k/" + outputKey)
            .withPresetId(HLS_2000K_PRESET_ID)
            .withSegmentDuration(SEGMENT_DURATION);
        List<CreateJobOutput> outputs = Arrays.asList(hlsAudio, hls0400k, hls0600k, hls1000k, hls1500k, hls2000k);
        
        // Setup master playlist which can be used to play using adaptive bitrate.
        CreateJobPlaylist playlist = new CreateJobPlaylist()
            .withName("hls_" + outputKey)
            .withFormat("HLSv3")
            .withOutputKeys(hlsAudio.getKey(), hls0400k.getKey(), hls0600k.getKey(), hls1000k.getKey(), hls1500k.getKey(), hls2000k.getKey());
        
        // Create the job.
        CreateJobRequest createJobRequest = new CreateJobRequest()
            .withPipelineId(PIPELINE_ID)
            .withInput(input)
            .withOutputKeyPrefix(OUTPUT_KEY_PREFIX + outputKey + "/")
            .withOutputs(outputs)
            .withPlaylists(playlist);
        return amazonElasticTranscoder.createJob(createJobRequest).getJob();
    }
}
// snippet-end:[elastictranscoder.java.create_hls_job.import]
