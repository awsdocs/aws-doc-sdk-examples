// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.custom-message-admin-create-user]
exports.handler = (event, context, callback) => {
  //
  if (event.userPoolId === "theSpecialUserPool") {
    // Identify why was this function invoked
    if (event.triggerSource === "CustomMessage_AdminCreateUser") {
      // Ensure that your message contains event.request.codeParameter event.request.usernameParameter. This is the placeholder for the code and username that will be sent to your user.
      event.response.smsMessage =
        "Welcome to the service. Your user name is " +
        event.request.usernameParameter +
        " Your temporary password is " +
        event.request.codeParameter;
      event.response.emailSubject = "Welcome to the service";
      event.response.emailMessage =
        "Welcome to the service. Your user name is " +
        event.request.usernameParameter +
        " Your temporary password is " +
        event.request.codeParameter;
    }
    // Create custom message for other events
  }
  // Customize messages for other user pools

  // Return to Amazon Cognito
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.custom-message-admin-create-user]
