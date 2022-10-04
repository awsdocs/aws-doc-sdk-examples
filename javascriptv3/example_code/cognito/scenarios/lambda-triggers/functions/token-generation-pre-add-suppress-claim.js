/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-pre-token-generation.html.
*/

// snippet-start:[javascript.v3.cognito.scenarios.lambda-triggers.PreTokenGenerationAddSuppressClaim]
exports.handler = async (event) => {
  event.response = {
    claimsOverrideDetails: {
      claimsToAddOrOverride: {
        my_first_attribute: "first_value",
        my_second_attribute: "second_value",
      },
      claimsToSuppress: ["email"],
    },
  };

  return event;
};
// snippet-end:[javascript.v3.cognito.scenarios.lambda-triggers.PreTokenGenerationAddSuppressClaim]
