// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.post-authentication]
exports.handler = (event, context, callback) => {
  // Send post authentication data to Cloudwatch logs
  console.log("Authentication successful");
  console.log("Trigger function =", event.triggerSource);
  console.log("User pool = ", event.userPoolId);
  console.log("App client ID = ", event.callerContext.clientId);
  console.log("User ID = ", event.userName);

  // Return to Amazon Cognito
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.post-authentication]
