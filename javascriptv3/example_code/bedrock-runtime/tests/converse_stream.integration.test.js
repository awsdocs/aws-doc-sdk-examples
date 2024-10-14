// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, test, vi } from "vitest";
import path from "node:path";
import { Writable } from "node:stream";

describe("ConverseStream with text generation models", () => {
  const fileName = "converseStream.js";
  const baseDirectory = path.join(__dirname, "..", "models");

  const subdirectories = [
    "amazonTitanText",
    "anthropicClaude",
    "cohereCommand",
    "metaLlama",
    "mistral",
  ];

  test.each(subdirectories)(
    "should invoke the model and return text",
    async (subdirectory) => {
      let output = "";
      const outputStream = new Writable({
        write(/** @type string */ chunk, encoding, callback) {
          output += chunk.toString();
          callback();
        },
      });

      const stdoutWriteSpy = vi
        .spyOn(process.stdout, "write")
        .mockImplementation(outputStream.write.bind(outputStream));

      const script = path.join(baseDirectory, subdirectory, fileName);

      try {
        await import(script);
        expect(output).toMatch(/\S/);
      } finally {
        stdoutWriteSpy.mockRestore();
      }
    },
  );
});
