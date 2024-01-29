// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.pre-token-generation-add-suppress-claims]
exports.handler = (event, context, callback) => {
  event.response = {
    claimsOverrideDetails: {
      claimsToAddOrOverride: {
        attribute_key2: "attribute_value2",
        attribute_key: "attribute_value",
      },
      claimsToSuppress: ["email"],
    },
  };

  // Return to Amazon Cognito
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-token-generation-add-suppress-claims]
