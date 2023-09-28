import { describe, it, expect, vi } from "vitest";

const send = vi.fn(() => Promise.resolve());

vi.doMock("@aws-sdk/client-comprehend", async () => {
  const actual = await vi.importActual("@aws-sdk/client-comprehend");
  return {
    ...actual,
    ComprehendClient: class {
      send = send;
    },
  };
});

const { handler } = await import("../src/index.js");

describe("analyze-sentiment-handler", () => {
  it("should return an object with the sentiment and language_code", async () => {
    send
      .mockResolvedValueOnce({
        Languages: [{ LanguageCode: "fr" }],
      })
      .mockResolvedValueOnce({
        Sentiment: "POSITIVE",
      });

    const response = await handler({ source_text: "J'adore." });
    expect(response).toEqual({
      sentiment: "POSITIVE",
      language_code: "fr",
    });
  });
});
