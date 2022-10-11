/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { writeFileSync } from "fs";
import { kebabCase, pascalCase } from "../../libs/utils/util-string";
import { nthAdjust } from "../../libs/ext-ramda";
import { getArgValidationErrors } from "./validations";
import { makePluginPath } from "./path-builder";
import { makeComponentContents, makePluginContents, makePackageJsonContents } from "./content-builder";
import { refreshRegistry } from "./register";
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
  const packageJsonContents = makePackageJsonContents(pluginName)
  writeFileSync(`${path}/package.json`, packageJsonContents);
  writeFileSync(`${path}/index.ts`, pluginContents);
  writeFileSync(`${path}/${pascalCase(pluginName)}Component.tsx`, componentContents);
  writeFileSync(`${path}/.gitignore`, "node_modules");
  execSync(`npm i file:src/plugins/${pluginName}`);
  refreshRegistry();
})();
