import { nodeResolve } from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";

export default {
  input: "src/index.js",
  output: {
    /**
     * The Lambda NodeJS runtime requires .mjs extensions to use ESM.
     */
    file: "dist/index.mjs",
    compact: true,
    format: "es",
  },

  plugins: [
    /**
     * By default Rollup will not bundle node_modules. This plugin allows that.
     */
    nodeResolve({ preferBuiltins: true }),
    /**
     * Allows CJS files to be included in bundle. This is mainly for Lodash.
     */
    commonjs(),
  ],
  external: [
    /**
     * Don't bundle the @aws-sdk. It's included in the Lambda NodeJS runtime.
     */
    /@aws-sdk/,
  ],
};
