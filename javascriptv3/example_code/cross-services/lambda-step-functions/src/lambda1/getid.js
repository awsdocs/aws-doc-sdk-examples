/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/serverless-workflows-using-step-functions.html.

Purpose:
getid.js is part of a tutorial demonstrates how to create an AWS serverless workflow by using the AWS SDK for JavaScript (v3)
and AWS Step Functions.

*/

// snippet-start:[lambda.JavaScript.lambda-step-functions.getid]

exports.handler = async (event) => {
  // Create a support case using the input as the case ID, then return a confirmation message
  try {
    const myCaseID = event.inputCaseID;
    var myMessage = "Case " + myCaseID + ": opened...";
    var result = { Case: myCaseID, Message: myMessage };
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[lambda.JavaScript.lambda-step-functions.getid]
