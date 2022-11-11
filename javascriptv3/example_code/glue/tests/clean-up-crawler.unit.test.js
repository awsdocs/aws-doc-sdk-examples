/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, expect, it, vi } from "vitest";
import { GlueClient, DeleteCrawlerCommand } from "@aws-sdk/client-glue";
import { cleanUpCrawlerStep } from "../scenarios/basic/steps/clean-up-crawler.js";

vi.mock("@aws-sdk/client-glue", () => {
  const DeleteCrawlerCommand = class {};
  const GlueClient = class {
    async send() {}
  };
  return { GlueClient, DeleteCrawlerCommand };
});

describe("clean-up-crawler", () => {
  it('should call "deleteCrawler"', async () => {
    process.env.CRAWLER_NAME = "crawler";
    const spy = vi.spyOn(GlueClient.prototype, "send");
    await cleanUpCrawlerStep({});
    return expect(spy).toHaveBeenCalledWith(new DeleteCrawlerCommand());
  });

  it('should not throw an error if the delete fails with an "EntityNotFoundException"', () => {
    vi.spyOn(GlueClient.prototype, "send").mockImplementationOnce(async () => {
      const err = new Error();
      err.name = "EntityNotFoundException";
      throw err;
    });

    return expect(cleanUpCrawlerStep({})).resolves.toBeTruthy();
  });

  it('should throw an error if the delete fails with an error other than "EntityNotFoundException"', () => {
    vi.spyOn(GlueClient.prototype, "send").mockImplementationOnce(async () => {
      const err = new Error();
      err.name = "SomeOtherError";
      throw err;
    });

    return expect(cleanUpCrawlerStep({})).rejects.toBeTruthy();
  });

  it("should return a context object", async () => {
    const actual = await cleanUpCrawlerStep({});
    return expect(actual).toEqual({});
  });
});
