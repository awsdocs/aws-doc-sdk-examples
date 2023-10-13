/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

const DEFAULT_REGION = "us-east-1";

const orDefaultRegion = (region) => region || DEFAULT_REGION;

const createClientForRegion = (region, ClientConstructor) =>
  new ClientConstructor({ region: orDefaultRegion(region) });

const createClientForDefaultRegion = (ClientConstructor) =>
  createClientForRegion(null, ClientConstructor);

export {
  DEFAULT_REGION,
  createClientForDefaultRegion,
  createClientForRegion,
  orDefaultRegion,
};
