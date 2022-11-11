/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

import { makeStartCrawlerStep } from "../scenarios/basic/steps/start-crawler.js";

describe("start-crawler", async () => {
  it("should call startCrawler with the crawler name from the environment variables", async () => {
    const startCrawler = vi.fn(async () => ({}));
    const getCrawler = vi.fn(async () => ({ Crawler: { State: "READY" } }));
    const actions = { startCrawler, getCrawler };

    const context = {};
    process.env.CRAWLER_NAME = "crawler_name";

    const step = makeStartCrawlerStep(actions);
    await step(context);

    expect(startCrawler).toHaveBeenCalledWith("crawler_name");
  });

  it("should return a context object", async () => {
    const startCrawler = vi.fn(async () => ({}));
    const getCrawler = vi.fn(async () => ({ Crawler: { State: "READY" } }));
    const actions = { startCrawler, getCrawler };

    const context = {};

    const step = makeStartCrawlerStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
