/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { pipe, concat } from "ramda";
import {
  makeDir,
  dirnameFromMetaUrl,
} from "../../../../../../javascriptv3/example_code/libs/utils/util-fs.js";
import { kebabCase } from "../../../../../../javascriptv3/example_code/libs/utils/util-string.js";

const __dirname = dirnameFromMetaUrl(import.meta.url);

const makePluginPath = pipe(
  kebabCase,
  concat(`${__dirname}/../plugins/`),
  makeDir
);

export { makePluginPath };
