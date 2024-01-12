// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

export const getEnv = (key) => process.env[key];
export const setEnv = (key, value) => {
  process.env[key] = value;
};
