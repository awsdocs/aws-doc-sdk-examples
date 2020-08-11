/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-verify-auth-challenge-response.html.

Purpose:
lambda-trigger-auth-challenge-verify.js is a Lambda function checks whether the user's response
to a challenge matches the expected response. The answerCorrect parameter is set to true if the
user's response matches the expected response.

Running the code:
node lambda-trigger-auth-challenge-verify.js
*/


// snippet-start:[cognito.javascript.lambda-trigger.auth-challenge-verifyV3]
exports.handler = async (event, context) => {
    try{
    if (event.request.privateChallengeParameters.answer == event.request.challengeAnswer) {
        event.response.answerCorrect = true;
    } else {
        event.response.answerCorrect = false;
    }
    }
    catch(err){
        // Return to Amazon Cognito
        return null;
    }
}
// snippet-end:[cognito.javascript.lambda-trigger.auth-challenge-verifyV3]
