// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, test, vi } from "vitest";
import path from "node:path";
import { Writable } from "node:stream";

describe("ConverseStream with text generation models", () => {
  const fileName = "converseStream.js";
  const baseDirectory = path.join(__dirname, "..", "models");

  const models = {
    amazonNovaText: "Amazon Nova",
    amazonTitanText: "Amazon Titan",
    anthropicClaude: "Anthropic Claude",
    cohereCommand: "Cohere Command",
    metaLlama: "Meta Llama",
    mistral: "Mistral",
  };

  const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

  test.sequential.each(
    Object.entries(models).map(([sub, name]) => [name, sub]),
  )("should invoke %s and return text", async (_, subdirectory) => {
    // Add a 500 ms delay before each test to avoid throttling issues
    await delay(500);
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
  });
});
