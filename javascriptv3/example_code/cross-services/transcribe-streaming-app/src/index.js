// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
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
const recordButton = document.getElementById("record");
const inputLanguageList = document.getElementById("inputLanguageList");
const transcribedText = document.getElementById("transcribedText");
const translatedText = document.getElementById("translatedText");
const translationLanguageList = document.getElementById(
  "translationLanguageList",
);
const email = document.getElementById("email");

window.onRecordPress = () => {
  if (recordButton.getAttribute("class") === "recordInactive") {
    startRecording();
  } else {
    stopRecording();
  }
};

const startRecording = async () => {
  window.clearTranscription();
  const selectedLanguage = inputLanguageList.value;
  if (selectedLanguage === "nan") {
    alert("Please select a language");
    return;
  }
  inputLanguageList.disabled = true;
  recordButton.setAttribute("class", "recordActive");
  try {
    const { startRecording } = await import("./libs/transcribeClient.js");
    await startRecording(selectedLanguage, onTranscriptionDataReceived);
  } catch (error) {
    alert(`An error occurred while recording: ${error.message}`);
    await stopRecording();
  }
};

const onTranscriptionDataReceived = (data) => {
  transcribedText.insertAdjacentHTML("beforeend", data);
};

const stopRecording = async () => {
  inputLanguageList.disabled = false;
  recordButton.setAttribute("class", "recordInactive");
  const { stopRecording } = await import("./libs/transcribeClient.js");
  stopRecording();
};

window.translateText = async () => {
  const sourceText = transcribedText.innerHTML;
  if (sourceText.length === 0) {
    alert("No text to translate!");
    return;
  }
  const targetLanguage = translationLanguageList.value;
  if (targetLanguage === "nan") {
    alert("Please select a language to translate to!");
    return;
  }
  try {
    const { translateTextToLanguage } = await import(
      "./libs/translateClient.js"
    );
    const translation = await translateTextToLanguage(
      sourceText,
      targetLanguage,
    );
    if (translation) {
      translatedText.innerHTML = translation;
    }
  } catch (error) {
    alert(`There was an error translating the text: ${error.message}`);
  }
};

window.clearTranscription = () => {
  transcribedText.innerHTML = "";
  translatedText.innerHTML = "";
};

window.sendEmail = async () => {
  /** @type {string} */
  const receiver = email.value;
  if (receiver.length === 0) {
    alert("Please enter an email address!");
    return;
  }
  const originalText = transcribedText.innerHTML;
  const translation = translatedText.innerHTML;
  const sender = receiver;
  try {
    const { sendEmail } = await import("./libs/emailClient.js");
    await sendEmail(sender, receiver, originalText, translation);
    alert("Success! Email sent!");
  } catch (error) {
    alert(`There was an error sending the email: ${error}`);
  }
};
// snippet-end:[transcribe.JavaScript.streaming.indexv3]
