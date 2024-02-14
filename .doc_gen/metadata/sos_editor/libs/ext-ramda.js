/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  apply,
  bind,
  compose,
  concat,
  cond,
  curry,
  flip,
  identity,
  is,
  map,
  pipe,
  prop,
  split,
  T,
  trim,
} from "ramda";

const flipMap = flip(map);

const flipApply = flip(apply);

const flipApplyMap = curry((funcs) =>
  compose(flipMap(funcs), flipApply, (x) => [x])
);

const concatMap = compose(map, concat);

const nthAdjust = curry((i, fn, list) => fn(list[i]));

const parseString = cond([
  [is(String), identity],
  [is(Error), prop("message")],
  [T, JSON.stringify],
]);

const promiseAll = bind(Promise.all, Promise);

const splitMapTrim = curry(pipe(split, map(trim)));

export {
  flipApply,
  flipMap,
  flipApplyMap,
  concatMap,
  nthAdjust,
  parseString,
  promiseAll,
  splitMapTrim,
};
