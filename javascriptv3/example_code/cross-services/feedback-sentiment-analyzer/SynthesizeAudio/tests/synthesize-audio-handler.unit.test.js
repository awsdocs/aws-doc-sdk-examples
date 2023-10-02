import { describe, it, expect, vi } from "vitest";

const send = vi.fn(() => Promise.resolve());

vi.doMock("@aws-sdk/client-polly", async () => {
  const actual = await vi.importActual("@aws-sdk/client-polly");
  return {
    ...actual,
    PollyClient: class {
      send = send;
    },
  };
});

vi.doMock("@aws-sdk/lib-storage", async () => {
  const actual = await vi.importActual("@aws-sdk/lib-storage");
  return {
    ...actual,
    Upload: class {
      done = () => Promise.resolve();
    },
  };
});

const { handler } = await import("../src/index.js");

describe("synthesize-audio-handler", () => {
  it("should return the audio key", async () => {
    send.mockResolvedValueOnce({});

    const response = await handler({
      bucket: "test-bucket",
      translated_text: "I love you.",
      object: "image.jpg",
    });
    expect(response).toEqual("image.jpg.mp3");
  });
});
