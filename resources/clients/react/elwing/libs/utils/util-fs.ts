/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { existsSync, mkdirSync, readdirSync, readFileSync } from "fs";
import {
  identity,
  ifElse,
  pipe,
  tap,
  invoker,
  split,
  filter,
  map,
  prop,
} from "ramda";

const makeDir = ifElse(existsSync, identity, tap(mkdirSync));

const readLines = pipe(readFileSync, invoker(0, "toString"), split("\n"));

const readSubdirSync = pipe(
  readdirSync,
  filter(invoker(0, "isDirectory")),
  map(prop("name"))
);

export { makeDir, readLines, readSubdirSync };
