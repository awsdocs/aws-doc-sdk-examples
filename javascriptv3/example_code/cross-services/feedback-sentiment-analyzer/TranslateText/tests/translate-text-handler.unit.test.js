import { describe, it, expect, vi } from "vitest";

const send = vi.fn(() => Promise.resolve());

vi.doMock("@aws-sdk/client-translate", async () => {
  const actual = await vi.importActual("@aws-sdk/client-translate");
  return {
    ...actual,
    TranslateClient: class {
      send = send;
    },
  };
});

const { handler } = await import("../src/index.js");

describe("translate-text-handler", () => {
  it("should return the the translated text", async () => {
    send.mockResolvedValueOnce({
      TranslatedText: "I love you.",
    });

    const response = await handler({
      extracted_text: "J'adore.",
      source_language_code: "fr",
    });
    expect(response).toEqual({ translated_text: "I love you." });
  });
});
