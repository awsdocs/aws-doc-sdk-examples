// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cognito.javascript.lambda-trigger.pre-token-generation-group-membership]
exports.handler = (event, context, callback) => {
  event.response = {
    claimsOverrideDetails: {
      claimsToAddOrOverride: {
        attribute_key2: "attribute_value2",
        attribute_key: "attribute_value",
      },
      claimsToSuppress: ["email"],
      groupOverrideDetails: {
        groupsToOverride: ["group-A", "group-B", "group-C"],
        iamRolesToOverride: [
          "arn:aws:iam::XXXXXXXXXXXX:role/sns_callerA",
          "arn:aws:iam::XXXXXXXXX:role/sns_callerB",
          "arn:aws:iam::XXXXXXXXXX:role/sns_callerC",
        ],
        preferredRole: "arn:aws:iam::XXXXXXXXXXX:role/sns_caller",
      },
    },
  };

  // Return to Amazon Cognito
  callback(null, event);
};
// snippet-end:[cognito.javascript.lambda-trigger.pre-token-generation-group-membership]
