/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { path } from "ramda";
import { log } from "../../../../libs/utils/util-log.js";
import { getTmp, setTmp } from "../../../../libs/utils/util-fs.js";
import { attachRolePolicy } from "../../../../iam/actions/attach-role-policy.js";
import { createRole } from "../../../../iam/actions/create-role.js";
import { parseString } from "../../../../libs/ext-ramda.js";
import {
  ARN_POLICY_LAMBDA_BASIC_EXECUTION,
  NAME_ROLE_LAMBDA,
} from "./constants.js";

const initializeHandler = async () => {
  const tmp = getTmp("roleArn");
  if (tmp) {
    log("You've already initialized.");
    return;
  }

  try {
    /** snippet-start:[javascript.v3.lambda.scenarios.basic.CreateRole] */
    log(`Creating role (${NAME_ROLE_LAMBDA})...`);
    const response = await createRole({
      AssumeRolePolicyDocument: parseString({
        Version: "2012-10-17",
        Statement: [
          {
            Effect: "Allow",
            Principal: {
              Service: "lambda.amazonaws.com",
            },
            Action: "sts:AssumeRole",
          },
        ],
      }),
      RoleName: NAME_ROLE_LAMBDA,
    });
    /** snippet-end:[javascript.v3.lambda.scenarios.basic.CreateRole] */

    log(
      `${NAME_ROLE_LAMBDA} created successfully. Saving Role ARN for later use.`
    );
    setTmp("roleArn", path(["Role", "Arn"])(response));
    log(
      `Attaching policy ${ARN_POLICY_LAMBDA_BASIC_EXECUTION} to ${NAME_ROLE_LAMBDA}...`
    );

    await attachRolePolicy(NAME_ROLE_LAMBDA, ARN_POLICY_LAMBDA_BASIC_EXECUTION);

    log(
      `${ARN_POLICY_LAMBDA_BASIC_EXECUTION} successfully attached to ${NAME_ROLE_LAMBDA}`
    );
  } catch (err) {
    log(err);
  }
};

export { initializeHandler };
