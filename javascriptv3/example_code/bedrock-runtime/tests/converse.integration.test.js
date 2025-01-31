// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, test, vi } from "vitest";
import path from "node:path";

describe("Converse with text generation models", () => {
  const baseDirectory = path.join(__dirname, "..", "models");
  const fileName = "converse.js";

  const subdirectories = [
    "ai21LabsJurassic2",
    "amazonTitanText",
    "anthropicClaude",
    "cohereCommand",
    "metaLlama",
    "mistral",
  ];

  test.each(subdirectories)(
    "should invoke the model and return text",
    async (subdirectory) => {
      const script = path.join(baseDirectory, subdirectory, fileName);
      const consoleLogSpy = vi.spyOn(console, "log");

      try {
        await import(script);
        const output = consoleLogSpy.mock.calls[0][0];
        expect(output).toMatch(/\S/);
      } finally {
        consoleLogSpy.mockRestore();
      }
    },
  );
});
