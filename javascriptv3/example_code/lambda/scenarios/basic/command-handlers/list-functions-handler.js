/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { compose, join, map, prop } from "ramda";
import { log } from "../../../../libs/utils/util-log.js";
import { listFunctions } from "../../../actions/list-functions.js";

const makeListItem = (s) => `• ${s}`;

const makeListItemFromFunctionName = compose(
  makeListItem,
  prop("FunctionName")
);

const getFunctionNames = compose(
  join("\n"),
  map(makeListItemFromFunctionName),
  prop("Functions")
);

const listFunctionsHandler = async () => {
  try {
    log(`Getting function list...`);
    const response = await listFunctions();

    const functionList = getFunctionNames(response);
    log(functionList);
  } catch (err) {
    log(err);
  }
};

export { listFunctionsHandler };
