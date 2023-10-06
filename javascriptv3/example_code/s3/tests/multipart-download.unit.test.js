import { describe, it, expect, vi } from "vitest";

const send = vi.fn(() => Promise.resolve());

vi.doMock("@aws-sdk/client-s3", async () => {
  const actual = await vi.importActual("@aws-sdk/client-s3");
  return {
    ...actual,
    S3Client: class {
      send = send;
    },
  };
});

const { getObjectRange, getRangeAndLength, isComplete } = await import(
  "../scenarios/multipart-download.js"
);

describe("multipart-download", () => {
  describe("getObjectRange", () => {
    it("should call 'send' with the provided range", async () => {
      await getObjectRange({
        bucket: "bucket",
        key: "key",
        start: 0,
        end: 10,
      });

      expect(send).toHaveBeenCalledWith(
        expect.objectContaining({
          input: expect.objectContaining({
            Range: "bytes=0-10",
            Bucket: "bucket",
            Key: "key",
          }),
        }),
      );
    });
  });

  describe("getRangeAndLength", () => {
    it("should take a unitless http content-range and return the start, end, and length", () => {
      expect(getRangeAndLength("0-10/100")).toEqual({
        start: 0,
        end: 10,
        length: 100,
      });
    });
  });

  describe("isComplete", () => {
    it("should return true if the end byte is equal to the last byte", () => {
      expect(isComplete({ start: 0, end: 10, length: 11 })).toBe(true);
    });

    it("should return false if the end byte is not equal to the last byte", () => {
      expect(isComplete({ start: 0, end: 10, length: 10 })).toBe(false);
    });
  });
});
