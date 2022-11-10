/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { wait } from "../../../../libs/utils/util-timers.js";
import { log } from "../log.js";

/** snippet-start:[javascript.v3.glue.scenarios.basic.StartCrawlerStep] */
const waitForCrawler = async (getCrawler, crawlerName) => {
  const waitTimeInSeconds = 30;
  const { Crawler } = await getCrawler(crawlerName);

  if (!Crawler) {
    throw new Error(`Crawler with name ${crawlerName} not found.`);
  }

  if (Crawler.State === "READY") {
    return;
  }

  log(`Crawler is ${Crawler.State}. Waiting ${waitTimeInSeconds} seconds...`);
  await wait(waitTimeInSeconds);
  return waitForCrawler(getCrawler, crawlerName);
};

const makeStartCrawlerStep =
  ({ startCrawler, getCrawler }) =>
  async (context) => {
    log("Starting crawler.");
    await startCrawler(context.envVars.CRAWLER_NAME);
    log("Crawler started.", { type: "success" });

    log("Waiting for crawler to finish running. This can take a while.");
    await waitForCrawler(getCrawler, context.envVars.CRAWLER_NAME);
    log("Crawler ready.", { type: "success" });

    return { ...context };
  };
/** snippet-end:[javascript.v3.glue.scenarios.basic.StartCrawlerStep] */

export { makeStartCrawlerStep, waitForCrawler };
