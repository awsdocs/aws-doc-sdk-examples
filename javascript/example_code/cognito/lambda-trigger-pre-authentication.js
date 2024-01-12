// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.pre-authentication]
exports.handler = (event, context, callback) => {
  if (
    event.callerContext.clientId === "user-pool-app-client-id-to-be-blocked"
  ) {
    var error = new Error(
      "Cannot authenticate users from this user pool app client"
    );

    // Return error to Amazon Cognito
    callback(error, event);
  }

  // Return to Amazon Cognito
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-authentication]
