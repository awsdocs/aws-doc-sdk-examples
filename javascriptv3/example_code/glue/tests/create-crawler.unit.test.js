/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import { makeCreateCrawlerStep } from "../scenarios/basic/steps/create-crawler.js";

describe("create-crawler", () => {
  it("should not create a crawler if it is already created", async () => {
    const getCrawler = vi.fn(async () => {});
    const createCrawler = vi.fn();
    const createCrawlerStep = makeCreateCrawlerStep({
      getCrawler,
      createCrawler,
    });

    await createCrawlerStep({ envVars: { CRAWLER_NAME: "crwlr" } });

    expect(createCrawler).not.toHaveBeenCalled();
  });

  it("should create a crawler if it is not already created", async () => {
    const getCrawler = vi.fn(async () => {
      throw new Error();
    });
    const createCrawler = vi.fn();
    const createCrawlerStep = makeCreateCrawlerStep({
      getCrawler,
      createCrawler,
    });

    await createCrawlerStep({ envVars: { CRAWLER_NAME: "crwlr" } });

    expect(createCrawler).toHaveBeenCalled();
  });

  it("should return a context object", async () => {
    const getCrawler = vi.fn();
    const createCrawler = vi.fn();
    const actions = { getCrawler, createCrawler };

    const context = { envVars: {} };

    const step = makeCreateCrawlerStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
