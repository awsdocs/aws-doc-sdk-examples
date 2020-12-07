//snippet-sourcedescription:[Microphone.java is a helper class to the BidirectionalStreaming example that setups the app to listen on the device's microphone for audio input.]
// snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Transcribe]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.amazonaws.transcribe;

//snippet-start:[transcribe.java2.bidir_streaming_microphone.complete]

//snippet-start:[transcribe.java2.bidir_streaming_microphone.import]
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
//snippet-end:[transcribe.java2.bidir_streaming_microphone.import]

public class Microphone {

    public static TargetDataLine get() throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, format);

        TargetDataLine dataLine = (TargetDataLine) AudioSystem.getLine(datalineInfo);
        dataLine.open(format);

        return dataLine;
    }
}
//snippet-end:[transcribe.java2.bidir_streaming_microphone.complete]