/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { deleteFiles } from "@aws-sdk-examples/libs/utils/util-fs.js";
import { log } from "@aws-sdk-examples/libs/utils/util-log.js";
import { deleteUserPool } from "../../../actions/delete-user-pool.js";
import { FILE_USER_POOLS } from "./constants.js";
import { getFirstValuesFromEntries } from "@aws-sdk-examples/libs/utils/util-csv.js";

/**
 * @param {string[]} userPoolIds
 */
function cleanUpUserPools(userPoolIds) {
  const deletePromises = userPoolIds.map((id) =>
    deleteUserPool(id).catch((err) => log(err)),
  );
  return Promise.all(deletePromises);
}

/**
 * @param {string[]} userPoolIds
 */
function createUserPoolList(userPoolIds) {
  return userPoolIds.map((id) => `• ${id}`).join("\n");
}

const cleanUpHandler = async () => {
  try {
    log("Tidying up 🧹");

    /**
     * @type {string[]}
     */
    const userPoolIds = getFirstValuesFromEntries(FILE_USER_POOLS);
    if (userPoolIds[0].length > 0) {
      log(`Deleting user pools: \n${createUserPoolList(userPoolIds)}`);
      await cleanUpUserPools(userPoolIds);
      log(`User pools deleted.`);
    }

    log("Removing temporary files.");
    await deleteFiles([`./${FILE_USER_POOLS}.tmp`]);

    log("All done ✨.");
  } catch (err) {
    log(err);
  }
};

export { cleanUpHandler };
