/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { curry } from "ramda";

const nthAdjust: <T, V>(i: number, fn: (x: T) => V, list: T[]) => V = curry(
  (i, fn, list) => fn(list[i])
);

export { nthAdjust };
