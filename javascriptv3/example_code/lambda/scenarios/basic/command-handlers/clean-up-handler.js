/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { readdirSync } from "fs";
import { log } from "@aws-sdk-examples/libs/utils/util-log.js";
import {
  deleteFiles,
  dirnameFromMetaUrl,
} from "@aws-sdk-examples/libs/utils/util-fs.js";

import { detachRolePolicy } from "../../../../iam/actions/detach-role-policy.js";
import { deleteRole } from "../../../../iam/actions/delete-role.js";

import { deleteFunction } from "../../../actions/delete-function.js";

import {
  ARN_POLICY_LAMBDA_BASIC_EXECUTION,
  NAME_ROLE_LAMBDA,
} from "./constants.js";

const dirname = dirnameFromMetaUrl(import.meta.url);

/**
 * Returns a function that retrieves files with a certain extension
 * within a directory.
 * @param {string} ext
 */
const getFilesByExt = (ext) => {
  return (dir) => {
    const files = readdirSync(dir);
    return files.filter((file) => file.endsWith(ext));
  };
};
const getZipFiles = getFilesByExt(".zip");
const getTmpFiles = getFilesByExt(".tmp");

/**
 * Delete all functions in the list. Log any failures.
 * @param {string[]} functionNames
 */
const cleanUpFunctions = (functionNames) =>
  Promise.all(functionNames.map((n) => deleteFunction(n).catch(log)));

/**
 * @param {string[]} tmpFileNames
 */
const cleanUpTmpFiles = (tmpFileNames) =>
  deleteFiles(tmpFileNames.map((tmpFileName) => `./${tmpFileName}`));

const cleanUpRolePolicy = (policyArn, roleName) =>
  detachRolePolicy(policyArn, roleName).catch(log);

const cleanUpRole = (roleName) => deleteRole(roleName).catch(log);

const cleanUpHandler = async () => {
  const zippedFuncs = getZipFiles(`${dirname}../../../functions/`);
  const tmpFiles = getTmpFiles("./");
  const funcNames = zippedFuncs.map((zip) => zip.split(".zip")[0]);

  try {
    log("Tidying up 🧹");

    log("Deleting Lambda functions.");
    await cleanUpFunctions(funcNames);

    log("Removing policy from role created during initialization.");
    await cleanUpRolePolicy(
      ARN_POLICY_LAMBDA_BASIC_EXECUTION,
      NAME_ROLE_LAMBDA,
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
