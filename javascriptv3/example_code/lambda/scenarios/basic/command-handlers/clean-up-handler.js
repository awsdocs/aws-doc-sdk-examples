/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { readdirSync } from "fs";
import { compose, filter, includes, map, nth, split, otherwise } from "ramda";
import { detachRolePolicy } from "../../../../iam/actions/detach-role-policy.js";
import { deleteRole } from "../../../../iam/actions/delete-role.js";
import { concatMap, promiseAll } from "../../../../libs/ext-ramda.js";
import { log } from "../../../../libs/utils/util-log.js";
import {
  deleteFiles,
  dirnameFromMetaUrl,
} from "../../../../libs/utils/util-fs.js";
import { deleteFunction } from "../../../actions/delete-function.js";
import {
  ARN_POLICY_LAMBDA_BASIC_EXECUTION,
  NAME_ROLE_LAMBDA,
} from "./constants.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

const getFilesByExt = (ext) => compose(filter(includes(ext)), readdirSync);
const getZipFiles = getFilesByExt(".zip");
const getTmpFiles = getFilesByExt(".tmp");

const cleanUpFunctions = compose(
  promiseAll,
  map(compose(otherwise(log), deleteFunction))
);

const cleanUpZipFiles = compose(
  deleteFiles,
  concatMap(`${dirname}../../../functions/`)
);
const cleanUpTmpFiles = compose(deleteFiles, concatMap("./"));

const cleanUpRolePolicy = compose(otherwise(log), detachRolePolicy);

const cleanUpRole = compose(otherwise(log), deleteRole);

const cleanUpHandler = async () => {
  const zippedFuncs = getZipFiles(`${dirname}../../../functions/`);
  const tmpFiles = getTmpFiles("./");
  const funcNames = map(compose(nth(0), split(".zip")), zippedFuncs);

  try {
    log("Tidying up 🧹");

    log("Deleting lambda functions.");
    await cleanUpFunctions(funcNames);

    log("Deleting local zipped versions of functions.");
    cleanUpZipFiles(zippedFuncs);

    log("Removing policy from role created during initialization.");
    await cleanUpRolePolicy(
      NAME_ROLE_LAMBDA,
      ARN_POLICY_LAMBDA_BASIC_EXECUTION
    );

    log("Deleting role created during initialization.");
    await cleanUpRole(NAME_ROLE_LAMBDA);

    log("Removing temporary files.");
    cleanUpTmpFiles(tmpFiles);

    log("All done ✨.");
  } catch (err) {
    log(err);
  }
};

export { cleanUpHandler };
