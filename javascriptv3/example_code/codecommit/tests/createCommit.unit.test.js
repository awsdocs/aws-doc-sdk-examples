/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect, vi } from "vitest";
import { main, getBranchParams } from "../src/createCommit";

vi.mock("../src/libs/codeCommitClient.js", () => {
  return {
    codeCommitClient: {
      ...vi.importActual("../src/libs/codeCommitClient.js"),
      send: vi.fn().mockResolvedValue({ isMock: true }),
    },
  };
});

describe("createCommit", () => {
  it("should mock CodeCommit client", async () => {
    const response = await main(getBranchParams);
    expect(response.isMock).toEqual(true);
  });
});
