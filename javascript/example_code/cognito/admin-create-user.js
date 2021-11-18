/* Sample code to create user from Cognito SDK */

const aws = require('aws-sdk');
/*Initializing CognitoIdentityServiceProvider from AWS SDK JS*/
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