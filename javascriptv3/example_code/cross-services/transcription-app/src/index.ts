/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

Purpose:
index.ts is part of a tutorial demonstrating how to build and deploy an app that transcribes and displays
voice recordings for authenticated users. To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

Inputs (replace in code):
- COGNITO_ID
- ID_TOKEN
- BUCKET
- REGION
- IDENTITY_POOL_ID

Running the code:
For more information, see https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/transcribe-app.html.

[Outputs | Returns]:

*/
// snippet-start:[transcribe.JavaScript.recording-app.config]
// Import the required AWS SDK clients and commands for Node.js
const AWS = require("aws-sdk");
require("./helper.ts");
require("./recorder.ts");
const { IAMClient, GetUserCommand } = require("@aws-sdk/client-iam");
const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");
const { S3RequestPresigner } = require("@aws-sdk/s3-request-presigner");
const { createRequest } = require("@aws-sdk/util-create-request");
const { formatUrl } = require("@aws-sdk/util-format-url");
const {
  TranscribeClient,
  StartTranscriptionJobCommand,
  GetTranscriptionJobCommand,
} = require("@aws-sdk/client-transcribe");
const {
  S3,
  S3Client,
  PutObjectCommand,
  GetObjectCommand,
  CreateBucketCommand,
  HeadBucketCommand,
  ListObjectsCommand,
  DeleteObjectCommand,
} = require("@aws-sdk/client-s3");
const { path } = require("path");
const fetch = require("node-fetch");
let COGNITO_ID = "cognito-idp.eu-west-1.amazonaws.com/USER_POOL_ID";
const idToken = getToken();
const loginData = {
  [COGNITO_ID]: idToken,
};

// Create the parameters.
const params = {
  Bucket: "BUCKET_NAME", // Amazon Simple Storage Service (Amazon S3) bucket to store transcriptions
  Region: "REGION", // AWS Region
  identityPoolID: "IDENTITY_POOL_ID", // IDENTITY_POOL_ID
};

// Create an Amazon Transcribe service client object.
const client = new TranscribeClient({
  region: params.Region,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: params.Region }),
    identityPoolId: params.identityPoolID,
    logins: loginData,
  }),
});

// Create and Amazon S3 client object.
const s3Client = new S3Client({
  region: params.Region,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region: params.Region }),
    identityPoolId: params.identityPoolID,
    logins: loginData,
  }),
});

// snippet-end:[transcribe.JavaScript.recording-app.config]
// snippet-start:[transcribe.JavaScript.recording-app.onload]
window.onload = getUserName = async () => {
  // Create a CognitoIdentityServiceProvider client object. This is V2 of the SDK for JS.
  const cognitoidentityserviceprovider = new AWS.CognitoIdentityServiceProvider(
    { region: params.Region }
  );
  // Set the parameters
  const userParams = {
    // Get the access token - see 'helper.ts'
    AccessToken: getAccessToken(),
  };
  // Get the current username
  cognitoidentityserviceprovider.getUser(userParams, function (err, data) {
    if (err) console.log(err, err.stack);
    // an error occurred
    else console.log(data.Username);
    var username = data.Username;
    // Pass the user name to the 'updateInterface' function
    updateInterface(username);
  });
};

// Updates the user interface with new transcriptions.
window.updateInterface = async function (username) {
  try {
    // If this is user's first sign-in, create folder with user's name in bucket.
    const Key = `${username}/`;
    try {
      const data = await s3Client.send(
        new PutObjectCommand({ Key: Key, Bucket: params.Bucket })
      );
      console.log("Folder created");
    } catch (err) {
      console.log("Error", err);
    }
    try {
      // Get a list of the objects in the Amazon S3 bucket.
      const data = await s3Client.send(
        new ListObjectsCommand({ Bucket: params.Bucket, Prefix: username })
      );
      // Create variable for the list of objects in the Amazon S3 bucket.
      const output = data.Contents;
      // Loop through the objects, populating a row on the user interface for each
      for (var i = 0; i < output.length; i++) {
        var obj = output[i];
        const objectParams = {
          Bucket: params.Bucket,
          Key: obj.Key,
        };
        // Return the name of an object from the table.
        const data = await s3Client.send(new GetObjectCommand(objectParams));
        console.log("Success", data.Body);
        // Extract the body contents from the returned data. This is a readable stream.
        const result = data.Body;
        // Create variable for the string version of the readable stream.
        let stringResult = "";
        for await (let chunk of yieldUint8Chunks(result)) {
          stringResult += String.fromCharCode.apply(null, chunk);
        }
        // Wrap in setTimeout function to provide time for string to be parsed.
        setTimeout(function () {
          // Your code to be executed after 1 second
          const outputJSON = JSON.parse(stringResult).results.transcripts[0]
            .transcript;
          const outputJSONTime = JSON.parse(stringResult)
            .jobName.split("/")[0]
            .replace("-job", "");
          i++;
          displayTranscriptionDetails(
            i,
            outputJSONTime,
            objectParams.Key,
            outputJSON
          );
        }, 1000);
      }
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("Error creating presigned URL", err);
  }
};

// Convert readable streams.
async function* yieldUint8Chunks(data) {
  const reader = data.getReader();
  try {
    while (true) {
      const { done, value } = await reader.read();
      if (done) return;
      yield value;
    }
  } finally {
    reader.releaseLock();
  }
}
// snippet-end:[transcribe.JavaScript.recording-app.onload]
// snippet-start:[transcribe.JavaScript.recording-app.create-transcriptions]

// Upload recordings to Amazon S3 bucket
window.upload = async function (blob, userName) {
  // Set parameters recording
  const Key = `${userName}/test-object-${Math.ceil(Math.random() * 10 ** 10)}`;
  try {
    // Create a presigned URL to  upload the transcription to the Amazon S3 bucket when it is ready.
    const signer = new S3RequestPresigner({ ...s3Client.config });
    const request = await createRequest(
      s3Client,
      new PutObjectCommand({ Key, Bucket: params.Bucket })
    );
    // Define the duration until expiration of the presigned URL.
    const expiration = new Date(Date.now() + 60 * 60 * 1000);
    // Create and format the presigned URL
    signedUrl = formatUrl(await signer.presign(request, expiration));
    console.log(`\nPutting "${Key}"`);
  } catch (err) {
    console.log("Error creating presigned URL", err);
  }
  try {
    // Upload the object to the Amazon S3 bucket using a presigned URL.
    response = await fetch(signedUrl, {
      method: "PUT",
      headers: {
        "content-type": "application/octet-stream",
      },
      body: blob,
    });
    // Create the transcription job name. In this case, it's the current date and time.
    const today = new Date();
    const date =
      today.getFullYear() +
      "-" +
      (today.getMonth() + 1) +
      "-" +
      today.getDate();
    const time =
      today.getHours() + "-" + today.getMinutes() + "-" + today.getSeconds();
    const jobName = date + "-time-" + time;

    // Create the transcription job
    createTranscriptionJob(
      "s3://" + params.Bucket + "/" + Key,
      jobName,
      params.Bucket,
      Key
    );
  } catch (err) {
    console.log("Error uploading object", err);
  }
};

// Create the transcription job.
const createTranscriptionJob = async (recording, jobName, bucket, key) => {
  // Set the parameters for transcriptions job
  const params = {
    TranscriptionJobName: jobName + "-job",
    LanguageCode: "en-US", // For example, 'en-US',
    OutputBucketName: bucket,
    OutputKey: key,
    Media: {
      MediaFileUri: recording, // For example, "https://transcribe-demo.s3-REGION.amazonaws.com/hello_world.wav"
    },
  };
  try {
    // Start the transcription job
    const data = await client.send(new StartTranscriptionJobCommand(params));
    console.log("Success - transcription submitted", data);
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[transcribe.JavaScript.recording-app.create-transcriptions]
// snippet-start:[transcribe.JavaScript.recording-app.delete-transcriptions]
// Delete a transcription.
window.deleteJSON = async (jsonFileName) => {
  try {
    const data = await s3Client.send(
      new DeleteObjectCommand({
        Bucket: params.Bucket,
        Key: jsonFileName,
      })
    );
    console.log("Success - JSON deleted");
  } catch (err) {
    console.log("Error", err);
  }
};
// Delete a row from the user interface.
window.deleteRow = function (rowid) {
  const row = document.getElementById(rowid);
  row.parentNode.removeChild(row);
};
// snippet-end:[transcribe.JavaScript.recording-app.delete-transcriptions]
