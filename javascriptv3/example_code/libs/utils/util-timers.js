/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { curry } from "ramda";

const wait = (seconds) =>
  new Promise((resolve) =>
    setTimeout(() => {
      resolve();
    }, seconds * 1000)
  );

const retry = curry(
  (config, fn) =>
    new Promise((resolve, reject) => {
      const { intervalInMs, maxRetries } = config;
      fn()
        .then(resolve)
        .catch((err) => {
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
    })
);

export { retry, wait };
