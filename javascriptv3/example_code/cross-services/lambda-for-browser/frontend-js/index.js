/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3), which is available
at https://github.com/aws/aws-sdk-js-v3.

// Purpose:
main.js is the JavaScript for an example demonstrating how to trigger an AWS Lambda function from the browser.

 */
// snippet-start:[cross-service.lambda-from-browser.javascriptv3.index]

// Import required AWS SDK clients and commands for Node.js.
import { InvokeCommand } from "@aws-sdk/client-lambda";
import { lambdaClient } from "../libs/lambdaClient";

const myFunction = async () => {
  const color = document.getElementById("c1").value
  const pattern = document.getElementById("p1").value
  const id = Math.floor(Math.random() * (10000 - 1 + 1)) + 1;
  const params = {
    FunctionName: 'forPathryusha_v3', /* required */
    Payload: JSON.stringify( { Item: {
        Id: id,
        Color: color,
        Pattern: pattern
      },
      TableName: "DesignRequests",
    })
  };
  try{
  const data = await lambdaClient.send(new InvokeCommand(params));
  alert("Success. Data added to table.");
    console.log('Success, payload', data);
} catch (err) {
    alert("Oops and error occurred.");
  console.log("Error", err);
  }
};

// Make the function available to the browser window.
window.myFunction = myFunction;

// snippet-end:[cross-service.lambda-from-browser.javascriptv3.index]
