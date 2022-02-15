/*Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
    SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
index.js contains the JavaScript for a tutorial demonstrating how to build a web app that
analyzes photos for Personal Protective Equipment (PPE) using AWS Rekognition through the
JavaScript SDK for JavaScript v3.

Inputs:
- BUCKET_IMAGES
- BUCKET_REPORTS
- EMAIL_SENDER_ADDRESS

Running the code:
To run the full tutorial, see
https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/javascriptv3/example_code/cross-services/photo-analyzer-ppe/Readme.md
 */
// snippet-start:[s3.JavaScript.detect-ppe.indexv3]
import { rekognitionClient } from "../libs/rekognitionClient.js";
import { s3Client } from "../libs/s3Client.js";
import { dynamoDBClient, REGION } from "../libs/dynamodbClient.js";
import { sesClient } from "../libs/sesClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import { ListObjectsCommand } from "@aws-sdk/client-s3";
import { DetectProtectiveEquipmentCommand } from "@aws-sdk/client-rekognition";
import { PutItemCommand } from "@aws-sdk/client-dynamodb";

const BUCKET = "S3_BUCKET_NAME";
const TABLE = "DDB_TABLE_NAME";
const FROM_EMAIL = "SENDER_EMAIL_ADDRESS";

export const sendEmail = async () => {
  // Helper function to send an email to user.
  const TO_EMAIL = document.getElementById("email").value;
  try {
    // Set the parameters
    const params = {
      Destination: {
        /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          TO_EMAIL, //RECEIVER_ADDRESS
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
                "<h1>Hello!</h1>" +
                "<p> The Amazon DynamoDB table " +
                TABLE +
                " has been updated with PPE information <a href='https://" +
                REGION +
                ".console.aws.amazon.com/dynamodb/home?region=" +
                REGION +
                "#item-explorer?table=" +
                TABLE +
                "'>here.</a></p>"
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: "PPE image report ready.",
        },
      },
      Source: FROM_EMAIL,
      ReplyToAddresses: [
        /* more items */
      ],
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    alert("Success. Email sent.");
  } catch (err) {
    console.log("Error sending email. ", err);
  }
};

export const processImages = async () => {
  try {
    const listPhotosParams = {
      Bucket: BUCKET,
    };
    // Retrieve list of objects in the Amazon S3 bucket.
    const data = await s3Client.send(new ListObjectsCommand(listPhotosParams));
    console.log("Success, list of objects in bucket retrieved.", data);

    // Helper function to convert floating numbers to integers.
    function float2int(value) {
      return value | 0;
    }

    // Loop through images to get the parameters for each.
    for (let i = 0; i < data.Contents.length; i++) {
      const key = data.Contents[i].Key;

      const imageParams = {
        Image: {
          S3Object: {
            Bucket: BUCKET,
            Name: key,
          },
        },
        SummarizationAttributes: {
          MinConfidence: float2int(50) /* required */,
          RequiredEquipmentTypes: ["FACE_COVER", "HAND_COVER", "HEAD_COVER"],
        },
      };
      const ppedata = await rekognitionClient.send(
          new DetectProtectiveEquipmentCommand(imageParams)
      );

      // Parse the results using conditional nested loops.
      const noOfPeople = ppedata.Persons.length;
      for (let i = 0; i < noOfPeople; i++) {
        if (ppedata.Persons[i].BodyParts[0].EquipmentDetections.length === 0) {
          const noOfBodyParts = ppedata.Persons[i].BodyParts.length;
          for (let j = 0; j < noOfBodyParts; j++) {
            const bodypart = ppedata.Persons[i].BodyParts[j].Name;
            const confidence = ppedata.Persons[i].BodyParts[j].Confidence;
            var equipment = "Not identified";
            const val = Math.floor(1000 + Math.random() * 9000);
            const id = val.toString() + "";
            const image = imageParams.Image.S3Object.Name;
            const ppeParams = {
              TableName: TABLE,
              Item: {
                id: { N: id + "" },
                bodyPart: { S: bodypart + "" },
                confidence: { S: confidence + "" },
                equipment: { S: equipment + "" },
                image: { S: image },
              },
            };
            const tableData = await dynamoDBClient.send(
                new PutItemCommand(ppeParams)
            );
          }
        } else {
          const noOfBodyParts = ppedata.Persons[i].BodyParts.length;
          for (let j = 0; j < noOfBodyParts; j++) {
            const bodypart = ppedata.Persons[i].BodyParts[j].Name;
            const confidence = ppedata.Persons[i].BodyParts[j].Confidence;
            var equipment =
                ppedata.Persons[i].BodyParts[j].EquipmentDetections[0].Type;
            const val = Math.floor(1000 + Math.random() * 9000);
            const id = val.toString() + "";
            const image = imageParams.Image.S3Object.Name;
            const ppeParams = {
              TableName: TABLE,
              Item: {
                id: { N: id + "" },
                bodyPart: { S: bodypart + "" },
                confidence: { S: confidence + "" },
                equipment: { S: equipment + "" },
                image: { S: image },
              },
            };
            const tableData = await dynamoDBClient.send(
                new PutItemCommand(ppeParams)
            );
          }
        }
      }
    }
    alert("Images analyzed and table updated.");
  } catch (err) {
    console.log("Error analyzing images. ", err);
  }
  try {
    sendEmail();
  } catch (err) {
    alert("Error sending email");
  }
};
// Expose the function to the browser.
window.processImages = processImages;
// snippet-end:[s3.JavaScript.detect-ppe.indexv3]
