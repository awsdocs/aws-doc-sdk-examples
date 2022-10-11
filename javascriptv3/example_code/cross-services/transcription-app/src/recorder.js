/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

Purpose:
recorder.js is part of a tutorial demonstrating how to build and deploy an app that transcribes and displays
voice recordings for authenticated users. To run the full tutorial, see
https://docs.aws.amazon.comsdk-for-javascript/v3/developer-guide/transcribe-app.html.

*/

// snippet-start:[transcribe.JavaScript.recording-app.recorder]

// Functions for recording transcriptions.
// Enable microphone on browser.
import "./helper.js";
import index from "./index.js";
navigator.mediaDevices.getUserMedia({ audio: true }).then((stream) => {
  handlerFunction(stream);
});

// This is a handler function to manage recordings.
function handlerFunction(stream) {
  let rec;
  rec = new MediaRecorder(stream);
  rec.ondataavailable = (e) => {
    audioChunks.push(e.data);
    if (rec.state == "inactive") {
      let blob = new Blob(audioChunks, { type: "audio/mpeg-3" });
      var recordedAudio = document.getElementById("recordedAudio");
      recordedAudio.src = URL.createObjectURL(blob);
      recordedAudio.controls = true;
      recordedAudio.autoplay = true;
      // Take username from 'index.js'.
      var username = index.username;
      // The upload function is in 'index.js'.
      upload(blob, username);
      alert("Refresh page in ~1 min to view your transcription.");
    }
  };
}

// Start recording.
window.startRecord = function () {
  console.log("Recording started");
  var record = document.getElementById("record");
  var stop = document.getElementById("stopRecord");
  record.disabled = true;
  record.style.backgroundColor = "blue";
  stop.disabled = false;
  rec.start();
};

// Stop recording.
window.stopRecord = function () {
  console.log("Recording stopped");
  var record = document.getElementById("record");
  var stop = document.getElementById("stopRecord");
  record.disabled = false;
  stop.disabled = true;
  record.style.backgroundColor = "red";
  rec.stop();
};

// snippet-end:[transcribe.JavaScript.recording-app.recorder]
