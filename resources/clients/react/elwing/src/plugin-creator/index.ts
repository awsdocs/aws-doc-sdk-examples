/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { writeFileSync } from "fs";
import {
  kebabCase,
  pascalCase,
} from "../../../../../../javascriptv3/example_code/libs/utils/util-string.js";
import { nthAdjust } from "../../../../../../javascriptv3/example_code/libs/ext-ramda.js";
import { getArgValidationErrors } from "./validations.js";
import { makePluginPath } from "./path-builder.js";
import {
  makeComponentContents,
  makePluginContents,
  makePackageJsonContents,
} from "./content-builder.js";
import { refreshRegistry } from "./register.js";
import { execSync } from "child_process";

(async () => {
  const errors = getArgValidationErrors(process.argv);

  if (errors) {
    console.error(errors);
    return;
  }

  const pluginName = nthAdjust(2, kebabCase, process.argv);
  const path = makePluginPath(pluginName);
  const pluginContents = makePluginContents(pluginName);
  const componentContents = makeComponentContents(pluginName);
  const packageJsonContents = makePackageJsonContents(pluginName);
  writeFileSync(`${path}/package.json`, packageJsonContents);
  writeFileSync(`${path}/index.ts`, pluginContents);
  writeFileSync(
    `${path}/${pascalCase(pluginName)}Component.tsx`,
    componentContents
  );
  writeFileSync(`${path}/.gitignore`, "node_modules");
  execSync(`npm i file:src/plugins/${pluginName}`);
  refreshRegistry();
})();
