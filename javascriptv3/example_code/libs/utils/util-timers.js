/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

const wait = (seconds) =>
  new Promise((resolve) =>
    setTimeout(() => {
      resolve();
    }, seconds * 1000)
  );

/**
 * @template T
 * @param {{ intervalInMs: number, maxRetries: number }} config
 * @param {() => Promise<T>} fn
 * @returns {Promise<T>}
 */
const retry = (config, fn) =>
  new Promise((resolve, reject) => {
    const { intervalInMs = 500, maxRetries = 10 } = config;
    fn()
      .then(resolve)
      .catch((err) => {
        console.warn(
          `Callback in retry function failed. Retrying... ${maxRetries}`
        );
        console.warn(err instanceof Error ? err.message : err);
        if (maxRetries === 0) {
          reject(err);
        } else {
          setTimeout(() => {
            retry({ intervalInMs, maxRetries: maxRetries - 1 }, fn).then(
              resolve,
              reject
            );
          }, intervalInMs);
        }
      });
  });

export { retry, wait };
