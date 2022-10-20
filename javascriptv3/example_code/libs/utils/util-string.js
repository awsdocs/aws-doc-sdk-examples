/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { pipe, adjust, join, split, toLower, map, toUpper } from "ramda";

import { v4 as uuidv4 } from "uuid";

const getUniqueName = (name) => `${uuidv4()}-${name.toLowerCase()}`;

const postfix = (source, str) => {
  if (typeof str !== "string") {
    throw new Error("Cannot postfix a non-string value.");
  }

  return `${source}${str}`;
};

const downcaseSplit = pipe(toLower, split("-"));

const capitalize = pipe(Array.from, adjust(0, toUpper), join(""));

const kebabCase = pipe(downcaseSplit, join("-"));

const pascalCase = pipe(downcaseSplit, map(capitalize), join(""));

const snakeCase = pipe(downcaseSplit, join("_"));

const titleCase = pipe(downcaseSplit, map(capitalize), join(" "));

export {
  capitalize,
  downcaseSplit,
  getUniqueName,
  kebabCase,
  pascalCase,
  postfix,
  snakeCase,
  titleCase,
};
