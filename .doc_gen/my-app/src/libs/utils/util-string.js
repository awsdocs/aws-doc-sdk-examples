/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { pipe, adjust, join, split, toLower, map, toUpper } from "ramda";

import { v4 as uuidv4 } from "uuid";

export const getUniqueName = (name) => `${uuidv4()}-${name.toLowerCase()}`;

export const postfix = (source, str) => {
  if (typeof str !== "string") {
    throw new Error("Cannot postfix a non-string value.");
  }

  return `${source}${str}`;
};

export const downcaseSplit = pipe(toLower, split("-"));

export const capitalize = pipe(Array.from, adjust(0, toUpper), join(""));

export const kebabCase = pipe(downcaseSplit, join("-"));

export const pascalCase = pipe(downcaseSplit, map(capitalize), join(""));

export const snakeCase = pipe(downcaseSplit, join("_"));

export const titleCase = pipe(downcaseSplit, map(capitalize), join(" "));

// snippet-start:[javascript.v3.utils.wrapText]
export const wrapText = (text, char = "=") => {
  const rule = char.repeat(80);
  return `${rule}\n    ${text}\n${rule}\n`;
};
// snippet-end:[javascript.v3.utils.wrapText]
