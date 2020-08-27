/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
add_data.js is part of a tutorial demonstrating how to build and deploy and app to submit
data to a DynamoDB table. add_data.js contains the functions required by the app. submitData submits the
the data inputted in the browser to the DynamoDB table, and sendMessage sends the administrator of the table
an SMS text message.

Inputs (replace in code):
- REGION
- IDENTITY_POOL_ID
- TABLE_NAME

Running the code:
node add_data.js
 */
// snippet-start:[s3.JavaScript.crossservice.addDataV3.complete]
// snippet-start:[s3.JavaScript.crossservice.addDataV3.config]
// Import required AWS SDK clients and commands for Node.js

const { CognitoIdentityClient } = require("@aws-sdk/client-cognito-identity");
const {
  fromCognitoIdentityPool,
} = require("@aws-sdk/credential-provider-cognito-identity");
const { DynamoDB, PutItemCommand } = require("@aws-sdk/client-dynamodb");
const { SNSClient, PublishCommand } = require("@aws-sdk/client-sns");
const REGION = "eu-west-1"; //REGION

// Initialize the Amazon Cognito credentials provider
const region = REGION; //REGION
const dbclient = new DynamoDB({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region }),
    identityPoolId: "eu-west-1:1bb96348-1c94-41b5-8c00-2f333a271a34", // IDENTITY_POOL_ID
  }),
});

const sns = new SNSClient({
  region: REGION,
  credentials: fromCognitoIdentityPool({
    client: new CognitoIdentityClient({ region }),
    identityPoolId: "eu-west-1:1bb96348-1c94-41b5-8c00-2f333a271a34", // IDENTITY_POOL_ID
  }),
});
// snippet-end:[s3.JavaScript.crossservice.addDataV3.config]
// snippet-start:[s3.JavaScript.crossservice.addDataV3.function]
const submitData = async () => {
  const id = document.getElementById("id").value;
  const title = document.getElementById("title").value;
  const name = document.getElementById("name").value;
  const body = document.getElementById("body").value;
  const tableName = "BUCKET_NAME";
  const params = {
    TableName: tableName,
    Item: {
      Id: { N: id + "" },
      Title: { S: title + "" },
      Name: { N: name + "" },
      Body: { S: body + "" },
    },
  };
  if (id != "" && title != "" && name != "" && body != "") {
    try {
      const data = await dbclient.send(new PutItemCommand(params));
      alert("Data added to table.");
      try {
        const messageParams = {
          Message: "A new item with ID value was added to the DynamoDB",
          PhoneNumber: "PHONE_NUMBER", //PHONE_NUMBER, in the E.164 phone number structure
        };
        const data = await sns.send(new PublishCommand(messageParams));
        console.log(
          "Success, message published. MessageID is " + data.MessageId
        );
      } catch (err) {
        console.error(err, err.stack);
      }
    } catch (err) {
      console.error(
        "An error occurred. Check the console for further information",
        err
      );
    }
  } else {
    alert("Enter data in each field.");
  }
};

window.submitData = submitData;
// snippet-end:[s3.JavaScript.crossservice.addDataV3.function]
// snippet-end:[s3.JavaScript.crossservice.addDataV3.complete]
