import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-ec2", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ec2");
  return {
    ...actual,
    EC2Client: class {
      send = send;
    },
  };
});

import { main } from "../actions/describe-key-pairs.js";

describe("create-key-pair", () => {
  it("should log the returned key pairs", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      KeyPairs: [
        {
          KeyName: "foo",
          KeyPairId: "bar",
        },
      ],
    });

    await main();

    expect(logSpy).nthCalledWith(
      1,
      "The following key pairs were found in your account:"
    );
    expect(logSpy).nthCalledWith(2, " • bar: foo");
  });
});
