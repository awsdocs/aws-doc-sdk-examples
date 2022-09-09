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
import * as TranscribeClient from "./libs/transcribeClient.js";
import * as TranslateClient from "./libs/translateClient.js";
import * as EmailClient from "./libs/emailClient.js";

window.onRecordPress = () => {
  let recordingButton = document.getElementById("record");
  if (recordingButton.getAttribute("class") === "recordInactive") {
    startRecording();
  } else {
    stopRecording();
  }
};

const startRecording = async() => {
  window.clearTranscription();
  const recordingButton = document.getElementById("record");
  const languageList = document.getElementById("inputLanguageList");
  const selectedLanguage = languageList.value;
  if (selectedLanguage === "nan") {
    alert("Please select a language");
    return;
  }
  languageList.disabled = true;
  recordingButton.setAttribute("class", "recordActive");
  try {
    await TranscribeClient.startRecording(selectedLanguage, onTranscriptionDataReceived);
  } catch(error) {
    alert("An error occurred while recording: " + error.message);
    stopRecording();
  }
};

const onTranscriptionDataReceived = (data) => {
  const outputDiv = document.getElementById("output");
  outputDiv.insertAdjacentHTML("beforeend", data);
}

const stopRecording = function () {
  let recordingButton = document.getElementById("record");
  let languageList = document.getElementById("inputLanguageList");
  languageList.disabled = false;
  recordingButton.setAttribute("class", "recordInactive");
  TranscribeClient.stopRecording();
};

window.translateText = async () => {
  const sourceText = document.getElementById("output").innerHTML;
  if (sourceText.length === 0) {
    alert("No text to translate!");
    return;
  }
  const targetLanguage = document.getElementById("translationLanguageList").value;
  if (targetLanguage === "nan") {
    alert("Please select a language to translate to!");
    return;
  }
  try {
    let translation = await TranslateClient.translateTextToLanguage(sourceText, targetLanguage);
    if (translation) {
      document.getElementById("translated").innerHTML = translation;
    }
  } catch (error) {
    alert("There was an error translating the text: " + error.message);
  }
};

window.clearTranscription = () => {
  document.getElementById("output").innerHTML = "";
  document.getElementById("translated").innerHTML = "";
};

window.sendEmail = async () => {
  const receiver = document.getElementById("email").value;
  if (receiver.length === 0) {
    alert("Please enter an email address!");
    return;
  }
  const originalText = document.getElementById("output").innerHTML;
  const translatedText = document.getElementById("translated").innerHTML;
  const sender = receiver;
  try {
    await EmailClient.sendEmail(sender, receiver, originalText, translatedText);
    alert("Success! Email sent!");
  } catch (error) {
    alert("There was an error sending the email: " + error);
  }
};
// snippet-end:[transcribe.JavaScript.streaming.indexv3]
