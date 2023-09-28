import { describe, it, expect, vi } from "vitest";

const send = vi.fn(() => Promise.resolve());

vi.doMock("@aws-sdk/client-textract", async () => {
  const actual = await vi.importActual("@aws-sdk/client-textract");
  return {
    ...actual,
    TextractClient: class {
      send = send;
    },
  };
});

const { handler } = await import("../src/index.js");

describe("extract-text-handler", () => {
  it("should create a single string from the word Blocks returned by Amazon Textract", async () => {
    send.mockResolvedValueOnce({
      Blocks: [
        {
          BlockType: "LINE",
          Text: "I love you.",
        },
        {
          BlockType: "WORD",
          Text: "I",
        },
        {
          BlockType: "WORD",
          Text: "know.",
        },
      ],
    });

    const response = await handler({ bucket: "bucket", object: "object" });
    expect(response).toEqual("I know.");
  });
});
