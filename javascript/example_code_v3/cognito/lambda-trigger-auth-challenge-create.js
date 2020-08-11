/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-create-auth-challenge.html.

Purpose:
In lambda-trigger-auth-challenge-create.js a CAPTCHA is created as a challenge to the user.
The URL for the CAPTCHA image is added to the public challenge parameters as "captchaUrl",
and the expected answer is added to the private challenge parameters.

Running the code:
node lambda-trigger-auth-challenge-create.js
*/

// snippet-start:[cognito.javascript.lambda-trigger.create-auth-challengeV3]
exports.handler = async (event, context) => {
    try{
        if (event.request.challengeName == 'CUSTOM_CHALLENGE')
        {
            event.response.publicChallengeParameters = {};
            event.response.publicChallengeParameters.captchaUrl = 'url/123.jpg'
            event.response.privateChallengeParameters = {};
            event.response.privateChallengeParameters.answer = '5';
            event.response.challengeMetadata = 'CAPTCHA_CHALLENGE';
        }
    }
    catch(err){
        return null
    }
};
// snippet-end:[cognito.javascript.lambda-trigger.create-auth-challengeV3]
