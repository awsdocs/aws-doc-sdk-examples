/* Sample code to get user from Cognito SDK as an administrator.*/

// snippet-start:[cognito.JavaScript.admin-get-user-v2]
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
        Username: EMAIL
    };

    let response = await cognito.adminGetUser(cognitoParams).promise();
    console.log(JSON.stringify(response, null, 2));
}
// snippet-end:[cognito.JavaScript.admin-get-user-v2]