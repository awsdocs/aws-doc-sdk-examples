/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 2 (v2).

Purpose:
admin-create-user.js demonstrates how an administrator can use Amazon Cognito to create a user.

Inputs:
 - USERPOOLID
 - EMAIL

*/

// snippet-start:[cognito.JavaScript.admin-create-user-v2]
const aws = require('aws-sdk');

// Initialize CognitoIdentityServiceProvider.
const cognito = new AWS.CognitoIdentityServiceProvider({
    apiVersion: "2016-04-18",
});

const USERPOOLID = "your Cognito User Pool ID";

exports.handler = async (event, context) => {
    const EMAIL = event.email;
    const cognitoParams = {
        UserPoolId: USERPOOLID,
        Username: EMAIL,
        UserAttributes: [{
            Name: "email",
            Value: EMAIL,
        },
        {
            Name: "email_verified",
            Value: "true",
        },
        ],
        TemporaryPassword: Math.random().toString(36).substr(2, 10),
    };

    let response = await cognito.adminCreateUser(cognitoParams).promise();
    console.log(JSON.stringify(response, null, 2));
}
// snippet-end:[cognito.JavaScript.admin-create-user-v2]