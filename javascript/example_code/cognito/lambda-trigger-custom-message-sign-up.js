// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.custom-message-sign-up]
exports.handler = (event, context, callback) => {
  //
  if (event.userPoolId === "theSpecialUserPool") {
    // Identify why was this function invoked
    if (event.triggerSource === "CustomMessage_SignUp") {
      // Ensure that your message contains event.request.codeParameter. This is the placeholder for code that will be sent
      event.response.smsMessage =
        "Welcome to the service. Your confirmation code is " +
        event.request.codeParameter;
      event.response.emailSubject = "Welcome to the service";
      event.response.emailMessage =
        "Thank you for signing up. " +
        event.request.codeParameter +
        " is your verification code";
    }
    // Create custom message for other events
  }
  // Customize messages for other user pools

  // Return to Amazon Cognito
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.custom-message-sign-up]
