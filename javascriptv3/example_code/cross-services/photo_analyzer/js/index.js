/*Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
index.js contains the JavaScript for a tutorial demonstrating how to build a web app that analyzes photos using AWS Rekognition through the
JavaScript SDK for JavaScript v3.
To run the full tutorial, see https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services/photo-analyzer.

Inputs:
- BUCKET_IMAGES
- BUCKET_REPORTS
- EMAIL_SENDER_ADDRESS

Running the code:
To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/photo-analyzer.html.
 */
<!-- snippet-start:[rekognition.Javascript.photo-analyzer.complete]-->
<!-- snippet-start:[rekognition.Javascript.photo-analyzer.config]-->
import { DetectLabelsCommand } from "@aws-sdk/client-rekognition";
import { rekognitionClient } from "../libs/rekognitionClient.js";
import { ListObjectsCommand, PutObjectCommand } from "@aws-sdk/client-s3";
import { REGION, s3Client } from "../libs/s3Client.js";
import { sesClient } from "../libs/sesClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";

// Set global parameters.
const BUCKET_IMAGES =
    "BUCKET_IMAGES";
const BUCKET_REPORTS =
    "BUCKET_REPORTs";
const EMAIL_SENDER_ADDRESS = "EMAIL_SENDER_ADDRESS"; // A verified Amazon SES email address.

<!-- snippet-end:[rekognition.Javascript.photo-analyzer.config]-->

<!-- snippet-start:[rekognition.Javascript.photo-analyzer.table-functions]-->

// Load table parameters.
$(function() {
  $('#myTable').DataTable( {
    scrollY:        "500px",
    scrollX:        true,
    scrollCollapse: true,
    paging:         true,
    columnDefs: [
      { width: 200, targets: 0 }
    ],
    fixedColumns: true
  } );
} );

// Load images from Amazon S3 bucket to the table.
const loadTable = async () => {
  window.alert = function() {};
  try {
    const listVideoParams = {
      Bucket: BUCKET_IMAGES
    };
    const data = await s3Client.send(new ListObjectsCommand(listVideoParams));
    console.log("Success", data);
    for (let i = 0; i < data.Contents.length; i++) {
      console.log('checking')
      var t = $('#myTable').DataTable();
      t.row.add([
        data.Contents[i].Key,
        data.Contents[i].Owner,
        data.Contents[i].LastModified,
        data.Contents[i].Size
      ]).draw(false);
    };
  } catch (err) {
    console.log("Error", err);
  }
};
loadTable();

// Refresh page to populate the table with the latest images.
const getImages = async () => {
  window.location.reload();
};
window.getImages = getImages;

<!-- snippet-end:[rekognition.Javascript.photo-analyzer.table-functions]-->
<!-- snippet-start:[rekognition.Javascript.photo-analyzer.process-images]-->
// Add images to the Amazon S3 bucket.
const addToBucket = async () => {
  try{
    // Create the parameters for uploading the video.
    const files = document.getElementById("imageupload").files;
    const file = files[0];
    const key = document.getElementById("imageupload").files[0].name
    const uploadParams = {
      Bucket: BUCKET_IMAGES,
      Body: file,
      Key: key
    };

    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    console.log("Success - image uploaded");
  } catch (err) {
    console.log("Error", err);
  }
};
// Expose function to browser.
window.addToBucket = addToBucket;


const ProcessImages = async () => {
  try {
    const listPhotosParams = {
      Bucket: BUCKET_IMAGES,
    };
    // Retrieve list of objects in the Amazon S3 bucket.
    const data = await s3Client.send(new ListObjectsCommand(listPhotosParams));
    console.log("Success, list of objects in bucket retrieved.", data);

    // Loop through images. For each image, retreive the image name,
    // then analyze image by detecting it's labels, then parse results
    // into CSV format.
    for (let i = 0; i < data.Contents.length; i++) {
      const key = data.Contents[i].Key;
      const imageParams = {
        Image: {
          S3Object: {
            Bucket: BUCKET_IMAGES,
            Name: key,
          },
        },
      };

      const lastdata = await rekognitionClient.send(
        new DetectLabelsCommand(imageParams)
      );
      console.log("Success, labels detected.", lastdata);
      var objectsArray = [];
      // Parse results into CVS format.
      const noOfLabels = lastdata.Labels.length;
      var j;
      for (j = 0; j < lastdata.Labels.length; j++) {
        var name = JSON.stringify(lastdata.Labels[j].Name);
        var confidence = JSON.stringify(lastdata.Labels[j].Confidence);
        var arrayfirst = [];
        var arraysecond = [];
        arrayfirst.push(name);
        arraysecond.push(confidence);
        arrayfirst.push(arraysecond);
        objectsArray.push(arrayfirst);
      }
      // Create a CSV file report for each images.
      create_csv_file(objectsArray, key);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
// Expose function to browser.
window.ProcessImages = ProcessImages;

// Helper function to create the CSV report.
function create_csv_file(objectsArray, key) {
  // Define the heading for each row of the data.
  var csv = "Object, Confidance \n";

  // Merge the data with CSV.
  objectsArray.forEach(function (row) {
    csv += row.join(",");
    csv += "\n";
  });
  // Upload the CSV file to Amazon S3 bucket for reports.
  uploadFile(csv, key);
}

// Helper function to upload reports to Amazon S3 bucket for reports.
const uploadFile = async (csv, key) => {
  const uploadParams = {
    Bucket: BUCKET_REPORTS,
    Body: csv,
    Key: key + ".csv",
  };
  try {
    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    const linkToCSV =
      "https://s3.console.aws.amazon.com/s3/object/" +
      uploadParams.Bucket +
      "?region=" +
      REGION +
      "&prefix=" +
      uploadParams.Key;
    console.log("Success. Report uploaded to " + linkToCSV + ".");

    // Send an email to notify the user when report is available.
    sendEmail(uploadParams.Bucket, uploadParams.Key, linkToCSV);
  } catch (err) {
    console.log("Error", err);
  }
};
// Helper function to send an email to the user.
const sendEmail = async (bucket, key, linkToCSV) => {
  const toEmail = document.getElementById("email").value;
  const fromEmail = EMAIL_SENDER_ADDRESS; //
  try {
    // Set the parameters.
    const params = {
      Destination: {
        /* required */
        CcAddresses: [
          /* Insert Cc email addresses here. */
        ],
        ToAddresses: [
          toEmail, //RECEIVER_ADDRESS
          /* Insert additional email addresses here.. */
        ],
      },
      Message: {
        /* required */
        Body: {
          /* required */
          Html: {
            Charset: "UTF-8",
            Data:
              "<h1>Hello!</h1><p>Please see the the analyzed video report for " +
              key +
              " <a href=" +
              linkToCSV +
              "> here</a></p>",
          },
          Text: {
            Charset: "UTF-8",
            Data:
              "Hello,\\r\\n" +
              "Please see the attached file for the analyzed video report at" +
              linkToCSV +
              "\n\n",
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: key + " analyzed video report ready",
        },
      },
      Source: fromEmail, // SENDER_ADDRESS
      ReplyToAddresses: [
        /* more items */
      ],
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    console.log("Success. Email sent.", data);
  } catch (err) {
    console.log("Error", err);
  }
};
<!-- snippet-end:[rekognition.Javascript.photo-analyzer.process-images]-->
<!-- snippet-end:[rekognition.Javascript.photo-analyzer.complete]-->
