// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the Amazon Rekognition Developer Guide' at
https://docs.aws.amazon.com/rekognition/latest/dg/image-bytes-javascript.html.

Purpose:
estimate-age.js is part of an sample that demonstrates how to use Amazon Rekognition to estimate the ages of faces in an photo.


Inputs :
- IDENTITY_POOL_ID

*/
// snippet-start:[rekognition.JavaScript.detect_faces_v3]

// Import required AWS SDK clients and commands for Node.js.
import { DetectFacesCommand } from "@aws-sdk/client-rekognition";
import { rekognitionClient } from "./libs/rekognitionClient.js";

// Calls DetectFaces API and shows estimated ages of detected faces.
window.DetectFaces = async (imageData) => {
  // Set the parameters.
  const params = {
    Image: {
      Bytes: imageData,
    },
    Attributes: ["ALL"],
  };
  try {
    const data = await rekognitionClient.send(new DetectFacesCommand(params));
    let table = "<table><tr><th>Low</th><th>High</th></tr>";
    // show each face and build out estimated age table
    for (let i = 0; i < data.FaceDetails.length; i++) {
      table += `<tr><td>${data.FaceDetails[i].AgeRange.Low}</td><td>${data.FaceDetails[i].AgeRange.High}</td></tr>`;
    }
    table += "</table>";
    document.getElementById("opResult").innerHTML = table;
    return data; // For unit tests.
  } catch (err) {
    console.log("Error", err);
  }
};

// Loads selected image and unencodes image bytes for Rekognition DetectFaces API.
window.ProcessImage = () => {
  /** @type {HTMLInputElement | null} */
  const control = document.getElementById("fileToUpload");
  const file = control.files[0];

  // Load base64 encoded image.
  const reader = new FileReader();
  reader.onload = (() => (e) => {
    const img = document.createElement("img");
    let image = null;
    img.src = e.target.result;
    let jpg = true;
    try {
      /** @type {string} */
      const result = e.target.result;
      image = atob(result.split("data:image/jpeg;base64,")[1]);
      console.log("image", image);
    } catch (e) {
      jpg = false;
    }
    if (jpg === false) {
      try {
        /** @type {string} */
        const result = e.target.result;
        image = atob(result.split("data:image/png;base64,")[1]);
      } catch (e) {
        alert("Not an image file Rekognition can process");
        return;
      }
    }
    // Unencode image bytes for Rekognition DetectFaces API.
    const length = image.length;
    const imageBytes = new ArrayBuffer(length);
    const ua = new Uint8Array(imageBytes);
    for (let i = 0; i < length; i++) {
      ua[i] = image.charCodeAt(i);
    }
    // Call Rekognition.
    window.DetectFaces(ua);
  })(file);
  reader.readAsDataURL(file);
};
// snippet-end:[rekognition.JavaScript.detect_faces_v3]
// For unit tests.
// module.exports = {DetectFaces}
