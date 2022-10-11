/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
index.js is part of a tutorial demonstrating how to:
- Transcribe speech in real-time using Amazon Transcribe
- Detect the language of the transcription using Amazon Comprehend
- Translate the transcription using Amazon Translate
- Send the transcription and translation by email using Amazon Simple Email Service (Amazon SES)
*/

// snippet-start:[transcribe.JavaScript.streaming.indexv3]
import { transcribeClient } from "./libs/transcribeClient.js";
import { translateClient } from "./libs/translateClient.js";
import { sesClient } from "./libs/sesClient.js";
import { comprehendClient } from "./libs/comprehendClient.js";
import { DetectDominantLanguageCommand } from "@aws-sdk/client-comprehend";
import { StartStreamTranscriptionCommand } from "@aws-sdk/client-transcribe-streaming";
import { TranslateTextCommand } from "@aws-sdk/client-translate";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import MicrophoneStream from "microphone-stream";

// Helper function to encode PCM audio.
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
            AudioChunk:
              pcmEncodeChunk(
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
      // The sample rate of the input audio in Hertz.
      MediaSampleRateHertz: 44100,
      AudioStream: audioStream(),
    });

    // Send the speech stream to Amazon Transcribe.
    const data = await transcribeClient.send(command);
    console.log("Success", data.TranscriptResultStream);
    // return data; //For unit tests only.
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
    console.log("Success. ", data);
    client.destroy();
  } catch (err) {
    console.log("Error. ", err);
  }
};

window.stopRecord = function () {
  window.location.reload();
};

window.translateText = async () => {
  try {
    const outPut = document.getElementById("output").innerHTML;
    const data = await comprehendClient.send(
      new DetectDominantLanguageCommand({ Text: outPut })
    );
    // return data; // Uncomment this for unit tests.
    const langCode = data.Languages[0].LanguageCode;
    try {
      const selectedValue = document.getElementById("list").value;
      const translateParams = {
        Text: outPut,
        SourceLanguageCode: langCode /* required */,
        TargetLanguageCode: selectedValue /* required */,
      };
      const data = await translateClient.send(
        new TranslateTextCommand(translateParams)
      );
      document.getElementById("translated").innerHTML = data.TranslatedText;
    } catch (err) {
      console.log("Error translating language. ", err);
    }
  } catch (err) {
    console.log("Error detecting language of text. ", err);
  }
};

window.clearTranscription = async () => {
  document.getElementById("output").innerHTML = "";
};

// Helper function to send an email to user.
window.sendEmail = async () => {
  const toEmail = document.getElementById("email").value;
  const outputDiv = document.getElementById("output").innerHTML;
  const translatedDiv = document.getElementById("translated").innerHTML;
  const fromEmail = "SENDER_ADDRESS";
  try {
    // Set the parameters
    const params = {
      Destination: {
        /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          toEmail, //RECEIVER_ADDRESS
          /* more To-email addresses */
        ],
      },
      Message: {
        /* required */
        Body: {
          /* required */
          Html: {
            Charset: "UTF-8",
            Data:
              "<h1>Hello!</h1><p>Here is your Amazon Transcribe recording:</p>" +
              "<h1>Original</h1>" +
              "<p>" +
              outputDiv +
              "</p>" +
              "<h1>Translation (if available)</h1>" +
              "<p>" +
              translatedDiv +
              "</p>",
          },
          Text: {
            Charset: "UTF-8",
            Data:
              "Hello,\\r\\n" +
              "Here is your Amazon Transcribe transcription:" +
              "\n" +
              outputDiv,
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: "Your Amazon Transcribe transcription.",
        },
      },
      Source: fromEmail, // Sender's email address (required)
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    alert("Success. Email sent.");
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[transcribe.JavaScript.streaming.indexv3]
// module.exports = {sendEmail, translateText, startRecord }
