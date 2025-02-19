// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { mkdir, readdir, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

/**
 * Creates the output directory if it doesn't exist and gets the next available image number
 * @param {string} outputDir - The directory path where images will be saved
 * @returns {Promise<number>} The next available image number
 */
async function prepareOutputDirectory(outputDir) {
  try {
    await mkdir(outputDir, { recursive: true });
    const files = await readdir(outputDir);

    // Find the highest existing image number
    const numbers = files
      .filter((file) => file.match(/^image-\d+\.png$/))
      .map((file) => Number.parseInt(file.match(/^image-(\d+)\.png$/)[1]));

    return numbers.length > 0 ? Math.max(...numbers) + 1 : 1;
  } catch (error) {
    console.error(`Error preparing output directory: ${error.message}`);
    throw error;
  }
}

/**
 * Saves an image to the output directory with automatic numbering
 * @param {string} imageData - Base64-encoded image data
 * @param {string} modelName - Name of the model used to generate the image
 * @returns {Promise<string>} The full path where the image was saved
 */
export async function saveImage(imageData, modelName) {
  // Set up the output directory path relative to this utility script
  const utilityDir = dirname(fileURLToPath(import.meta.url));
  const outputDir = join(utilityDir, "..", "output", modelName);

  // Get the next available image number
  const imageNumber = await prepareOutputDirectory(outputDir);

  // Create the image filename with padded number
  const paddedNumber = imageNumber.toString().padStart(2, "0");
  const filename = `image-${paddedNumber}.png`;
  const fullPath = join(outputDir, filename);

  // Save the image
  const buffer = Buffer.from(imageData, "base64");
  await writeFile(fullPath, buffer);

  return fullPath;
}
