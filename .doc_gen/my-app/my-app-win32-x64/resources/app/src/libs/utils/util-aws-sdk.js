/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { curry, defaultTo } from "ramda";

const DEFAULT_REGION = "us-east-1";

const orDefaultRegion = defaultTo(DEFAULT_REGION);

const createClientForRegion = curry(
  (region, ClientConstructor) =>
    new ClientConstructor({ region: orDefaultRegion(region) })
);

const createClientForDefaultRegion = createClientForRegion(null);

export {
  DEFAULT_REGION,
  createClientForDefaultRegion,
  createClientForRegion,
  orDefaultRegion,
};
