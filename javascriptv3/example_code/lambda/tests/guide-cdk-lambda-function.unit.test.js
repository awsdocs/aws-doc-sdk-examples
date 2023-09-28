import { describe, it, expect, vi } from "vitest";

const mockSendFn = vi.fn();
vi.doMock("@aws-sdk/client-s3", () => {
  return {
    S3Client: class {
      send = mockSendFn;
    },
    ListObjectsCommand: class {},
  };
});

const { handler } = await import(
  "../guide_supplements/guide-cdk-lambda-function.js"
);

describe("guide-cdk-lambda-function", () => {
  it("should return a list of object names if S3 responds successfully", async () => {
    process.env.BUCKET = "test-bucket";
    mockSendFn.mockImplementationOnce(() =>
      Promise.resolve({
        Contents: [{ Key: "test-key" }],
      }),
    );

    const response = await handler({ httpMethod: "GET", path: "/" });

    expect(response.statusCode).toBe(200);
    expect(response.body).toEqual(["test-key"]);
  });

  it("should return a 204 if the bucket is empty", async () => {
    process.env.BUCKET = "test-bucket";
    mockSendFn.mockImplementationOnce(() => Promise.resolve({ Contents: [] }));

    const response = await handler({ httpMethod: "GET", path: "/" });

    expect(response.statusCode).toBe(204);
  });

  it("should return a 400 if there is no bucket name", async () => {
    process.env.BUCKET = undefined;
    mockSendFn.mockImplementationOnce(() =>
      Promise.resolve({ Contents: [{ Key: "test-key" }] }),
    );

    const response = await handler({ httpMethod: "GET", path: "/" });

    expect(response.statusCode).toBe(400);
  });

  it("should return a 400 if an unsupported http method is called", async () => {
    const response = await handler({ httpMethod: "PATCH", path: "/" });
    expect(response.statusCode).toBe(400);
  });
});
