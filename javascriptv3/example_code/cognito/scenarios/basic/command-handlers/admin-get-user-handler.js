/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { getFirstEntry } from "../../../../libs/utils/util-csv.js";
import { log } from "../../../../libs/utils/util-log.js";
import { adminGetUser } from "../../../actions/admin-get-user.js";
import { FILE_USER_POOLS } from "./constants.js";

const validateUsername = (username) => {
  if (!username) {
    throw new Error(
      `Username is missing. It must be provided as an argument to the 'admin-get-user' command.`
    );
  }
};

const getUser = async (username) => {
  const [userPoolId] = getFirstEntry(FILE_USER_POOLS);

  if (!userPoolId) {
    throw new Error('User Pool id is missing. Did you run "create-user-pool"?');
  }

  return await adminGetUser({ userPoolId, username });
};

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

    log(formatUser(user));
  } catch (err) {
    log(err);
  }
};

export { adminGetUserHandler };
