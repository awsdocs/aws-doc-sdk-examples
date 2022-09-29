/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { compose, inc, nth } from "ramda";

const handler = compose((x) => Promise.resolve(x), inc, nth(0));

export { handler };
