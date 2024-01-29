// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/**
 *
 * @param {Array} arr
 * @param {number} stride
 */
export function* chunkArray(arr, stride = 1) {
  for (let i = 0; i < arr.length; i += stride) {
    yield arr.slice(i, Math.min(i + stride, arr.length));
  }
}

/**
 * @param {string[]} positionalArgs
 */
export function startsWith(positionalArgs) {
  /**
   * @param {string[]} list
   */
  return (list) => {
    for (const [i, v] of positionalArgs.entries()) {
      if (list[i] !== v) return false;
    }
    return true;
  };
}
