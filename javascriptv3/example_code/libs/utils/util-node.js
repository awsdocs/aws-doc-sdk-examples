// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

export const getEnv = (/** @type {string} */ key) => process.env[key];
export const setEnv = (/** @type {string} */ key, value) => {
  process.env[key] = value;
};
