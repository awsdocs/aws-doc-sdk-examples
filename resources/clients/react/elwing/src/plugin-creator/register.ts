import { writeFileSync } from "fs";
import { format } from "prettier";
import { concat, join, map, pipe } from "ramda";
import {
  dirnameFromMetaUrl,
  readSubdirSync,
} from "../../../../../../javascriptv3/example_code/libs/utils/util-fs.js";
import { pascalCase } from "../../../../../../javascriptv3/example_code/libs/utils/util-string.js";
import { copyright } from "./copyright.js";

const __dirname = dirnameFromMetaUrl(import.meta.url);

const pluginsPath = `${__dirname}/../plugins`;
const manifestPath = `${__dirname}/../plugins/manifest.ts`;

const makeImport = (pluginName: string) =>
  `import { ${pascalCase(pluginName)} } from "ewp-${pluginName}";`;

const makeImports = pipe(
  map(makeImport),
  join(""),
  concat('import { AppPlugin } from "./AppPlugin";')
);

const makeDeclaration = (pluginNames: string[]) =>
  `const plugins: AppPlugin[] = [${map(pascalCase, pluginNames)}];`;

const makeExports = () => `export default plugins`;

const buildRegistryCode = () => {
  const pluginNames = readSubdirSync(pluginsPath, { withFileTypes: true });
  const imports = makeImports(pluginNames);
  const pluginDeclaration = makeDeclaration(pluginNames);
  const exports = makeExports();
  const code = `${copyright}${imports}${pluginDeclaration}${exports}`;

  return format(code, { parser: "babel" });
};

const refreshRegistry = () => {
  const registryCode = buildRegistryCode();
  writeFileSync(manifestPath, registryCode);
};

export { refreshRegistry };
