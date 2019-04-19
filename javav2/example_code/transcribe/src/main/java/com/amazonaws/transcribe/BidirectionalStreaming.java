//snippet-sourcedescription:[BidirectionalStreaming.java demonstrates how to use the AWS Transcribe service to transcribe an audio input from the microphone.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Transcribe]
//snippet-keyword:[bidirectional streaming]
//snippet-service:[transcribe]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-04-18]
//snippet-sourceauthor:[AWS]

package com.amazonaws.transcribe;
//snippet-start:[transcribe.java.bidir_streaming.complete]

//snippet-start:[transcribe.java.bidir_streaming.import]
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import javax.sound.sampled.AudioInputStream;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.transcribestreaming.TranscribeStreamingAsyncClient;
import software.amazon.awssdk.services.transcribestreaming.model.LanguageCode;
import software.amazon.awssdk.services.transcribestreaming.model.MediaEncoding;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionRequest;
import software.amazon.awssdk.services.transcribestreaming.model.StartStreamTranscriptionResponseHandler;
import software.amazon.awssdk.services.transcribestreaming.model.TranscriptEvent;
//snippet-end:[transcribe.java.bidir_streaming.import]

public class BidirectionalStreaming {
	
	//snippet-start:[transcribe.java.bidir_streaming.main]

	 public static void main(String[] args) throws Exception {
	        TranscribeStreamingAsyncClient client = TranscribeStreamingAsyncClient.builder().credentialsProvider(ProfileCredentialsProvider.create()).build();

	        StartStreamTranscriptionRequest request = StartStreamTranscriptionRequest.builder()
	                                                                                 .mediaEncoding(MediaEncoding.PCM)
	                                                                                 .languageCode(LanguageCode.EN_US)
	                                                                                 .mediaSampleRateHertz(16_000).build();

	        TargetDataLine mic = Microphone.get();
	        mic.start();

	        AudioStreamPublisher publisher = new AudioStreamPublisher(new AudioInputStream(mic));

	        StartStreamTranscriptionResponseHandler response =
	            StartStreamTranscriptionResponseHandler.builder().subscriber(e -> {
	                TranscriptEvent event = (TranscriptEvent) e;
	                event.transcript().results().forEach(r -> r.alternatives().forEach(a -> System.out.println(a.transcript())));
	            }).build();

	        client.startStreamTranscription(request, publisher, response).join();
	    }
	 
	//snippet-end:[transcribe.java.bidir_streaming.main]

	
	public static TargetDataLine get() throws Exception {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info datalineInfo = new DataLine.Info(TargetDataLine.class, format);

        TargetDataLine dataLine = (TargetDataLine) AudioSystem.getLine(datalineInfo);
        dataLine.open(format);

        return dataLine;
    }

}

//snippet-end:[transcribe.java.bidir_streaming.complete]

