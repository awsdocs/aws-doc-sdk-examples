// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
import { splitMapTrim } from "./util-string.js";

/**
 * @param {string} fileNames
 */
export const deleteFiles = (fileNames) => Promise.all(fileNames.map(unlink));

// snippet-start:[javascript.v3.utils.dirnameFromMetaUrl]
export const dirnameFromMetaUrl = (metaUrl) =>
  fileURLToPath(new URL(".", metaUrl));
// snippet-end:[javascript.v3.utils.dirnameFromMetaUrl]

export const getNewLineDelimitedEntries = (str) =>
  splitMapTrim("\n", getTmp(str) || "");

export const getTmp = (name) => {
  try {
    return readFileSync(`./${name}.tmp`, { encoding: "utf-8" });
  } catch (e) {
    return null;
  }
};

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

export const makeDir = (dir) => (existsSync(dir) ? dir : mkdirSync(dir));

export const readLines = (path) => readFileSync(path).toString().split("\n");

export const readSubdirSync = (directory) =>
  readdirSync(directory, { withFileTypes: true })
    .filter((dirent) => dirent.isDirectory())
    .map((dirent) => dirent.name);

export const zip = (inputPath) =>
  new Promise((resolve, reject) => {
    try {
      readdirSync(inputPath);
    } catch (err) {
      reject(
        new Error(
          `Cannot zip directory ${inputPath}. Directory doesn't exist.`,
        ),
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
