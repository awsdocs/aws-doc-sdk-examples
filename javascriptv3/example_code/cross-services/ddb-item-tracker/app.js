/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

Purpose:
app.js is the main file in the Amazon DynamoDB Item Tracker example app.
It interacts with the Express web framework to execute code.

INPUTS:
- REGION

*/
// snippet-start:[cross-service.JavaScript.ddb-item-tracker.app]
import express from "express";
import { v4 as uuidv4 } from "uuid";
const port = process.env.PORT || 3000;
const app = express();
import bodyParser from "body-parser";
import path from "path";
import { fileURLToPath } from "url";
const __filename = fileURLToPath(import.meta.url);
import { UpdateCommand, PutCommand, ScanCommand } from "@aws-sdk/lib-dynamodb";
import { ddbDocClient } from "./public/libs/ddbDocClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import { sesClient, REGION } from "./public/libs/sesClient.js";
import { s3Client } from "./public/libs/s3Client.js";
import { PutObjectCommand } from "@aws-sdk/client-s3";

// Setting path for public directory
const __dirname = path.dirname(__filename);
const static_path = path.join(__dirname, "/public");
app.use(express.static(static_path));
app.use(express.urlencoded({ extended: true }));

// Variables to update.
export const tableName = "TABLE_NAME";
export const bucketName = "BUCKET_NAME";
export const senderEmail = "SENDER_EMAIL_ADDRESS";

app.use(bodyParser.json());

app.post("/add", (req, res) => {
  const body = req.body;
  const id = uuidv4();
  const d = new Date();
  const month = d.getMonth() + 1;
  const day = d.getDate();
  const todaydate =
    d.getFullYear() +
    "/" +
    (month < 10 ? "0" : "") +
    month +
    "/" +
    (day < 10 ? "0" : "") +
    day;
  const params = {
    TableName: tableName,
    Item: {
      id: id,
      guide: body.guide,
      description: body.description,
      status: body.status,
      date: todaydate,
    },
  };
  const run = async () => {
    try {
      const data = await ddbDocClient.send(new PutCommand(params));
      console.log("Added item:", JSON.stringify(data, null, 2));
      console.log(data);
      res.contentType = "application/json";
      res.send(data);
    } catch (err) {
      console.error(
        "Unable to add item. Error JSON:",
        JSON.stringify(err, null, 2)
      );
    }
  };
  run();
});

app.post("/request", (req, res) => {
  var params = {
    TableName: tableName,
  };

  const run = async () => {
    try {
      const data = await ddbDocClient.send(new ScanCommand(params));
      console.log("data", data);
      res.send(data);
    } catch (err) {
      console.log("Error", err);
    }
  };
  run();
});

app.post("/changewi", (req, res) => {
  console.log(req.body);
  var params = {
    TableName: tableName,
    Key: {
      id: req.body.id,
    },
    UpdateExpression: "set #description=:d, #status=:s",
    ExpressionAttributeValues: {
      ":d": req.body.description,
      ":s": req.body.status,
    },
    ExpressionAttributeNames: {
      "#description": "description",
      "#status": "status",
    },
  };
  const run = async () => {
    try {
      const data = await ddbDocClient.send(new UpdateCommand(params));
      res.contentType = "application/json";
      res.send(data);
    } catch (err) {
      console.error(err);
    }
  };
  run();
});

app.post("/uploadCSV", (req, res) => {
  const d = new Date();
  const month = d.getMonth() + 1;
  const day = d.getDate();
  const hour = d.getHours();
  const minute = d.getMinutes();
  const todaydate =
    d.getFullYear() +
    (month < 10 ? "0" : "") +
    month +
    (day < 10 ? "0" : "") +
    day +
    hour +
    minute;
  const uploadParams = {
    Bucket: bucketName,
    Body: req.body.csv,
    Key: "Items_" + todaydate + ".csv",
  };
  const run = async () => {
    try {
      await s3Client.send(new PutObjectCommand(uploadParams));
      const linkToCSV =
        "https://s3.console.aws.amazon.com/s3/buckets/" +
        uploadParams.Bucket +
        "?region=" +
        REGION +
        "&tab=objects";
      console.log("Success. Report uploaded to " + linkToCSV + ".");
      console.log("Email", req.body.email);
      try {
        const emailParams = {
          Destination: {
            /* required */
            CcAddresses: [
              /* more items */
            ],
            ToAddresses: [
              req.body.email, //RECEIVER_ADDRESS
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
                  "<p> The CSV has been uploaded to <a href=" +
                  linkToCSV +
                  ">here.</a></p>",
              },
            },
            Subject: {
              Charset: "UTF-8",
              Data: "Report ready.",
            },
          },
          Source: "SENDER_EMAIL_ADDRESS",
          ReplyToAddresses: [
            /* more items */
          ],
        };
        const data = await sesClient.send(new SendEmailCommand(emailParams));
        console.log("Success. Email sent.", data);
        res.contentType = "application/json";
        res.send(data);
      } catch (err) {
        console.log("Error", err);
      }
    } catch (err) {
      console.log("Error", err);
    }
  };
  run();
  module.exports = run();
});

app.post("/report", (req, res) => {
  // Helper function to send an email to user.
  // Set the parameters
  console.log("This is the email address: ", req.body.email);
  const params = {
    Destination: {
      /* required */
      CcAddresses: [
        /* more items */
      ],
      ToAddresses: [
        req.body.email, //RECEIVER_ADDRESS
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
            tableName +
            " has been updated with PPE information <a href='https://" +
            REGION +
            ".console.aws.amazon.com/dynamodb/home?region=" +
            REGION +
            "#item-explorer?table=" +
            tableName +
            "'>here.</a></p>",
        },
      },
      Subject: {
        Charset: "UTF-8",
        Data: "PPE image report ready.",
      },
    },
    Source: senderEmail,
    ReplyToAddresses: [
      /* more items */
    ],
  };
  const run = async () => {
    try {
      const data = await sesClient.send(new SendEmailCommand(params));
      console.log("Success. Email sent.", data);
      res.contentType = "application/json";
      res.send(data);
    } catch (err) {
      console.log("Error", err);
    }
  };
  run();
});

app.listen(port, () => {
  console.log(`Listening on port ${port}`);
});
// snippet-end:[cross-service.JavaScript.ddb-item-tracker.app]
