// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { parseString } from "@aws-doc-sdk-examples/lib/utils/util-string.js";

const log = (str) => {
  const parsed = parseString(str);
  console.log(parsed);
  return parsed;
};

export { log };
