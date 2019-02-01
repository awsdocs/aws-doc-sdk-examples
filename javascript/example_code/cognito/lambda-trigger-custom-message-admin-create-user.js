// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// 
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of
// the License is located at
// 
// http://aws.amazon.com/apache2.0/
// 
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

// snippet-sourcedescription:[A custom message Lambda function with the CustomMessage_AdminCreateUser trigger returns a user name and verification code and so include both request.usernameParameter and request.codeParameter in the message body.]
// snippet-service:[cognito-idp]
// snippet-keyword:[JavaScript]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[Code Sample]
// snippet-keyword:[lambda_trigger]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
// snippet-sourceauthor:[AWS]

// snippet-start:[cognito.javascript.lambda-trigger.custom-message-admin-create-user]
exports.handler = (event, context, callback) => {
    //
    if(event.userPoolId === "theSpecialUserPool") {
        // Identify why was this function invoked
        if(event.triggerSource === "CustomMessage_AdminCreateUser") {
            // Ensure that your message contains event.request.codeParameter event.request.usernameParameter. This is the placeholder for the code and username that will be sent to your user.
            event.response.smsMessage = "Welcome to the service. Your user name is " + event.request.usernameParameter + " Your temporary password is " + event.request.codeParameter;
            event.response.emailSubject = "Welcome to the service";
            event.response.emailMessage = "Welcome to the service. Your user name is " + event.request.usernameParameter + " Your temporary password is " + event.request.codeParameter;
        }
        // Create custom message for other events
    }
    // Customize messages for other user pools

    // Return to Amazon Cognito
    callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.custom-message-admin-create-user]