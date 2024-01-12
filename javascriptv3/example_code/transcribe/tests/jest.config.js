// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
module.exports = {
  testEnvironment: "node",
  moduleDirectories: ["node_modules"],
  testMatch: ["**/tests/*.test.js"],
  transform: {
    "^.+\\.(js|jsx)$": "babel-jest",
  },
};
