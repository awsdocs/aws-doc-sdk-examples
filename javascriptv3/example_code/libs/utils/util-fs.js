/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { compose, map, tryCatch, always } from "ramda";
import { unlink, readFile } from "fs/promises";
import {
  readdirSync,
  createWriteStream,
  readFileSync,
  writeFileSync,
} from "fs";
import archiver from "archiver";
import { fileURLToPath } from "url";
import { log } from "./util-log.js";
import { promiseAll } from "../ext-ramda.js";

const deleteFiles = compose(promiseAll, map(unlink));

const dirnameFromMetaUrl = (metaUrl) => {
  return fileURLToPath(new URL(".", metaUrl));
};

const getTmp = tryCatch(
  (name) => readFileSync(`./${name}.tmp`, { encoding: "utf-8" }),
  always(null)
);

const setTmp = (name, data) =>
  writeFileSync(`./${name}.tmp`, data, { encoding: "utf-8" });

const handleZipWarning = (resolve) => (w) => {
  log(w);
  resolve();
};

const handleZipEnd = (resolve, path) => async () => {
  log(`Zipped successfully.`);
  const buffer = await readFile(path);
  resolve(buffer);
};

const zip = (inputPath) =>
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

export { deleteFiles, dirnameFromMetaUrl, getTmp, setTmp, zip };
