// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, afterAll, it, expect } from "vitest";

import { run, FILTER_NAME } from "../src/ses_createreceiptfilter";
import { deleteReceiptFilter } from "../src/libs/sesUtils";

describe("ses_createreceiptfilter", () => {
  afterAll(async () => {
    await deleteReceiptFilter(FILTER_NAME);
  });

  /**
   * @typedef {import('@aws-sdk/client-ses').DeleteReceiptFilterCommandOutput} DeleteReceiptFilterCommandOutput
   */

  it("should successfully create a filter", async () => {
    /** @type {DeleteReceiptFilterCommandOutput} */
    const result = await run();
    expect(result.$metadata.httpStatusCode).toBe(200);
  });
});
