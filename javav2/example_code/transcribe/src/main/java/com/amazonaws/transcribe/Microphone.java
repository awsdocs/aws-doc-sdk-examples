//snippet-sourcedescription:[Microphone.java is a helper class to the BidirectionalStreaming example that setups the app to listen on the device's microphone for audio input.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Transcribe]
//snippet-keyword:[bidirectional streaming]
//snippet-service:[transcribe]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-18]
//snippet-sourceauthor:[AWS]
package com.amazonaws.transcribe;

//snippet-start:[transcribe.java.bidir_streaming.microphone.complete]

//snippet-start:[transcribe.java.bidir_streaming.microphone.import]
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
//snippet-end:[transcribe.java.bidir_streaming.microphone.import]

public class Microphone {

    public static TargetDataLine get() throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, format);

        TargetDataLine dataLine = (TargetDataLine) AudioSystem.getLine(datalineInfo);
        dataLine.open(format);

        return dataLine;
    }
}
//snippet-end:[transcribe.java.bidir_streaming.microphone.complete]
