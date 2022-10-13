/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { getFirstEntry } from "../../../../libs/utils/util-csv.js";
import { log } from "../../../../libs/utils/util-log.js";
import { listUsers } from "../../../actions/list-users.js";
import { FILE_USER_POOLS } from "./constants.js";

const getUsers = () => {
  const [userPoolId] = getFirstEntry(FILE_USER_POOLS);

  if (!userPoolId) {
    throw new Error('User Pool id is missing. Did you run "create-user-pool"?');
  }

  return listUsers({ userPoolId });
};

const formatUsers = (users) => {
  return users.map((user) => `• ${user.Username}`).join("\n");
};

const listUsersHandler = async () => {
  try {
    const response = await getUsers();

    log(formatUsers(response.Users));
  } catch (err) {
    log(err);
  }
};

export { listUsersHandler };
