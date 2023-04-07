import {
  TranscribeStreamingClient,
  StartStreamTranscriptionCommand,
} from "@aws-sdk/client-transcribe-streaming";

import MicrophoneStream from "microphone-stream";
import { useEffect, useState } from "react";

interface TranscriptionServiceHookProps {
  recording: boolean;
}

export function useTranscriptionServiceHook({
  recording,
}: TranscriptionServiceHookProps) {
  const [transcription, setTranscription] = useState("");

  useEffect(() => {
    (async () => {
      if (recording) {
        const micStream = new MicrophoneStream();
        micStream.setStream(
          await window.navigator.mediaDevices.getUserMedia({
            audio: true,
            video: false,
          })
        );

        const transcribeStreamingClient = new TranscribeStreamingClient({});
      }
    })();
  }, [recording]);

  return transcription;
}
