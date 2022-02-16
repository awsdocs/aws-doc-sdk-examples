/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3)

Purpose:
admin-create-user.js demonstrates how to create an user from Cognito SDK.

*/

// snippet-start:[ses.JavaScript.filters.admin-create-user]
// Load the AWS SDK for Node.js.
var AWS = require('aws-sdk');
// Set the AWS Region.
AWS.config.update({ region: 'REGION' });
// Initialize CogntioIdentityServiceProvider SDK.
var cognitoidentityserviceprovider = new AWS.CognitoIdentityServiceProvider();
var params = {
    UserPoolId: "UserPoolID",
    Username: "user@sample.com",
    DesiredDeliveryMediums: [
        "EMAIL"
    ],
    MessageAction: "RESEND",
    TemporaryPassword: "Password@123",
    UserAttributes: [
        {
            Name: "email",
            Value: "user@sample.com"
        }
    ]
}
var response = await cognitoidentityserviceprovider.adminCreateUser(params).promise()
console.log(JSON.stringify(response, null, 2))
// snippet-end:[ses.JavaScript.filters.admin-create-user]