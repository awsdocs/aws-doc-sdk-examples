// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect } from "vitest";

const { RESPONSE_HEADERS } = await import("../src/common.js");
import { getHandler } from "../src/functions/upload.js";

describe("upload handler", () => {
  it("should return a JSON body with a url prop", async () => {
    const event = {
      body: JSON.stringify({
        file_name: "test.jpg",
      }),
    };

    const handler = getHandler({ createPresignedPutURL: () => "signed" });
    const result = await handler(event);

    expect(result).toEqual({
      statusCode: 200,
      headers: RESPONSE_HEADERS,
      body: JSON.stringify({
        url: "signed",
      }),
    });
  });
});
