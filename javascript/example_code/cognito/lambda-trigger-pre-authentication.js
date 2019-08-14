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

// snippet-sourcedescription:[This sample function prevents users from a specific user pool app client to sign-in to the user pool.]
// snippet-service:[cognito-idp]
// snippet-keyword:[JavaScript]
// snippet-keyword:[Amazon Cognito]
// snippet-keyword:[Code Sample]
// snippet-keyword:[lambda_trigger]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-30]
// snippet-sourceauthor:[AWS]

// snippet-start:[cognito.javascript.lambda-trigger.pre-authentication]
exports.handler = (event, context, callback) => {
    if (event.callerContext.clientId === "user-pool-app-client-id-to-be-blocked") {
        var error = new Error("Cannot authenticate users from this user pool app client");

        // Return error to Amazon Cognito
        callback(error, event);
    }

    // Return to Amazon Cognito
    callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-authentication]