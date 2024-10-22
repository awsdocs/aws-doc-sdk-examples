// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { listFunctions } from "../../../actions/list-functions.js";

const makeListItem = (s) => `• ${s}`;

/**
 * @param {import('@aws-sdk/client-lambda').FunctionConfiguration} funcObj
 */
const makeListItemFromFunctionName = (funcObj) =>
  makeListItem(funcObj.FunctionName);

/**
 *
 * @param {import('@aws-sdk/client-lambda').ListFunctionsCommandOutput} response
 * @returns
 */
const getFunctionNames = (response) =>
  response.Functions.map(makeListItemFromFunctionName).join("\n");

const listFunctionsHandler = async () => {
  try {
    logger.log("Getting function list...");
    const response = await listFunctions();

    const functionList = getFunctionNames(response);
    logger.log(functionList);
  } catch (err) {
    logger.error(err);
  }
};

export { listFunctionsHandler };
