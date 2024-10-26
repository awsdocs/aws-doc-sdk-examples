// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { deleteFiles } from "@aws-doc-sdk-examples/lib/utils/util-fs.js";
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { deleteUserPool } from "../../../actions/delete-user-pool.js";
import { FILE_USER_POOLS } from "./constants.js";
import { getFirstValuesFromEntries } from "@aws-doc-sdk-examples/lib/utils/util-csv.js";

/**
 * @param {string[]} userPoolIds
 */
function cleanUpUserPools(userPoolIds) {
  const deletePromises = userPoolIds.map((id) =>
    deleteUserPool(id).catch((err) => logger.error(err)),
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
    logger.log("Tidying up");

    /**
     * @type {string[]}
     */
    const userPoolIds = getFirstValuesFromEntries(FILE_USER_POOLS);
    if (userPoolIds[0].length > 0) {
      logger.log(`Deleting user pools: \n${createUserPoolList(userPoolIds)}`);
      await cleanUpUserPools(userPoolIds);
      logger.log("User pools deleted.");
    }

    logger.log("Removing temporary files.");
    await deleteFiles([`./${FILE_USER_POOLS}.tmp`]);

    logger.log("All done ✨.");
  } catch (err) {
    logger.error(err);
  }
};

export { cleanUpHandler };
