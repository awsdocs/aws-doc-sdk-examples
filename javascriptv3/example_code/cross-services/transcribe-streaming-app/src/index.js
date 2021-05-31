/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
index.ts is part of a tutorial demonstrating how stream speech using Amazon Transcribe.
*/
// snippet-start:[transcribe.JavaScript.streaming.indexv3]
import { transcribeClient } from "./libs/transcribeClient.js";
import { StartStreamTranscriptionCommand } from "@aws-sdk/client-transcribe-streaming";
import MicrophoneStream from "microphone-stream";
import getUserMedia from "get-user-media-promise";

const pcmEncodeChunk = (chunk) => {
  const input = MicrophoneStream.toRaw(chunk);
  var offset = 0;
  var buffer = new ArrayBuffer(input.length * 2);
  var view = new DataView(buffer);
  for (var i = 0; i < input.length; i++, offset += 2) {
    var s = Math.max(-1, Math.min(1, input[i]));
    view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7fff, true);
  }
  return Buffer.from(buffer);
};

window.startRecord = async () => {
  try {
    console.log("Recording started");
    var record = document.getElementById("record");
    var stop = document.getElementById("stopRecord");
    record.disabled = true;
    record.style.backgroundColor = "blue";
    stop.disabled = false;

    // Start the browser microphone.
    const micStream = new MicrophoneStream();
    micStream.setStream(
      await window.navigator.mediaDevices.getUserMedia({
        video: false,
        audio: true,
      })
    );

    // Acquire the microphone audio stream.
    const audioStream = async function* () {
      for await (const chunk of micStream) {
        yield {
          AudioEvent: {
            AudioChunk: pcmEncodeChunk(
              chunk
            ) /* pcm Encoding is optional depending on the source. */,
          },
        };
      }
    };

    const command = new StartStreamTranscriptionCommand({
      // The language code for the input audio. Valid values are en-GB, en-US, es-US, fr-CA, and fr-FR.
      LanguageCode: "en-US",
      // The encoding used for the input audio. The only valid value is pcm.
      MediaEncoding: "pcm",
      // The sample rate of the input audio in Hertz. We suggest that you use 8000 Hz for low-quality audio and 16000 Hz for
      // high-quality audio. The sample rate must match the sample rate in the audio file.
      MediaSampleRateHertz: 16000,
      AudioStream: audioStream(),
    });

    // Send the speech stream to Amazon Transcribe.
    const data = await transcribeClient.send(command);
    console.log("Success", data.TranscriptResultStream);
    for await (const event of data.TranscriptResultStream) {
      for (const result of event.TranscriptEvent.Transcript.Results || []) {
        if (result.IsPartial === false) {
          const noOfResults = result.Alternatives[0].Items.length;
          // Print results to browser window.
          for (let i = 0; i < noOfResults; i++) {
            console.log(result.Alternatives[0].Items[i].Content);
            const outPut = result.Alternatives[0].Items[i].Content + " ";
            const outputDiv = document.getElementById("output");
            outputDiv.insertAdjacentHTML("beforeend", outPut);
          }
        }
      }
    }
    console.log("DONE", data);
    client.destroy();
  } catch (err) {
    console.log("Error. ", err);
  }
};

window.stopRecord = function () {
  const micStream = new MicrophoneStream();
  console.log("Recording stopped");
  var record = document.getElementById("record");
  var stop = document.getElementById("stopRecord");
  record.disabled = false;
  stop.disabled = true;
  record.style.backgroundColor = "red";
  micStream.stop();
};
// snippet-end:[transcribe.JavaScript.streaming.indexv3]
