/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-token-generation.html.

Purpose:
lambda-trigger-pre-token-generation-group-membership.js uses the Pre Token Generation Lambda to
modify the user's group membership.

Running the code:
node lambda-trigger-pre-token-generation-group-membership.js
*/

// snippet-start:[cognito.javascript.lambda-trigger.pre-token-generation-group-membershipV3]
exports.handler = async (event, context) => {
    try {
        event.response = {
            "claimsOverrideDetails": {
                "claimsToAddOrOverride": {
                    "attribute_key2": "attribute_value2",
                    "attribute_key": "attribute_value"
                },
                "claimsToSuppress": ["email"],
                "groupOverrideDetails": {
                    "groupsToOverride": ["group-A", "group-B", "group-C"],
                    "iamRolesToOverride": ["arn:aws:iam::XXXXXXXXXXXX:role/sns_callerA", "arn:aws:iam::XXXXXXXXX:role/sns_callerB", "arn:aws:iam::XXXXXXXXXX:role/sns_callerC"],
                    "preferredRole": "arn:aws:iam::XXXXXXXXXXX:role/sns_caller"
                }
            }
        }
    }
    catch(err){
        // Return to Amazon Cognito
        return null;
    }
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-token-generation-group-membershipV3]
