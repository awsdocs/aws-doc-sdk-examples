/*Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
index.js contains the JavaScript for an web app that analyzes videos for label detection using the
AWS JavaScript SDK for JavaScript v3.

Inputs:
- BUCKET
- IAM_ROLE_ARN

 */
import { rekognitionClient } from "../libs/rekognitionClient.js";
import { s3Client } from "../libs/s3Client.js";
import { sesClient } from "../libs/sesClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import {
  ListObjectsCommand,
  PutObjectCommand,
  DeleteObjectCommand,
} from "@aws-sdk/client-s3";
import {
  StartFaceDetectionCommand,
  GetFaceDetectionCommand,
} from "@aws-sdk/client-rekognition";
const BUCKET = "BUCKET_NAME";
const IAM_ROLE_ARN = "IAM_ROLE_ARN";

$(function () {
  $("#myTable").DataTable({
    scrollY: "500px",
    scrollX: true,
    scrollCollapse: true,
    paging: true,
    columnDefs: [{ width: 200, targets: 0 }],
    fixedColumns: true,
  });
});
// Upload the video.

const uploadVideo = async () => {
  try {
    // Retrieve a list of objects in the bucket.
    const listObjects = await s3Client.send(
      new ListObjectsCommand({ Bucket: BUCKET })
    );
    console.log("Object in bucket: ", listObjects);
    console.log("listObjects.Contents ", listObjects.Contents);

    const noOfObjects = listObjects.Contents;
    // If the Amazon S3 bucket is not empty, delete the existing content.
    if (noOfObjects != null) {
      for (let i = 0; i < noOfObjects.length; i++) {
        await s3Client.send(
          new DeleteObjectCommand({
            Bucket: BUCKET,
            Key: listObjects.Contents[i].Key,
          })
        );
      }
    }
    console.log("Success - bucket empty.");

    // Create the parameters for uploading the video.
    const files = document.getElementById("videoupload").files;
    const file = files[0];
    const uploadParams = {
      Bucket: BUCKET,
      Body: file,
    };
    uploadParams.Key = path.basename(file.name);
    await s3Client.send(new PutObjectCommand(uploadParams));
    console.log("Success - video uploaded");
  } catch (err) {
    console.log("Error", err);
  }
};
window.uploadVideo = uploadVideo;
const getVideo = async () => {
  try {
    const listVideoParams = {
      Bucket: BUCKET,
    };
    const data = await s3Client.send(new ListObjectsCommand(listVideoParams));
    console.log("Success - video deleted", data);
    const videoName = data.Contents[0].Key;
    document.getElementById("videoname").innerHTML = videoName;
    const videoDate = data.Contents[0].LastModified;
    document.getElementById("videodate").innerHTML = videoDate;
    const videoOwner = data.Contents[0].Owner;
    document.getElementById("videoowner").innerHTML = videoOwner;
    const videoSize = data.Contents[0].Size;
    document.getElementById("videosize").innerHTML = videoSize;
  } catch (err) {
    console.log("Error", err);
  }
};
window.getVideo = getVideo;

const ProcessImages = async () => {
  try {
    // Create the parameters required to start face detection.
    const videoName = document.getElementById("videoname").innerHTML;
    const startDetectParams = {
      Video: {
        S3Object: {
          Bucket: BUCKET,
          Name: videoName,
        },
      },
      notificationChannel: {
        roleARN: IAM_ROLE_ARN,
        SNSTopicArn: SNSTOPIC,
      },
    };
    // Start the Amazon Rekognition face detection process.
    const data = await rekognitionClient.send(
      new StartFaceDetectionCommand(startDetectParams)
    );
    console.log("Success, face detection started. ", data);
    const faceDetectParams = {
      JobId: data.JobId,
    };
    try {
      var finished = false;
      var facesArray = [];
      // Detect the faces.
      while (!finished) {
        var results = await rekognitionClient.send(
          new GetFaceDetectionCommand(faceDetectParams)
        );
        // Wait until the job succeeds.
        if (results.JobStatus == "SUCCEEDED") {
          finished = true;
        }
      }
      finished = false;
      // Parse results into CVS format.
      var i;
      for (i = 0; i < results.Faces.length; i++) {
        var boundingbox = JSON.stringify(results.Faces[i].Face.BoundingBox);
        var confidence = JSON.stringify(results.Faces[i].Face.Confidence);
        var pose = JSON.stringify(results.Faces[i].Face.Pose);
        var quality = JSON.stringify(results.Faces[i].Face.Quality);
        var arrayfirst = [];
        var arraysecond = [];
        var arraythird = [];
        var arrayforth = [];
        arrayfirst.push(boundingbox);
        arraysecond.push(confidence);
        arraythird.push(pose);
        arrayforth.push(quality);
        arrayfirst.push(arraysecond);
        arrayfirst.push(arraythird);
        arrayfirst.push(arrayforth);
        facesArray.push(arrayfirst);
      }
      // Create the CSV file.
      create_csv_file(facesArray);
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
window.ProcessImages = ProcessImages;

// Helper function to create the CSV file.
function create_csv_file(facesArray) {
  // Define the heading for each row of the data.
  var csv = "Bounding Box, , , , Confidance, Pose, , ,  Quality, ,\n";

  // Merge the data with CSV.
  facesArray.forEach(function (row) {
    csv += row.join(",");
    csv += "\n";
  });
  // Upload the CSV file to Amazon S3.
  uploadFile(csv);
}

// Helper function to upload file to Amazon S3.
const uploadFile = async (csv) => {
  const uploadParams = {
    Bucket: BUCKET,
    Body: csv,
    Key: "Face.csv",
  };
  try {
    await s3Client.send(new PutObjectCommand(uploadParams));
    const linkToCSV =
      "https://s3.console.aws.amazon.com/s3/object/" +
      uploadParams.Bucket +
      "?region=" +
      REGION +
      "&prefix=" +
      uploadParams.key;
    console.log("Success. Report uploaded to " + linkToCSV + ".");

    // Send an email to notify user when report is available.
    sendEmail(uploadParams.Bucket, uploadParams.Key);
  } catch (err) {
    console.log("Error", err);
  }
};
// Helper function to send an email to user.
const sendEmail = async (bucket, key) => {
  const toEmail = document.getElementById("email").value;
  const fromEmail = "SENDER_ADDRESS"; //
  try {
    const linkToCSV =
      "https://s3.console.aws.amazon.com/s3/object/" +
      bucket +
      "?region=" +
      REGION +
      "&prefix=" +
      key;
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
