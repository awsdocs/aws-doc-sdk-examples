// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { getFirstEntry } from "@aws-doc-sdk-examples/lib/utils/util-csv.js";
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { adminGetUser } from "../../../actions/admin-get-user.js";
import { FILE_USER_POOLS } from "./constants.js";

const validateUsername = (username) => {
  if (!username) {
    throw new Error(
      `Username is missing. It must be provided as an argument to the 'admin-get-user' command.`,
    );
  }
};

const getUser = async (username) => {
  const [userPoolId] = getFirstEntry(FILE_USER_POOLS);

  if (!userPoolId) {
    throw new Error('User pool id is missing. Did you run "create-user-pool"?');
  }

  return await adminGetUser({ userPoolId, username });
};

/**
 * @param {import('@aws-sdk/client-cognito-identity-provider').AdminGetUserCommandOutput} user
 */
const formatUser = (user) =>
  `
  ${user.Username} {
    "email": "${user.UserAttributes[2].Value}",
    "status": "${user.UserStatus}"
  }
  `.trim();

const adminGetUserHandler = async ([_cmd, username]) => {
  try {
    validateUsername(username);

    const user = await getUser(username);

    logger.log(formatUser(user));
  } catch (err) {
    logger.error(err);
  }
};

export { adminGetUserHandler };
