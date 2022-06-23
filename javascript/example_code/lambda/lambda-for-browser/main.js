/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (lambda-for-browser).

// Purpose:
main.js is the JavaScript for an example demonstrating how to trigger an AWS Lambda function from the browser.

Input:
- REGION
- IDENTITY_POOL_ID
 */
// snippet-start:[cross-service.lambda-from-browser.javascript.index]

// Initialize the Amazon Cognito credentials provider.
AWS.config.region = "REGION";
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
  IdentityPoolId: "IDENTITY_POOL_ID",
});

// Create client.
const lambda = new AWS.Lambda();

const myFunction = async () => {
  const color = document.getElementById("c1").value
  const pattern = document.getElementById("p1").value
  const id = Math.floor(Math.random() * (10000 - 1 + 1)) + 1;
  const params = {
    FunctionName: 'forPathryusah', /* required */
    Payload: JSON.stringify( { Item: {
        Id: id,
        Color: color,
        Pattern: pattern
      },
      TableName: "DesignRequests",
    })
  };
  lambda.invoke(params,  function (err, data){
    if (err) console.log(err, err.stack); // an error occurred
    else console.log('Success, payload', data);           // successful response
  })
};
// snippet-end:[cross-service.lambda-from-browser.javascript.index]
