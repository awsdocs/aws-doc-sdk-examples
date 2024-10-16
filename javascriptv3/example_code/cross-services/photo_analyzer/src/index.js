// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  DetectLabelsCommand,
  RekognitionClient,
} from "@aws-sdk/client-rekognition";
import {
  ListObjectsCommand,
  PutObjectCommand,
  S3Client,
} from "@aws-sdk/client-s3";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { SendEmailCommand, SESClient } from "@aws-sdk/client-ses";

import { outputNames } from "./constants.js";

const email = process.env.VERIFIED_EMAIL_ADDRESS;
const region = process.env.REGION;
/** @type {Record<string, string> } */
const outputs = JSON.parse(process.env.CFN_OUTPUTS);

const imagesBucketName = outputs[outputNames.IMAGES_BUCKET_OUTPUT];
const reportsBucketName = outputs[outputNames.REPORTS_BUCKET_OUTPUT];
const identityPoolId = outputs[outputNames.IDENTITY_POOL_OUTPUT];

const credentials = fromCognitoIdentityPool({
  client: new CognitoIdentityClient({ region: "us-east-1" }),
  identityPoolId,
});

const s3Client = new S3Client({ credentials, region });
const rekognitionClient = new RekognitionClient({ credentials, region });
const sesClient = new SESClient({ credentials, region });

/**
 * Update the info text box.
 * @param {string} info
 */
const updateInfo = (info = "") => {
  const infoEl = document.getElementById("info");
  infoEl.innerText = info;
};

// Load images from Amazon S3 bucket to the table.
const loadTable = async () => {
  try {
    updateInfo();
    const { Contents } = await s3Client.send(
      new ListObjectsCommand({ Bucket: imagesBucketName }),
    );
    /** @type { HTMLUListElement | null } */
    const imageList = document.getElementById("image-list");
    if (imageList) {
      imageList.innerHTML = "";
      for (const content of Contents) {
        const li = document.createElement("li");
        const textNode = document.createTextNode(
          `${content.Key} (${Math.ceil(content.Size / 1024)}KiB)`,
        );
        li.append(textNode);
        imageList.append(li);
      }
    } else {
      throw new Error("Could not find element.");
    }
  } catch (caught) {
    if (caught instanceof Error) {
      const errMessage = `Error listing S3 objects. ${caught.name}: ${caught.message}`;
      console.error(errMessage);
      updateInfo(errMessage);
    } else {
      throw caught;
    }
  }
};

window.addToBucket = async () => {
  try {
    /** @type { HTMLInputElement } */
    const fileInput = document.getElementById("input-image");
    const file = fileInput.files[0];

    await s3Client.send(
      new PutObjectCommand({
        Bucket: imagesBucketName,
        Body: file,
        Key: file.name,
      }),
    );
    loadTable();
    updateInfo(`${file.name} added to ${imagesBucketName}.`);
  } catch (caught) {
    if (caught instanceof Error) {
      const errMessage = `Error putting object in bucket. ${caught.name}: ${caught.message}`;
      console.error(errMessage);
      updateInfo(errMessage);
    } else {
      throw caught;
    }
  }
  return false;
};

window.processImages = async () => {
  try {
    updateInfo("");
    const listPhotosParams = {
      Bucket: imagesBucketName,
    };
    // Retrieve list of objects in the Amazon S3 bucket.
    const { Contents } = await s3Client.send(
      new ListObjectsCommand(listPhotosParams),
    );

    // Loop through images. For each image, retrieve the image name,
    // then analyze image by detecting it's labels, then parse results
    // into CSV format.
    for (const content of Contents) {
      const imageName = content.Key;
      const { Labels } = await rekognitionClient.send(
        new DetectLabelsCommand({
          Image: {
            S3Object: {
              Bucket: imagesBucketName,
              Name: imageName,
            },
          },
        }),
      );
      /** @type {string[][]} */
      const analysis = [];
      // Parse results into CVS format.
      for (const label of Labels) {
        analysis.push([label.Name, label.Confidence]);
      }
      // Create a CSV file report for each images.
      createCsv({
        headers: ["Object", "Confidence"],
        rows: analysis,
        name: imageName,
      });
    }
  } catch (caught) {
    if (caught instanceof Error) {
      const errMessage = `Error analyzing images. ${caught.name}: ${caught.message}`;
      console.error(errMessage);
      updateInfo(errMessage);
    } else {
      throw caught;
    }
  }
  return false;
};

/**
 * Process a list of rows into a CSV string and upload to S3 bucket.
 * @param {{ headers: string[], rows: string[][], name: string }}
 */
const createCsv = async ({ headers, rows, name }) => {
  const csv = `${headers.join(",")}\n${rows.map((row) => row.join(",")).join("\n")}`;
  await uploadFile(csv, name);
};

// Helper function to upload reports to Amazon S3 bucket for reports.
const uploadFile = async (csv, key) => {
  try {
    await s3Client.send(
      new PutObjectCommand({
        Bucket: reportsBucketName,
        Body: csv,
        Key: `${key}.csv`,
      }),
    );
    const region = await s3Client.config.region();
    const linkToCSV = `https://s3.console.aws.amazon.com/s3/object/${reportsBucketName}?region=${region}&prefix=${key}.csv`;

    // Send an email to notify the user when report is available.
    sendEmail(`${key}.csv`, linkToCSV);
  } catch (caught) {
    if (caught instanceof Error) {
      const errMessage = `Error uploading CSV. ${caught.name}: ${caught.message}`;
      updateInfo(errMessage);
      console.error(errMessage);
    } else {
      throw caught;
    }
  }
};
// Helper function to send an email to the user.
const sendEmail = async (key, linkToCSV) => {
  const toEmail = document.getElementById("email").value;
  const fromEmail = email;
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
            Data: `<h1>Hello!</h1><p>Please see the the analyzed video report for ${key} <a href=${linkToCSV}> here</a></p>`,
          },
          Text: {
            Charset: "UTF-8",
            Data: `Hello,\\r\\nPlease see the attached file for the analyzed video report at${linkToCSV}\n\n`,
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: `${key} analyzed video report ready`,
        },
      },
      Source: fromEmail, // SENDER_ADDRESS
      ReplyToAddresses: [
        /* more items */
      ],
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    console.log("Email sent.", data);
  } catch (caught) {
    if (caught instanceof Error) {
      const errMessage = `Error sending email. ${caught.name}: ${caught.message}`;
      console.error(errMessage);
      updateInfo(errMessage);
    } else {
      throw caught;
    }
  }
};

window.addEventListener("load", () => {
  loadTable();
});
