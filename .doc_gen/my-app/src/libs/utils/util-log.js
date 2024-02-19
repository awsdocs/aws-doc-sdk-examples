/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { bind, compose, tap } from "ramda";
import { parseString } from "../ext-ramda.js";

const boundLog = bind(console.log, console);

const log = compose(tap(boundLog), parseString);

export { log };
