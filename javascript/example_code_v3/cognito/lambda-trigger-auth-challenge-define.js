/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-define-auth-challenge.html.

Purpose:
lambda-trigger-auth-challenge-define.js defines a series of challenges for authentication and
issues tokens only if all of the challenges are successfully completed.

Running the code:
node lambda-trigger-auth-challenge-define.js
*/
// snippet-start:[cognito.javascript.lambda-trigger.define-auth-challengeV3]
exports.handler = async (event, context) => {
    try {
        if (event.request.session.length == 1 && event.request.session[0].challengeName == 'SRP_A') {
            event.response.issueTokens = false;
            event.response.failAuthentication = false;
            event.response.challengeName = 'PASSWORD_VERIFIER';
        } else if (event.request.session.length == 2 && event.request.session[1].challengeName == 'PASSWORD_VERIFIER' && event.request.session[1].challengeResult == true) {
            event.response.issueTokens = false;
            event.response.failAuthentication = false;
            event.response.challengeName = 'CUSTOM_CHALLENGE';
        } else if (event.request.session.length == 3 && event.request.session[2].challengeName == 'CUSTOM_CHALLENGE' && event.request.session[2].challengeResult == true) {
            event.response.issueTokens = true;
            event.response.failAuthentication = false;
        } else {
            event.response.issueTokens = false;
            event.response.failAuthentication = true;
        }
    }
    catch(err){
        // Return to Amazon Cognito
        return null;
    }

};
// snippet-end:[cognito.javascript.lambda-trigger.define-auth-challengeV3]
