/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, expect, it, vi } from "vitest";
import { makeCleanUpCrawlerStep } from "../scenarios/basic/steps/clean-up-crawler.js";

describe("clean-up-crawler", () => {
  it('should call "deleteCrawler"', async () => {
    const deleteCrawler = vi.fn(async () => {});
    const cleanUpCrawlerStep = makeCleanUpCrawlerStep({ deleteCrawler });
    process.env.CRAWLER_NAME = "crawler";
    await cleanUpCrawlerStep({});
    expect(deleteCrawler).toHaveBeenCalledWith("crawler");
  });

  it('should not throw an error if the delete fails with an "EntityNotFoundException"', () => {
    const deleteCrawler = vi.fn(async () => {
      const err = new Error();
      err.name = "EntityNotFoundException";
      throw err;
    });

    const cleanUpCrawlerStep = makeCleanUpCrawlerStep({ deleteCrawler });

    expect(
      cleanUpCrawlerStep({ envVars: { CRAWLER_NAME: "crawler" } })
    ).resolves.toBeTruthy();
  });

  it('should throw an error if the delete fails with an error other than "EntityNotFoundException"', () => {
    const deleteCrawler = vi.fn(async () => {
      const err = new Error();
      err.name = "SomeOtherError";
      throw err;
    });

    const cleanUpCrawlerStep = makeCleanUpCrawlerStep({ deleteCrawler });

    expect(
      cleanUpCrawlerStep({ envVars: { CRAWLER_NAME: "crawler" } })
    ).rejects.toBeTruthy();
  });

  it("should return a context object", async () => {
    const deleteCrawler = vi.fn(async () => {});
    const actions = { deleteCrawler };

    const context = { envVars: {} };

    const step = makeCleanUpCrawlerStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
