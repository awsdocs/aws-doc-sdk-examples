/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { pipe, toLower, split, toUpper, map, join, adjust } from "ramda";

// TODO: Make the delimiter generic.
const downcaseSplit = pipe(toLower, split("-"));

const capitalize = pipe<string[], string[], string[], string>(
  Array.from,
  adjust(0, toUpper),
  join("")
);

const kebabCase = pipe(downcaseSplit, join("-"));

const pascalCase = pipe<string[], string[], string[], string>(
  downcaseSplit,
  map(capitalize),
  join("")
);

const snakeCase = pipe(downcaseSplit, join("_"));

const titleCase = pipe(downcaseSplit, map(capitalize), join(" "));

export {
  downcaseSplit,
  capitalize,
  kebabCase,
  pascalCase,
  snakeCase,
  titleCase,
};
