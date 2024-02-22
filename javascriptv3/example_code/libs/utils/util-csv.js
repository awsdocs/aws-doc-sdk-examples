// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { splitMapTrim } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { getNewLineDelimitedEntries } from "./util-fs.js";

const getFirstEntry = (input) => {
  const entries = getNewLineDelimitedEntries(input);
  const mappedEntries = entries.map((entry) => splitMapTrim(",", entry));
  return mappedEntries[0];
};

const getSecondValuesFromEntries = (input) => {
  const entries = getNewLineDelimitedEntries(input);
  return entries.map((entry) => {
    const parts = splitMapTrim(",", entry);
    return parts[1];
  });
};

const getFirstValuesFromEntries = (input) => {
  const entries = getNewLineDelimitedEntries(input);
  return entries.map((entry) => {
    const parts = splitMapTrim(",", entry);
    return parts[0];
  });
};

export { getFirstEntry, getFirstValuesFromEntries, getSecondValuesFromEntries };
