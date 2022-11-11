/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";

/** snippet-start:[javascript.v3.glue.scenarios.basic.CleanUpCrawler] */
const makeCleanUpCrawlerStep =
  ({ deleteCrawler }) =>
  async (context) => {
    log(`Deleting crawler.`);

    try {
      await deleteCrawler(process.env.CRAWLER_NAME);
      log("Crawler deleted.", { type: "success" });
    } catch (err) {
      if (err.name === "EntityNotFoundException") {
        log(`Crawler is already deleted.`);
      } else {
        throw err;
      }
    }

    return { ...context };
  };
/** snippet-end:[javascript.v3.glue.scenarios.basic.CleanUpCrawler] */

export { makeCleanUpCrawlerStep };
