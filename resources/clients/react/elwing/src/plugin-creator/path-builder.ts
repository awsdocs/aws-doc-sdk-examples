/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { pipe, concat } from "ramda";
import { makeDir } from "../../libs/utils/util-fs";
import { kebabCase } from "../../libs/utils/util-string";

const makePluginPath = pipe(
  kebabCase,
  concat(`${__dirname}/../plugins/`),
  makeDir
);

export { makePluginPath };
