/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { pipe, otherwise, map, join } from "ramda";
import { promiseAll } from "../../../../libs/ext-ramda.js";
import { deleteFiles } from "../../../../libs/utils/util-fs.js";
import { log } from "../../../../libs/utils/util-log.js";
import { deleteUserPool } from "../../../actions/delete-user-pool.js";
import { FILE_USER_POOLS } from "./constants.js";
import { getFirstValuesFromEntries } from "../../../../libs/utils/util-csv.js";

const cleanUpUserPools = pipe(
  map(pipe(deleteUserPool, otherwise(log))),
  promiseAll
);

const createUserPoolList = pipe(
  map((x) => `• ${x}`),
  join("\n")
);

const cleanUpHandler = async () => {
  try {
    log("Tidying up 🧹");

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
