// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, test, vi } from "vitest";
import path from "node:path";

describe("Converse with text generation models", () => {
  const baseDirectory = path.join(__dirname, "..", "models");
  const fileName = "converse.js";

  const models = {
    ai21LabsJurassic2: "AI21 Labs Jurassic-2",
    amazonNovaText: "Amazon Nova",
    amazonTitanText: "Amazon Titan",
    anthropicClaude: "Anthropic Claude",
    cohereCommand: "Cohere Command",
    metaLlama: "Meta Llama",
    mistral: "Mistral",
  };

  test.each(Object.entries(models).map(([sub, name]) => [name, sub]))(
    "should invoke %s and return text",
    async (_, subdirectory) => {
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
