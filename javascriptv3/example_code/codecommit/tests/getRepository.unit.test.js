/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect, vi } from "vitest";
import { main, params } from "../src/getRepository";

vi.mock("../src/libs/codeCommitClient.js", () => {
  return {
    codeCommitClient: {
      ...vi.importActual("../src/libs/codeCommitClient.js"),
      send: vi.fn().mockResolvedValue({ isMock: true }),
    },
  };
});

describe("getRepository", () => {
  it("should mock CodeCommit client", async () => {
    const response = await main(params);
    expect(response.isMock).toEqual(true);
  });
});
