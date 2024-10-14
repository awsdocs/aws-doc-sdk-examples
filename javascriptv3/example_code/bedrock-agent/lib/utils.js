// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * @param {string[]} variables
 */
export const checkForPlaceholders = (variables) => {
  if (
    variables.some(
      (variable) => Number.isNaN(+variable) && variable.includes("["),
    )
  ) {
    throw new Error(
      "Error: One or more variables contain unresolved placeholders. Please ensure all placeholders are replaced with valid data and the brackets have been removed before proceeding.",
    );
  }
};
