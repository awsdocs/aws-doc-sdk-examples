/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

Purpose:
helper.js is part of a tutorial demonstrating how to build and deploy an app that transcribes and displays
voice recordings for authenticated users. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

Running the code:
For more information, see https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

*/
// This file contains vanilla JavaScript helper functions for user interface.

// snippet-start:[transcribe.JavaScript.recording-app.helper]

window.downloadInnerHtml = function (filename, elId, mimeType) {
  var elHtml = document.getElementById(elId).innerHTML;
  var link = document.createElement("a");
  mimeType = mimeType || "text/plain";
  link.setAttribute("download", filename);
  link.setAttribute(
    "href",
    "data:" + mimeType + ";charset=utf-8," + encodeURIComponent(elHtml)
  );
  link.click();
};

// Delete a row from the user interface.
window.deleteRow = function (rowid) {
  const row = document.getElementById(rowid);
  row.parentNode.removeChild(row);
};

// Display transcription details on user interface.
window.displayTranscriptionDetails = function (
  i,
  outputJSONTime,
  jobName,
  outputJSON
) {
  var table = document.getElementById("myTable");
  var row = table.insertRow(1);
  var cell1 = row.insertCell(0);
  var cell2 = row.insertCell(1);
  var cell3 = row.insertCell(2);
  var cell4 = row.insertCell(3);
  cell1.innerHTML = "<p id='dateTime'></p>";
  cell2.innerHTML = "<p id=" + i + "></p>";
  cell3.innerHTML = "<button type='button' id='download'>Download</button>";
  cell4.innerHTML = "<button type='button' id='delete'>Delete</button>";
  document.getElementById(i).innerHTML = outputJSON;
  row.setAttribute("id", "row" + i);
  document
    .getElementById("download")
    .setAttribute(
      "onclick",
      "downloadInnerHtml('output.txt','" + i + "','text')"
    );
  document.getElementById("dateTime").innerHTML = outputJSONTime;
  document
    .getElementById("delete")
    .setAttribute(
      "onclick",
      "deleteRow(" + "'" + "row" + i + "'); deleteJSON(" + "'" + jobName + "')"
    );
};

// Strips the token ID from the app URL after authentication.
window.getToken = function () {
  var idtoken = window.location.href;
  var idtoken1 = idtoken.split("=")[1];
  var idtoken2 = idtoken1.split("&")[0];
  var idtoken3 = idtoken2.split("&")[0];
  return idtoken3;
};

window.getAccessToken = function () {
  var accesstoken = window.location.href;
  var accesstoken1 = accesstoken.split("=")[2];
  var accesstoken2 = accesstoken1.split("&")[0];
  return accesstoken2;
};
// snippet-end:[transcribe.JavaScript.recording-app.helper]
