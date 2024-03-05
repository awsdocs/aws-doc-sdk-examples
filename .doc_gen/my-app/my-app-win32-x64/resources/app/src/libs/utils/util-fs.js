/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  compose,
  map,
  tryCatch,
  always,
  identity,
  ifElse,
  invoker,
  split,
  tap,
  pipe,
  filter,
  prop,
  defaultTo,
  curry,
} from "ramda";
import { unlink, readFile } from "fs/promises";
import {
  readdirSync,
  createWriteStream,
  readFileSync,
  writeFileSync,
  existsSync,
  mkdirSync,
} from "fs";
import archiver from "archiver";
import { fileURLToPath } from "url";
import { log } from "./util-log.js";
import { promiseAll, splitMapTrim } from "../ext-ramda.js";

export const deleteFiles = compose(promiseAll, map(unlink));

// snippet-start:[javascript.v3.utils.dirnameFromMetaUrl]
export const dirnameFromMetaUrl = (metaUrl) => {
  return fileURLToPath(new URL(".", metaUrl));
};
// snippet-end:[javascript.v3.utils.dirnameFromMetaUrl]

export const getDelimitedEntries = curry((delimiter, str) =>
  pipe(getTmp, defaultTo(""), splitMapTrim(delimiter))(str)
);

export const getNewLineDelimitedEntries = getDelimitedEntries("\n");

export const getTmp = tryCatch(
  (name) => readFileSync(`./${name}.tmp`, { encoding: "utf-8" }),
  always(null)
);

export const setTmp = (name, data) =>
  writeFileSync(`./${name}.tmp`, data, { encoding: "utf-8" });

export const handleZipWarning = (resolve) => (w) => {
  log(w);
  resolve();
};

export const handleZipEnd = (resolve, path) => async () => {
  log(`Zipped successfully.`);
  const buffer = await readFile(path);
  resolve(buffer);
};

export const makeDir = ifElse(existsSync, identity, tap(mkdirSync));

export const readLines = pipe(
  readFileSync,
  invoker(0, "toString"),
  split("\n")
);

export const readSubdirSync = pipe(
  readdirSync,
  filter(invoker(0, "isDirectory")),
  map(prop("name"))
);

/**
 *
 * @param {string} inputPath
 * @returns {Promise<Buffer>}
 */
export const zip = (inputPath) =>
  new Promise((resolve, reject) => {
    try {
      readdirSync(inputPath);
    } catch (err) {
      reject(
        new Error(`Cannot zip directory ${inputPath}. Directory doesn't exist.`)
      );
      return;
    }

    const archive = archiver("zip");

    log(`Zipping ${inputPath}...`);

    const output = createWriteStream(`${inputPath}.zip`);
    output.on("close", handleZipEnd(resolve, `${inputPath}.zip`));

    archive.pipe(output);
    archive.on("error", reject);
    archive.on("warning", handleZipWarning(resolve));
    archive.directory(inputPath, false);
    archive.finalize();
  });
