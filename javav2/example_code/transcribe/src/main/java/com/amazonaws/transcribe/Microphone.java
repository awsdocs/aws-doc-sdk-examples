//snippet-sourcedescription:[Microphone.java is a helper class to the BidirectionalStreaming example that setups the app to listen on the device's microphone for audio input.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Transcribe]
//snippet-keyword:[bidirectional streaming]
//snippet-service:[transcribe]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-18]
//snippet-sourceauthor:[AWS]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.amazonaws.transcribe;

//snippet-start:[transcribe.java.bidir_streaming_microphone.complete]

//snippet-start:[transcribe.java.bidir_streaming_microphone.import]
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
//snippet-end:[transcribe.java.bidir_streaming_microphone.import]

public class Microphone {

    public static TargetDataLine get() throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, format);

        TargetDataLine dataLine = (TargetDataLine) AudioSystem.getLine(datalineInfo);
        dataLine.open(format);

        return dataLine;
    }
}
//snippet-end:[transcribe.java.bidir_streaming_microphone.complete]
