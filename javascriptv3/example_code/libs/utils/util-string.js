// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { v4 as uuidv4 } from "uuid";

/**
 * @param {string} name
 */
export const getUniqueName = (name) => `${uuidv4()}-${name.toLowerCase()}`;

export const postfix = (source, str) => {
  if (typeof str !== "string") {
    throw new Error("Cannot postfix a non-string value.");
  }

  return `${source}${str}`;
};

/**
 * @param {string} str
 */
const downcaseSplit = (str) => str.toLowerCase().split("-");

/**
 * @param {string} str
 */
const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

export const kebabCase = (str) => downcaseSplit(str).join("-");
export const pascalCase = (str) => downcaseSplit(str).map(capitalize).join("");
export const snakeCase = (str) => downcaseSplit(str).join("_");
export const titleCase = (str) => downcaseSplit(str).map(capitalize).join(" ");

/**
 * Take a string as input and split it into an array using the delimiter provided.
 * Trim each element in the array.
 * @param {string} delimiter
 * @param {string} input
 */
export const splitMapTrim = (delimiter, input) =>
  input.split(delimiter).map((s) => s.trim());

export const parseString = (input) => {
  if (typeof input === "string") {
    return input;
  }
  if (input instanceof Error) {
    return input.message;
  }
  return JSON.stringify(input);
};

// snippet-start:[javascript.v3.utils.wrapText]
export const wrapText = (text, char = "=") => {
  const rule = char.repeat(80);
  return `${rule}\n    ${text}\n${rule}\n`;
};
// snippet-end:[javascript.v3.utils.wrapText]
